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
  private LinkedHashMap<Integer, HashMap<String, String>> filled_stabs = new LinkedHashMap<>();
  private LinkedHashMap<Integer, HashMap<String, String>> empty_stabs = new LinkedHashMap<>();
  private ArrayList<HashMap<Integer, Step>> context;
  public int response;

  public Pumpkin(ArrayList<HashMap<Integer, Step>> context_, Classifier ts) {
    context = context_;
    
    lines = new ArrayList(context_.get(0).keySet());
    Collections.sort(lines, Collections.reverseOrder());

    for (String key : Conf.js_keys) {
      for (InnerClass c : ts.classes) {
        if (key.equals(c.ast_type)) {
          fill(c); 
          break;
        }
      }
    }
  }
  
  public boolean is_continue() {
    System.err.println("@"+filled_stabs.size());
    System.err.println("@"+empty_stabs.size());
    return !filled_stabs.isEmpty();
  }

  public ArrayList<HashMap<String, String>> snippetize(int[] res, ArrayList<HashMap<String, String>> snippets) {
    /*ArrayList<Integer> found = new ArrayList<Integer>();
    for (int i : res)
      found.add(i);

    if (context.get(0).size() > 2)
      for ( Entry<Integer, HashMap<String, String>> e : stabs.entrySet()) 
        if (found.contains(e.getKey())) {
          HashMap<String, String> tmp = new HashMap<>(e.getValue());
          tmp.put("found", "true");
          snippets.add(tmp);
        }

    for ( Entry<Integer, HashMap<String, String>> e : stabs.entrySet())
      if (!found.contains(e.getKey())) {
        HashMap<String, String> tmp = new HashMap<>(e.getValue());
        tmp.put("found", "false");
        snippets.add(tmp);
      }*/
    
    ArrayList<Integer> found = new ArrayList<Integer>();
    
    for (Integer c : res) 
      if (c!= 1 && filled_stabs.containsKey(c)) {
        HashMap<String, String> tmp = new HashMap<>(filled_stabs.get(c));
        tmp.put("found", "true");
        snippets.add(tmp);
        found.add(c);
      }

    for ( Entry<Integer, HashMap<String, String>> e : filled_stabs.entrySet()) 
      if (!found.contains(e.getKey())) {
        HashMap<String, String> tmp = new HashMap<>(e.getValue());
        tmp.put("found", "false");
        snippets.add(tmp);
      }

    for ( Entry<Integer, HashMap<String, String>> e : empty_stabs.entrySet())
      if (!found.contains(e.getKey())) {
        HashMap<String, String> tmp = new HashMap<>(e.getValue());
        tmp.put("found", "false");
        snippets.add(tmp);
      }

    return snippets;
  }
  
  public void candidates() {
  }

  private void fill(InnerClass innerClass) {
    String snippet = "";
    HashMap<String, String> temp = new HashMap<>();
    boolean complete = false;

    System.err.println("@"+innerClass.ast_type);
    
    for (LinkedHashMap<String, String> scs : innerClass.scheme)
      for (Entry<String, String> sc : scs.entrySet()){ 
        if (sc.getKey().contains("literal"))
          snippet += sc.getValue();
        else if (sc.getKey().contains("stab_req")) { 
          String var = getFirstVar(sc.getValue());
          System.err.println(var);
          if (!var.isEmpty()) {
            snippet += var;
            complete = true;
          }
        }
      }

    temp.put("code", innerClass.executor_command);
    temp.put("documentation", innerClass.description);
    temp.put("ast_type", innerClass.ast_type);
    temp.put("tab", innerClass.tab);
    
    if (complete) {
      temp.put("content", innerClass.content.replace("**VAR**", snippet));
      filled_stabs.put(Integer.parseInt(innerClass.executor_command), temp);
    } else {
      temp.put("content", innerClass.content);
      empty_stabs.put(Integer.parseInt(innerClass.executor_command), temp);
    }
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
            //if (el.text != null && !el.text.isEmpty() && el.text.contains(type) && el.type.equals("Identifier")) {
    	    if (el.ast_type != null && !el.ast_type.isEmpty() && type.equals(el.ast_type)) {
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
    for (Entry<Integer, HashMap<String, String>> stab : filled_stabs.entrySet()) {
        keys.add(stab.getValue().get("ast_type"));
    }
    /*TODO:
     * */
    out.add(keys);
    out.add(context);
    return new Gson().toJson(out);
  }
}
