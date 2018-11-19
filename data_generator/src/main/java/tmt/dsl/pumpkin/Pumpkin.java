package tmt.dsl.pumpkin;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import tmt.dsl.Classifier;
import tmt.dsl.executor.info.Step;
import tmt.dsl.formats.context.in.InnerClass;

public class Pumpkin {
  ArrayList<Integer> candidates = new ArrayList<>();
  ArrayList<HashMap<Integer, Step>> context;
  HashMap<Integer, InnerClass> stabs = new HashMap<>();

  public Pumpkin(int[] res, ArrayList<HashMap<Integer, Step>> context_, ArrayList<Classifier> ts) {
    for (int r : res)
      if (r != 1)
        candidates.add(r);
    
    for (Classifier t : ts)
      for (InnerClass c : t.classes)
        stabs.put(Integer.parseInt(c.executor_command), c);
    context = context_;
  }

  public int[] snippetize() {
    for (Integer c : candidates)
      stabs.get(c);
    
    System.err.println(candidates +" * "+new Gson().toJson(context));
    return null;
  }

}
