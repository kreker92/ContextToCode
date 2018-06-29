package tmt.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import tmt.code.snippets.stackoverflow.Row;
import tmt.conf.Conf;
import tmt.conf.Utils;
import tmt.export.Detectum;

public class Analyze {
  public static void main(String[] args) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    Gson gson = new Gson();

    File f = new File(Conf.answers_output.replace("?", "_all"));
    Conf.answers = gson.fromJson(new FileReader(f), Conf.gson_answers);

    f = new File(Conf.posts_output.replace("?", "_all"));
    for (Row p : gson.fromJson(new FileReader(f), Row[].class)) {
      Conf.posts.add(p);
    }

    for ( Entry<Integer, ArrayList<Row>> a : Conf.answers.entrySet() ) {
      Collections.sort(a.getValue(), Utils.comparator_score_desc);
      for (Row k : a.getValue())
        k.parse();
    }
    
    Detectum detectum = new Detectum();
    detectum.export();
  }
}