package tmt.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import tmt.code.snippets.codesearch.Response;
import tmt.code.snippets.codesearch.Result;
import tmt.code.snippets.stackoverflow.Conf;
import tmt.code.snippets.stackoverflow.Dt;
import tmt.code.snippets.stackoverflow.Row;
import tmt.code.snippets.stackoverflow.Validate;
import tmt.conf.Utils;

public class LoadDataFromSearch {
  static Gson gson;

  public static void main(String[] args) throws JsonSyntaxException, IOException, InterruptedException, KeyManagementException, NoSuchAlgorithmException  {
    gson = new Gson();
    //createBaseFromSO();
    dataFromCodeSearch();
//          loadCodeSearch();
  }

  private static void dataFromCodeSearch() throws JsonSyntaxException, IOException, InterruptedException, KeyManagementException, NoSuchAlgorithmException {
    File file = new File(Conf.root+Conf.root_key);        
    if(!file.exists()){
      file.mkdir();
    }

    
    String[] queries_raw = Utils.readFile("/root/ContextToCode/data/datasets/android/cs/1515272").split(";");
    String check = Utils.readFile("../log/queries.txt");
    
    PrintStream fileStream = new PrintStream(
        new FileOutputStream("../log/queries.txt", true)); 
    System.setOut(fileStream);
    
    ArrayList<String> queries = new ArrayList<String>();
    for (String q : queries_raw) {
      if (q.contains("import android.")) {
        queries.add(q.replace("import ", "").replace(",", ""));
      }
    }
    for (String q : queries) {
      Integer nextpage = 0;
      
      if (!check.contains(q)) {
        System.out.println(q+"!");
        while (nextpage != null) {
          Response resp = new Gson().fromJson(Utils.readStringFromURL("https://searchcode.com/api/codesearch_I/?q="+URLEncoder.encode(q, "UTF-8")+"&lan=23&&p="+nextpage),
              Response.class);	
          for ( Result r : resp.getResults()) {
            r.url = r.url.replace("view", "raw");
            String str = Utils.readStringFromURL(r.url);
            Utils.savePlainFile(Conf.root+Conf.root_key+"/"+r.id, str);
            TimeUnit.SECONDS.sleep(1);
          }

          nextpage = resp.nextpage;
        }
      }
    }
  }

  public static void createBaseFromSO() throws JsonIOException, JsonSyntaxException, IOException {
    File f = new File(Conf.answers_output.replace("?", "_all"));
    Validate v = new Validate();
//    Conf.answers = gson.fromJson(new FileReader(f), Conf.gson_answers);

    f = new File(Conf.posts_output.replace("?", "_all"));
    ArrayList<Row> posts_arr = new ArrayList<Row>(Arrays.asList(gson.fromJson(new FileReader(f), Row[].class)));
//    for (Row p : gson.fromJson(new FileReader(f), Row[].class)) {
//      Conf.posts.put(p.getScore(), p);
//    }

//    for ( Entry<Integer, ArrayList<Row>> a : Conf.answers.entrySet() ) {
//      Collections.sort(a.getValue(), Utils.comparator_score_desc);
//      for (Row k : a.getValue()) {
//        k.init(v);
//        k.setPost(Conf.posts.get(k.getParentId()));
//      }
//    }
    Collections.sort(posts_arr, Utils.cmpr);
    Utils.saveJsonFile(Conf.posts_output.replace("?", "_all"), posts_arr);
//    Dt dt = new Dt();
//    dt.export();
  }
  
}