import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public class ElementInfo {
  public String node;
  public String type;
  public String text;
  public String parent;
  //public PsiReference  ref;
  //public PsiReference[]  refs;
  public String original;
  public String child;
  int line;

  public ElementInfo(PsiElement psiElement, int line_) {
      text = psiElement.getText();
      node = psiElement.getNode().toString().split(":")[0];
      type = psiElement.getNode().getElementType().toString();
      line = line_;
      if (psiElement.getParent() != null) {
          parent = psiElement.getParent().toString().split(":")[0];
          child = psiElement.getChildren().toString();
          original = psiElement.getOriginalElement().getText();
//          if (parent.equals("PsiJavaCodeReferenceElement") && !psiElement.getNode().getTreeParent().getText().contains(".")) {
//              System.err.println(psiElement.getNode().getTreeParent().getText() + " * " + psiElement.getOriginalElement().getText() + " * " + psiElement.getContext() + " * " + psiElement.getResolveScope() + "*" + psiElement.getTextOffset());
//              System.err.println(psiElement.getReference()+ "*" + psiElement.getOriginalElement() + "*" + psiElement.getContext() + "*" + psiElement.getResolveScope() + "*" + psiElement.getTextOffset());
//              System.exit(1);
//          }
      }
    //  ref = psiElement.getReference();
    //  refs = psiElement.getReferences();
  }
}
