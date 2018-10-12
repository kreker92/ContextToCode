package tmt.dsl;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;

import tmt.dsl.data.Generator;
import tmt.dsl.data.Utils;
import tmt.dsl.formats.context.in.InnerContext;

public class GServer {

  public static final int LEARN = 1;
  public static final int EVAL  = 2;
  public static final int INFERENCE  = 3;
  public static final int PATTERN  = 4;

  public static void main(String[] args) throws Exception{
    if (args[0].equals("learn")) 
      router(LEARN, "");
    else if (args[0].equals("eval"))
      router(EVAL, "");
    else if (args[0].equals("inference"))
      router(INFERENCE, "");
    else if (args[0].equals("pattern"))
      router(PATTERN, "");
  }

  public static int router(int swtch, String data) throws Exception {
    int res = 0;

    Generator g = new Generator(); 

    PrintStream fileStream = new PrintStream(
        new FileOutputStream("../log/log.txt", true)); 
    System.setOut(fileStream);

    g.limit = 15000;
    g.dsl_buffer = "/root/data/dsl_buffer/";
    g.gson  = new Gson();
    g.good_types = new ArrayList( Arrays.asList( g.gson.fromJson(new FileReader("/root/ContextToCode/output/conf/good_types"), String[].class)) );
    g.bad_types = new ArrayList( Arrays.asList( g.gson.fromJson(new FileReader("/root/ContextToCode/output/conf/bad_types"), String[].class)) );


    if (swtch == LEARN) {
      g.key = "intent.getAction()";
      g.description = "Retrieve the general action to be performed, such as ACTION_VIEW.";

      g.root = "/root/ContextToCode/data/datasets/android/";
      g.root_key = "ast/";
      g.vectors = "/root/ContextToCode/output/funcs/vectors";

      
      g.snippetize(); 
      g.setTrainAndTest(null);
    }
    else if (swtch == EVAL) {
      g.root_key = "cs/parsed";
      g.root = "/root/ContextToCode/output/";

      g.key = null;
      g.model = "";

      res = g.eval();
    }
    else if (swtch == PATTERN) {
      g.key = "intent.getAction()";
      g.description = "Retrieve the general action to be performed, such as ACTION_VIEW.";

      g.root = "/root/ContextToCode/data/datasets/android/";
      g.root_key = "ast/";
      g.vectors = "/root/ContextToCode/output/funcs/vectors";

      g.loadCodeSearch(null, g.ASC, 5);
    }
    else if (swtch == INFERENCE) {
      //      g.root_key = "/"+new Timestamp(System.currentTimeMillis())+"/";
      g.root_key = "/test/";
      g.root = "/root/ContextToCode/output/buffer";
      g.vectors = g.root+g.root_key+"/vectors";

      g.key = null;
      g.model = "";

      InnerContext[] code = null;
      if (data != null) {
        code = new Gson().fromJson(data, InnerContext[].class);
      }

      res = g.setTrainAndTest(code);
    }

    return res;
  }
}
