package tmt.snippets;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import tmt.stackoverflow.Row;
import tmt.utils.Utils;

public class Analyze {
  private static Row[] java_rows;

  public static void main(String[] args) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
    Gson gson = new Gson();
    java_rows = gson.fromJson(new FileReader("/root/stackoverflow/output.json"), Row[].class);
    
    int count = 0;
    for ( Row r : java_rows) {
      if ( count > 100 )
        break;
      count ++;
     System.err.println(Utils.parse(r.getBody(), "<code>", "</code>"));
    }
  }
}
