package tmt;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.QuickFix;
import com.intellij.lang.annotation.ProblemGroup;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuggestGenerate implements ProblemDescriptor {
    private int curr_line;
    private String annotation;
    private String fix;

    public SuggestGenerate(int selected_line, String documentation, String prediction) {
        curr_line = selected_line;
        annotation = documentation;
        fix = prediction;
    }

    @Override
    public PsiElement getPsiElement() {
        return null;
    }

    @Override
    public PsiElement getStartElement() {
        return null;
    }

    @Override
    public PsiElement getEndElement() {
        return null;
    }

    @Override
    public TextRange getTextRangeInElement() {
        return null;
    }

    @Override
    public int getLineNumber() {
        return curr_line;
    }

    @NotNull
    @Override
    public ProblemHighlightType getHighlightType() {
        return ProblemHighlightType.WARNING;
    }

    @Override
    public boolean isAfterEndOfLine() {
        return false;
    }

    @Override
    public void setTextAttributes(TextAttributesKey key) {

    }

    @Nullable
    @Override
    public ProblemGroup getProblemGroup() {
        return null;
    }

    @Override
    public void setProblemGroup(@Nullable ProblemGroup problemGroup) {

    }

    @Override
    public boolean showTooltip() {
        return false;
    }

    @NotNull
    @Override
    public String getDescriptionTemplate() {
        return "";
    }

    @Nullable
    @Override
    public QuickFix[] getFixes() {
        return new QuickFix[0];
    }

    public String getAnnotationMessage() {
        return annotation;
    }

    public String getFixMessage() {
        return fix;
    }
}
