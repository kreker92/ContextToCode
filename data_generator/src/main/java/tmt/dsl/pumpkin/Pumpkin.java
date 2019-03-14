package tmt.dsl.pumpkin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.intellij.util.containers.HashSet;

import tmt.conf.Conf;
import tmt.dsl.Classifier;
import tmt.dsl.executor.info.Step;
import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;

public class Pumpkin {
//  ArrayList<Integer> candidates = new ArrayList<>();
  ArrayList<Integer> lines;
  private HashMap<Integer, HashMap<String, String>> stabs = new HashMap<>();
  private ArrayList<HashMap<Integer, Step>> context;

  public Pumpkin(ArrayList<HashMap<Integer, Step>> context_, Classifier ts) {
    context = context_;
    
    lines = new ArrayList(context_.get(0).keySet());
    Collections.sort(lines, Collections.reverseOrder());

    for (InnerClass c : ts.classes) {
      HashMap<String, String> snpt = fill(c); 

      if (snpt != null)
        stabs.put(Integer.parseInt(c.executor_command), snpt);
    }
  }
  
  public boolean is_continue() {
//    System.err.println(!stabs.isEmpty());
    return !stabs.isEmpty();
  }

  public ArrayList<HashMap<String, String>> snippetize(int[] res, ArrayList<HashMap<String, String>> snippets) {
    for (Integer c : res) {
      if (c!= 1 && stabs.containsKey(c))
        snippets.add(stabs.get(c));
    }
    return snippets;
  }
  
  public void candidates() {
  }

  private HashMap<String, String> fill(InnerClass innerClass) {
    boolean complete = false;
    String snippet = "";

    for (LinkedHashMap<String, String> scs : innerClass.scheme)
      for (Entry<String, String> sc : scs.entrySet()){ 
        if (sc.getKey().contains("literal"))
          snippet += sc.getValue();
        else if (sc.getKey().contains("stab_req")) { 
          String var = getFirstVar(sc.getValue());
          if (!var.isEmpty()) {
            snippet += var;
            complete = true;
          }
        }
      }
    
    if (complete) {
      HashMap<String, String> temp = new HashMap<>();
      temp.put("prediction", snippet);
      temp.put("code", innerClass.executor_command);
      temp.put("documentation", innerClass.description);
      temp.put("ast_type", innerClass.ast_type);
      return temp;
    }
    
    return null;
  }

  private String getFirstVar(String type) {
    for (HashMap<Integer, Step> cs : context)
      for (Integer line : lines) {
    	if  (Conf.lang.equals("java")) {
	        for (ElementInfo el : (ArrayList<ElementInfo>)cs.get(line).additional_info.get("el")) 
	          if (el.ast_type != null && !el.ast_type.isEmpty() && type.equals(el.ast_type)) 
	          //&& (!el.text.equals("TextView") && !el.text.equals("View") && !el.text.equals("Cursor") && !el.text.equals("TextView"))) 
	            return el.text;
    	} else if (Conf.lang.equals("javascript")) {
    	  for (ElementInfo el : (ArrayList<ElementInfo>)cs.get(line).additional_info.get("el")) 
            if (el.text != null && !el.text.isEmpty() && el.text.contains(type) && el.type.equals("Identifier")) {
            //&& (!el.text.equals("TextView") && !el.text.equals("View") && !el.text.equals("Cursor") && !el.text.equals("TextView"))) 
              return el.text;
            }
    	}
      }
    return "";
  }
  
  public String getContext() {
    ArrayList out = new ArrayList<>();
    
    HashSet<String> keys = new HashSet<>();
    for (Entry<Integer, HashMap<String, String>> stab : stabs.entrySet()) {
        keys.add(stab.getValue().get("ast_type"));
    }
    /*TODO:
     * */
    out.add(keys);
    out.add(context);
    return new Gson().toJson(out);
  }
}
