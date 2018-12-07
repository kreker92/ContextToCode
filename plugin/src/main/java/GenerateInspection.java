import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GenerateInspection extends LocalInspectionTool {

    @Override
    public ProblemDescriptor[] checkFile(@NotNull final PsiFile psiFile,
                                         @NotNull final InspectionManager manager,
                                         final boolean isOnTheFly) {
        Analyzer an = new Analyzer(psiFile);

        an.analyze(manager);
        ArrayList<SuggestGenerate> suggests = an.getSuggests();
        return suggests.toArray(new ProblemDescriptor[suggests.size()]);
    }

    private boolean isStatic(PsiField field) {
        return field.getModifierList() != null && field.getModifierList().hasExplicitModifier("static");
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "Clean Code";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "GenerateInspection";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Instance field count";
    }
    
}
