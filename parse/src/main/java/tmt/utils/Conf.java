package tmt.utils;

import java.util.ArrayList;
import java.util.HashMap;

import tmt.stackoverflow.Row;

public class Conf {
  public static ArrayList<Row> posts = new ArrayList<Row>();
  public static HashMap<Integer, ArrayList<Row>> answers = new HashMap<Integer, ArrayList<Row>>();
  
  public static int chunk = 1000000;
  
  public static String input = "/root/stackoverflow/Posts.xml";
  
  public static String posts_output = "/root/stackoverflow/data/posts?.json";
  public static String answers_output = "/root/stackoverflow/data/answers?.json";
}
