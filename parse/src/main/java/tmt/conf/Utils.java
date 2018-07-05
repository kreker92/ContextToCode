package tmt.conf;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import tmt.code.snippets.stackoverflow.Row;

public class Utils {

  public static ArrayList<String> parse (String heystack, String delim1, String delim2){
    if (heystack != null) {
      String[] res = StringUtils.substringsBetween(heystack, delim1, delim2);

      if (res != null)
        return new ArrayList<String>(Arrays.asList(res));
    }

    return new ArrayList<String>();
  }
  
  public static String searchGhub(String query) {
    String str = null;
    try {
      str = readStringFromURL("https://api.github.com/search/code?q="+URLEncoder.encode(query, "UTF-8")+"+language:java+user:google&client_id=1d37f4c170ab6b715f4b1d37f4c170ab6b715f4b&client_secret=3b877aec7dd7ba8fe51f2b8d717a074e3b540bf3");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return str;
  }
  
  public static String readStringFromURL(String requestURL) throws IOException
  {
    String str = "";
    System.err.println(requestURL);
    URL oracle = new URL(requestURL);
    URLConnection yc = oracle.openConnection();
    BufferedReader in = new BufferedReader(new InputStreamReader(
                            yc.getInputStream()));
    String inputLine;
    while ((inputLine = in.readLine()) != null) 
      str += inputLine;
    in.close();
    return str;
  }

  /*
   * Function formatResponse
   *   Given a Stack Overflow response post, replace all XML escape character codes with the
   *   characters they represent.
   *   
   *   Input: String post - Stack Overflow answer, or block of text with XML escape character codes.
   *   Returns: String - formatted post with XML escape character codes removed.
   */
  public static String formatResponse(String post) {
      //Fix xml reserved escape chars:
      post = post.replaceAll("&;quot;", "\"");
      post = post.replaceAll("&quot;", "\"");
      post = post.replaceAll("&quot", "\"");
      post = post.replaceAll("&;apos;", "'");
      post = post.replaceAll("&apos;", "'");
      post = post.replaceAll("&apos", "'");
      post = post.replaceAll("&;lt;","<");
      post = post.replaceAll("&lt;","<");
      post = post.replaceAll("&lt", "<");
      post = post.replaceAll("&;gt;",">");
      post = post.replaceAll("&gt;", ">");
      post = post.replaceAll("&gt", ">");
      post = post.replaceAll("&;amp;", "&");
      post = post.replaceAll("&amp;", "&");
      post = post.replaceAll("&amp", "&");
      return post;
  }
 
  public static void save (String where, Object what) throws IOException {
    try (Writer writer = new FileWriter(where)) {
      Gson gson = new GsonBuilder().create();
      gson.toJson(what, writer);
    }
  }
  
  public static Comparator<Row> comparator_score_desc = new Comparator<Row>() {
    public int compare(Row o1, Row o2) {
      return o2.getScore().compareTo(o1.getScore());
    }
  };
//  public static HashMap<Integer, ArrayList<Row>> loadAnswers(HashMap<Integer, ArrayList<LinkedTreeMap<String, String>>> fromJson) {
//    System.err.println(fromJson.get("796508").get(0).keySet());
//    return null;
//  }
}
