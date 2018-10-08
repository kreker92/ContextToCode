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
}