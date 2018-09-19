package tmt.search;

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

import com.google.gson.Gson;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public abstract class SearchHandlerBase extends HandlerBase {
  public NanoHTTPD.Response get(UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
    byte[] response = null;
    try {
      response = (GServer.router(GServer.INFERENCE)+"").getBytes();
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