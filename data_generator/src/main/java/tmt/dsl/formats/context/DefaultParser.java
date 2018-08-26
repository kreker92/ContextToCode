package tmt.dsl.formats.context;

import java.util.ArrayList;
import java.util.HashSet;

public class DefaultParser {

  public static Vector[] getSnippet(int start, String[] code, HashSet<String> commands, int parent_id, String key) {
    ArrayList<Vector> res = new ArrayList<Vector>();

    int count = 0;
    for (int i = start; i >= 0; i --) {
      String line = code[i];
      System.err.println(line);
      if (i != start && (isStart(line, key) || count > 3))
        break;
      String line_clean = clean(line);
      if (hasSense(line_clean)) {
        Vector v = new Vector(line_clean, commands, i, line, i == start, count, parent_id);
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
    if (string.contains("public ") || string.contains("private ") || string.contains("protected ") || string.contains(key))
      return true;
    else
      return false;
  }
}
