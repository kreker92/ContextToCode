package tmt;

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
//import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;


import java.util.ArrayList;
import java.util.List;

import tmt.util.Actions;

public class Analyzer {
    private ArrayList<SuggestGenerate> suggests = new ArrayList<>();
    private Project project;
    private Editor ed;
    private PsiFile fi;
    private Actions act;
    private int scope;

    public Analyzer(PsiFile file_, int sc_, Editor ed) {
        project = file_.getProject();
        fi = file_;
        act = new Actions(project);
        scope = sc_;
        this.ed = FileEditorManager.getInstance(fi.getProject()).getSelectedTextEditor();
    }

    public void analyze(InspectionManager manager) {
        ContextHelperPanel helperComponent = new ContextHelperPanel(project, act);

        try {
            ArrayList<InnerContext> output_elements = new ArrayList<>();

            parseFile(fi, output_elements, fi.getText(), ed.getSelectionModel().getSelectionEnd());

            String request = new Gson().toJson(output_elements);
            System.err.println(request);

            ArrayList<LinkedTreeMap<String, String>> res = new Gson().fromJson(act.send(request), ArrayList.class);
            helperComponent.setQueryingStatus(res);

            for ( LinkedTreeMap<String, String> sugg : res) {
                suggests.add(new SuggestGenerate(scope, sugg.get("documentation"), sugg.get("prediction")));
            }

        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "Greeting", Messages.getInformationIcon());
            e.printStackTrace();
        }
    }

    public ArrayList<SuggestGenerate> getSuggests() {
        return suggests;
    }


    private void parseFile(PsiFile psf, ArrayList<InnerContext> output_elements, String text, Integer end) {
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
            SelectionContext context = contextExtractor.extractContext(previous, e+1, line, text.substring(previous, e+1));
            SelectionContextQueryBuilder queryBuilder = new SelectionContextQueryBuilder(context);
            queryBuilder.buildQuery();
            previous = e+1;
            line ++;
            output_elements.add(context.ic);
        }
    }
}

class SelectionContext {

    private final List<PsiElement> psiElements;
    public InnerContext ic = new InnerContext();

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

    public SelectionContextExtractor(PsiFile psiFile) {
//            this.selectionStartOffset = editor.getSelectionModel().getSelectionStart();
//            this.selectionEndOffset = editor.getSelectionModel().getSelectionEnd();
        this.psiFile = psiFile;
    }

    public SelectionContext extractContext(int selectionStartOffset, int selectionEndOffset, int line, String text) {
        List<PsiElement> psiElements = new ArrayList<>();
        SyntaxUtils.traversePsiElement(psiFile, psiElements, selectionStartOffset, selectionEndOffset);
        return new SelectionContext(psiElements, selectionStartOffset, selectionEndOffset, line, text);
    }
}

class SelectionContextQueryBuilder {
    private final int MAX_WORDS_FOR_QUERY = 5;

    private final SelectionContext context;

    public SelectionContextQueryBuilder(SelectionContext context) {
        this.context = context;
    }

    public void buildQuery() {
        List<PsiElement> psiElements = context.getPsiElements();
        ArrayList<String> bad_types = new ArrayList<String>();

        bad_types.add("PsiJavaToken");
        bad_types.add("PsiDocToken");
        bad_types.add("PsiElement(BAD_CHARACTER)");
        bad_types.add("PsiModifierList");
        bad_types.add("PsiField");
        bad_types.add("PsiTypeElement");
//            bad_types.add("PsiReferenceExpression");
        bad_types.add("PsiParameter");

        for (PsiElement psiElement : psiElements) {
            ElementInfo el = new ElementInfo(psiElement, context.ic.line_num);

            if (SyntaxUtils.meaninglessForContextTokenTypes.contains(psiElement.getNode().getElementType())) {
                continue;
            }
/*                if (psiElement.getChildren().length > 0) {
                    // This is an abstract node, while for bag-of-words model we are only concerned with
                    // concrete nodes.
                    continue;
                }
                if (psiElement.getTextLength() == 0) {
                    // The node's text representation is empty: won't help us with forming the query. E.g.
                    // REFERENCE_PARAMETER_LIST is present in PSI trie but its text may be empty.
                    continue;
                } */

            context.ic.elements.add(el);
//                if (!bad_types.contains(el.parent)) {
//                    context.ic.clean_line_text += psiElement.getText().toLowerCase() + " ";
//                }
        }

//            List<WordInfo> words =
//                    wordCountMap.entrySet()
//                            .stream()
//                            .map(entry -> new WordInfo(entry.getKey(), entry.getValue()))
//                            .sorted(WordInfo.COMPARATOR_BY_TIMES_ENCOUNTERED)
//                            .collect(Collectors.toList());
//             return words.stream()
//                    .limit(MAX_WORDS_FOR_QUERY)
//                    .map(WordInfo::getText)
//                    .collect(Collectors.joining(" "));
    }

//        private class WordInfo {
//            final Comparator<? super WordInfo> COMPARATOR_BY_TIMES_ENCOUNTERED =
//                    Comparator.comparing(WordInfo::getTimesEncountered).reversed();
//
//            private String text;
//
//            private int timesEncountered;
//
//            WordInfo(String text, int timesEncountered) {
//                this.text = text;
//                this.timesEncountered = timesEncountered;
//            }
//
//            String getText() {
//                return text;
//            }
//
//            int getTimesEncountered() {
//                return timesEncountered;
//            }
//        }

}

//class NullShaderFileType extends LanguageFileType {
//
//    public final NullShaderFileType INSTANCE = new NullShaderFileType();
//
//    protected NullShaderFileType() {
//        super(NullShaderLanguage.INSTANCE);
//    }
//
//    @NotNull
//    @Override
//    public String getName() {
//        return "Null Shader";
//    }
//
//    @NotNull
//    @Override
//    public String getDescription() {
//        return "Null Engine shader";
//    }
//
//    @NotNull
//    @Override
//    public String getDefaultExtension() {
//        return "ns";
//    }
//
//    @Nullable
//    @Override
//    public Icon getIcon() {
//        return Icons.NULL_SHADER_ICON;
//    }
//}
//
//class Icons {
//    public final Icon NULL_SHADER_ICON = IconLoader.getIcon("/icons/cube.png");
//}
//
//class NullShaderLanguage extends Language {
//
//    public final NullShaderLanguage INSTANCE = new NullShaderLanguage();
//
//    protected NullShaderLanguage() {
//        super("NullShader");
//    }
//}