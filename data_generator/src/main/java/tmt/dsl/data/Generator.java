package tmt.dsl.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.nio.charset.Charset;
import java.util.Map.Entry;

import tmt.conf.Utils;
import tmt.dsl.executor.Executor;

import java.nio.file.Files;

import org.apache.commons.lang3.ArrayUtils;

import tmt.dsl.DSL;
import tmt.dsl.Classifier;
import tmt.dsl.executor.info.Step;
import tmt.dsl.formats.context.ContextDSL;
import tmt.dsl.formats.context.Parser;
import tmt.dsl.formats.context.Vector;
import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;
import tmt.dsl.pumpkin.Pumpkin;
import tmt.dsl.snippetize.Snippetizer;
import tmt.dsl.tensorflow.TF;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

//if (args[0].equals("generate_redirects")) {
//Map<String,Collection<RedirectData>> redirects = new HashMap<String, Collection<RedirectData>>();
//HashSet<String> queries = new HashSet<String>();      
//
//redirects = RedirectUtils.loadRedirects(
//  new Gson().fromJson(new FileReader(dsl_buffer+"redirects.json"), RedirectsJson[].class));
//for ( ArrayList<String> r : new Gson().fromJson(new FileReader(dsl_buffer+"all_requests.json"), ArrayList[].class)) {
//if (count > limit)
//  break;
//queries.add(r.get(0).trim().toLowerCase());
//count ++;
//}
//
//Set<String> qs = new HashSet<String>(queries);
//ArrayList<String> inputs = new ArrayList<String>(qs);
//
//Integer[] outputs = new Gson().fromJson(new FileReader("/root/data/dsl_buffer/answers.json"), Integer[].class);
//
//RedirectDSL rdsl = new RedirectDSL(inputs, new ArrayList<Integer>(Arrays.asList(outputs)), redirects);
//rdsl.check_redirects();
//} else if (args[0].equals("log")) {
//String client = args[2];
//int client_id =  Integer.parseInt(args[3]);
//String command = args[1];
//
//ArrayList<Event> events = new ArrayList<Event>();
//BufferedReader br = new BufferedReader(new FileReader("/root/NeuralProgramSynthesis/dsl/data/logs/"+client+".json"));
//for(String line; (line = br.readLine()) != null; ) {
//Event e = new Gson().fromJson(line.replace("@timestamp", "timestamp"), Event.class);
//if (e._source != null) {
//  if (count > limit)
//      break;
//  events.add(e);
//  e.setTimes();
//  e.setId(count);
//}
//count++;
//}
//br.close();
//
//HashMap<EventAtMinute, EventAtMinute> inputs = new HashMap<EventAtMinute, EventAtMinute>();
//
//for (Event e : events) {
//EventAtMinute ev = new EventAtMinute(e);
//if (!inputs.containsKey(ev))
//  inputs.put(ev, ev);
//else
//  inputs.get(ev).update(ev);
//}
//
//ArrayList<Integer> outputs = null;
//      
//LogDSL log_dsl = new LogDSL(new ArrayList<EventAtMinute>(inputs.values()), outputs, client, command, client_id);
//log_dsl.execute(command);
//} else if (args[0].equals("context")) {

public class Generator  {

//  public static int limit = 15000;

  private static Gson gson = new Gson();
  public final String root = "../data/datasets/";
  private InnerClass[] code;

  public static ArrayList<String> good_types;
  public static ArrayList<String> bad_types;
  
  public static final int ASC = 1;
  public static final int DESC = 2;
  
  public Generator () throws JsonSyntaxException, JsonIOException, FileNotFoundException {
	  good_types = new ArrayList( Arrays.asList( gson.fromJson(new FileReader("../output/conf/good_types"), String[].class)) );
	  bad_types = new ArrayList( Arrays.asList( gson.fromJson(new FileReader("../output/conf/bad_types"), String[].class)) );
  }

  public ArrayList<HashMap<Integer, Step>> setTrainAndTest(Classifier t) throws Exception{
    String filename = root+"context.json";
    File f_ = new File(filename);
    f_.createNewFile();
    new FileOutputStream(f_, false);

    ArrayList<HashMap<Integer, Step>> output = new ArrayList<>();
    
    try {
        long start1 = System.currentTimeMillis();

        int max = 0;

        HashMap<Integer, ArrayList<Vector>> sequences = new HashMap<>();
        //      BufferedReader br = new BufferedReader(new FileReader(vectors));
        //  for(String line; (line = br.readLine()) != null; ) {
        //    for (Vector v : new Gson().fromJson(line, Vector[].class)) {

        for (Vector v : t.vs) {
          if (!sequences.containsKey(v.parent_id)) {
            ArrayList<Vector> tmp = new ArrayList<>();
            sequences.put(v.parent_id, tmp);
          }

          for (Integer n : v.vector)
            if (n > max)
              max = n;

          sequences.get(v.parent_id).add(v);
        }
        //  }
        ContextDSL cntx_dsl = null;
        //      br.close();

        for (Entry<Integer, ArrayList<Vector>> s : sequences.entrySet()) {
          cntx_dsl = new ContextDSL(s.getValue(), root);  
          cntx_dsl.execute();
          output.addAll(cntx_dsl.getData());
        }
        
        DSL.send(new Gson().toJson(output), "", filename);

      System.err.println("max: "+max+" time: "+(System.currentTimeMillis() - start1)+" size: "+output.size());
    } catch (Exception e) {
      e.printStackTrace();
    } 
    
    return output;
  }

  public static ArrayList<HashMap<String, String>> filter_through_npi(ArrayList<HashMap<Integer, Step>> context, Classifier t, String executor_command) throws NumberFormatException, Exception {
//    StringBuffer sb = new StringBuffer();
//
//    Process p = Runtime.getRuntime().exec("python3 /root/ContextToCode/predictor/main.py --do_inference");
//    p.waitFor();
//
//    BufferedReader reader = 
//         new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//    String line = "";           
//    while ((line = reader.readLine())!= null) {
//      sb.append(line+"\n");
//    }
    ArrayList<HashMap<String, String>> snippets = new ArrayList<>();

    if ( context.size() > 0) {
      System.err.println("In");
      //    HashMap<Integer, Step> st = context.get(context.size()-1);
      //    System.err.println(st.get(st.keySet().size()-1).program.get("id").getValue()+" ^ "+st.get(st.keySet().size()-1).additional_info.get("path")
      //        +" ^ "+st.get(st.keySet().size()-1).additional_info.get("line")+" ^ "+st.get(st.keySet().size()-1).additional_info.get("text"));
      Pumpkin pmp = new Pumpkin(new Gson().fromJson(Utils.sendPost(new Gson().toJson(context), "http://78.46.103.68:8081/"), int[].class), context, t);
      return pmp.snippetize(executor_command, snippets);
    } else {
      System.err.println(context);
      return snippets;
    }
  }

/*  public int eval() throws Exception {
	  Classifier t = new Classifier();//(key_, description_, folder_, executor_comand_);
    loadCodeSearch(null, ASC, 2, t, null);

    ArrayList<HashMap<Integer, Step>> output = new ArrayList<>();

    HashMap<Integer, ArrayList<Vector>> sequences = new HashMap<>();
    BufferedReader br = new BufferedReader(new FileReader(t.vectors));
    for(String line; (line = br.readLine()) != null; ) {
      for (Vector v : new Gson().fromJson(line, Vector[].class)) {
        if (!sequences.containsKey(v.parent_id)) {
          ArrayList<Vector> tmp = new ArrayList<>();
          sequences.put(v.parent_id, tmp);
        }

        sequences.get(v.parent_id).add(v);
      }
    }
    ContextDSL cntx_dsl = null;
    br.close();

    for (Entry<Integer, ArrayList<Vector>> s : sequences.entrySet()) {
      cntx_dsl = new ContextDSL(s.getValue(), root);//t.executor_comand);  
      cntx_dsl.execute();
      output.addAll(cntx_dsl.getData());
    }

    //    TF tf = new TF(output, model);
    //    return tf.eval();
    return 0;
  }*/

  public void loadCode(InnerClass[] data, int direction, Classifier t) throws JsonSyntaxException, IOException, InterruptedException {
    if ( direction == DESC )
      ArrayUtils.reverse(data);
    for ( InnerClass c : data ) {
      if (c.matches(t.classes)) {
        for (InnerClass key : t.classes)
          if (c.hasElements(key)) {
            if (key.type.equals("truekey"))
              c.executor_command = key.executor_command;
            else
              c.executor_command = "1";
          }
      } else {
        c.executor_command = Executor.NOT_CONNECT;
      }
    }
    code = data;
  }
  
  public void iterateCode(InnerClass[] code, Classifier t, String path, HashSet<String> commands, ArrayList<Vector[]> res, int limit) {
	  for (int line = code.length-1; line >= 0; line --) {
		  if ( (!t.blocking && line == code.length-1) || (/*TRIN*/t.blocking  && code[line].matches(t.classes)) ) {
			  Vector[] snip = Parser.getSnippet(line, code, commands, path, t.classes, good_types, bad_types, limit);
			  if (snip.length > 0) {
				  res.add(snip);
			  }
		  }
	  }
  }

//  public static Comparator<PopularItem> comparator_desc = new Comparator<PopularItem>() {
//    public int compare(PopularItem o1, PopularItem o2) {
//      int c = o2.count.compareTo(o1.count);
//      return c;
//    }
//  };

  public void hotEncode(HashSet<String> commands, String hots, ArrayList<Vector[]> res, Classifier t) throws IOException {
    HashMap<String, Double> hot_ecnoding = new HashMap<>();
System.err.println(res);
    Double count = 0.0;
    File f = new File(hots);
    if(f.exists() && !f.isDirectory()) { 
      hot_ecnoding = new Gson().fromJson(new FileReader(hots), HashMap.class);
      count = hot_ecnoding.values().size()*1.0;
    }
    for (String comm : commands) {
      if (!hot_ecnoding.containsKey(comm)) {
        hot_ecnoding.put(comm, count);
        count++;
      }
    }
    Utils.saveJsonFile(hots, hot_ecnoding);
    
    for (Vector[] c : res) {
      for (Vector v : c) {
        v.vectorize(hot_ecnoding);
        t.vs.add(v);
      }
    }
  }

  public void snippetize() throws JsonSyntaxException, IOException {
    int summ = 0;

    try {
      ArrayList<Vector> vs = new ArrayList<>();//loadCodeSearch(null, DESC, 3, null);
      
      HashMap<Integer, HashMap<String, Integer>> pre_snippet = new HashMap<>();
      HashMap<Integer, HashMap<String, Integer>> snippet = new HashMap<>();

      
//      HashMap<Integer, ArrayList<Vector>> sequences = new HashMap<>();
      for (Vector v : vs) {

        if (!pre_snippet.containsKey(v.getLevel())) {
          HashMap<String, Integer> temp = new HashMap<String, Integer>();
          pre_snippet.put(v.getLevel(), temp);
        }
        
        if (!pre_snippet.get(v.getLevel()).containsKey(v.getOrign()))
          pre_snippet.get(v.getLevel()).put(v.getOrign(), 0);
        
        pre_snippet.get(v.getLevel()).put(v.getOrign(), pre_snippet.get(v.getLevel()).get(v.getOrign()) + 1);
        if (v.getLevel() == 0)
          summ+=1;

//        if (!sequences.containsKey(v.parent_id)) {
//          ArrayList<Vector> tmp = new ArrayList<>();
//          sequences.put(v.parent_id, tmp);
//        }
//
//        for (Integer n : v.vector)
//          if (n > max)
//            max = n;
//
//        sequences.get(v.parent_id).add(v);
      }
      
      for (Entry<Integer, HashMap<String, Integer>> ps : pre_snippet.entrySet()) {
        HashMap<String, Integer> s = new HashMap<>();
        
        for (Entry<String, Integer> p : ps.getValue().entrySet()) 
          if (p.getValue() > 5)
            s.put(p.getKey(), p.getValue());

        snippet.put(ps.getKey(), Utils.sortByValue(s));
      }
      
      Utils.writeFile1(new Gson().toJson(snippet), root+"/pre_snippet", false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

class PopularType {
  private String raw;
  private String actual;

  public PopularType(InnerClass t, ArrayList<String> bad_types) {
    raw = t.line_text;
    actual = t.getLine(bad_types);
  }

  public PopularType(String text) {
    raw = text;
    actual = text;
  }

  public int hashCode () {
    return actual.hashCode();
  }
  
  public String toString() {
    return "actual: '"+actual+"', raw: '"+raw+"'";
  }

  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof PopularType))
      return false;
    return this.actual.equals(((PopularType) obj).actual);
  }
}

class PopularIdentifier {
	private String raw;
	private String actual;

	public PopularIdentifier(InnerClass t, ArrayList<String> bad_types) {
		raw = t.line_text;
		actual = t.getLine(bad_types);
	}

	public PopularIdentifier(String text) {
		raw = text;
		actual = text;
	}

	public int hashCode () {
		return actual.hashCode();
	}

	public String toString() {
		return "actual: '"+actual+"', raw: '"+raw+"'";
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof PopularIdentifier))
			return false;
		return this.actual.equals(((PopularIdentifier) obj).actual);
	}
}