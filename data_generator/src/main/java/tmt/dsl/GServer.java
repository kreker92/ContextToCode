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
import tmt.dsl.formats.context.Vector;
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
    
    ArrayList<Template> templates = getTemplates();

    if (swtch == LEARN) {
    	for (Template template : templates) 
    		g.loadCodeSearch(null, g.ASC, 3, template);
    	
    	g.setTrainAndTest(null, templates);
    }
    else if (swtch == EVAL) {
      res = g.eval();
    }
    else if (swtch == PATTERN) {
    	for (Template template : templates) 
    		g.loadCodeSearch(null, g.ASC, 5, template);
    }
    else if (swtch == INFERENCE) {
      InnerContext[] code = null;
      if (data != null) {
        code = new Gson().fromJson(data, InnerContext[].class);
      }

      res = g.setTrainAndTest(code, null);
    }

    return res;
  }

  private static ArrayList<Template> getTemplates() {
	  ArrayList<Template> res = new ArrayList<>();
//      g.key = "intent.getAction()";
//      g.description = "Retrieve the general action to be performed, such as ACTION_VIEW.";
//
//      g.root = "/root/ContextToCode/data/datasets/android/";
//      g.root_key = "ast/";
	  res.add(new Template("intent.getAction()", "Retrieve the general action to be performed, such as ACTION_VIEW.", "android/ast/", "9"));
	  res.add(new Template("DriverManager.getConnection", "Open DB connection", "database/ast/parsed/", "8"));
	  return res;
  }
}
