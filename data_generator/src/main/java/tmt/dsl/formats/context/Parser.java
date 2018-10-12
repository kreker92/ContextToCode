package tmt.dsl.formats.context;

import java.util.ArrayList;
import java.util.HashSet;

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
  
  public static Vector[] getSnippet(int line_num, InnerContext[] code, HashSet<String> commands, String path, String key, ArrayList<String> goodTypes, ArrayList<String> badTypes, int  limit) {
    ArrayList<Vector> res = new ArrayList<Vector>();

    int count = 0;
    for (int i = line_num; i >= 0; i --) {
      String line = code[i].line_text;
      if (i != line_num && (isStart(line, key) || count > limit))
        break;
      String line_clean = clean(line);
      if (hasSense(line_clean)) {
        Vector v = new Vector(code[i], commands, i, line, i == line_num, count, path, line_num, goodTypes, badTypes);
        if (!v.isEmpty()) {
          res.add(0, v);
          count ++;
        }
      }
    }

    return res.toArray(new Vector[res.size()]);
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

  private static boolean isStart(String string, String key) {
    if (string.contains("public ") || string.contains("private ") || string.contains("protected ") || key != null && string.contains(key))
      return true;
    else
      return false;
  }
}
