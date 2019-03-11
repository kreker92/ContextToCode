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

import tmt.conf.Conf;
import tmt.conf.Utils;
import tmt.dsl.data.Generator;
import tmt.dsl.executor.info.Element;
import tmt.dsl.executor.info.Step;
import tmt.dsl.formats.context.Vector;
import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;
import tmt.dsl.formats.langs.JavaScriptAST;

public class GServer {

  public static final int LEARN = 1;
  public static final int EVAL  = 2;
  public static final int INFERENCE  = 3;
  public static final int USE_CASES  = 4;
  
  public static void main(String[] args) throws Exception{
    
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

  public static ArrayList<HashMap<String, String>> router(int swtch, InnerClass[] code) throws IOException {
    ArrayList<HashMap<String, String>> res = null;

    Generator g = new Generator();

    Classifier t1 = new Classifier("android/ast");
    //getTemplates();
    ArrayList<Classifier> templates = new ArrayList<>();

    if (swtch == LEARN) {

      if (Conf.lang.equals("java"))
        createUseCasesJava(g, templates);
      else if (Conf.lang.equals("javascript"))
        createUseCasesJavaScript(g, templates);

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
    else if (swtch == INFERENCE) {
      Classifier c = new Classifier(Conf.templates);
      res = doInference(g, c, code);
    }
    return res;
  }

  private static void createUseCasesJava(Generator g, ArrayList<Classifier> templates) throws JsonSyntaxException, IOException {
    ArrayList<String> bad_types = new ArrayList<>();
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
    
    HashMap<String, Double> ast_types = new Gson().fromJson(Utils.readFile(Conf.root+"/pop_lines"), HashMap.class);
    HashMap<String, Double> commands = new Gson().fromJson(Utils.readFile(Conf.root+"/pop_comm"), HashMap.class);
    HashSet<String> types = new HashSet<>();

    int count = 3;

    for (Entry<String, Double> com : commands.entrySet()) 
      if (/*ast_types.com.getKey().contains("PsiType:Cursor") &&*/ com.getValue() > 8000) {

        Classifier t1 = new Classifier("android-copy/ast");
        String type = com.getKey().split("#")[0];
        
        String folder = com.getKey().replace("#PsiIdentifier:", "_").replace("PsiType:", "")+"_";
        File f = new File(Conf.root+"/classifiers/"+folder);

        if (!bad_types.contains(type) && !f.exists()) {
          types.add(type);
          /* background class */
          InnerClass background_class = new InnerClass("falsekey", "1", type);
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

          InnerClass ic = new InnerClass("truekey", count+"", type);
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

  private static void createUseCasesJavaScript(Generator g, ArrayList<Classifier> templates) throws JsonSyntaxException, IOException {
    Classifier t1 = new Classifier("sandbox/");
    t1.domain = "/data_picks/";
    templates.add(t1);
    
   /* InnerClass background_class = new InnerClass("falsekey", "1", type);
    background_class.elements.add(new ElementInfo("ast_type", type, null));
    background_class.elements.add(new ElementInfo("class_method", type+"#PsiIdentifier:*", null));
    LinkedHashMap<String, String> temp7_1 = new LinkedHashMap<>();
    temp7_1.put("literal1","String mCursorString = ");
    temp7_1.put("stab_req",type);
    temp7_1.put("literal2",".getString(int id))");
    background_class.scheme.add(temp7_1);
    background_class.description = " Returns the value of the requested column as a String. ";*/

    LinkedHashMap<String, String> temp6_1 = new LinkedHashMap<>();
    temp6_1.put("literal1","String mCursorString = ");
//    temp6_1.put("stab_req",type);
    temp6_1.put("literal2",".getString(int id))");


    InnerClass ic = new InnerClass("truekey", "4", "Property:css");
    ic.elements.add(new ElementInfo("type", "CallExpression", "Property:css"));
//    ic.elements.add(new ElementInfo("class_method", com.getKey(), null));
    ic.scheme.add(temp6_1);
    ic.description = " Returns the value of the requested column as a String. ";

    t1.classes.add(ic);
//    t1.classes.add(background_class);
//    t1.domain = folder;
  }
  

  private static void createEvalCases(Generator g, ArrayList<Classifier> templates) throws JsonSyntaxException, IOException {

    HashMap<String, LinkedTreeMap<String, String>> outputs = new Gson().fromJson(Utils.readFile("/root/ContextToCode/predictor/log/1class/expect_to_prog"), HashMap.class);
    
    Classifier t1 = new Classifier("android_crossvalidation2/");
    
    for ( Entry<String, LinkedTreeMap<String, String>> classes : outputs.entrySet() ) {
        String class_method = "PsiType:"+classes.getValue().get("dir").replace("_", "#PsiIdentifier:");
        String type = StringUtils.substringsBetween(class_method, "PsiType:", "#")[0];
        
        InnerClass ic = new InnerClass("truekey", classes.getKey(), type);
        ic.elements.add(new ElementInfo("ast_type", "PsiType:"+type, null));
        ic.elements.add(new ElementInfo("class_method", class_method, null));
        
        HashMap<String, String> scheme_ = new Gson().fromJson(Utils.readFile("/root/ContextToCode/predictor/log/1class/"+classes.getValue().get("dir")+"/scheme"), HashMap.class);
        ic.addScheme(scheme_, type, classes.getValue().get("dir")); 
        
        ic.description = scheme_.get("description");
        
        t1.classes.add(ic);
        t1.domain = classes.getValue().get("dir");
        templates.add(t1);
    }
//    System.err.println(templates);
//    System.exit(1);
  }

  private static ArrayList<HashMap<String, String>> doInference(Generator g, Classifier classifier, InnerClass[] code) throws IOException{
    for ( InnerClass c : code )
      c.executor_command = "1";

    ArrayList<Vector[]> res = new ArrayList<>();

    g.loadCode(code, g.ASC, classifier);
    
    g.iterateCode(code, classifier, "inference", res, 5);

    ArrayList<HashMap<String, String>> snippets = null;//g.fromCache(classifier.vs);

    if (snippets != null)
        return snippets;
    else {
        ArrayList<HashMap<Integer, Step>> out = g.setTrainAndTest(classifier);

        return g.filter_through_npi(out, classifier);
    }
  }

  private static void doEval(Generator g, Classifier t) throws IOException {

    evalCounter c;

    HashMap<Integer, String> folder1 = new HashMap<>();
    HashMap<Integer, String> folder2 = new HashMap<>();
    HashMap<Integer, String> folder3 = new HashMap<>();
    HashMap<Integer, String> folder4 = new HashMap<>();
    HashMap<Integer, String> folder5 = new HashMap<>();
//    HashMap<Integer, ArrayList<ArrayList<Integer>>> results = new HashMap<>();
//    HashMap<Integer, ArrayList<ArrayList<Integer>>> results1 = new HashMap<>();

    int count = 0;
    c = new evalCounter(t.classes.size()+1);
    
    HashMap<String, Integer> counter = new HashMap<>();
    counter.put("no", 0);
    counter.put("yes", 0);
    
    for (File f : new File(Conf.root+t.folder+"folder1").listFiles()) {
      folder1.put(count, f.getPath());
      count ++;
    }
    
    for ( Entry<Integer, String> f : folder1.entrySet() ) {
      t.blocking = false;
      if (f.getValue().contains("99726047")) {
        System.err.println("count: "+f.getValue());
        validate_line_by_line(f, g, t, counter, c);
        System.err.println(counter);
      }
    }
    
    System.exit(1);
    
    for ( Entry<Integer, String> f : folder1.entrySet() ) {
      validate_file(f, g, t, c);
      System.err.println("count: "+f.getKey());
    }
    
    Utils.writeFile1(c.toString(), Conf.root+"/folder1_validation", true);
    
    for (File f : new File(Conf.root+t.folder+"folder2").listFiles()) {
      folder2.put(count, f.getPath());
      count ++;
    }
    
    c = new evalCounter(t.classes.size()+1);
    for ( Entry<Integer, String> f : folder2.entrySet() ) {
      validate_file(f, g, t, c);
      System.err.println("count: "+f.getKey());
    }
    
    Utils.writeFile1(c.toString(), Conf.root+"/folder2_validation", true);
    
    for (File f : new File(Conf.root+t.folder+"folder3").listFiles()) {
      folder3.put(count, f.getPath());
      count ++;
    }
    
    c = new evalCounter(t.classes.size()+1);
    for ( Entry<Integer, String> f : folder3.entrySet() ) {
      validate_file(f, g, t, c);
      System.err.println("count: "+f.getKey());
    }
    
    Utils.writeFile1(c.toString(), Conf.root+"/folder3_validation", true);
    
    for (File f : new File(Conf.root+t.folder+"folder4").listFiles()) {
      folder4.put(count, f.getPath());
      count ++;
    }
    
    c = new evalCounter(t.classes.size()+1);
    for ( Entry<Integer, String> f : folder4.entrySet() ) {
      validate_file(f, g, t, c);
      System.err.println("count: "+f.getKey());
    }
    
    c = new evalCounter(t.classes.size()+1);
    for ( Entry<Integer, String> f : folder4.entrySet() ) {
      validate_file(f, g, t, c);
      System.err.println("count: "+f.getKey());
    }
    
    Utils.writeFile1(c.toString(), Conf.root+"/folder4_validation", true);
    
    for (File f : new File(Conf.root+t.folder+"folder5").listFiles()) {
      folder5.put(count, f.getPath());
      count ++;
    }
    
    c = new evalCounter(t.classes.size()+1);
    for ( Entry<Integer, String> f : folder5.entrySet() ) {
      validate_file(f, g, t, c);
      System.err.println("count: "+f.getKey());
    }
    
    Utils.writeFile1(c.toString(), Conf.root+"/folder5_validation", true);
  }
  
  private static void validate_file(Entry<Integer, String> f, Generator g, Classifier t, evalCounter c) throws IOException {
    InnerClass[] code = new Gson().fromJson(Utils.readFile(f.getValue()), InnerClass[].class);
    g.loadCode(code, g.ASC, t);

    ArrayList<Vector[]> res = new ArrayList<>();
    t.clear();

    g.iterateCode(code, t, f.getValue(), res, 5);

    ArrayList<HashMap<Integer, Step>> info = g.setTrainAndTest(t);

//    results.put(f.getKey(), new ArrayList<ArrayList<Integer>>());

    for (HashMap<Integer, Step> i : info) {
      ArrayList<HashMap<Integer, Step>> send = new ArrayList<>();
      send.add(i);

//      ArrayList<HashMap<String, String>> response = g.filter_through_npi(send, t);
//      results.get(f.getKey()).add(parse_response(response));

      c.add(g.filter_through_npi(send, t), Integer.parseInt(i.get(Collections.max(i.keySet())).program.get("id").getValue().toString()), info);
    }
  }
  
  private static void validate_line_by_line(Entry<Integer, String> f, Generator g, Classifier t, HashMap<String, Integer> counter, evalCounter c) throws IOException {
    InnerClass[] code = new Gson().fromJson(Utils.readFile(f.getValue()), InnerClass[].class);
    g.loadCode(code, g.ASC, t);

    for (int carret = code.length; carret > 2; carret --) {
      ArrayList<Vector[]> res = new ArrayList<>();
      t.clear();

      g.iterateCode(Arrays.copyOfRange(code, 0, carret), t, f.getValue(), res, 5);
       
      ArrayList<HashMap<Integer, Step>> info = g.setTrainAndTest(t);

      //    results.put(f.getKey(), new ArrayList<ArrayList<Integer>>());

      for (HashMap<Integer, Step> i : info) {
        ArrayList<HashMap<Integer, Step>> send = new ArrayList<>();
        send.add(i);

        ArrayList<HashMap<String, String>> response = g.filter_through_npi(send, t);

        c.add(response, Integer.parseInt(i.get(Collections.max(i.keySet())).program.get("id").getValue().toString()), info);
        if (!response.isEmpty())
          counter.put("yes", counter.get("yes")+1);
        else
          counter.put("no", counter.get("no")+1);
        
        System.err.println(counter+" * "+c);
        
        if (counter.get("no") > 2820)
          System.exit(1);
      }
    }
    
    
  }

  private static ArrayList<Integer> parse_response(ArrayList<HashMap<String, String>> response) {
    ArrayList<Integer> res = new ArrayList<>();
    
    for (HashMap<String, String> r : response) 
      res.add(Integer.parseInt(r.get("code")));
      
    return res;
  }

  private static void doLearn(Generator g, Classifier t) throws IOException {
/*    PopularCounter popular = new PopularCounter();

    File file = new File(Conf.root+"/context.json"); 
    file.delete();
    file = new File(Conf.root+"/log.json"); 
    file.delete();
    File file = new File(Conf.root+"/pop_comm"); 
    file.delete();
    file = new File(Conf.root+"/pop_lines"); 
    file.delete();*/
    
    ArrayList<Vector[]> res = new ArrayList<>();
    
    int count = 0;

    File[] files = new File(Conf.root+t.folder).listFiles();

    for (File f : files) {
//        if (f.getPath().contains("97827446")) {

      t.clear();

      InnerClass[] code = getRaw(f);
//      System.exit(1);
      g.loadCode(code, g.ASC, t);

      g.iterateCode(code, t, f.getPath(), res, 3);

    /*  for ( InnerClass c : code )
      //  if(c.matches(t.classes))
    	    popular.add(c);*/
      
      count ++;
      
      System.err.println("count: "+count);
//      if (count > 1000) 
//    	  break;  
////        }
    }

   /* Utils.writeFile1(new Gson().toJson(Utils.sortByValue(popular.ast_types)), Conf.root+"/pop_lines", false);
    Utils.writeFile1(new Gson().toJson(Utils.sortByValue(popular.commands)), Conf.root+"/pop_comm", false); 
    System.exit(1);*/
    g.setTrainAndTest(t);
    t.clear();
  }
  
  private static InnerClass[] getRaw(File f) throws JsonSyntaxException, IOException {
    if (Conf.lang.equals("java"))
      return new Gson().fromJson(Utils.readFile(f.getPath()), InnerClass[].class);
    else if (Conf.lang.equals("javascript")) {
      return new JavaScriptAST(f).getClasses();
    }
    return null;
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
    //      Conf.root = "/root/ContextToCode/data/datasets/android/";
    //      Conf.root_key = "ast/";
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

    InnerClass ic5 = new InnerClass("truekey", "2", "PsiType:Cursor");
    ic5.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic5.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:getString", null));
    LinkedHashMap<String, String> temp6_1 = new LinkedHashMap<>();
    temp6_1.put("literal1","String mCursorString = ");
    temp6_1.put("stab_req","PsiType:Cursor");
    temp6_1.put("literal2",".getString(int id))");
    ic5.scheme.add(temp6_1);
    ic5.description = " Returns the value of the requested column as a String. ";
    
    InnerClass ic6 = new InnerClass("truekey", "3", "PsiType:Cursor");
    ic6.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic6.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:getColumnIndex", null));
    ic6.scheme.add(temp6_1);
    ic6.description = " Returns the value of the requested column as a String. ";

    InnerClass ic8 = new InnerClass("truekey", "4", "PsiType:Cursor");
    ic8.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic8.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:close", null));
    ic8.scheme.add(temp6_1);
    ic8.description = " Returns the value of the requested column as a String. ";
    
    InnerClass ic9 = new InnerClass("truekey", "5", "PsiType:Cursor");
    ic9.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic9.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:getLong", null));
    ic9.scheme.add(temp6_1);
    ic9.description = " Returns the value of the requested column as a String. ";
    
    InnerClass ic10 = new InnerClass("truekey", "6", "PsiType:Cursor");
    ic10.elements.add(new ElementInfo("ast_type", "PsiType:Cursor", null));
    ic10.elements.add(new ElementInfo("class_method", "PsiType:Cursor#PsiIdentifier:getInt", null));
    ic10.scheme.add(temp6_1);
    ic10.description = " Returns the value of the requested column as a String. ";
    
    InnerClass ic7 = new InnerClass("falsekey", "1", "PsiType:Cursor");
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
//  public ArrayList<String> bad_types;

  public PopularCounter () {
//    bad_types = bad_types_;
  }

  public void add(InnerClass c) {
    String prev_type = "";
    if (Conf.lang.equals("java")) {
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
    else if (Conf.lang.equals("javascript")) {
      //if (c.line_text.contains("$")) {
        for (ElementInfo e : c.elements ) {
          if (e.type != null && !e.type.isEmpty()) {
            if (ast_types.containsKey(e.type)) 
              ast_types.put(e.type, ast_types.get(e.type)+1);
            else 
              ast_types.put(e.type, 1);
          } 
          if (e.type.contains("CallExpression")) {
            if (commands.containsKey(e.text))
              commands.put(e.text, commands.get(e.text)+1);
            else
              commands.put(e.text, 1);
          }
        }
      //}
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
  HashMap<Integer, Integer> counter = new HashMap<>();

  
  public evalCounter(int j) {
    for (int i = 0; i < j; i++) {
      HashMap<Boolean, Integer> temp = new HashMap<>();
      temp.put(true, 0);
      temp.put(false, 0);
      classes_counter.put(i, temp);
      counter.put(i, 0);
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
          
//          if (code == 12)
//            given_variants += 1;
 
          if (code == executor_command)
            correctness = true;
        }   

        if (correctness) {
          correct_prediction += 1;
        }
        else
          incorrect_prediction += 1;
    
//        System.err.println(res+" * "+executor_command+" * "+correctness);
        
        classes_counter.get(executor_command).put(correctness, classes_counter.get(executor_command).get(correctness)+1);
      } 
      for (HashMap<String, String> r : res) {
        int code = Integer.parseInt(r.get("code"));
        counter.put(code, counter.get(code)+1);
      } 
    }
  }

  public String toString() {
    return "no context: " + lines_without_context + ", yes context: "+lines_with_context+", class: " 
        + lines_with_class + ", correct: " + correct_prediction + ", incorrect"  + incorrect_prediction +
        " by class, "+classes_counter+", counter"+counter;
  }
}