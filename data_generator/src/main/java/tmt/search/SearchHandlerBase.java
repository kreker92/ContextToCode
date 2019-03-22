package tmt.search;

import java.util.Arrays;
import java.util.HashMap;
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

import tmt.dsl.GServer;
import tmt.dsl.data.Utils;
import tmt.dsl.formats.context.in.InnerClass;
import tmt.dsl.formats.langs.JavaScriptAST;

import com.google.gson.Gson;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.ResponseException;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public abstract class SearchHandlerBase extends HandlerBase {
  public NanoHTTPD.Response post(UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
    byte[] response = null;
    Map<String, String> files = new HashMap<String, String>();

    try {
      session.parseBody(files);
    } catch (IOException ioe) {
    } catch (ResponseException re) {
    }

    try {
      HashMap<String, String> g = new Gson().fromJson(files.get("postData"), HashMap.class);
      
      BufferedWriter writer = new BufferedWriter(new FileWriter("/root/ContextToCode/data_generator/lang_scripts/input/input.js"));
      writer.write(g.get("text"));
      writer.close();
      
      // run the Unix "ps -ef" command
      // using the Runtime exec method:
      Process p = Runtime.getRuntime().exec("/root/ContextToCode/data_generator/lang_scripts/js_parser/bin/js_parser.js /root/ContextToCode/data_generator/lang_scripts/input/input.js");
      
      BufferedReader stdInput = new BufferedReader(new 
           InputStreamReader(p.getInputStream()));

//      BufferedReader stdError = new BufferedReader(new 
//           InputStreamReader(p.getErrorStream()));

      // read the output from the command
      BufferedWriter writer1 = new BufferedWriter(new FileWriter("/root/ContextToCode/data_generator/lang_scripts/input/input_parsed"));
      
      String s = new String();
      for (String line; (line = stdInput.readLine()) != null; s += line);
      
      writer1.write(s.replace(", 0", ""));
      writer1.close();

      ArrayList<InnerClass> arrayList = new ArrayList<InnerClass>(Arrays.asList
          (new JavaScriptAST("/root/ContextToCode/data_generator/lang_scripts/input/input_parsed").getClasses())); 
      arrayList.add(new Gson().fromJson(Utils.readFile("../data/datasets/stab.json"),  InnerClass.class));

      System.err.println(arrayList);
      response = new Gson().toJson(GServer.router(GServer.INFERENCE, arrayList.toArray(new InnerClass[arrayList.size()]))).getBytes();

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