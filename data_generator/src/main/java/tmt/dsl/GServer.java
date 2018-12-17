package tmt.dsl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.google.gson.Gson;

import tmt.conf.Utils;
import tmt.dsl.data.Generator;
import tmt.dsl.executor.info.Step;
import tmt.dsl.formats.context.Vector;
import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;

public class GServer {

  public static final int LEARN = 1;
  public static final int EVAL  = 2;
  public static final int INFERENCE  = 3;
  public static final int PATTERN  = 4;

  public static void main(String[] args) throws Exception{
    if (args[0].equals("learn")) 
      router(LEARN, null);
    else if (args[0].equals("eval"))
      router(EVAL, null);
    else if (args[0].equals("inference")) {
      InnerClass[] code = new Gson().fromJson(Utils.readFile("/root/ContextToCode/data/datasets/post"), InnerClass[].class);
      router(INFERENCE, code);
    }
    else if (args[0].equals("pattern"))
      router(PATTERN, null);
  }

  public static ArrayList<HashMap<String, String>> router(int swtch, InnerClass[] code) throws Exception {
    ArrayList<HashMap<String, String>> res = null;

    Generator g = new Generator(); 

    PrintStream fileStream = new PrintStream(
        new FileOutputStream("../log/log.txt", true)); 
    System.setOut(fileStream);
    
    ArrayList<Classifier> templates = getTemplates();

    if (swtch == LEARN) 
      doLearn(g, templates.get(0));
    else if (swtch == EVAL) 
      doEval(g, templates.get(0));
//    	g.loadCode(null, g.ASC, 3, templates.get(0), null);
//
//    	ArrayList<HashMap<Integer, Step>> out = g.setTrainAndTest(templates.get(0));
//
//    	res = g.filter_through_npi(out, templates.get(1));
    
    else if (swtch == PATTERN) {
//      doPattern(g, templates.get(0));
    }
    else if (swtch == INFERENCE) 
      doInference(g, templates.get(0));    
//      for ( InnerClass c : code )
//        c.executor_command = "1";
//    		
//      g.loadCode(code, g.ASC, 4, templates.get(0), null);
//      
//      ArrayList<HashMap<Integer, Step>> out = g.setTrainAndTest(templates.get(0));
//      
//      res = g.filter_through_npi(out, templates.get(1));
    

    return res;
  }
  
  private static void doInference(Generator g, Classifier classifier) {
    // TODO Auto-generated method stub
    
  }

  private static void doEval(Generator g, Classifier t) throws Exception {
    
    t.blocking = false;
    
    File[] files = new File(g.root+t.folder).listFiles();
    int count = 0;
    
    for (File f : files) {
      System.err.println(f.getPath());

      InnerClass[] code = new Gson().fromJson(Utils.readFile(f.getPath()), InnerClass[].class);
      g.loadCode(code, g.ASC, t);
      
      for (int line = code.length-1; line >= 0; line --) {
        ArrayList<Vector[]> res = new ArrayList<>();
        HashSet<String> commands = new HashSet<>();
        t.clear();
        if (!code[line].executor_command.equals("1") && line == 165) {
          System.err.println(line+"@");
          g.iterateCode(Arrays.copyOfRange(code, 0, line), t, f.getPath(), commands, res, 5);
          g.hotEncode(commands, g.root+"hots", res, t);

          ArrayList<HashMap<Integer, Step>> info = g.setTrainAndTest(t);
          System.err.println(res+"@");
          ArrayList<HashMap<String, String>> output = g.filter_through_npi(info, t, code[line].executor_command);

          count ++;

         // if (count > 21)
            System.exit(1);
        }
      }
    }
  }

  private static void doLearn(Generator g, Classifier t) throws Exception {
    ArrayList<Vector[]> res = new ArrayList<>();
    HashSet<String> commands = new HashSet<>();
    PopularCounter popular = new PopularCounter(g.bad_types);
    
    File file = new File(g.root+"/context.json"); 
    file.delete();
    file = new File(g.root+"/log.json"); 
    file.delete();
    file = new File(g.root+"/pop_comm"); 
    file.delete();
    file = new File(g.root+"/pop_lines"); 
    file.delete();
    
    File[] files = new File(g.root+t.folder).listFiles();
    for (File f : files) {
      System.err.println(f.getPath());

      InnerClass[] code = new Gson().fromJson(Utils.readFile(f.getPath()), InnerClass[].class);
      g.loadCode(code, g.ASC, t);
      
      g.iterateCode(code, t, f.getPath(), commands, res, 3);

      for ( InnerClass c : code )
        popular.add(c);
    }
    
    g.hotEncode(commands, g.root+"hots", res, t);
    
    Utils.writeFile1(Utils.sortByValue(popular.ast_types).toString(), g.root+"/pop_lines", false);
    Utils.writeFile1(Utils.sortByValue(popular.commands).toString(), g.root+"/pop_comm", false);

    g.setTrainAndTest(t);
//    ArrayList<HashMap<Integer, Step>> out = new ArrayList<>();
//    
//    for (int i = 20; i > 10; i--) {
//      out.add(out_raw.get(i));
//      res = g.filter_through_npi(out, templates.get(1));
//      out.clear();
//    }
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

  private static ArrayList<Classifier> getTemplates() {
	  ArrayList<Classifier> res = new ArrayList<>();
//      g.key = "intent.getAction()";
//      g.description = "Retrieve the general action to be performed, such as ACTION_VIEW.";
//
//      g.root = "/root/ContextToCode/data/datasets/android/";
//      g.root_key = "ast/";
//	   res.add(new Template("DriverManager.getConnection", "Open DB connection", "database/ast/parsed/", "8"));

	  Classifier t1 = new Classifier("android_crossvalidation/ast");
	  
	  Classifier t2 = new Classifier("android/ast1");
	  
	  InnerClass ic = new InnerClass("falsekey", "2");
	  ic.elements.add(new ElementInfo("ast_type", "PsiType:String", null));
      ic.elements.add(new ElementInfo("ast_type", "PsiIdentifier:equals", null)); 
	  
	  InnerClass ic1 = new InnerClass("falsekey", "3");
      ic1.elements.add(new ElementInfo("ast_type", "PsiType:StringBuilder", null));
      ic1.elements.add(new ElementInfo("ast_type", "PsiIdentifier:append", null)); 

      InnerClass ic2 = new InnerClass("truekey", "4");
      ic2.elements.add(new ElementInfo("ast_type", "PsiType:PrintWriter", null));
      ic2.elements.add(new ElementInfo("ast_type", "PsiIdentifier:print", null));
      
      InnerClass ic3 = new InnerClass("truekey", "5");
      ic3.elements.add(new ElementInfo("type", "NEW_EXPRESSION", "new Intent("));
      ic3.elements.add(new ElementInfo("type", "JAVA_CODE_REFERENCE", "Intent"));
      
      InnerClass ic4 = new InnerClass("truekey", "6"); 
      ic4.elements.add(new ElementInfo("ast_type", "PsiType:View", null));
      ic4.elements.add(new ElementInfo("ast_type", "PsiIdentifier:findViewById", null));
      LinkedHashMap<String, String> temp4_1 = new LinkedHashMap<>();
      temp4_1.put("literal1","mView = ");
      temp4_1.put("stab_req","PsiType:View");
      temp4_1.put("literal2",".findViewById(int id))");
      ic4.scheme.add(temp4_1);
      ic4.description = " Finds the first descendant view with the given ID, the view itself if the ID matches getId(), or null if the ID is invalid (< 0) or there is no matching view in the hierarchy.";
      
      InnerClass ic5 = new InnerClass("truekey", "7"); 
      ic5.elements.add(new ElementInfo("ast_type", "PsiType:TextView", null));
      ic5.elements.add(new ElementInfo("ast_type", "PsiIdentifier:findViewById", null));
      LinkedHashMap<String, String> temp5_1 = new LinkedHashMap<>();
      temp5_1.put("literal1","mTextView = ");
      temp5_1.put("stab_req","PsiType:TextView");
      temp5_1.put("literal2",".findViewById(int id))");
      ic5.scheme.add(temp5_1);
      ic5.description = " Finds the first descendant view with the given ID, the view itself if the ID matches getId(), or null if the ID is invalid (< 0) or there is no matching view in the hierarchy.";

      InnerClass ic6 = new InnerClass("truekey", "8"); 
      ic6.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
      ic6.elements.add(new ElementInfo("ast_type", "PsiIdentifier:getString", null));
      LinkedHashMap<String, String> temp6_1 = new LinkedHashMap<>();
      temp6_1.put("literal1","String mCursorString = ");
      temp6_1.put("stab_req","PsiType:Cursor");
      temp6_1.put("literal2",".getString(int id))");
      ic6.scheme.add(temp6_1);
      ic6.description = " Returns the value of the requested column as a String. ";

      
      InnerClass ic7 = new InnerClass("falsekey", "9"); 
      ic7.elements.add(new ElementInfo("ast_type", "PsiType:TextView", null));
      ic7.elements.add(new ElementInfo("ast_type", "PsiIdentifier:setText", null));
      LinkedHashMap<String, String> temp7_1 = new LinkedHashMap<>();
      temp7_1.put("stab_req","PsiType:TextView");
      temp7_1.put("literal1",".setText(int id))");
      ic7.scheme.add(temp7_1);
      ic7.description = " Sets the text to be displayed using a string resource identifier. ";
      
      InnerClass ic9 = new InnerClass("truekey", "9"); 
      ic9.elements.add(new ElementInfo("ast_type", "PsiType:TextView", null));
      ic9.elements.add(new ElementInfo("ast_type", "PsiIdentifier:setText", null));
      LinkedHashMap<String, String> temp9_1 = new LinkedHashMap<>();
      temp9_1.put("stab_req","PsiType:TextView");
      temp9_1.put("literal1",".setText(int id))");
      ic9.scheme.add(temp7_1);
      ic9.description = " Sets the text to be displayed using a string resource identifier. ";

      InnerClass ic8 = new InnerClass("truekey", "10"); 
      ic8.elements.add(new ElementInfo("ast_type", "PsiType:Context", null));
      ic8.elements.add(new ElementInfo("ast_type", "PsiIdentifier:getResources", null));
      LinkedHashMap<String, String> temp8_1 = new LinkedHashMap<>();
      temp8_1.put("literal1","Resources mResource = ");
      temp8_1.put("stab_req","PsiType:Context");
      temp8_1.put("literal2",".getResources())");
      ic8.scheme.add(temp8_1);
      ic8.description = " Returns a Resources instance for the application's package. ";

      t2.classes.add(ic4);
//      t2.classes.add(ic3);
//      t2.classes.add(ic1);
//      t2.classes.add(ic6);
//      t2.classes.add(ic7);
//      t2.classes.add(ic5);
//      t2.classes.add(ic8);
//      t2.classes.add(ic);
//      
//      t1.classes.add(ic3);
//      t1.classes.add(ic5);
//      t1.classes.add(ic9);
	  
//	  res.add(t1);
	  res.add(t2);
      
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

    
    class PopularCounter {
      public HashMap<String, Integer> ast_types = new HashMap<>();
      public HashMap<String, Integer> commands = new HashMap<>();
      public ArrayList<String> bad_types;

      public PopularCounter(ArrayList<String> bad_types_) {
        bad_types = bad_types_;
      }

      public void add(InnerClass c) {
        String prev_type = "";
        for (ElementInfo e : c.elements ) {
          if (e.ast_type != null && !e.ast_type.isEmpty()) {
            //        PopularType t1 = new PopularType(c, bad_types);
            if (e.ast_type.contains("PsiType:")) {
              prev_type = e.ast_type;
              if (ast_types.containsKey(e.ast_type)) 
                ast_types.put(e.ast_type, ast_types.get(e.ast_type)+1);
              else 
                ast_types.put(e.ast_type, 1);
            } else if (e.ast_type.contains("PsiIdentifier:") && !prev_type.isEmpty()) {
              if (commands.containsKey(prev_type+"*#*"+e.ast_type))
                commands.put(prev_type+"*#*"+e.ast_type, commands.get(prev_type+"*#*"+e.ast_type)+1);
              else
                commands.put(prev_type+"*#*"+e.ast_type, 1);
            }
          }
        }
      }
    }