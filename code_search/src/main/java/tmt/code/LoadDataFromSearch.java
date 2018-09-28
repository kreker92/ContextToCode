package tmt.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
  static String key = "Class.forName";
  static String query = "DriverManager%20getConnection%20query";
  static String root_key = "cs/DriverManager_getConnection_Query";
  static String root = "/root/ContextToCode/output/";

  public static void main(String[] args) throws JsonSyntaxException, IOException, InterruptedException  {
    gson = new Gson();
    createBaseFromSO();
    //      dataFromCodeSearch();
    //      loadCodeSearch();
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
    Collections.sort(posts_arr, cmpr);
    Utils.saveJsonFile(Conf.posts_output.replace("?", "_all"), posts_arr);
//    Dt dt = new Dt();
//    dt.export();
  }
  
  public static Comparator<Row> cmpr = new Comparator<Row>() {
    public int compare(Row o1, Row o2) {
      return o2.getScore().compareTo(o1.getScore()); 
    }
  };
}