package tmt;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.text.BlockSupport;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import tmt.util.Actions;

public class QuickFix extends LocalQuickFixOnPsiElement {
    private final Actions act;
    private String code_line;
    private int line_num;

    public QuickFix(String code_line_, Project project, int ln, PsiElement el) {
        super(el);
        this.code_line = code_line_;
        System.err.println(code_line);
        act = new Actions(project);
        line_num = ln;
    }

    @NotNull
    @Override
    public String getText() {
        return "Generate next line: " + code_line;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Simple properties";
    }

    @Override
    public void invoke(@NotNull Project project,
                       @NotNull PsiFile file,
                       @NotNull PsiElement startElement,
                       @NotNull PsiElement endElement) {
    /*    assert startElement == endElement;

        BlockSupport blockSupport = BlockSupport.getInstance(project);
        TextRange textRange = startElement.getTextRange();
        int startOffset = textRange.getStartOffset();
        int endOffset = textRange.getEndOffset();
        blockSupport.reparseRange(file, startOffset, endOffset, " ::");*/
        act.insert(code_line, line_num);
    }
}

/*public class AsciiDocConvertMarkdownHeading extends LocalQuickFixBase {
    public static final String NAME = "Convert to AsciiDoc Heading";

    public AsciiDocConvertMarkdownHeading() {
        super(NAME);
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement element = descriptor.getPsiElement();
        StringBuilder text = new StringBuilder(element.getText());
        for (int i = 0;i < text.length();++i) {
            if (text.charAt(i) == '#') {
                text.setCharAt(i, '=');
            }
            else {
                break;
            }
        }
        element.replace(createHeading(project, text.toString()));
    }

    @NotNull
    public static PsiElement createHeading(@NotNull Project project, @NotNull String text) {
        AsciiDocFile file = createFileFromText(project, text);
        return PsiTreeUtil.findChildOfType(file, AsciiDocSection.class).getFirstChild();
    }

    @NotNull
    private static AsciiDocFile createFileFromText(@NotNull Project project, @NotNull String text) {
        return (AsciiDocFile)PsiFileFactory.getInstance(project).createFileFromText("a.adoc", AsciiDocLanguage.INSTANCE, text);
    }
}*/
