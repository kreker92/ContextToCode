package tmt.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.internal.LinkedTreeMap;

import tmt.stackoverflow.Row;

public class Utils {

  public static ArrayList<String> parse (String heystack, String delim1, String delim2){
    if (heystack != null) {
      String[] res = StringUtils.substringsBetween(heystack, delim1, delim2);

      if (res != null)
        return new ArrayList<String>(Arrays.asList(res));
    }

    return new ArrayList<String>();
  }
 
//  public static HashMap<Integer, ArrayList<Row>> loadAnswers(HashMap<Integer, ArrayList<LinkedTreeMap<String, String>>> fromJson) {
//    System.err.println(fromJson.get("796508").get(0).keySet());
//    return null;
//  }
}
