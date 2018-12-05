import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InstanceFieldCountInspection extends LocalInspectionTool {

    public static final int MAX_FIELD_COUNT = 7;

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        Map<PsiClass, Set<PsiField>> fieldsOfClass = new HashMap<>();
        System.err.println("buildVisitor");

        return new JavaElementVisitor() {
            @Override
            public void visitField(PsiField field) {

               /* if (isStatic(field)) {
                    return;
                }

                if (field.getContainingClass().isInterface()) {
                    return;
                }

                Set<PsiField> fields = fieldsOfClass.computeIfAbsent(
                        field.getContainingClass(),
                        x -> new HashSet<>());
                fields.add(field);*/

                if (field.getText().contains("a")) {
                    holder.registerProblem(field.getOriginalElement(), "Too many instance fields in one class.");
                }
            }
        };
    }

    @Override
    public ProblemDescriptor[] checkFile(@NotNull final PsiFile psiFile,
                                         @NotNull final InspectionManager manager,
                                         final boolean isOnTheFly) {
        System.err.println("checkFile");
        return null;
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
        return "InstanceFieldCountInspection";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Instance field count";
    }
    
}
