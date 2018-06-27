package tmt.conf;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
 
  public static void save (String where, Object what) throws IOException {
    try (Writer writer = new FileWriter(where)) {
      Gson gson = new GsonBuilder().create();
      gson.toJson(what, writer);
    }
  }
//  public static HashMap<Integer, ArrayList<Row>> loadAnswers(HashMap<Integer, ArrayList<LinkedTreeMap<String, String>>> fromJson) {
//    System.err.println(fromJson.get("796508").get(0).keySet());
//    return null;
//  }
}
