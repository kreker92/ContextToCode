import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import util.Actions;

class QuickFix extends BaseIntentionAction {
    private final Actions act;
    private String code_line;
    private int line_num;

    QuickFix(String code_line_, Project project, int ln) {
        this.code_line = code_line_;
        System.err.println(code_line);
        act = new Actions(project);
        line_num = ln;
    }

    @NotNull
    @Override
    public String getText() {
        return "Generate next line: "+code_line;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Simple properties";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, PsiFile file) throws
            IncorrectOperationException {
        act.insert(code_line, line_num);
    }

}
