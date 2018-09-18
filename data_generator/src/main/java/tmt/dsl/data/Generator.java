package tmt.dsl.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import tmt.dsl.executor.info.Step;
import tmt.dsl.formats.context.ContextDSL;
import tmt.dsl.formats.context.Parser;
import tmt.dsl.formats.context.Vector;
import tmt.dsl.formats.context.in.InnerContext;
import tmt.dsl.tensorflow.TF;

import com.google.gson.Gson;
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

  public static int limit;
  
  public static String dsl_buffer;
  public static Gson gson;
  public static String key;
  public static String query;
  public static String root_key;
  public static String root;
  public static String vectors;
  public static String model;
  
  public static ArrayList<String> good_types;
  public static ArrayList<String> bad_types;

  public static void setTrainAndTest() throws Exception{
    int count = 0;
    
    try {
      int max = 0;
      loadCodeSearch();

      ArrayList<HashMap<Integer, Step>> output = new ArrayList<>();

      HashMap<Integer, ArrayList<Vector>> sequences = new HashMap<>();
      BufferedReader br = new BufferedReader(new FileReader(vectors));
      for(String line; (line = br.readLine()) != null; ) {
        for (Vector v : new Gson().fromJson(line, Vector[].class)) {
          if (!sequences.containsKey(v.parent_id)) {
            ArrayList<Vector> tmp = new ArrayList<>();
            sequences.put(v.parent_id, tmp);
          }

          for (Integer n : v.vector)
            if (n > max)
              max = n;

          sequences.get(v.parent_id).add(v);
        }
      }
      ContextDSL cntx_dsl = null;
      br.close();
      
      for (Entry<Integer, ArrayList<Vector>> s : sequences.entrySet()) {
        cntx_dsl = new ContextDSL(s.getValue(), root+root_key);  
        cntx_dsl.execute();
        for (HashMap<Integer, Step> v : cntx_dsl.getData()){
          count += v.keySet().size();
        }
        output.addAll(cntx_dsl.getData());
      }

      cntx_dsl.send(new Gson().toJson(output), "");
      System.err.println("max:"+max+" * "+output.size()+" * "+          count);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

  public int eval() throws Exception {
    loadCodeSearch();

    ArrayList<HashMap<Integer, Step>> output = new ArrayList<>();

    HashMap<Integer, ArrayList<Vector>> sequences = new HashMap<>();
    BufferedReader br = new BufferedReader(new FileReader(vectors));
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
      cntx_dsl = new ContextDSL(s.getValue(), root+root_key);  
      cntx_dsl.execute();
      output.addAll(cntx_dsl.getData());
    }

//    TF tf = new TF(output, model);
//    return tf.eval();
    return 0;
  }

  private static void loadCodeSearch() throws JsonSyntaxException, IOException, InterruptedException {
    //      ArrayList<String> code = new ArrayList<String>(Arrays.asList(Utils.readFile("/root/toCode/output/cs/66533677").split("\n")));
    ArrayList<Vector[]> res = new ArrayList<>();
    HashSet<String> commands = new HashSet<>();
    HashMap<String, Integer> hot_ecnoding = new HashMap<>();

    System.err.println("!"+root+root_key);
    File file = new File(root+root_key+"/context.json"); 
    file.delete();
    file = new File(root+root_key+"/log.json"); 
    file.delete();

    File[] files = new File(root+root_key).listFiles();
    for (File f : files) {
      if (f.getPath().contains("psi") || key != null) {
        InnerContext[] code = gson.fromJson(Utils.readFile(f.getPath()), InnerContext[].class);
        for (int line = code.length-1; line >= 0; line --) {
          if (key == null && line == code.length-1 || /*TRIN*/key != null  && code[line].line_text.contains(key) ) {
            Vector[] snip = Parser.getSnippet(line, code, commands, f.getPath(), line, key, good_types, bad_types);
            if (snip.length > 0)
              res.add(snip);
          }
        }
      }
    }
    
    hot_ecnoding = hotEncode(commands);

    ArrayList<Vector> output = new ArrayList<>();
    for (Vector[] c : res) {
      for (Vector v : c) {
        v.vectorize(hot_ecnoding);
        output.add(v);
      }
    }
    
    System.err.println(commands+" ^ "+hot_ecnoding);
    Utils.saveJsonFile(vectors, output);
  }

  private static HashMap<String, Integer> hotEncode(HashSet<String> commands) throws IOException {
    HashMap<String, Integer> hot_ecnoding = new HashMap<>();

    int count = 0;
    File f = new File("/root/ContextToCode/output/buffer/hots");
    if(f.exists() && !f.isDirectory()) { 
        return new Gson().fromJson(new FileReader("/root/ContextToCode/output/buffer/hots"), HashMap.class);
    }
    else {
        for (String comm : commands) {
          hot_ecnoding.put(comm, count);
          count++;
        }
        Utils.saveJsonFile("/root/ContextToCode/output/buffer/hots", hot_ecnoding);
        return hot_ecnoding;
    }
  }
}