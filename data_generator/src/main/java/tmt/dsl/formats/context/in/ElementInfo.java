package tmt.dsl.formats.context.in;

import tmt.dsl.data.Utils;

import com.intellij.psi.PsiElement;

public class ElementInfo {
  public String node = "";
  public String text = "";
  public int line;
  public String type = "";
  public String ast_type = "";
  public String parent;
  public String child;
  
  public ElementInfo(String type, String value, String text_key) {
    if (type.equals("ast_type"))
      ast_type = value;
    if (type.equals("type"))
      this.type = value;
    if (text_key != null)
      text = text_key;
  }
  
  public boolean equals(Object o2) {
    if (o2 == null)
      return false;
    if (o2 == this) 
      return true;
    if (!(o2 instanceof ElementInfo)) 
      return false;
    if ( !ast_type.isEmpty() )
      return Utils.compare(ast_type, ((ElementInfo)o2).ast_type);
    else if ( !type.isEmpty() )
      if (Utils.compare(type, ((ElementInfo)o2).type)) 
        if ( !text.isEmpty() ) 
          return ((ElementInfo)o2).text.contains(text);
        else
          return true;

    return false;
  }
  
  public int hashCode() {
	  System.exit(1);
    return (type+" "+ast_type+" "+parent+" "+node+" "+text).hashCode();
  }
  
  public String toString() {
    return type+" "+ast_type+" "+parent+" "+node+" "+text;
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
