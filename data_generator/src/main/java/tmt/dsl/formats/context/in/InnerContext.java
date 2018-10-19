package tmt.dsl.formats.context.in;

import java.util.ArrayList;

public class InnerContext {
  public ArrayList<ElementInfo> elements = new ArrayList<>();
  public int start;
  public int end;
  public int line_num;
  public String line_text;
  
  
  public String getLine(ArrayList<String> bad_types) {
    String res = "";
    
    for (ElementInfo el : elements) 
      if (!bad_types.contains(el.parent))
        res += el.text+" ";
    
    return res.toLowerCase().replaceAll("[^a-z]", " ").replaceAll(" +", " ").trim();
  }


  public boolean matches(ArrayList<String> keys) {
    for (String key : keys) {
      boolean found = false;
      for (ElementInfo el : elements)
        if (el.ast_type != null && el.ast_type.equals(key)) 
          found = true;
      
      if (!found)
        return false;
    }
    return true;
  }
}