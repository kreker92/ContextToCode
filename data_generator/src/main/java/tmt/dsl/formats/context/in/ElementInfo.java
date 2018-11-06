package tmt.dsl.formats.context.in;

import tmt.dsl.data.Utils;

import com.intellij.psi.PsiElement;

public class ElementInfo {
  public String node = "";
  public String text;
  public int line;
  public String type = "";
  public String ast_type = "";
  public String parent;
  public String child;
  
  public ElementInfo(String type, String value) {
    if (type.equals("ast_type"))
      ast_type = value;
    if (type.equals("type"))
      type = value;
  }
  
  public boolean equals(Object o2) {
    if (o2 == null)
      return false;
    if (o2 == this) 
      return true;
    if (!(o2 instanceof ElementInfo)) 
      return false;
    
    if ( Utils.compare(ast_type, ((ElementInfo)o2).ast_type) )
     /*   node.equals(((ElementInfo)o2).node) ||
        type.equals(((ElementInfo)o2).type)) */
      return true;
//      &&  ast_type.equals(((ElementInfo)o2).ast_type))
      
    return false;
  }
  
  public int hashCode() {
    return (type+" "+ast_type+" "+parent+" "+node+" "+text).hashCode();
  }
//
//
//  public ElementInfo(PsiElement psiElement, int line_) {
//      text = psiElement.getText().trim();
//      node = psiElement.getNode().toString();
//      parent = psiElement.getNode().toString();
//      line = line_;
//  }
}
