package tmt.dsl;

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
  
  public static void main(String[] args) throws Exception{
    if (args[0].equals("learn")) 
      router(LEARN, "");
    else if (args[0].equals("eval"))
      router(EVAL, "");
    else if (args[0].equals("inference"))
      router(INFERENCE, "");
  }
  
  public static int router(int swtch, String data) throws Exception {
    int res = 0;
    
    Generator g = new Generator(); 
    
    PrintStream fileStream = new PrintStream("../log/log.txt");
    System.setOut(fileStream);
    
    g.limit = 15000;
    g.dsl_buffer = "/root/data/dsl_buffer/";
    g.gson  = new Gson();
    g.good_types = new ArrayList( Arrays.asList( g.gson.fromJson(new FileReader("/root/ContextToCode/output/conf/good_types"), String[].class)) );
    g.bad_types = new ArrayList( Arrays.asList( g.gson.fromJson(new FileReader("/root/ContextToCode/output/conf/bad_types"), String[].class)) );
    
    
    if (swtch == LEARN) {
      g.key = "DriverManager.getConnection";
      g.query = "DriverManager%20getConnection%20query";
      
      g.root = "/root/ContextToCode/output/";
      g.root_key = "cs/parsed";
      g.vectors = "/root/ContextToCode/output/funcs/vectors";
      
      g.setTrainAndTest(null);
    }
    else if (swtch == EVAL) {
      g.root_key = "cs/parsed";
      g.root = "/root/ContextToCode/output/";

      g.key = null;
      g.model = "";
      
      res = g.eval();
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
