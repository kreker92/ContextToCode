package tmt.dsl.formats.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
