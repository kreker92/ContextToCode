package analyzer;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceCodeFragment;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.impl.source.PsiTypeElementImpl;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;

public class ElementInfo {
  public String node;
  public String type;
  public String text;
  public String parent;
  public String ast_type;
  //public PsiReference  ref;
  //public PsiReference[]  refs;
  //public String original;
  //public String child;
  int line;

  public ElementInfo(PsiElement psiElement, int line_) {
      text = psiElement.getText();
      node = psiElement.getNode().toString().split(":")[0];
      type = psiElement.getNode().getElementType().toString();
      line = line_;
      if (psiElement.getParent() != null) {
          parent = psiElement.getParent().toString().split(":")[0];
        //  child = psiElement.getChildren().toString();
        //  original = psiElement.getOriginalElement().getText();
      }

//                System.err.println(" ____________ "+psiElement.getParent() + " ----- " + this.text
//                  + " * "
//                  + this.node+" * "
//                  + this.line);
      if (psiElement instanceof PsiReferenceExpression) {
          if(((PsiReferenceExpression) psiElement).getType() != null)
              ast_type = ((PsiReferenceExpression) psiElement).getType().toString();
          else if (!psiElement.getParent().toString().contains("PsiMethodCallExpression") && psiElement.getReference() != null)
              ast_type = psiElement.getReference().toString();
      } else if (psiElement instanceof PsiTypeElementImpl) {
          ast_type = ((PsiTypeElementImpl) psiElement).getType().toString();
      } else if (node.equals("PsiIdentifier") && psiElement.getContext().getParent().getOriginalElement().toString().contains("PsiMethodCallExpression")) {
          ast_type = psiElement.toString();
      } else if (psiElement instanceof PsiLiteralExpressionImpl) {
          ast_type = ((PsiLiteralExpressionImpl) psiElement).getType().toString();
      }
    //  ref = psiElement.getReference();
    //  refs = psiElement.getReferences();
  }

  public void print (PsiElement psiElement, String text_) {
//          System.err.println(text_+" ____________ "+psiElement.getParent() + " ----- " + this.text
//                  + " * "
//                  + this.node+" * "
//                  + this.line);
//          System.err.println(psiElement.getReference()+ "*" + psiElement.getClass() + "*" + psiElement.getContext());
      String type = null;
      if (type != null)
        System.err.println(text_+" * "+this.text + " * "+type);
  }

  public String toString() {
      return ast_type;
  }
}
