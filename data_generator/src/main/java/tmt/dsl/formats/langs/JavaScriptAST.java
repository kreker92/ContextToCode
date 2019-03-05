package tmt.dsl.formats.langs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import tmt.conf.Utils;
import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JavaScriptAST {
  private InnerClass[] out;
  
  public JavaScriptAST (File f) {
    Node[] nodes = null;
    try {
      nodes = new Gson().fromJson(Utils.readFile(f.getPath()), Node[].class);
    } catch (JsonSyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    ArrayList<InnerClass> cs = new ArrayList<>();
    
    for (Node n : nodes)
      cs.add(n.toClass()); 
    
    out = cs.toArray(new InnerClass[cs.size()]);
  }

  public InnerClass[] getClasses() {
    return out;
  }

}

class Node {
  String type;
  Integer id;
  public ArrayList<Node> links;
  
  private static Comparator<Node> comparator_id_desc = new Comparator<Node>() {
    public int compare(Node o1, Node o2) {
      int c = o2.id.compareTo(o1.id);
      return c;
    }
  };
  
  /*
   *   public ArrayList<ElementInfo> elements = new ArrayList<>();
  public int start;
  public int end;
  public int line_num;
  public String line_text;
  public String type;
  public String ast_type;
  //ACTUAL "PROGRAM" in traces
  public String executor_command = "1";
   */

  public InnerClass toClass() {
//  public Node () {
//  Collections.sort(links, comparator_id_desc);
//}
    return null;
  }
}
