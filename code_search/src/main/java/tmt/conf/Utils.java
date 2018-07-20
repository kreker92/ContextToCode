package tmt.conf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
      str += inputLine+"\n";
    in.close();
    return str;
  }
  
  public static String readFile(String where) throws IOException {
    String everything = "";
    BufferedReader br = new BufferedReader(new FileReader(where));
    try {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        everything = sb.toString();
    } finally {
        br.close();
    }
    return everything;
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
 
  public static void saveJsonFile (String where, Object what) throws IOException {
    try (Writer writer = new FileWriter(where)) {
      Gson gson = new GsonBuilder().create();
      gson.toJson(what, writer);
    }
  }
  
  public static void savePlainFile (String name, String text) throws IOException {
	  try (PrintWriter out = new PrintWriter(name)) {
		  out.println(text);
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
