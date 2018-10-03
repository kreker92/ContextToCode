import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
//import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.openapi.command.CommandProcessor;


import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileType;

import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Sets;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

public class HelloAction extends AnAction {
    private List<PsiFile> myPsiFiles = new ArrayList<>();
    private PsiFileFactory factory;
    private Project project;
    private Editor ed;

    public HelloAction() {
        super("Hello");
    }

    public void actionPerformed(AnActionEvent event) {
        project = event.getProject();
        factory = PsiFileFactory.getInstance(project);
        System.err.print("!1");

        ed = event.getData(PlatformDataKeys.EDITOR);
        PsiFile fi = event.getData(LangDataKeys.PSI_FILE);
        Actions act = new Actions(ed);

        ContextHelperPanel helperComponent = new ContextHelperPanel(project, act);

        try {
         // case 1:
            if (true) {
                setUp();
            } else {
                // case 2:

                ArrayList<InnerContext> output_elements = new ArrayList<>();

                ed.getCaretModel().moveToLogicalPosition(new LogicalPosition(ed.getCaretModel().getLogicalPosition().line, 400));

                parseFile(fi, output_elements, fi.getText(), ed.getSelectionModel().getSelectionEnd());

                String request = new Gson().toJson(output_elements);
                System.err.println(request);
//            System.err.print(Eval.sendGet("телефон"))

//            act.insert("\n\n ");

//            TimeUnit.SECONDS.sleep(1);

                helperComponent.setQueryingStatus(act.send(request));

//            CommandProcessor.getInstance().executeCommand(project, () -> getApplication().runWriteAction(() -> {
//                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
//                if (editor != null) {
//                    int offset = editor.getCaretModel().getOffset();
//                    Document document = editor.getDocument();
//                    String key = isXmlFile ?
//                            "@" + element.getTag() + "/" + element.getName()
//                            : "R." + element.getTag() + "." + element.getName();
//                    if (key != null) {
//                        document.insertString(offset, key);
//                        editor.getCaretModel().moveToOffset(offset + key.length());
//                    }
//                }
//            }), "InsertResultToEditor", "", UndoConfirmationPolicy.DO_NOT_REQUEST_CONFIRMATION);


//            helperComponent.process("{psiElement.text} {elementLanguage.displayName.toLowerCase()}");
            }

        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "Greeting", Messages.getInformationIcon());
            e.printStackTrace();
        }
    }

    private void setUp() throws Exception {
        File myTargetDir = new File("C:\\Users\\user\\Documents\\backup\\data\\raw\\");
        System.err.println(myTargetDir);
        if (!myTargetDir.isDirectory()) throw new Exception(myTargetDir + " is not a directory");

        File checkDir = new File("C:\\Users\\user\\Documents\\backup\\data\\parsed\\");
        System.err.println(checkDir);
        if (!myTargetDir.isDirectory()) throw new Exception(myTargetDir + " is not a directory");

        ArrayList<String> checkFiles = new ArrayList<>();
        for ( File f : checkDir.listFiles()) {
            checkFiles.add(f.getName());
        }

        File[] myFiles = myTargetDir.listFiles();
        for (File file : myFiles) {
            if (!checkFiles.contains(file.getName()+".json")) {
                String text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                ArrayList<InnerContext> output_elements = new ArrayList<>();

                final String s = "*.java";
                FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName("*.java"); // RegExp plugin is not installed

                final PsiFile psf = PsiFileFactory.getInstance(project).createFileFromText(file.getName(), fileType, text, -1, true);

                parseFile(psf, output_elements, text, null);

                try (Writer writer = new FileWriter("C:\\Users\\user\\Documents\\backup\\data\\parsed\\" + file.getName() + ".json")) {
                    Gson gson = new GsonBuilder().create();
                    gson.toJson(output_elements, writer);
                }
            }
        }
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
            traversePsiElement(psiFile, psiElements, selectionStartOffset, selectionEndOffset);
            return new SelectionContext(psiElements, selectionStartOffset, selectionEndOffset, line, text);
        }

        private void traversePsiElement(PsiElement element, List<PsiElement> selectedElements, int selectionStartOffset, int selectionEndOffset) {
            int elementStart = element.getTextOffset();
            int elementEnd = elementStart + element.getTextLength();
            if (selectionStartOffset <= elementStart && elementEnd <= selectionEndOffset) {
                selectedElements.add(element);
            }
            for (PsiElement childElement : element.getChildren()) {
                traversePsiElement(childElement, selectedElements, selectionStartOffset, selectionEndOffset);
            }
        }
    }

    class SelectionContextQueryBuilder {
        private final int MAX_WORDS_FOR_QUERY = 5;

        private final Set<IElementType> meaninglessForContextTokenTypes =
                Sets.newHashSet(
                        TokenType.WHITE_SPACE,

                        JavaTokenType.C_STYLE_COMMENT,
                        JavaTokenType.END_OF_LINE_COMMENT,

                        JavaTokenType.LPARENTH,
                        JavaTokenType.RPARENTH,
                        JavaTokenType.LBRACE,
                        JavaTokenType.RBRACE,
                        JavaTokenType.LBRACKET,
                        JavaTokenType.RBRACKET,
                        JavaTokenType.SEMICOLON,
                        JavaTokenType.COMMA,
                        JavaTokenType.DOT,
                        JavaTokenType.ELLIPSIS,
                        JavaTokenType.AT,

                        JavaTokenType.EQ,
                        JavaTokenType.GT,
                        JavaTokenType.LT,
                        JavaTokenType.EXCL,
                        JavaTokenType.TILDE,
                        JavaTokenType.QUEST,
                        JavaTokenType.COLON,
                        JavaTokenType.PLUS,
                        JavaTokenType.MINUS,
                        JavaTokenType.ASTERISK,
                        JavaTokenType.DIV,
                        JavaTokenType.AND,
                        JavaTokenType.OR,
                        JavaTokenType.XOR,
                        JavaTokenType.PERC,

                        JavaTokenType.EQEQ,
                        JavaTokenType.LE,
                        JavaTokenType.GE,
                        JavaTokenType.NE,
                        JavaTokenType.ANDAND,
                        JavaTokenType.OROR,
                        JavaTokenType.PLUSPLUS,
                        JavaTokenType.MINUSMINUS,
                        JavaTokenType.LTLT,
                        JavaTokenType.GTGT,
                        JavaTokenType.GTGTGT,
                        JavaTokenType.PLUSEQ,
                        JavaTokenType.MINUSEQ,
                        JavaTokenType.ASTERISKEQ,
                        JavaTokenType.DIVEQ,
                        JavaTokenType.ANDEQ,
                        JavaTokenType.OREQ,
                        JavaTokenType.XOREQ,
                        JavaTokenType.PERCEQ,
                        JavaTokenType.LTLTEQ,
                        JavaTokenType.GTGTEQ,
                        JavaTokenType.GTGTGTEQ,

                        JavaTokenType.DOUBLE_COLON,
                        JavaTokenType.ARROW);

        private final SelectionContext context;

        public SelectionContextQueryBuilder(SelectionContext context) {
            this.context = context;
        }

        public void buildQuery() {
            List<PsiElement> psiElements = context.getPsiElements();

            for (PsiElement psiElement : psiElements) {
                if (meaninglessForContextTokenTypes.contains(psiElement.getNode().getElementType())) {
                    continue;
                }
                if (psiElement.getChildren().length > 0) {
                    // This is an abstract node, while for bag-of-words model we are only concerned with
                    // concrete nodes.
                    continue;
                }
                if (psiElement.getTextLength() == 0) {
                    // The node's text representation is empty: won't help us with forming the query. E.g.
                    // REFERENCE_PARAMETER_LIST is present in PSI trie but its text may be empty.
                    continue;
                }

                context.ic.elements.add(new ElementInfo(psiElement, context.ic.line_num));
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