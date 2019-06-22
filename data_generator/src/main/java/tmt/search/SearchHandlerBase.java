package tmt.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import tmt.conf.Conf;
import tmt.dsl.GServer;
import tmt.dsl.data.Utils;
import tmt.dsl.formats.context.in.InnerClass;
import tmt.dsl.formats.langs.JavaScriptAST;
import tmt.dsl.pumpkin.Pumpkin;

import com.google.gson.Gson;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.ResponseException;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public abstract class SearchHandlerBase extends HandlerBase {
  public NanoHTTPD.Response post(UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
    byte[] response = "".getBytes();
    Map<String, String> files = new HashMap<String, String>();

    try {
      session.parseBody(files);
    } catch (IOException ioe) {
    } catch (ResponseException re) {
    }

    try {
      HashMap<String, String> g = new Gson().fromJson(files.get("postData"), HashMap.class);
      
      LinkedHashMap<String, Tab> tabs = new LinkedHashMap<>();
      ArrayList<String> else_tabs = new ArrayList<>();
      ArrayList<String> main_tabs = new ArrayList<>();

      BufferedWriter writer = new BufferedWriter(new FileWriter("/root/ContextToCode/data_generator/lang_scripts/input/input.js"));
      writer.write(g.get("text").replace("�", ""));
      writer.close();

      // run the Unix "ps -ef" command
      // using the Runtime exec method:
      Process p = Runtime.getRuntime().exec("python /root/ContextToCode/data_generator/lang_scripts/js.py file");

      BufferedReader stdInput = new BufferedReader(new 
          InputStreamReader(p.getInputStream()));

      //      BufferedReader stdError = new BufferedReader(new 
      //           InputStreamReader(p.getErrorStream()));

      // read the output from the command
      /* BufferedWriter writer1 = new BufferedWriter(new FileWriter("/root/ContextToCode/data_generator/lang_scripts/input/input_parsed"));

      String s = new String();
      for (String line; (line = stdInput.readLine()) != null; s += line);

      writer1.write(s.replace(", 0", ""));
      writer1.close();*/

      ArrayList<InnerClass> arrayList = new ArrayList<InnerClass>(Arrays.asList
          (new JavaScriptAST("/root/ContextToCode/data_generator/lang_scripts/parsed").getClasses())); 
      arrayList.add(new Gson().fromJson(Utils.readFile("../data/datasets/stab.json"),  InnerClass.class));

      InnerClass[] code = new InnerClass[arrayList.size()];
      code = arrayList.toArray(code);

      ArrayList<HashMap<String, String>> res = GServer.router(GServer.INFERENCE, code);

      for (HashMap<String, String> found : res) {
        if (tabs.size() > 0)
          tabs.put(found.get("ast_type"), new Tab(found.get("found").equals("true") ? found.get("tab").replace("nav-link", "nav-link bg-info") : found.get("tab"), found.get("content")));
        else
          tabs.put(found.get("ast_type"), new Tab(found.get("found").equals("true") ? found.get("tab").replace("nav-link", "nav-link bg-info active") : found.get("tab").replace("nav-link", "nav-link active"), found.get("content").replace("tab-pane fade", "tab-pane fade show active")));

        if (found.get("found").equals("true")) 
          main_tabs.add(found.get("ast_type"));
        else
          else_tabs.add(found.get("ast_type"));
      } 
      
      HashMap<String, Object> output = new HashMap<>();
      output.put("tabs", tabs);
      output.put("else_tabs", else_tabs);
      output.put("main_tabs", main_tabs);
      
      response = new Gson().toJson(output).getBytes();
//      System.err.println(tabs);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return NanoHTTPD.newFixedLengthResponse(
        getStatus(),
        getMimeType(),
        new ByteArrayInputStream(response),
        response.length);
  }

  public static String readStringFromURL(String requestURL) throws IOException
  {
    try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
        StandardCharsets.UTF_8.toString()))
        {
      scanner.useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
        }
  }
}

class Tab {
  String content;
  String tab;
  
  
  public Tab(String tab_, String content_) {
    content = content_;
    tab = tab_;
  }
}