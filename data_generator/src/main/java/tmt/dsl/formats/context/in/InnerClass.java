package tmt.dsl.formats.context.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class InnerClass {
  public ArrayList<ElementInfo> elements = new ArrayList<>();
  public int start;
  public int end;
  public int line_num;
  public String line_text;
  public String type;
  public String executor_command = "1";
  
  public ArrayList<LinkedHashMap<String, String>> scheme = new ArrayList<LinkedHashMap<String, String>>();
  public String description;
  
  public InnerClass(String type_, String executor_command_) {
    type = type_;
    executor_command = executor_command_;
  }


  public String getLine(ArrayList<String> bad_types) {
    String res = "";
    
    for (ElementInfo el : elements) 
      if (!bad_types.contains(el.parent))
        res += el.text+" ";
    
    return res.toLowerCase().replaceAll("[^a-z]", " ").replaceAll(" +", " ").trim();
  }


  public boolean matches(ArrayList<InnerClass> keys) {
    for (InnerClass key : keys) 
      if (this.hasElements(key))
        return true;
        
    return false;
  }


  public boolean hasElements(InnerClass innerContext) {
    if (this.elements.isEmpty())
      return false;
    
    for (ElementInfo e1 : innerContext.elements) {
      boolean found_el = false;
      for (ElementInfo e2 : elements)
        if (e1.equals(e2)) 
          found_el = true;
        
      
      if (!found_el)
        return false;
    }
    return true;
  }
}