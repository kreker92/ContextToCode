package tmt.dsl.formats.context.in;

import java.util.ArrayList;

public class InnerContext {
  public ArrayList<ElementInfo> elements = new ArrayList<>();
  public int start;
  public int end;
  public int line_num;
  public String line_text;
  public String type;
  public String executor_command;

  
  public InnerContext(String type_, String executor_command_) {
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


  public boolean matches(ArrayList<InnerContext> keys) {
    for (InnerContext key : keys) 
      if (key.sameElements(this))
        return true;
        
    return false;
  }


  public boolean sameElements(InnerContext innerContext) {
    for (ElementInfo e1 : innerContext.elements) {
      boolean found = false;
      for (ElementInfo e2 : elements)
        if (e1.equals(e2)) {
          found = true;
        }
      
      if (!found)
        return false;
    }
    return true;
  }
}