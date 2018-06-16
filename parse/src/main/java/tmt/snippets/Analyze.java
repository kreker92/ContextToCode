package tmt.snippets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import tmt.stackoverflow.Row;
import tmt.utils.Conf;
import tmt.utils.Utils;

public class Analyze {
  static HashMap<Integer, ArrayList<Row>> answers;
  static HashMap<Row, ArrayList<Row>> complete;

  public static void main(String[] args) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    Gson gson = new Gson();
    
    File f = new File(Conf.answers_output.replace("?", "_all"));
    answers = gson.fromJson(new FileReader(f), Conf.gson_answers);
        ArrayList<Row> posts = new ArrayList<Row>();
        f = new File(Conf.posts_output.replace("?", "_all"));
            posts = gson.fromJson(new FileReader(f), Row[].class);

    for (Row p : posts)
       complete.put(p., answers.get(p)) 
  }
}