package tmt.dsl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

import tmt.conf.Utils;
import tmt.dsl.data.Generator;
import tmt.dsl.executor.info.Element;
import tmt.dsl.executor.info.Step;
import tmt.dsl.formats.context.Vector;
import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;

public class GServer {

  public static final int LEARN = 1;
  public static final int EVAL  = 2;
  public static final int INFERENCE  = 3;
  public static final int USE_CASES  = 4;
  
  private static ArrayList<String> bad_types = new ArrayList<>();

  public static void main(String[] args) throws Exception{
    
    bad_types.add("PsiType:String");
    bad_types.add("PsiType:StringBuilder");
    bad_types.add("PsiType:long");
    bad_types.add("PsiType:View");
    bad_types.add("PsiType:int");
    bad_types.add("PsiType:void");
    bad_types.add("PsiType:boolean"); 
    bad_types.add("PsiType:HashMap<String, String>");
    bad_types.add("PsiType:Exception"); 
    bad_types.add("PsiType:ArrayList<String>"); 
    
    if (args[0].equals("learn")) 
      router(LEARN, null);
    else if (args[0].equals("eval"))
      router(EVAL, null);
    else if (args[0].equals("inference")) {
      InnerClass[] code = new Gson().fromJson(Utils.readFile("/root/ContextToCode/data/datasets/post"), InnerClass[].class);
      router(INFERENCE, code);
    }
    else if (args[0].equals("case_creation"))
      router(USE_CASES, null);
  }

  public static ArrayList<HashMap<String, String>> router(int swtch, InnerClass[] code) throws Exception {
    ArrayList<HashMap<String, String>> res = null;

    Generator g = new Generator(); 

    PrintStream fileStream = new PrintStream(
        new FileOutputStream("../log/log.txt", true)); 
    System.setOut(fileStream);

    Classifier t1 = new Classifier("android/ast");
    //getTemplates();
    ArrayList<Classifier> templates = new ArrayList<>();

    if (swtch == LEARN) {
      
      createUseCases(g, templates);
      for ( Classifier t : templates )
        doLearn(g, t);
    }
    else if (swtch == EVAL) {
      createEvalCases(g, templates);
      doEval(g, templates.get(0));


    //    	g.loadCode(null, g.ASC, 3, templates.get(0), null);
    //
    //    	ArrayList<HashMap<Integer, Step>> out = g.setTrainAndTest(templates.get(0));
    //
    //    	res = g.filter_through_npi(out, templates.get(1));
    }
    else if (swtch == USE_CASES) {
      //      createUseCases(g);
      //      doDataMask(g, templates.get(0));
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

  private static void createUseCases(Generator g, ArrayList<Classifier> templates) throws JsonSyntaxException, IOException {
    HashMap<String, Double> ast_types = new Gson().fromJson(Utils.readFile(g.root+"/pop_lines"), HashMap.class);
    HashMap<String, Double> commands = new Gson().fromJson(Utils.readFile(g.root+"/pop_comm"), HashMap.class);
    HashSet<String> types = new HashSet<>();

    int count = 3;
    for (Entry<String, Double> com : commands.entrySet()) 
      if (/*ast_types.com.getKey().contains("PsiType:Cursor") &&*/ com.getValue() > 8000) {

        Classifier t1 = new Classifier("android-copy/ast");
        String type = com.getKey().split("#")[0];
        
        String folder = com.getKey().replace("#PsiIdentifier:", "_").replace("PsiType:", "");
        File f = new File(g.root+"/classifiers/"+folder);

        if (!bad_types.contains(type) && !f.exists()) {
          types.add(type);
          /* background class */
          InnerClass background_class = new InnerClass("falsekey", "1");
          background_class.elements.add(new ElementInfo("ast_type", type, null));
          background_class.elements.add(new ElementInfo("class_method", type+"#PsiIdentifier:*", null));
          LinkedHashMap<String, String> temp7_1 = new LinkedHashMap<>();
          temp7_1.put("literal1","String mCursorString = ");
          temp7_1.put("stab_req",type);
          temp7_1.put("literal2",".getString(int id))");
          background_class.scheme.add(temp7_1);
          background_class.description = " Returns the value of the requested column as a String. ";
          /* background class */

          LinkedHashMap<String, String> temp6_1 = new LinkedHashMap<>();
          temp6_1.put("literal1","String mCursorString = ");
          temp6_1.put("stab_req",type);
          temp6_1.put("literal2",".getString(int id))");

//          File f = new File("/root/ContextToCode/predictor/log/1class/"+folder+"/info");
//
//          if(f.exists()) {
//            String info = Utils.readFile("/root/ContextToCode/predictor/log/1class/"+folder+"/info");
//            count = Integer.parseInt(StringUtils.substringsBetween(info, "\"prog\": \"", "\"")[0]);
//          }

          InnerClass ic = new InnerClass("truekey", count+"");
          ic.elements.add(new ElementInfo("ast_type", type, null));
          ic.elements.add(new ElementInfo("class_method", com.getKey(), null));
          ic.scheme.add(temp6_1);
          ic.description = " Returns the value of the requested column as a String. ";

          t1.classes.add(ic);
          t1.classes.add(background_class);
          t1.domain = folder;
          templates.add(t1);
          count ++;
        }
      }
    
//    System.err.println(count);
//    System.exit(1);
  }
  
  private static void createEvalCases(Generator g, ArrayList<Classifier> templates) throws JsonSyntaxException, IOException {

    
    Classifier t1 = new Classifier("android_crossvalidation/ast");
    
    for (File file : new File("/root/ContextToCode/predictor/log/1class/").listFiles()) {
      if (file.isDirectory()) {// && file.getName().contains("Context_") && !file.getName().contains("Cursor_getLong") && !file.getName().contains("Cursor_close")) {
        String info = Utils.readFile("/root/ContextToCode/predictor/log/1class/"+file.getName()+"/info");
        String command = StringUtils.substringsBetween(info, "\"prog\": \"", "\"")[0];
        String class_method = "PsiType:"+file.getName().replace("_", "#PsiIdentifier:");
        String type = StringUtils.substringsBetween(class_method, "PsiType:", "#")[0];
        
        InnerClass ic = new InnerClass("truekey", command);
        ic.elements.add(new ElementInfo("ast_type", "PsiType:"+type, null));
        ic.elements.add(new ElementInfo("class_method", class_method, null));
        
        LinkedHashMap<String, String> temp6_1 = new LinkedHashMap<>();
        temp6_1.put("literal1","String mCursorString = ");
        temp6_1.put("stab_req","PsiType:"+type);
        temp6_1.put("literal2",".getString(int id))");
        ic.scheme.add(temp6_1);
        
        ic.description = " Returns the value of the requested column as a String. ";
        
        t1.classes.add(ic);
        t1.domain = file.getName();
        templates.add(t1);
      }
    }
//    System.err.println(templates);
//    System.exit(1);
  }

  private static void doInference(Generator g, Classifier classifier) {

  }

  private static void doEval(Generator g, Classifier t) throws Exception {

    evalCounter c = new evalCounter();

    File[] files = new File(g.root+t.folder).listFiles();
    int count = 0;

    for (File f : files) {
      InnerClass[] code = new Gson().fromJson(Utils.readFile(f.getPath()), InnerClass[].class);
      g.loadCode(code, g.ASC, t);

      ArrayList<Vector[]> res = new ArrayList<>();
      t.clear();

      g.iterateCode(code, t, f.getPath(), res, 5);

      ArrayList<HashMap<Integer, Step>> info = g.setTrainAndTest(t);

      for (HashMap<Integer, Step> i : info) {
        ArrayList<HashMap<Integer, Step>> send = new ArrayList<>();
        send.add(i);
        c.add(g.filter_through_npi(send, t), Integer.parseInt(i.get(Collections.max(i.keySet())).program.get("id").getValue().toString()), info);
      }
      System.err.println(c);
      
      count ++;
      
      System.err.println("count: "+count);
      if (count > 500) {
        System.err.println(c);
        System.exit(1);
      }
    }
  }

  private static void doLearn(Generator g, Classifier t) throws Exception {
/*    PopularCounter popular = new PopularCounter(g.bad_types);

    File file = new File(g.root+"/context.json"); 
    file.delete();
    file = new File(g.root+"/log.json"); 
    file.delete();
    file = new File(g.root+"/pop_comm"); 
    file.delete();
    file = new File(g.root+"/pop_lines"); 
    file.delete();*/ 
    
    ArrayList<Vector[]> res = new ArrayList<>();
    
    int count = 0;

    File[] files = new File(g.root+t.folder).listFiles();
    for (File f : files) {
//        if (f.getPath().contains("97827446")) {

      System.err.println(f.getPath());
      t.clear();

      InnerClass[] code = new Gson().fromJson(Utils.readFile(f.getPath()), InnerClass[].class);
      g.loadCode(code, g.ASC, t);

      g.iterateCode(code, t, f.getPath(), res, 3);

/*      for ( InnerClass c : code )
      //  if(c.matches(t.classes))
    	    popular.add(c);*/
      
      count ++;
      
      System.err.println("count: "+count);
//      if (count > 1000) 
//    	  break;  
////        }
    }

/*    Utils.writeFile1(new Gson().toJson(Utils.sortByValue(popular.ast_types)), g.root+"/pop_lines", false);
    Utils.writeFile1(new Gson().toJson(Utils.sortByValue(popular.commands)), g.root+"/pop_comm", false);*/ 
    
    g.setTrainAndTest(t);
    t.clear();
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

    Classifier t1 = new Classifier("android/ast");

    Classifier t2 = new Classifier("android/ast");

    /*InnerClass ic = new InnerClass("falsekey", "2");
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

    InnerClass ic7 = new InnerClass("truekey", "9"); 
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
    ic8.description = " Returns a Resources instance for the application's package. "; */

    InnerClass ic5 = new InnerClass("truekey", "2");
    ic5.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic5.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:getString", null));
    LinkedHashMap<String, String> temp6_1 = new LinkedHashMap<>();
    temp6_1.put("literal1","String mCursorString = ");
    temp6_1.put("stab_req","PsiType:Cursor");
    temp6_1.put("literal2",".getString(int id))");
    ic5.scheme.add(temp6_1);
    ic5.description = " Returns the value of the requested column as a String. ";
    
    InnerClass ic6 = new InnerClass("truekey", "3");
    ic6.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic6.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:getColumnIndex", null));
    ic6.scheme.add(temp6_1);
    ic6.description = " Returns the value of the requested column as a String. ";

    InnerClass ic8 = new InnerClass("truekey", "4");
    ic8.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic8.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:close", null));
    ic8.scheme.add(temp6_1);
    ic8.description = " Returns the value of the requested column as a String. ";
    
    InnerClass ic9 = new InnerClass("truekey", "5");
    ic9.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic9.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:getLong", null));
    ic9.scheme.add(temp6_1);
    ic9.description = " Returns the value of the requested column as a String. ";
    
    InnerClass ic10 = new InnerClass("truekey", "6");
    ic10.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic10.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:getInt", null));
    ic10.scheme.add(temp6_1);
    ic10.description = " Returns the value of the requested column as a String. ";
    
    InnerClass ic7 = new InnerClass("falsekey", "1");
    ic7.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic7.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:*", null));
    LinkedHashMap<String, String> temp7_1 = new LinkedHashMap<>();
    temp7_1.put("literal1","String mCursorString = ");
    temp7_1.put("stab_req","PsiType:Cursor");
    temp7_1.put("literal2",".getString(int id))");
    ic7.scheme.add(temp6_1);
    ic7.description = " Returns the value of the requested column as a String. ";

//    t2.classes.add(ic4);
//          t2.classes.add(ic3);
//          t2.classes.add(ic1);
    
//    t2.classes.add(ic8);
//    t2.classes.add(ic6);
//    t2.classes.add(ic5);
    t2.classes.add(ic8);
    t2.classes.add(ic10);
    t2.classes.add(ic9);
    t2.classes.add(ic5);
    t2.classes.add(ic7);
    t2.classes.add(ic6);

//          t2.classes.add(ic5);
//          t2.classes.add(ic4);
//          t2.classes.add(ic);
    //      
    //      t1.classes.add(ic3);
    //      t1.classes.add(ic5);
    //      t1.classes.add(ic9);

    //	  res.add(t1);
    res.add(t2);

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
          if (commands.containsKey(prev_type+"#"+e.ast_type))
            commands.put(prev_type+"#"+e.ast_type, commands.get(prev_type+"#"+e.ast_type)+1);
          else
            commands.put(prev_type+"#"+e.ast_type, 1);
        }
      }
    }
  }
}

class evalCounter {

  int lines_without_context = 0;
  int lines_with_context = 0;
  int lines_with_class = 0;
  int correct_prediction = 0;
  int incorrect_prediction = 0;
  int given_variants = 0;
  HashMap<Integer, HashMap<Boolean, Integer>> classes_counter = new HashMap<>();
  HashMap<Integer, Integer> first_counter = new HashMap<>();

  
  public evalCounter() {
    for (int i = 0; i < 52; i++) {
      HashMap<Boolean, Integer> temp = new HashMap<>();
      temp.put(true, 0);
      temp.put(false, 0);
      classes_counter.put(i, temp);
      first_counter.put(i, 0);
    }
  }

  public void add(ArrayList<HashMap<String, String>> res,
      int executor_command, ArrayList<HashMap<Integer, Step>> info) {

    if (info.isEmpty())
      lines_without_context+=1;
    else {
      lines_with_context += 1;
      
      boolean correctness = false;
      if (executor_command > 1) {
        lines_with_class += 1; 

        for (HashMap<String, String> r : res) {
          int code = Integer.parseInt(r.get("code"));
          
          if (code == 12)
            given_variants += 1;
          if (code == executor_command)
            correctness = true;
        }   

        if (correctness) {
          correct_prediction += 1;
        }
        else
          incorrect_prediction += 1;
    
        System.err.println(res+" * "+executor_command+" * "+correctness);
        
        classes_counter.get(executor_command).put(correctness, classes_counter.get(executor_command).get(correctness)+1);
      } else 
        for (HashMap<String, String> r : res) {
          int code = Integer.parseInt(r.get("code"));
          first_counter.put(code, first_counter.get(code)+1);
        } 
    }
  }

  public String toString() {
    return "no context: " + lines_without_context + ", yes context: "+lines_with_context+", class: " 
        + lines_with_class + ", correct: " + correct_prediction + ", incorrect"  + incorrect_prediction +
        " by class, "+classes_counter+", first_counter"+first_counter+" № of var.," +given_variants;
  }
}