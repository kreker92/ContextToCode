package tmt.dsl.pumpkin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;

import tmt.dsl.Classifier;
import tmt.dsl.executor.info.Step;
import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;

public class Pumpkin {
  ArrayList<Integer> candidates = new ArrayList<>();
  ArrayList<Integer> lines;
  HashMap<Integer, InnerClass> stabs = new HashMap<>();
  private ArrayList<HashMap<Integer, Step>> context;

  public Pumpkin(int[] res, ArrayList<HashMap<Integer, Step>> context_, Classifier ts) {
    for (int r : res)
      if (r != 1)
        candidates.add(r);
    
    for (InnerClass c : ts.classes)
      stabs.put(Integer.parseInt(c.executor_command), c);
    
    lines = new ArrayList(context_.get(0).keySet());
    Collections.sort(lines, Collections.reverseOrder());
    
    context = context_;
  }

  public ArrayList<HashMap<String, String>> snippetize() {
    ArrayList<HashMap<String, String>> snippets = new ArrayList<>();
    for (Integer c : candidates)
      if (stabs.containsKey(c))
        fill(stabs.get(c), snippets);
    
    System.err.println(new Gson().toJson(snippets)+" - "+candidates);
    return snippets;
  }

  private void fill(InnerClass innerClass, ArrayList<HashMap<String, String>> snippets) {
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
      snippets.add(temp);
    }
  }

  private String getFirstVar(String type) {
    for (HashMap<Integer, Step> cs : context)
      for (Integer l : lines)
        for (ElementInfo el : (ArrayList<ElementInfo>)cs.get(l).additional_info.get("el")) 
          if (el.ast_type != null && !el.ast_type.isEmpty() && type.equals(el.ast_type) && 
          (!el.text.equals("TextView") && !el.text.equals("View") && !el.text.equals("Cursor") && !el.text.equals("TextView"))) 
            return el.text;
    return "";
  }

}
