package tmt.conf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

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
  
  public static <K, V> HashMap<K, V> sortByValue(Map<K, V> map) {
    List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
    Collections.sort(list, new Comparator<Object>() {
      @SuppressWarnings("unchecked")
      public int compare(Object o1, Object o2) {
        return ((Comparable<V>) ((Map.Entry<K, V>) (o2)).getValue()).compareTo(((Map.Entry<K, V>) (o1)).getValue());
      }
    });

    HashMap<K, V> result = new LinkedHashMap<>();
    for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
      Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
      result.put(entry.getKey(), entry.getValue());
    }

    return result;
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
  
  public static boolean compare(String str1, String str2) {
    return (str1 == null ? str2 == null : str1.equals(str2));
  }
  

  public static int get_percent_diff(double now, double ago) {
    double min;
    double max;

    if (now > ago) {
      min = ago;
      max = now;
    } else {
      min = now;
      max = ago;      
    }

    if ( (max - min) > min )
      return 100;
    else
      return (int)((max - min) / (min / 100));
  }

  public static void writeFile(List<String> lines, String filename, boolean append) throws IOException {
    Path file = Paths.get(filename);
    if (append)
      Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
    else
      Files.write(file, lines, Charset.forName("UTF-8"));
  }

  public static void writeFile1(String lines, String filename, boolean append) throws IOException {
    File file = new File(filename);
    FileWriter fr = null;
    BufferedWriter br = null;
    String dataWithNewLine=lines+System.getProperty("line.separator");
    try{
      fr = new FileWriter(file);
      br = new BufferedWriter(fr);
      for(int i = 1; i>0; i--){
        br.write(dataWithNewLine);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }finally{
      try {
        br.close();
        fr.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static String readUrl(String urlString) throws Exception {
    BufferedReader reader = null;
    try {
      URL url = new URL(urlString);
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer buffer = new StringBuffer();
      int read;
      char[] chars = new char[1024];
      while ((read = reader.read(chars)) != -1)
        buffer.append(chars, 0, read); 

      return buffer.toString();
    } finally {
      if (reader != null)
        reader.close();
    }
  }

  public static String sendPost(String string, String url_) throws IOException {
    HttpClient httpclient = HttpClients.createDefault();
    HttpPost httppost = new HttpPost(url_);

    // Request parameters and other properties.
    List<NameValuePair> params = new ArrayList<NameValuePair>(2);
    params.add(new BasicNameValuePair("context", string));
//    params.add(new BasicNameValuePair("param-2", "Hello!"));
    httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

    //Execute and get the response.
    HttpResponse response = httpclient.execute(httppost);
    HttpEntity entity = response.getEntity();

    String res = "";
    if (entity != null) {
      try (InputStream instream = entity.getContent()) {
        Scanner s = new Scanner(instream).useDelimiter("\\A");
        res += s.hasNext() ? s.next() : "";
      }
    }
    
    return res;
  }
}
