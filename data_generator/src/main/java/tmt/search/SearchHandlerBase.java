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

      BufferedWriter writer = new BufferedWriter(new FileWriter("/root/ContextToCode/data_generator/lang_scripts/input/input.js"));
      writer.write(g.get("text").replace("�", ""));
      writer.close();

      // run the Unix "ps -ef" command
      // using the Runtime exec method:
      Process p = Runtime.getRuntime().exec("python /root/ContextToCode/data_generator/lang_scripts/js.py");

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
      
      for (String key : Conf.js_keys) 
        for (HashMap<String, String> found : res)
          if (key.equals(found.get("ast_type"))) {
            if (tabs.size() > 0)
              tabs.put(key, new Tab(found.get("content"), found.get("tab")));
            else
              tabs.put(key, new Tab(found.get("content").replace("tab-pane fade", "tab-pane fade show active"), found.get("tab").replace("nav-link", "nav-link active")));
            break;
          }
      
      response = new Gson().toJson(tabs).getBytes();
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
  
  
  public Tab(String content_, String tab_) {
    content = content_;
    tab = tab_;
  }
}