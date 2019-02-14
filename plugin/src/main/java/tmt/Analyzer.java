package tmt;

import com.intellij.openapi.editor.Document;
import org.jetbrains.annotations.NotNull;
import tmt.analyze.ElementInfo;
import tmt.analyze.InnerContext;
import tmt.analyze.SyntaxUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import tmt.conf.Conf;
import tmt.conf.Utils;
import tmt.dsl.GServer;
import tmt.dsl.cache.Cache;
import tmt.dsl.formats.context.in.InnerClass;
import tmt.util.Actions;
import tmt.util.Util;

class Analyzer {
    private final Cache cache;
    private ArrayList<SuggestGenerate> suggests = new ArrayList<>();
    private Project project;
    private PsiFile fi;
    private Document document;

    Analyzer(PsiFile file_, Document document_) {
        this.project = file_.getProject();
        fi = file_;
        document = document_;
        cache = new Cache();
    }

    void analyze(int scope, PsiElement element) {
//        ContextHelperPanel helperComponent = ContextHelperPanel.getPanel(project);
        int end = document.getLineEndOffset(scope);

        try {
            ArrayList<InnerContext> output_elements = new ArrayList<>();

            parseFile(fi, output_elements, fi.getText(), end);

            String request = new Gson().toJson(output_elements);

            ArrayList<HashMap<String, String>> res = new ArrayList<>();
            try {
                res = getResponse(request);
          //      System.err.println("Response got." + res);
             } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Classifier returned 500 code");
            }
            suggests.clear();
            for (HashMap<String, String> sugg : res) {
           /* ArrayList<LinkedTreeMap<String, String>> res = new ArrayList<>();
            try {
                String response = act.send(request);
//                System.err.println("Response got." + response);
                res = new Gson().fromJson(response, ArrayList.class);
            } catch (Exception e) {
                System.err.println("Classifier returned 500 code");
            }
            System.err.println("!"+res);
            suggests.clear();
            for (LinkedTreeMap<String, String> sugg : res) {*/
//                QuickFix[] qf = {new tmt.QuickFix(sugg.get("prediction"), project, scope, element)};
//                suggests.add(new SuggestGenerate(element, element, sugg.get("documentation"), qf,
//                        ProblemHighlightType.INFORMATION,false, new TextRange(document.getLineStartOffset(scope), document.getLineEndOffset(scope)),
//                true,null,true));
                suggests.add(new SuggestGenerate(scope, sugg.get("documentation"), sugg.get("prediction"), project, document, element));
            }
        } catch (Exception e) {
         //   Messages.showMessageDialog(project, e.getMessage(), "Error Occurred", Messages.getInformationIcon());
            e.printStackTrace();
        }
    }

    private ArrayList<HashMap<String, String>> getResponse(String request) throws Exception {
        ArrayList<InnerClass> arrayList = new ArrayList<InnerClass>(Arrays.asList(new Gson().fromJson(request,  InnerClass[].class)));
        arrayList.add(new Gson().fromJson(Conf.stab,  InnerClass.class));
        return GServer.router(GServer.INFERENCE, arrayList.toArray(new InnerClass[arrayList.size()]));
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
