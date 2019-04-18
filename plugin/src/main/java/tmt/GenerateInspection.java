package tmt;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.TestFrameworks;
import com.intellij.codeInsight.daemon.impl.AnnotationHolderImpl;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.codeInspection.*;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationSession;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.JavaVersionService;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringManager;
import com.intellij.refactoring.migration.MigrationManager;
import com.intellij.refactoring.migration.MigrationMap;
import com.intellij.testIntegration.TestFramework;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.junit.JUnit5ConverterInspection;
import com.siyeh.ig.junit.JUnitCommonClassNames;
import com.siyeh.ig.psiutils.TestUtils;
import org.jetbrains.annotations.Nullable;
import tmt.analyze.SyntaxUtils;
import tmt.analyze.filter.Filter;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import tmt.attributes.TextAttributes;
import tmt.dsl.data.Generator;

import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.AnnotationUtil.CHECK_HIERARCHY;

public class GenerateInspection extends LocalInspectionTool {

    public GenerateInspection() {
        System.err.println("*&&*");
    }
    @Override
    public ProblemDescriptor[] checkFile(@NotNull final PsiFile psiFile,
                                         @NotNull final InspectionManager manager,
                                         final boolean isOnTheFly) {
        Editor ed = FileEditorManager.getInstance(psiFile.getProject()).getSelectedTextEditor();

        if (ed == null) {
            System.out.println("No editor.");
            return new ProblemDescriptor[0];
        }

        ArrayList<SuggestGenerate> suggests = new ArrayList<>();

        for (int j = ed.getCaretModel().getPrimaryCaret().getLogicalPosition().line; j > 0; j--) {
            String current_text;
            if (ed.getDocument().getLineEndOffset(j) != ed.getDocument().getTextLength()) {
                current_text = psiFile.getText().substring(ed.getDocument().getLineStartOffset(j), ed.getDocument().getLineEndOffset(j));
            } else {
                return new ProblemDescriptor[0];
            }
        }

        return suggests.toArray(new ProblemDescriptor[0]);
    }

    @NotNull
    @Override
    public String getShortName() {
        return "tmt.GenerateInspection";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Instance field count";
    }

    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder,
                                          final boolean isOnTheFly,
                                          @NotNull final LocalInspectionToolSession session) {
        final PsiFile psiFile = session.getFile();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(psiFile.getProject());
        Document document = documentManager.getDocument(psiFile);
//        Generator g = new Generator();

        Analyzer an = new Analyzer(psiFile, document);

        return new PsiElementVisitor() {
            HashSet<Integer> lines = new HashSet<>();
            @Override
            public void visitElement(PsiElement element) {
                if (element.isValid() && !element.getText().isEmpty()) {
                    int el_line = document.getLineNumber(element.getTextRange().getStartOffset());

                    String current_text = "";
                    if (document.getLineEndOffset(el_line) != document.getTextLength()) {
                        current_text = psiFile.getText().substring(document.getLineStartOffset(el_line), document.getLineEndOffset(el_line)).trim();
                    }

                    if (!lines.contains(el_line)
                            && validBlock(element, current_text)) {
                       // System.err.println("^"+el_line+" - "+element + "-" + current_text);
                        if (Filter.pass(psiFile, el_line, document)) {
                            an.analyze(el_line, element);

                            ArrayList<SuggestGenerate> problems = Filter.removeDoubles(an.getSuggests(), current_text, holder, psiFile.getProject(), element);

                            for (SuggestGenerate s : problems)
                                holder.registerProblem(s.getPsiElement(), s.getFixMessage(), s.getFixes());
                        }
//                        LocalQuickFix[] qf = {new tmt.QuickFix(element.getText(), null, 1, element)};
//                        holder.registerProblem(element, element.toString(), qf);
                        lines.add(el_line);
//                        System.err.println(holder.getResults()/*element.getText()*/+"!" + el_line);
                    }
                }
            }
        };
    }

    public boolean validBlock(PsiElement element, String current_text) {
        return (!(element instanceof PsiComment) && !(element instanceof PsiDocToken) && !(element instanceof PsiImportStatement) &&
                element.getText().contains(current_text) && (!element.getText().contains("\n")
                        || (element instanceof PsiStatement && !element.getText().contains(";"))
                        || element instanceof PsiDeclarationStatement
                        || element instanceof PsiExpressionStatement));
    }
}
