import com.intellij.lang.annotation.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SimpleAnnotator implements Annotator  {
    ArrayList<Integer> annotated = new ArrayList<>();
    private static final Key<ArrayList<Integer>> ANNOTATED = Key.create("ANNOTATED");
    @Override
    public void annotate(@NotNull final PsiElement  element, @NotNull AnnotationHolder holder) {
        /*int lineNum = document.getLineNumber(needHighlightPsiElement.getTextRange().getStartOffset());
final TextAttributes textattributes = new TextAttributes(null, backgroundColor, null, EffectType.LINE_UNDERSCORE, Font.PLAIN);
final Project project = needHighlightPsiElement.getProject();
final FileEditorManager editorManager = FileEditorManager.getInstance(project);
final Editor editor = editorManager.getSelectedTextEditor();
editor.getMarkupModel().addLineHighlighter(lineNum, HighlighterLayer.CARET_ROW, textattributes);*/
//        System.err.println(annotated.size());
//        }

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                Project project = element.getProject();

                Editor ed = FileEditorManager.getInstance(project).getSelectedTextEditor();
                Document document = ed.getDocument();
                int line = document.getLineNumber(element.getTextRange().getStartOffset());

                if (project.getUserData(ANNOTATED) == null)
                    project.putUserData(ANNOTATED, new ArrayList<Integer>());

                if (!annotated.contains(line)) {
             /*  Analyzer an = new Analyzer(element);
                if (element instanceof PsiLiteralExpression) {
                    an.analyze();
                    PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                    String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;

                    if (value != null && value.startsWith("simple" + ":")) {
//                project.putUserData();*/
                    String key = "test";
                    TextRange range = new TextRange(document.getLineStartOffset(line), document.getLineEndOffset(line));
                    System.err.println(range);
                    holder.createWarningAnnotation(range, "Uresollve 1").
                            registerFix(new QuickFix(key));
                    holder.createWarningAnnotation(range, "Uresollve 2").
                            registerFix(new QuickFix(key + "!"));

                    annotated.add(line);
//                    }
//                }
                    System.err.println(project.getUserData(ANNOTATED).size() + "!");
                }
            }
        });
    }
}