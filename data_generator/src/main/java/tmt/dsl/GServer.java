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
import tmt.dsl.formats.context.in.ElementInfo;
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
    		g.loadCodeSearch(null, g.ASC, 3, template, null);
    	
    	g.setTrainAndTest(null, templates);
    }
    else if (swtch == EVAL) {
      res = g.eval();
    }
    else if (swtch == PATTERN) {
    	//Template t = new Template(new ArrayList<String>(Arrays.asList("PsiType:Intent", "PsiIdentifier:getAction")), new ArrayList<String>(Arrays.asList("PsiType:Intent", "PsiIdentifier:getAction")), "Retrieve the general action to be performed, such as ACTION_VIEW.", "android.content.intent/ast/", "9"); 
    	//g.loadCodeSearch(null, g.ASC, 5, t, null);
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
  
  /**
  
  InnerContext ic = new InnerContext("truekey", "2");
	  ic.elements.add(new ElementInfo("ast_type", "PsiType:Intent"));
	  ic.elements.add(new ElementInfo("ast_type", "PsiIdentifier:getAction"));
	  
	  InnerContext ic1 = new InnerContext("truekey", "4");
      ic1.elements.add(new ElementInfo("ast_type", "PsiType:Intent"));
      ic1.elements.add(new ElementInfo("ast_type", "PsiIdentifier:startActivity"));
      
   predict_connect_right 30 predict_connect_wrong 19 predict_not_connect_right 68 predict_not_connect_wrong 12
   
  */

  private static ArrayList<Template> getTemplates() {
	  ArrayList<Template> res = new ArrayList<>();
//      g.key = "intent.getAction()";
//      g.description = "Retrieve the general action to be performed, such as ACTION_VIEW.";
//
//      g.root = "/root/ContextToCode/data/datasets/android/";
//      g.root_key = "ast/";
//	   res.add(new Template("DriverManager.getConnection", "Open DB connection", "database/ast/parsed/", "8"));

	  Template t1 = new Template("Retrieve the general action to be performed, such as ACTION_VIEW", "android.content.intent/ast/");
	  
	  InnerContext ic = new InnerContext("truekey", "2");
	  ic.elements.add(new ElementInfo("ast_type", "PsiType:Intent"));
	  ic.elements.add(new ElementInfo("ast_type", "PsiIdentifier:getAction"));
	  
	  InnerContext ic1 = new InnerContext("truekey", "4");
      ic1.elements.add(new ElementInfo("ast_type", "PsiType:Intent"));
      ic1.elements.add(new ElementInfo("ast_type", "PsiIdentifier:startActivity"));

      InnerContext ic2 = new InnerContext("truekey", "3");
      ic2.elements.add(new ElementInfo("ast_type", "PsiType:Intent"));
      ic2.elements.add(new ElementInfo("ast_type", "PsiIdentifier:putExtra"));
      
	  t1.keys.add(ic);
	  t1.keys.add(ic1);
	  t1.keys.add(ic2);
	  
	  res.add(t1);
      
      /*Template t2 = new Template("Retrieve the general action to be performed, such as ACTION_VIEW", "database/ast2/");
	  
	  InnerContext ic1 = new InnerContext("truekey", "4");
	  ic1.elements.add(new ElementInfo("ast_type", "PsiReferenceExpression:DriverManager"));
	  ic1.elements.add(new ElementInfo("ast_type", "PsiIdentifier:getConnection"));
	  
	  InnerContext ic1 = new InnerContext("truekey", "4");
      ic1.elements.add(new ElementInfo("ast_type", "PsiType:Intent"));
      ic1.elements.add(new ElementInfo("ast_type", "PsiIdentifier:startActivity"));
	  
	  t2.keys.add(ic1);
	  //t1.keys.add(ic1);
	  
	  res.add(t2);*/
	  
	/*  Template t2 = new Template("Retrieve the general action to be performed, such as ACTION_VIEW", "android.content.intent/ast/");

	  InnerContext ic2 = new InnerContext("falsekey", "2");
	  ic2.elements.add(new ElementInfo("ast_type", "PsiType:Intent"));
	  ic2.elements.add(new ElementInfo("ast_type", "PsiIdentifier:getAction"));

	  InnerContext ic3 = new InnerContext("truekey", "4");
	  ic3.elements.add(new ElementInfo("ast_type", "PsiType:Intent"));
	  ic3.elements.add(new ElementInfo("ast_type", "PsiIdentifier:startActivity"));

	  t2.keys.add(ic2);
	  t2.keys.add(ic3);

	  res.add(t2); */
      
	//  res.add(new Template( new ArrayList<String>(Arrays.asList("PsiReferenceExpression:DriverManager", "PsiIdentifier:getConnection")), "Retrieve the general action to be performed, such as ACTION_VIEW", "database/ast2/", "3"));
	//  res.add(new Template( new ArrayList<String>(Arrays.asList("PsiType:Intent", "PsiIdentifier:getAction")), new ArrayList<String>(Arrays.asList("PsiType:Intent", "PsiIdentifier:startActivity")), "Retrieve the general action to be performed, such as ACTION_VIEW", "android.content.intent/ast/", "4"));
/*	  res.add(new Template(".startActivity(", "Launch a new activity", "android.app.activity/ast/", "9"));
	  res.add(new Template(".putExtra(", "Launch a new activity", "android.content.intent/ast/", "10"));
      res.add(new Template("activity.finish()", "Launch a new activity", "android.app.activity/ast/", "11"));
      res.add(new Template("new Intent()", "Launch a new activity", "android.content.intent/ast/", "12"));*/ 
	  return res;
  }
}
