package tmt.analyze.filter;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import tmt.SuggestGenerate;
import tmt.analyze.SyntaxUtils;
;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.openapi.editor.Document;

import java.util.ArrayList;
import java.util.List;

public class Filter {
    public static boolean pass(PsiFile psiFile, int current_scope, Document document) {
        List<PsiElement> psiElements = new ArrayList<>();
        SyntaxUtils.traversePsiElement(psiFile, psiElements,
                document.getLineStartOffset(current_scope),
                document.getLineEndOffset(current_scope));

        return check(psiElements);
    }

    private static boolean check(List<PsiElement> psiElements) {
        ArrayList<PsiElement> els = new ArrayList<>();

        for (PsiElement psiElement : psiElements) {
            if (SyntaxUtils.meaninglessForContextTokenTypes.contains(psiElement.getNode().getElementType()))
                continue;
            els.add(psiElement);
        }

        if (els.isEmpty())
            return false;

        return true;
    }

    public static ArrayList<SuggestGenerate> removeDoubles(ArrayList<SuggestGenerate> suggests, String current_text, ProblemsHolder holder, Project project, PsiElement element) {
        ArrayList<SuggestGenerate> res = new ArrayList<>();

        for ( SuggestGenerate s : suggests)
            if (!current_text.contains(s.getFixMessage())) {
                res.add(s);
            }

        return res;
    }
}
