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
    
    // create new file
    File train = new File("/root/ContextToCode/data/datasets/android-copy/cs/");
                            
    // array of files and directory
    ArrayList<String> trains = new ArrayList<String>(Arrays.asList(train.list()));
    for (String t : trains ) {
    	System.err.println(t);
    	dataFromCodeSearch(trains, t);
    }
//          loadCodeSearch();
  }

  private static void dataFromCodeSearch(ArrayList<String> files, String file) throws JsonSyntaxException, IOException, InterruptedException, KeyManagementException, NoSuchAlgorithmException {
   
    String[] queries_raw = Utils.readFile("/root/ContextToCode/data/datasets/android-copy/cs/"+file).split(";");
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
            
            boolean found = false;
            for (String f : files)
            	if (f.contains(r.id+""))
            		found = true;
            		
            if (!found) {
            	Utils.savePlainFile(Conf.root+Conf.root_key+"/"+r.id, str);
//                System.err.println(r.id);
            }
            
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