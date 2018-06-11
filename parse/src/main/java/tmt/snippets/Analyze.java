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

  public static void main(String[] args) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    Gson gson = new Gson();
    Type fooType = new TypeToken<HashMap<Integer, ArrayList<Row>>>() {}.getType();    

    int count = 1;
    File f = new File(Conf.answers_output.replace("?", count+""));
    while(f.exists() && !f.isDirectory()) { 
      answers = gson.fromJson(new FileReader(f), fooType);

      for ( Entry<Integer, ArrayList<Row>> r : answers.entrySet())
        for ( Row p : r.getValue()) {
          p.parse();
          if (!p.getCode().isEmpty())
            System.err.println(count);
        }
      count ++; 
      f = new File(Conf.answers_output.replace("?", count+""));
    }
  }
}