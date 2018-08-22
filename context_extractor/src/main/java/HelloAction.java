import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.codeInsight.intention.IntentionAction;
//import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileType;

import java.util.*;
import java.util.stream.Collectors;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Sets;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

public class HelloAction extends AnAction {
    private List<PsiFile> myPsiFiles = new ArrayList<>();
    private PsiFileFactory factory;
    private Project project;

    public HelloAction() {
        super("Hello");
    }

    public void actionPerformed(AnActionEvent event) {
        project = event.getProject();
        try {
            factory = PsiFileFactory.getInstance(project);
            setUp();
        } catch (Exception e) {
            Messages.showMessageDialog(project, e.getMessage(), "Greeting", Messages.getInformationIcon());
            e.printStackTrace();
        }
    }

    private void setUp() throws Exception {
        File myTargetDir = new File("C:\\Users\\user\\Documents\\backup\\iris\\");
        System.err.println(myTargetDir);
        if (!myTargetDir.isDirectory()) throw new Exception(myTargetDir + " is not a directory");

        File[] myFiles = myTargetDir.listFiles();
        for (File file : myFiles) {
            System.err.println(file.getAbsolutePath());
            String text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

            final String s = "*.java";
            FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName("*.java"); // RegExp plugin is not installed

            final PsiFile psf = PsiFileFactory.getInstance(project).createFileFromText("text.java", fileType, text, -1, true);

            SelectionContextExtractor contextExtractor = new SelectionContextExtractor(psf);
            SelectionContext context = contextExtractor.extractContext();
            SelectionContextQueryBuilder queryBuilder = new SelectionContextQueryBuilder(context);
            queryBuilder.buildQuery();
        }
    }
}

    class SelectionContext {

        private final List<PsiElement> psiElements;

        SelectionContext(List<PsiElement> psiElements) {
            this.psiElements = psiElements;

        }

        List<PsiElement> getPsiElements() {
            return psiElements;
        }
    }

class SelectionContextExtractor {

        private final PsiFile psiFile;

        private final int selectionStartOffset;

        private final int selectionEndOffset;

        public SelectionContextExtractor(PsiFile psiFile) {
//            this.selectionStartOffset = editor.getSelectionModel().getSelectionStart();
//            this.selectionEndOffset = editor.getSelectionModel().getSelectionEnd();
            this.selectionStartOffset = 881;
            this.selectionEndOffset = 984;
            this.psiFile = psiFile;
        }

        public SelectionContext extractContext() {
            List<PsiElement> psiElements = new ArrayList<>();
            traversePsiElement(psiFile, psiElements);
            return new SelectionContext(psiElements);
        }

        private void traversePsiElement(PsiElement element, List<PsiElement> selectedElements) {
            int elementStart = element.getTextOffset();
            int elementEnd = elementStart + element.getTextLength();
            if (selectionStartOffset <= elementStart && elementEnd <= selectionEndOffset) {
                selectedElements.add(element);
            }
            for (PsiElement childElement : element.getChildren()) {
                traversePsiElement(childElement, selectedElements);
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

            Map<String, Integer> wordCountMap = new HashMap<>();
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
                System.err.println(psiElement.getContext().getText()+" ^ "+psiElement.getText());
                String elementText = psiElement.getText();
                int counter = wordCountMap.getOrDefault(elementText, 0);
                wordCountMap.put(elementText, counter + 1);
            }
            System.err.println(wordCountMap);
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

        private class WordInfo {
            final Comparator<? super WordInfo> COMPARATOR_BY_TIMES_ENCOUNTERED =
                    Comparator.comparing(WordInfo::getTimesEncountered).reversed();

            private String text;

            private int timesEncountered;

            WordInfo(String text, int timesEncountered) {
                this.text = text;
                this.timesEncountered = timesEncountered;
            }

            String getText() {
                return text;
            }

            int getTimesEncountered() {
                return timesEncountered;
            }
        }
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