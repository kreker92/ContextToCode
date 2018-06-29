package tmt.conf;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.reflect.TypeToken;

import tmt.code.snippets.stackoverflow.Row;

public class Conf {
  public static HashMap<Integer, Row> posts = new HashMap<>();
  public static HashMap<Integer, ArrayList<Row>> answers = new HashMap<Integer, ArrayList<Row>>();
  public static Type gson_answers = new TypeToken<HashMap<Integer, ArrayList<Row>>>() {}.getType();    

  
  public static int chunk = 1000000;
  
  public static String input = "/root/stackoverflow/Posts.xml";
  
  public static String posts_output = "/root/stackoverflow/data/posts?.json";
  public static String answers_output = "/root/stackoverflow/data/answers?.json";
  
  public static HashMap<Row, ArrayList<Row>> complete;
}
