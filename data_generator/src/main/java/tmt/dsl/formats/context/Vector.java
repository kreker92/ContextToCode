package tmt.dsl.formats.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.Gson;

import tmt.conf.Conf;
import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;

public class Vector {
  public ArrayList<Integer> vector = new ArrayList<>();
  //ACTUAL "VALUE" in traces
  public ArrayList<String> strings = new ArrayList<>();
  public Integer row = null;
  private Integer level = null;
  private String origin;
  //not used in traces
  private String node;
  //not used in traces
  private String parent;
  private int label = 0;
  public int parent_id;
  public String path;
  public int line_num;
  public ArrayList<ElementInfo> el = new ArrayList<>();
  
  //ACTUAL "PROGRAM" in traces
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
  
  public Vector(InnerClass c, int i, boolean b, int level_, String path_, int line_num_) {
    origin = new Gson().toJson(c.elements);//.toLowerCase().replaceAll("[^a-z]", " ").replaceAll(" +", " ").trim();;
    line_num = c.line_num;
    path = path_;

    if (b)
      label = 1;

    for ( ElementInfo s : c.elements ) {
//      if (!goodTypes.contains(s.node) && !badTypes.contains(s.node)) {
//        System.err.println(s.node);
//        System.exit(1);
//      }
      el.add(s);
      switch (Conf.lang) {
        case "java":
          if (s.ast_type != null && !s.ast_type.equals("null")) {
            strings.add(s.ast_type.trim());
            parent = s.parent;
            node = s.node;
          } else if (Conf.good_types.contains(s.node) && !Conf.bad_types.contains(s.parent)) {
            strings.add(s.text.trim());
            parent = s.parent;
            node = s.node;
          }
          break;
        case "javascript":
          if (s.ast_type != null && !s.ast_type.equals("null")) {
            strings.add(s.ast_type.trim());
            parent = s.parent;
            node = s.node;
          } else if (Conf.good_types.contains(s.node) && !Conf.bad_types.contains(s.parent)) {
            strings.add(s.text.trim());
            parent = s.parent;
            node = s.node;
          }
          break;
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
    return program;//strings+" - "+origin+" - "+level;
  }

  public String getProgram() {
    return program;
  }
}
