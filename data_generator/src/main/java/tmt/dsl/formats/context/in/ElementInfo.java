package tmt.dsl.formats.context.in;

import com.intellij.psi.PsiElement;

public class ElementInfo {
  public String node;
  public String text;
  int line;
  public String type;
  public String ast_type;
  public String parent;
  public String child;
//
//
//  public ElementInfo(PsiElement psiElement, int line_) {
//      text = psiElement.getText().trim();
//      node = psiElement.getNode().toString();
//      parent = psiElement.getNode().toString();
//      line = line_;
//  }
}
