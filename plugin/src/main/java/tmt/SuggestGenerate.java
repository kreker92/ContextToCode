package tmt;

import com.intellij.codeInspection.*;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.ProblemGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tmt.attributes.TextAttributes;

import java.lang.reflect.Array;

public class SuggestGenerate implements ProblemDescriptor {
    private final int curr_line;
//    private final TextRange range;
    private String fix;
    private String annotation;
    private Project project;
    private PsiElement element;

//    public SuggestGenerate(@NotNull PsiElement startElement,
//                                 @NotNull PsiElement endElement,
//                                 @NotNull String descriptionTemplate,
//                                 LocalQuickFix[] fixes,
//                                 @NotNull ProblemHighlightType highlightType,
//                                 boolean isAfterEndOfLine,
//                                 @Nullable TextRange rangeInElement,
//                                 final boolean tooltip,
//                                 @Nullable HintAction hintAction,
//                                 boolean onTheFly,
//                           String prediction) {
//        super(startElement, endElement, descriptionTemplate, fixes, highlightType, isAfterEndOfLine, rangeInElement, tooltip, onTheFly);
//       // myHintAction = hintAction;
//        fix = prediction;
//    }
//
//    public String getFixMessage() {
//        return fix;
//    }

    public SuggestGenerate(int selected_line, String documentation, String prediction, Project project_, Document document, PsiElement element_) {
        curr_line = selected_line;
        annotation = documentation;
        fix = prediction;
        project = project_;
        element = element_;

//        range = new TextRange(0, document.getLineEndOffset(curr_line)-document.getLineStartOffset(curr_line));

        this.setTextAttributes(TextAttributes.CRITICAL);
    }

    @Override
    public PsiElement getPsiElement() {
        return element;
    }

    @Override
    public PsiElement getStartElement() {
        return element;
    }

    @Override
    public PsiElement getEndElement() {
        return element;
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
        return ProblemHighlightType.INFORMATION;
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
        return true;
    }

    @NotNull
    @Override
    public String getDescriptionTemplate() {
        return "";
    }

    @Nullable
    @Override
    public LocalQuickFix[] getFixes() {
        LocalQuickFix[] qf = {new QuickFix(fix, project, curr_line, element)};
        return qf;
    }

    public String toString() {
        return "annotation: "+annotation+", curr_line: "+curr_line+", fix: "+fix+", element: "+element;
    }

    public String getFixMessage() {
        return fix;
    }
}
