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
import java.util.SortedSet;
import java.util.TreeSet;

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

//    	for (String key : Conf.js_keys) {
      if (Conf.lang.equals("java"))
        createUseCasesJava(g, templates);
      else if (Conf.lang.equals("javascript"))
        createUseCasesJavaScript(g, templates, "onscroll");

      for ( Classifier t : templates )
        doLearn(g, t);
//    	}
    }
    else if (swtch == EVAL) {
      if (Conf.lang.equals("java"))
        createEvalCasesJava(g, templates);
      else if (Conf.lang.equals("javascript"))
        createUseCasesJavaScript(g, templates, "document.getelementsbytagname");
      
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
     /* Classifier c = null;
      if (Conf.lang.equals("java"))
        c = new Classifier(Conf.templates);
      else if (Conf.lang.equals("javascript"))
        c = createJSUseCaseInference(); */ 
      
      if (Conf.lang.equals("java"))
        createEvalCasesJava(g, templates);
      else if (Conf.lang.equals("javascript"))
        createUseCasesJavaScript(g, templates, "");
      
      res = doInference(g, templates.get(0), code);
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

  private static void createUseCasesJavaScript(Generator g, ArrayList<Classifier> templates, String key) throws JsonSyntaxException, IOException {
    Classifier t1 = new Classifier("sandbox/");
    t1.domain = "/"+key+"/";
        
    LinkedHashMap<String, String> num_ = new LinkedHashMap<>();
    //* math *//
    num_.put("stab_req","");
    num_.put("stab_req","LiteralNumber");
    //**//
	
	LinkedHashMap<String, String> string_ = new LinkedHashMap<>();
    string_.put("stab_req","");
    string_.put("stab_req","LiteralString");
	
	LinkedHashMap<String, String> doc_ = new LinkedHashMap<>();
    doc_.put("stab_req","");
    doc_.put("stab_req","document");
	
	LinkedHashMap<String, String> event_ = new LinkedHashMap<>();
    event_.put("stab_req","");
    event_.put("stab_req","el");
	
    InnerClass math = fill_case("math", "4", key, num_, t1);

    InnerClass math_f = fill_case("math.floor", "6", key, num_, t1);

    InnerClass math_r = fill_case("math.random", "7", key, num_, t1);
	
    InnerClass getelementbyid = fill_case("document.getelementbyid", "14", key, doc_, t1);

    InnerClass getelementsbytagname = fill_case("document.getelementsbytagname", "12", key, doc_, t1);

    InnerClass getelementsbyclassname = fill_case("document.getelementsbyclassname", "13", key, doc_, t1);

    InnerClass addeventlistener = fill_case("addeventlistener", "17", key, event_, t1);
	
	InnerClass indexof = fill_case("indexof", "15", key, string_, t1);

    InnerClass onclick = fill_case("onclick", "18", key, event_, t1);

    InnerClass onscroll = fill_case("onscroll", "21", key, event_, t1);

    InnerClass mouseout = fill_case("onmouseout", "20", key, event_, t1);

    InnerClass mouseover = fill_case("onmouseover", "19", key, event_, t1);

   /* InnerClass document_getElementByClassName = fill_case("document.getelementsbyclassname", "14", key, t1);
    document_getElementByClassName.elements.add(new ElementInfo("type", "Identifier", "document"));
    document_getElementByClassName.elements.add(new ElementInfo("type", "Property", "Property:getElementsByClassName"));
    
    InnerClass document_getElementById = fill_case("document.getelementbyid", "15", key, t1);
    document_getElementById.elements.add(new ElementInfo("type", "Identifier", "document"));
    document_getElementById.elements.add(new ElementInfo("type", "Property", "Property:getElementById"));
    
    InnerClass indexOf = fill_case("indexof", "16", key, t1);
    indexOf.elements.add(new ElementInfo("type", "CallExpression", "Property:indexOf"));
    
    InnerClass load = fill_case("onload", "17", key, t1);
    load.elements.add(new ElementInfo("type", "CallExpression", "Property:load"));
    
    InnerClass addEventListener = fill_case("addeventlistener", "18", key, t1);
//    addEventListener.elements.add(new ElementInfo("type", "CallExpression", null));
    addEventListener.elements.add(new ElementInfo("type", "CallExpression", "Property:addEventListener"));
    
    InnerClass click = fill_case("onclick", "19", key, t1);
    click.elements.add(new ElementInfo("type", "CallExpression", "Property:click"));
    
    InnerClass mouseover = fill_case("onmouseover", "20", key, t1);
//    mouseover.elements.add(new ElementInfo("type", "CallExpression", "Property:mouseover"));
    mouseover.elements.add(new ElementInfo("type", "Property", "Property:mouseover"));
    
    InnerClass mouseout = fill_case("onmouseout", "21", key, t1);
//    mouseout.elements.add(new ElementInfo("type", "CallExpression", null));
    mouseout.elements.add(new ElementInfo("type", "Property", "Property:mouseout"));
    
    InnerClass scroll = fill_case("onscroll", "22", key, t1);
    scroll.elements.add(new ElementInfo("type", "Property", "Property:scroll"));
    
    InnerClass prompt = fill_case("prompt", "12", key, window_, t1);
    prompt.elements.add(new ElementInfo("type", "CallExpression", null));
    prompt.elements.add(new ElementInfo("type", "Identifier", "prompt"));

    InnerClass confirm = fill_case("confirm", "13", key, window_, t1);
    confirm.elements.add(new ElementInfo("type", "CallExpression", null));
    confirm.elements.add(new ElementInfo("type", "Identifier", "confirm"));

    InnerClass log = fill_case("console.log", "14", key, window_, t1);
    log.elements.add(new ElementInfo("type", "CallExpression", "Property:log"));
    log.elements.add(new ElementInfo("type", "Identifier", "console"));

    InnerClass window_location = fill_case("window.location", "15", key, window_, t1);
    window_location.elements.add(new ElementInfo("type", "Identifier", "window"));
    window_location.elements.add(new ElementInfo("type", "Property", "Property:location"));
    */
    
    templates.add(t1);
  }
  
  private static InnerClass fill_case(String key, String prog, String actual, LinkedHashMap<String, String> sheme_, Classifier t1) {
    InnerClass c;
    
    if (actual.equals(key))
      c = new InnerClass("truekey", prog, key);
    else
      c = new InnerClass("falsekey", prog, key);
    
    c.scheme.add(sheme_);
    c.description = " Edit CSS. ";
    c.tab = compile_tab(Conf.tabs.get(key).get("id"), Conf.tabs.get(key).get("name"));
    c.content = compile_content(Conf.tabs.get(key).get("id"), Conf.tabs.get(key).get("title"), Conf.tabs.get(key).get("content"),
        Conf.tabs.get(key).containsKey("code") ? Conf.tabs.get(key).get("code"): "", Conf.tabs.get(key).containsKey("script") ? Conf.tabs.get(key).get("script"): "");
    t1.classes.add(c);
    
    return c;
  }
  
  public static String compile_tab(String id, String name) {
    return "<a class='nav-link' id='tip-"+id+"-tab' data-toggle='pill' href='#tip-"+id+"' role='tab' aria-controls='tip-1' aria-selected='false'>"+name+"</a>";
  }

  public static String compile_content(String id, String title, String content, String code, String script) {
    return " <div class='tab-pane' id='tip-"+id+"' role='tabpanel' aria-labelledby='tip-"+id+"-tab' data-tip='"+id+"'> <script type=\"text/javascript\"> "+script+" </script> <div class='code-wrapper'><h1 style='font-size: 2.5rem; font-weight: 700;	margin-top: 0;	margin-bottom: 0.75em; font-size: 1.875rem;	margin-top: 1.875rem;'>"+title+"</h1> "+content+" </div>"+code+" </div>  ";
  }

  private static void createEvalCasesJava(Generator g, ArrayList<Classifier> templates) throws JsonSyntaxException, IOException {

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
    c = new evalCounter(t.classes.size()+8);
    
    HashMap<String, Integer> counter = new HashMap<>();
    counter.put("no", 0);
    counter.put("yes", 0);

    for (File f : new File(Conf.root+t.folder).listFiles()) {
      folder1.put(count, f.getPath());
      count ++;
    }
    
    for ( Entry<Integer, String> f : folder1.entrySet() ) {
      t.blocking = false;
//      if (f.getValue().contains("/39383")) {
        System.err.println("count: "+f.getValue());
        validate_line_by_line(f, g, t, counter, c);
        System.err.println(counter);
//      }
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

    g.iterateCode(code, t, f.getValue(), res, 5, null);

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
//    InnerClass[] code = new Gson().fromJson(Utils.readFile(f.getValue()), InnerClass[].class);
    InnerClass[] code = getRaw(f.getValue());
    g.loadCode(code, g.ASC, t);
    
    HashMap<String, Integer> map = new HashMap<>();
//    map.put(0, 0);
//    map.put(1, 0);

    ArrayList<String> tmp = new ArrayList<String>();

    for (int carret = code.length; carret > 2; carret --) {
      ArrayList<Vector[]> res = new ArrayList<>();
      t.clear();

      g.iterateCode(Arrays.copyOfRange(code, 0, carret), t, f.getValue(), res, 5, map);
       
      ArrayList<HashMap<Integer, Step>> info = g.setTrainAndTest(t);

      //    results.put(f.getKey(), new ArrayList<ArrayList<Integer>>());
      for (HashMap<Integer, Step> i : info) {
    	  if (Integer.parseInt(i.get(Collections.max(i.keySet())).program.get("id").getValue().toString()) > 1 ) {
        ArrayList<HashMap<Integer, Step>> send = new ArrayList<>();
        send.add(i);

        ArrayList<HashMap<String, String>> response = g.filter_through_npi(send, t);

        c.add(response, Integer.parseInt(i.get(Collections.max(i.keySet())).program.get("id").getValue().toString()), info);
        if (!response.isEmpty())
          counter.put("yes", counter.get("yes")+1);
        else
          counter.put("no", counter.get("no")+1);
        
        tmp.add(f.getValue()+" # "+counter+" * "+c);
//        if (response.size() > 0)
//        
////        if (counter.get("no") > 2820)
//          System.exit(1);
      }
      }
    }
    Utils.writeFile(tmp, "/root/ContextToCode/eval", true);
  }

  private static ArrayList<Integer> parse_response(ArrayList<HashMap<String, String>> response) {
    ArrayList<Integer> res = new ArrayList<>();
    
    for (HashMap<String, String> r : response) 
      res.add(Integer.parseInt(r.get("code")));
      
    return res;
  }

  private static void doLearn(Generator g, Classifier t) throws IOException {
    PopularCounter popular = new PopularCounter();

    /*File file = new File(Conf.root+"/context.json"); 
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
    
    HashMap<String, Integer> map = new HashMap<>();

    for (File f : files) {
      if (f.getName().equals("0")) {

        t.clear();

        InnerClass[] code = getRaw(f.getPath());
              Utils.writeFile1(new Gson().toJson(code), Conf.root+"/classifiers/"+t.domain+"/all.json", false);
              System.exit(1);
        g.loadCode(code, g.ASC, t);

        g.iterateCode(code, t, f.getPath(), res, 3, map);

//        /*   for ( InnerClass c : code )
//        if(c.matches(t.classes))
//    	    popular.add(c);*/

        count ++;

        System.err.println("count: "+count);
        //      if (count > 1000) 
        //    	  break;  
      }
    }

    /*Utils.writeFile1(new Gson().toJson(Utils.sortByValue(popular.ast_types)), Conf.root+"/pop_lines", false);
    Utils.writeFile1(new Gson().toJson(Utils.sortByValue(popular.commands)), Conf.root+"/pop_comm", false); 
    System.exit(1);
    
    SortedSet<String> keys = new TreeSet<>(map.keySet());
    for (String key : keys) 
       System.err.println(key+","+map.get(key));
    
    System.exit(1);
    */

    g.setTrainAndTest(t);
    t.clear();
  }
  
  private static ArrayList<HashMap<String, String>> doInference(Generator g, Classifier t, InnerClass[] code_) throws IOException {
    /*  for ( InnerClass c : code )
      c.executor_command = "1";

    ArrayList<Vector[]> res = new ArrayList<>();

    g.loadCode(code, g.ASC, classifier);

    HashMap<Integer, Integer> map = new HashMap<>();
    map.put(0, 0);
    map.put(1, 0);

    g.iterateCode(code, classifier, "inference", res, 5, map);

    ArrayList<HashMap<String, String>> snippets = null;//g.fromCache(classifier.vs);

    if (snippets != null)
        return snippets;
    else {
        ArrayList<HashMap<Integer, Step>> out = g.setTrainAndTest(classifier);

        return g.filter_through_npi(out, classifier);
    } */

    t.blocking = false;
    
    g.loadCode(code_, g.ASC, t);

    // for (int carret = code_.length; carret > 2; carret --) {
    ArrayList<Vector[]> res = new ArrayList<>();

    g.iterateCode(code_, t, "inference", res, 5, null);

    ArrayList<HashMap<Integer, Step>> info = g.setTrainAndTest(t);

    for (HashMap<Integer, Step> i : info) {
    	ArrayList<HashMap<Integer, Step>> send = new ArrayList<>();
    	send.add(i);

    	ArrayList<HashMap<String, String>> response = g.filter_through_npi(send, t);
//    	System.err.println("length: "+code_.length+", response:"+response);
    	return response;
    }
    //  }

    return null;
  }
  
  private static InnerClass[] getRaw(String f) throws JsonSyntaxException, IOException {
    if (Conf.lang.equals("java"))
      return new Gson().fromJson(Utils.readFile(f), InnerClass[].class);
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
    
        System.err.println(res+" * "+executor_command+" * "+correctness+" * "+classes_counter);
        
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
