package tmt;

import com.intellij.codeInspection.HintAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tmt.analyze.ContextHelperPanel;
import tmt.analyze.ElementInfo;
import tmt.analyze.InnerContext;
import tmt.analyze.SyntaxUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tmt.util.Actions;

class Analyzer {
    private ArrayList<SuggestGenerate> suggests = new ArrayList<>();
    private Project project;
    private int end;
    private PsiFile fi;
    private Actions act;
    private int scope;
    private PsiElement element;
    private Document document;

    Analyzer(PsiFile file_, int sc_, Document document_, PsiElement element_) {
        project = file_.getProject();
        fi = file_;
        act = new Actions(project);
        scope = sc_;
        element = element_;
        end = document_.getLineEndOffset(sc_);
        document = document_;
    }

    void analyze() {
//        ContextHelperPanel helperComponent = ContextHelperPanel.getPanel(project);

        try {
            ArrayList<InnerContext> output_elements = new ArrayList<>();

            parseFile(fi, output_elements, fi.getText(), end);

            String request = new Gson().toJson(output_elements);
            System.err.println("!"+request);
            //System.err.println("Request sent.");

            ArrayList<LinkedTreeMap<String, String>> res = new ArrayList<>();
            try {
                String response = act.send(request);
                System.err.println("Response got." + response);
                res = new Gson().fromJson(response, ArrayList.class);
             } catch (Exception e) {
                System.err.println("Classifier returned 500 code");
            }
//            helperComponent.setQueryingStatus(res);

            System.err.println("1;"+res);
            for (LinkedTreeMap<String, String> sugg : res) {
//                QuickFix[] qf = {new tmt.QuickFix(sugg.get("prediction"), project, scope, element)};
//                suggests.add(new SuggestGenerate(element, element, sugg.get("documentation"), qf,
//                        ProblemHighlightType.INFORMATION,false, new TextRange(document.getLineStartOffset(scope), document.getLineEndOffset(scope)),
//                true,null,true));
                suggests.add(new SuggestGenerate(scope, sugg.get("documentation"), sugg.get("prediction"), project, document, element));
            }
            System.err.println("2;"+suggests);
        } catch (Exception e) {
         //   Messages.showMessageDialog(project, e.getMessage(), "Error Occurred", Messages.getInformationIcon());
            e.printStackTrace();
        }
    }

    ArrayList<SuggestGenerate> getSuggests() {
        return suggests;
    }

    private void parseFile(PsiFile psf, ArrayList<InnerContext> output_elements, @NotNull String text, Integer end) {
        SelectionContextExtractor contextExtractor = new SelectionContextExtractor(psf);
        String word = "\n";
        ArrayList<Integer> ends = new ArrayList<>();

        for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
            if (end == null || i <= end)
                ends.add(i);
        }

        int previous = 0;
        int line = 0;

        for (int e : ends) {
            SelectionContext context = contextExtractor.extractContext(previous, e + 1, line, text.substring(previous, e + 1));
            SelectionContextQueryBuilder queryBuilder = new SelectionContextQueryBuilder(context);
            queryBuilder.buildQuery();
            previous = e + 1;
            line++;
            output_elements.add(context.ic);
        }
    }
}

class SelectionContext {

    private final List<PsiElement> psiElements;
    InnerContext ic = new InnerContext();

    SelectionContext(List<PsiElement> psiElements, int selectionStartOffset, int selectionEndOffset, int line, String text) {
        this.psiElements = psiElements;
        ic.line_num = line;
        ic.start = selectionStartOffset;
        ic.end = selectionEndOffset;
        ic.line_text = text;
    }

    List<PsiElement> getPsiElements() {
        return psiElements;
    }
}

class SelectionContextExtractor {

    private final PsiFile psiFile;

    SelectionContextExtractor(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    SelectionContext extractContext(int selectionStartOffset, int selectionEndOffset, int line, String text) {
        List<PsiElement> psiElements = new ArrayList<>();
        SyntaxUtils.traversePsiElement(psiFile, psiElements, selectionStartOffset, selectionEndOffset);
        return new SelectionContext(psiElements, selectionStartOffset, selectionEndOffset, line, text);
    }
}

class SelectionContextQueryBuilder {
    private final SelectionContext context;

    SelectionContextQueryBuilder(SelectionContext context) {
        this.context = context;
    }

    void buildQuery() {
        List<PsiElement> psiElements = context.getPsiElements();
        for (PsiElement psiElement : psiElements) {
            ElementInfo el = new ElementInfo(psiElement, context.ic.line_num);

            if (SyntaxUtils.meaninglessForContextTokenTypes.contains(psiElement.getNode().getElementType())) {
                continue;
            }

            context.ic.elements.add(el);
        }
    }
}
