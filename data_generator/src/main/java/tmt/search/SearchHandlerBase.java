package tmt.search;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import tmt.dsl.GServer;
import tmt.dsl.data.Utils;
import tmt.dsl.formats.context.in.InnerClass;

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
      response = new Gson().toJson(GServer.router(GServer.INFERENCE, new Gson().fromJson(files.get("postData")+"",  InnerClass[].class))).getBytes();

    } catch (Exception e) {
      
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