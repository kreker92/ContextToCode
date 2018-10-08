import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public class ElementInfo {
  public String node;
  public String type;
  public String text;
  public String parent;
  //public PsiReference  ref;
  //public PsiReference[]  refs;
  public String child;
  int line;

  public ElementInfo(PsiElement psiElement, int line_) {
      text = psiElement.getText();
      node = psiElement.getNode().toString().split(":")[0];
      type = psiElement.getNode().getElementType().toString();
      line = line_;
      parent = psiElement.getParent().toString().split(":")[0];;
      child = psiElement.getChildren().toString();
//      System.err.println(psiElement.);
    //  ref = psiElement.getReference();
    //  refs = psiElement.getReferences();
  }
}
