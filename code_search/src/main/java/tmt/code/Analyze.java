package tmt.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import tmt.code.snippets.Vector;
import tmt.code.snippets.codesearch.Response;
import tmt.code.snippets.codesearch.Result;
import tmt.code.snippets.stackoverflow.Row;
import tmt.code.validate.Validate;
import tmt.conf.Conf;
import tmt.conf.Utils;
import tmt.export.Dt;

import java.util.Arrays;

public class Analyze {
  static Gson gson;
  static String key = "Class.forName";
  static String query = "DriverManager%20getConnection%20Class%20forName";
  static String root_key = "cs/DriverManager_getConnection_Class_forName";
  static String root = "/root/ContextToCode/output/";

  public static void main(String[] args) throws JsonSyntaxException, IOException, InterruptedException  {
    gson = new Gson();
    //		createBaseFromSO();
    dataFromCodeSearch();
    //      loadCodeSearch();
  }

  private static void loadCodeSearch() throws JsonSyntaxException, IOException, InterruptedException {
    //      ArrayList<String> code = new ArrayList<String>(Arrays.asList(Utils.readFile("/root/toCode/output/cs/66533677").split("\n")));
    ArrayList<Vector[]> res = new ArrayList<>();
    File[] files = new File(root+root_key).listFiles();
    HashSet<String> commands = new HashSet<>();
    HashMap<String, Integer> hot_ecnoding = new HashMap<>();

    for (File f : files) {
      System.err.println(f.getPath());
      String[] code = Utils.readFile(f.getPath()).split("\n");

      for (int line = code.length-1; line >= 0; line --) {
        String c = code[line];
        if (c.contains(key)) {
          Vector[] snip = getSnippet(line, code, commands, (f.getPath()+line).hashCode());
          if (snip.length > 0)
            res.add(snip);
        }
      }
    }

    hotEncode(commands, hot_ecnoding);

    ArrayList<Vector> output = new ArrayList<>();
    for (Vector[] c : res) {
      for (Vector v : c) {
        v.vectorize(hot_ecnoding);
        output.add(v);
      }
    }
    
    Utils.saveJsonFile(root+"funcs/vectors", output);
  }

  private static void hotEncode(HashSet<String> commands, HashMap<String, Integer> hot_ecnoding) {
    int count = 0;
    for (String comm : commands) {
      hot_ecnoding.put(comm, count);
      count++;
    }
  }

  static Vector[] getSnippet(int start, String[] code, HashSet<String> commands, int parent_id) {
    ArrayList<Vector> res = new ArrayList<Vector>();

    int count = 0;
    for (int i = start; i >= 0; i --) {
      String line = code[i];
      if (i != start && (isStart(line) || count > 3))
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

  private static boolean isStart(String string) {
    if (string.contains("public ") || string.contains("private ") || string.contains("protected ") || string.contains(key))
      return true;
    else
      return false;
  }

  private static void dataFromCodeSearch() throws JsonSyntaxException, IOException, InterruptedException {
    File file = new File(root+root_key);        
    if(!file.exists()){
      file.mkdir();
    }
    Integer nextpage = 0;
    while (nextpage != null) {
      Response resp = new Gson().fromJson(Utils.readStringFromURL("https://searchcode.com/api/codesearch_I/?q="+query+"&lan=23&&p="+nextpage),
          Response.class);	
      for ( Result r : resp.getResults()) {
        r.url = r.url.replace("view", "raw");
        String str = Utils.readStringFromURL(r.url);
        Utils.savePlainFile(root+root_key+"/"+r.id, str);
        TimeUnit.SECONDS.sleep(1);
      }
      
      nextpage = resp.nextpage;
    }
  }

  public static void createBaseFromSO() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    File f = new File(Conf.answers_output.replace("?", "_all"));
    Validate v = new Validate();
    Conf.answers = gson.fromJson(new FileReader(f), Conf.gson_answers);

    f = new File(Conf.posts_output.replace("?", "_all"));
    for (Row p : gson.fromJson(new FileReader(f), Row[].class)) {
      Conf.posts.put(p.getId(), p);
    }

    for ( Entry<Integer, ArrayList<Row>> a : Conf.answers.entrySet() ) {
      Collections.sort(a.getValue(), Utils.comparator_score_desc);
      for (Row k : a.getValue()) {
        k.init(v);
        k.setPost(Conf.posts.get(k.getParentId()));
      }
    }
    Dt dt = new Dt();
    dt.export();
  }
}