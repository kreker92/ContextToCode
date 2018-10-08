package tmt.code.snippets.stackoverflow;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.reflect.TypeToken;

public class Conf {
  public static HashMap<Integer, Row> posts = new HashMap<>();
  public static HashMap<Integer, ArrayList<Row>> answers = new HashMap<Integer, ArrayList<Row>>();
  public static Type gson_answers = new TypeToken<HashMap<Integer, ArrayList<Row>>>() {}.getType();    

  
  public static int chunk = 1000000;
  
  public static String input = "/root/stackoverflow/Posts.xml";
  
  public static String posts_output = "../data/snippets/posts?.json";
  public static String answers_output = "../data/snippets/answers?.json";
  public static String root = "../data/datasets/";
  
  public static String key = "Class.forName";
  public static String query = "android.app.AlertDialog";
  public static String root_key = "/android/cs/";

  
  public static HashMap<Row, ArrayList<Row>> complete;
}
