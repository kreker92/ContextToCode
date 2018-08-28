package tmt.dsl.formats.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerContext;

public class Vector {
  public ArrayList<Integer> vector = new ArrayList<>();
  private ArrayList<String> strings = new ArrayList<>();
  private Integer row = null;
  public Integer level = null;
  private String origin;
  private int label = 0;
  public int parent_id;

  public Vector(String o, HashSet<String> commands, int i, String line, boolean b, int level_, int parent_id_) {
    origin = line;

    if (b)
      label = 1;

    for ( String s : o.split(" ") )
      if (!s.trim().isEmpty()) {
        strings.add(s.trim());
        commands.add(s.trim());
      }
    row = i;
    level = level_;
    parent_id = parent_id_;
  }
  
  public Vector(InnerContext c, HashSet<String> commands, int i, String line, boolean b, int level_, int parent_id_, ArrayList<String> goodTypes, ArrayList<String> badTypes) {
    origin = line;

    if (b)
      label = 1;

    for ( ElementInfo s : c.elements )
//      if (!goodTypes.contains(s.node) && !badTypes.contains(s.node)) {
//        System.err.println(s.node);
//        System.exit(1);
//      }
      if (goodTypes.contains(s.node)) {
        strings.add(s.text.trim());
        commands.add(s.text.trim());
      }
    row = i;
    level = level_;
    parent_id = parent_id_;
  }

  public boolean isEmpty() {
    return strings.isEmpty();
  }

  public void vectorize(HashMap<String, Integer> hot_ecnoding) {
    for (String s : strings) {
      vector.add(hot_ecnoding.get(s));
    }
  }

  public String toString() {
    return strings+" - "+origin+" - "+level;
  }
}
