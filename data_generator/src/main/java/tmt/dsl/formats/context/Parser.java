package tmt.dsl.formats.context;

import java.util.ArrayList;
import java.util.HashSet;

import tmt.dsl.Template;
import tmt.dsl.formats.context.in.InnerContext;

public class Parser {

 /* public static Vector[] getSnippet(int start, String[] code, HashSet<String> commands, int parent_id, String key) {
    ArrayList<Vector> res = new ArrayList<Vector>();

    int count = 0;
    for (int i = start; i >= 0; i --) {
      String line = code[i];
      String line_clean = clean(line);
      
      if (hasSense(line_clean)) {
        Vector v = new Vector(line_clean, commands, i, line, i == start, count, parent_id);
        if (!v.isEmpty()) {
          res.add(0, v);
          count ++;
        }
      }
      if (i != start && (isStart(line, key) || count > 3))
        break;
    }

    return res.toArray(new Vector[res.size()]);
  } */
  
  public static Vector[] getSnippet(int line_num, InnerContext[] code, HashSet<String> commands, String path, ArrayList<InnerContext> keys, ArrayList<String> goodTypes, ArrayList<String> badTypes, int  limit) {
    ArrayList<Vector> res = new ArrayList<Vector>();
    HashSet<String> commands_local = new HashSet<>();

    int count = 0;
    for (int i = line_num; i >= 0; i --) {
      String line = code[i].line_text;
      if (i != line_num && (isStart(code[i], keys) || count > limit))
        break;
      String line_clean = clean(line);
//      System.err.println(i+"!"+path);
      if (hasSense(line_clean)) {
        Vector v = new Vector(code[i], commands_local, i, line, i == line_num, count, path, line_num, goodTypes, badTypes);
        if (!v.isEmpty()) {
          res.add(0, v);
          count ++;
        }
      }
    }
    if (res.size() > 1) {
      commands.addAll(commands_local);
      return res.toArray(new Vector[res.size()]);
    }
    else
      return new Vector[0];
  }
  
  private static String clean(String line_raw) {
    String line = "";
    if (line_raw.contains("//"))
      line = line.split("//")[0];
    else
      line = line_raw;
    return line.trim().replaceAll("\"([^\"]*)\"", " ").replaceAll("(?:--|[\\[\\]{}()+/\\\\])", " ").replace(",", " ").replace(".", " ").replace("=", " ").replace(";", " ");
  }

  private static boolean hasSense(String line) {
    if (line.equals("}") || line.equals("{") || line.isEmpty())
      return false;
    else
      return true;
  }

  private static boolean isStart(InnerContext code, ArrayList<InnerContext> keys) {
    if (code.line_text.contains("public ") || code.line_text.contains("private ") || code.line_text.contains("protected ") ) // || code.matches(keys))
      return true;
    else
      return false;
  }
}
