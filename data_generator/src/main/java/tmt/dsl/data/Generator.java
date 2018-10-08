package tmt.dsl.data;

import java.io.BufferedReader;
import java.io.File;
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

import java.nio.file.Files;

import tmt.dsl.executor.info.Step;
import tmt.dsl.formats.context.ContextDSL;
import tmt.dsl.formats.context.Parser;
import tmt.dsl.formats.context.Vector;
import tmt.dsl.formats.context.in.ElementInfo;
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

  public static int setTrainAndTest(InnerContext[] data) throws Exception{
    int count = 0;
    int res = 0;
    
    try {
      long start1 = System.currentTimeMillis();

      int max = 0;
      ArrayList<Vector> vs = loadCodeSearch(data);

      ArrayList<HashMap<Integer, Step>> output = new ArrayList<>();

      HashMap<Integer, ArrayList<Vector>> sequences = new HashMap<>();
      BufferedReader br = new BufferedReader(new FileReader(vectors));
    //  for(String line; (line = br.readLine()) != null; ) {
    //    for (Vector v : new Gson().fromJson(line, Vector[].class)) {
		for (Vector v : vs) {
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
      
	  System.out.println("time: "+(System.currentTimeMillis() - start1)+" timestamp: "+new Timestamp(System.currentTimeMillis()));
      long start = System.currentTimeMillis();
      
      /*StringBuffer sb = new StringBuffer();
      
      Process p = Runtime.getRuntime().exec("python3 /root/ContextToCode/predictor/main.py --do_inference");
      p.waitFor();

      BufferedReader reader = 
           new BufferedReader(new InputStreamReader(p.getInputStream()));

      String line = "";           
      while ((line = reader.readLine())!= null) {
        sb.append(line+"\n");
      }

      String str = sb.toString();*/
      
      res = Integer.parseInt(Utils.readUrl("http://78.46.103.68:8081/"));
      System.out.println("max:"+res+" time:"+(System.currentTimeMillis() - start));
    } catch (Exception e) {
      e.printStackTrace();
    } 
    
    return res;
  }

  public static int getPattern(InnerContext[] data) throws Exception{
    int res = 0;

    try {
      long start1 = System.currentTimeMillis();

      int max = 0;
      ArrayList<Vector> vs = loadCodeSearch(data);
      System.err.println(vs);

      ArrayList<HashMap<Integer, Step>> output = new ArrayList<>();

      HashMap<Integer, ArrayList<Vector>> sequences = new HashMap<>();
    //  BufferedReader br = new BufferedReader(new FileReader(vectors));
      for (Vector v : vs) {
        if (!sequences.containsKey(v.parent_id)) {
          ArrayList<Vector> tmp = new ArrayList<>();
          sequences.put(v.parent_id, tmp);
        }

        for (Integer n : v.vector)
          if (n > max)
            max = n;

        sequences.get(v.parent_id).add(v);
      }
      ContextDSL cntx_dsl = null;
      //br.close();
      for (Entry<Integer, ArrayList<Vector>> s : sequences.entrySet()) {
        cntx_dsl = new ContextDSL(s.getValue(), root+root_key);  
        cntx_dsl.execute();
        output.addAll(cntx_dsl.getData());
      }

      cntx_dsl.send(new Gson().toJson(output), "");

     /* System.out.println("time: "+(System.currentTimeMillis() - start1)+" timestamp: "+new Timestamp(System.currentTimeMillis()));
      long start = System.currentTimeMillis();

      res = Integer.parseInt(Utils.readUrl("http://78.46.103.68:8081/"));
      System.out.println("max:"+res+" time:"+(System.currentTimeMillis() - start));*/
    } catch (Exception e) {
      e.printStackTrace();
    } 

    return res;
  }


  public int eval() throws Exception {
    loadCodeSearch(null);

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

  private static ArrayList<Vector> loadCodeSearch(InnerContext[] data) throws JsonSyntaxException, IOException, InterruptedException {
    //      ArrayList<String> code = new ArrayList<String>(Arrays.asList(Utils.readFile("/root/toCode/output/cs/66533677").split("\n")));
    ArrayList<Vector[]> res = new ArrayList<>();
    HashSet<String> commands = new HashSet<>();
    HashMap<String, Double> hot_ecnoding = new HashMap<>();
    HashMap<String, Integer> popular_lines = new HashMap<>();
    HashMap<String, Integer> popular_commands = new HashMap<>();

    System.err.println("!"+root+root_key);
    File file = new File(root+root_key+"/context.json"); 
    file.delete();
    file = new File(root+root_key+"/log.json"); 
    file.delete();
        file = new File(root+root_key+"/pop_comm"); 
    file.delete();
        file = new File(root+root_key+"/pop_lines"); 
    file.delete();
    
    int count = 0;
    int top = 0;
    int all = 0;

    File[] files = new File(root+root_key).listFiles();
    for (File f : files) {
     // if (f.getPath().contains("psi") || key != null) {
        
        InnerContext[] code;
       // System.err.println(f.getPath());
        if (data == null)
          code = gson.fromJson(Utils.readFile(f.getPath()), InnerContext[].class);
        else
          code = data;

        for (InnerContext c : code ) {
            if (!c.elements.isEmpty() && !c.line_text.contains("import ")) {
                String t = c.getLine(bad_types);
              //  if (t.equals("catch remoteexception e"))
                //    System.err.println(c.line_text);

                if (popular_lines.containsKey(t)) {
                    popular_lines.put(t, popular_lines.get(t)+1);
                }
                else
                    popular_lines.put(t, 1);
                for (ElementInfo e : c.elements ) {
                    if (popular_commands.containsKey(e.text))
                        popular_commands.put(e.text, popular_commands.get(e.text)+1);
                    else
                        popular_commands.put(e.text, 1);
                }
            }
    	}
        
        for (int line = code.length-1; line >= 0; line --) {
        	if (key == null && line == code.length-1 || /*TRIN*/key != null  && code[line].line_text.contains(key) ) {
        		Vector[] snip = Parser.getSnippet(line, code, commands, f.getPath(), line, key, good_types, bad_types);
        		if (snip.length > 0)
        			res.add(snip);
        	}
        }
//      }
    }
            int counter = 0;
            for (Integer c : popular_lines.values() ) {
                counter ++;
                if (c > 1)
                    count += 1;
                if (counter < 50)
                    top += c;
                all += c;
            }
    
    Utils.writeFile1(sortByValue(popular_commands).toString(), root+root_key+"/pop_comm", false);
    Utils.writeFile1(sortByValue(popular_lines).toString(), root+root_key+"/pop_lines", false);
    //Files.write(root+root_key+"/pop_lines", sortByValue(popular_lines).toString(), Charset.forName("UTF-8"));
    System.err.println(top+" * "+count+" * "+all);
    System.exit(1);
    hot_ecnoding = hotEncode(commands);

    ArrayList<Vector> output = new ArrayList<>();
    for (Vector[] c : res) {
      for (Vector v : c) {
        v.vectorize(hot_ecnoding);
        output.add(v);
      }
    }
        
//    System.err.println(output+" ^ "+hot_ecnoding);
//    System.exit(1);
    //Utils.saveJsonFile(vectors, output);
	return output;
  }
  
  private static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
	    List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
	    Collections.sort(list, new Comparator<Object>() {
	        @SuppressWarnings("unchecked")
	        public int compare(Object o1, Object o2) {
	            return ((Comparable<V>) ((Map.Entry<K, V>) (o2)).getValue()).compareTo(((Map.Entry<K, V>) (o1)).getValue());
	        }
	    });

	    Map<K, V> result = new LinkedHashMap<>();
	    for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }

	    return result;
	}

  private static HashMap<String, Double> hotEncode(HashSet<String> commands) throws IOException {
    HashMap<String, Double> hot_ecnoding = new HashMap<>();

    Double count = 0.0;
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