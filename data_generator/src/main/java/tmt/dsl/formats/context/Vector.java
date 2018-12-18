package tmt.dsl.formats.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;

public class Vector {
  public ArrayList<Integer> vector = new ArrayList<>();
  public ArrayList<String> strings = new ArrayList<>();
  public Integer row = null;
  private Integer level = null;
  private String origin;
  private String node;
  private String parent;
  private int label = 0;
  public int parent_id;
  public String path;
  public int line_num;
  public ArrayList<ElementInfo> el = new ArrayList<>();
  
  private String program;

  /*public Vector(String o, HashSet<String> commands, int i, String line, boolean b, int level_, int parent_id_) {
    origin = line.toLowerCase().replaceAll("[^a-z]", " ").replaceAll(" +", " ").trim();

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
  }*/
  
  public Vector(InnerClass c, HashSet<String> commands, int i, String line, boolean b, int level_, String path_, int line_num_, ArrayList<String> goodTypes, ArrayList<String> badTypes) {
    origin = line;//.toLowerCase().replaceAll("[^a-z]", " ").replaceAll(" +", " ").trim();;
    line_num = c.line_num;
    path = path_;

    if (b)
      label = 1;

    for ( ElementInfo s : c.elements ) {
//      if (!goodTypes.contains(s.node) && !badTypes.contains(s.node)) {
//        System.err.println(s.node);
//        System.exit(1);
//      }
      el.add(new ElementInfo("ast_type", s.ast_type, s.text));
      if (s.ast_type != null && !s.ast_type.equals("null")) {
        strings.add(s.ast_type.trim());
        parent = s.parent;
        node = s.node;
//        commands.add(s.node+" * "+s.parent+" * "+s.text.trim());
        commands.add(s.ast_type.trim());
      } else if (goodTypes.contains(s.node) && !badTypes.contains(s.parent)) {
        strings.add(s.text.trim());
        parent = s.parent;
        node = s.node;
//        commands.add(s.node+" * "+s.parent+" * "+s.text.trim());
        commands.add(s.text.trim());
      }
    }
    row = i;
    level = level_;
    parent_id = (path+line_num_).hashCode();
    program = c.executor_command;
  }
  
  public String getNode() {
    return node;
  }
  
  public String getParent() {
    return parent;
  }

  public boolean isEmpty() {
    return strings.isEmpty();
  }
  
  public String getOrign() {
    return origin;
  }
  
  public int getLevel() {
    return level;
  }

  public String toString() {
    return strings+" - "+origin+" - "+level;
  }

  public String getProgram() {
    return program;
  }
}
