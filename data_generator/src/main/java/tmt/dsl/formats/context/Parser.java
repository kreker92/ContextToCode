package tmt.dsl.formats.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import tmt.conf.Conf;
import tmt.dsl.Classifier;
import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;

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
  
  public static Vector[] getSnippet(int line_num, InnerClass[] code, String path, ArrayList<InnerClass> keys, int  limit) {
    ArrayList<Vector> res = new ArrayList<Vector>();
    ArrayList<String> sequence = new ArrayList<>();
    
    InnerClass start_l = new InnerClass("falsekey", "1", "Function");
    start_l.elements.add(new ElementInfo("type", "FunctionDeclaration", ""));
    
    ArrayList<InnerClass> start = new ArrayList<InnerClass>(Arrays.asList(start_l));

    int count = 0;
    for (int i = line_num; i >= 0; i --) {
      String line = code[i].line_text;
      
      if (i != line_num && (isStart(code[i], keys, start) || withContext(sequence, limit, count))) 
        break;
      String line_clean = clean(line);
//      System.err.println(i+"!"+path);
      if (hasSense(line_clean)) {
        Vector v = new Vector(code[i], i, i == line_num, count, path, line_num);
        if (!v.isEmpty()) {
          res.add(0, v);
          sequence.add(code[i].executor_command);
          count ++;
        }
      }
    }

    if (res.size() > 1 && seqToPass(sequence)) 
      return res.toArray(new Vector[res.size()]);
    else
      return new Vector[0];
  }
  
  private static boolean seqToPass(ArrayList<String> sequence) {
     if (sequence.get(sequence.size()-1).equals("1") 
         || sequence.size() == 2 && sequence.get(sequence.size()-2).equals("1"))
       return true;
     else
       return false;
}

  private static boolean withContext(ArrayList<String> sequence, int limit, int count) {
    if (sequence.size() >= limit 
        && sequence.get(count-2).equals("1") 
        && sequence.get(count-1).equals("1")) 
      return true;
    else
      return false;
}

  public static String clean(String line_raw) {
    String line = "";
    if (line_raw.contains("//"))
      line = line.split("//")[0];
    else
      line = line_raw;
    return line.trim().replaceAll("\"([^\"]*)\"", " ").replaceAll("(?:--|[\\[\\]{}()+/\\\\])", " ").replace(",", " ").replace(".", " ").replace("=", " ").replace(";", " ");
  }

  public static boolean hasSense(String line) {
    if (Conf.lang.equals("java")) {
      if (line.equals("}") || line.equals("{") || line.isEmpty())
        return false;
      else
        return true;
    } else { //if (Conf.lang.equals("javascript")) {
      return true;
    }
  }

  private static boolean isStart(InnerClass code, ArrayList<InnerClass> keys, ArrayList<InnerClass> start) {
    if (Conf.lang.equals("java")) {
      if (code.line_text.contains("public ") || code.line_text.contains("private ") || code.line_text.contains("protected ") ) // || code.matches(keys))
        return true;
      else
        return false;
    } else if (Conf.lang.equals("javascript")) {
      if (code.matches(start)) {
//        System.err.println("#"+code.elements);
//        System.exit(1);
        return true;
      }
      else
        return false;
    } else {
      return false;
    }
  }
}
