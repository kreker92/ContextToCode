package tmt;

import com.intellij.openapi.editor.CaretModel;
import tmt.analyze.filter.Filter;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GenerateInspection extends LocalInspectionTool {

    @Override
    public ProblemDescriptor[] checkFile(@NotNull final PsiFile psiFile,
                                         @NotNull final InspectionManager manager,
                                         final boolean isOnTheFly) {

        Editor ed = FileEditorManager.getInstance(psiFile.getProject()).getSelectedTextEditor();

        if (ed == null) {
            System.out.println("No editor.");
            return new ProblemDescriptor[0];
        }

        int current_scope = ed.getCaretModel().getPrimaryCaret().getLogicalPosition().line;
        String current_text;
        if (ed.getDocument().getLineEndOffset(current_scope) != ed.getDocument().getTextLength()) {
            current_text = psiFile.getText().substring(ed.getDocument().getLineStartOffset(current_scope), ed.getDocument().getLineEndOffset(current_scope));
        } else {
            return new ProblemDescriptor[0];
        }

        ArrayList<SuggestGenerate> suggests = new ArrayList<>();

        if (Filter.pass(psiFile, current_scope, ed)) {
            Analyzer an = new Analyzer(psiFile, current_scope, ed);
            an.analyze();
            suggests = Filter.removeDoubles(an.getSuggests(), current_text);
        }

        return suggests.toArray(new ProblemDescriptor[0]);
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
        return "tmt.GenerateInspection";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Instance field count";
    }

}
