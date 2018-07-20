package tmt.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

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

  public static void main(String[] args) throws JsonSyntaxException, IOException, InterruptedException  {
    gson = new Gson();
    //		createBaseFromSO();
    //		dataFromCodeSearch();
    loadCodeSearch();
  }

  private static void loadCodeSearch() throws JsonSyntaxException, IOException, InterruptedException {
    //      ArrayList<String> code = new ArrayList<String>(Arrays.asList(Utils.readFile("/root/toCode/output/cs/66533677").split("\n")));
    HashMap<String, Integer> counter = new HashMap<>();
    ArrayList<String[]> res = new ArrayList<>();
    File[] files = new File("/root/toCode/output/cs/").listFiles();

    for (File f : files) {
      String[] code = Utils.readFile(f.getPath()).split("\n");
      int line = 0;

      for (String c : code) {
        if (c.contains("DriverManager.getConnection")) {
          String[] snip = getSnippet(line, code);
          if (snip.length > 0)
            res.add(snip);
        }
        line ++;
      }
    }

    for (String[] r : res) {
      if (r.length > 1) {
        String line = r[r.length-2];
        int count = 1;

        if (counter.containsKey(line))
          count = counter.get(line)+1;

        counter.put(line, count);
      }
    }
    
    for (Entry<String, Integer> c : counter.entrySet()) {
      if (c.getValue() > 10) {
        System.err.println(c);
      }
    }
  }

  static String[] getSnippet(int l, String[] code) {
    ArrayList<String> res = new ArrayList<String>();
    int count = 0;
    for (int i = l; i > 0; i --) {
      String line = code[i].trim();
      if (isStart(line) || count > 3)
        break;
      if (hasSense(line)) {
        res.add(0, line);
        count ++;
      }
    }
    return res.toArray(new String[res.size()]);
  }

  private static boolean hasSense(String line) {
    if (line.equals("}") || line.equals("{") || line.isEmpty() || line.contains("//"))
      return false;
    else
      return true;
  }

  private static boolean isStart(String string) {
    if (string.contains("public ") || string.contains("private ") || string.contains("protected "))
      return true;
    else
      return false;
  }

  private static void dataFromCodeSearch() throws JsonSyntaxException, IOException, InterruptedException {
    for (int i = 1; i < 50; i ++) {
      Response resp = new Gson().fromJson(Utils.readStringFromURL("https://searchcode.com/api/codesearch_I/?q=DriverManager.getConnection&lan=23&&p="+i),
          Response.class);	
      for ( Result r : resp.getResults()) {
        r.url = r.url.replace("view", "raw");
        String str = Utils.readStringFromURL(r.url);
        Utils.savePlainFile("/root/toCode/output/cs/"+r.id, str);
        TimeUnit.SECONDS.sleep(1);
      }
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