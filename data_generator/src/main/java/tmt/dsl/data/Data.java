package tmt.dsl.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tmt.dsl.executor.info.Step;
import tmt.dsl.formats.context.ContextDSL;
import tmt.dsl.formats.context.Vector;
import tmt.dsl.formats.detection_log.Event;
import tmt.dsl.formats.detection_log.EventAtMinute;
import tmt.dsl.formats.detection_log.LogDSL;
import tmt.dsl.formats.redirect.RedirectDSL;
import tmt.dsl.formats.redirect.RedirectData;
import tmt.dsl.formats.redirect.RedirectUtils;
import tmt.dsl.formats.redirect.RedirectsJson;

import com.google.gson.Gson;

public class Data {

  public static final int limit = 15000;
  private static String dsl_buffer = "/root/data/dsl_buffer/";
  
  public static void main(String[] args) {
    int count = 0;
    try {
      if (args[0].equals("generate_redirects")) {
        Map<String,Collection<RedirectData>> redirects = new HashMap<String, Collection<RedirectData>>();
        HashSet<String> queries = new HashSet<String>();      

        redirects = RedirectUtils.loadRedirects(
            new Gson().fromJson(new FileReader(dsl_buffer+"redirects.json"), RedirectsJson[].class));
        for ( ArrayList<String> r : new Gson().fromJson(new FileReader(dsl_buffer+"all_requests.json"), ArrayList[].class)) {
          if (count > limit)
            break;
          queries.add(r.get(0).trim().toLowerCase());
          count ++;
        }

        Set<String> qs = new HashSet<String>(queries);
        ArrayList<String> inputs = new ArrayList<String>(qs);

        Integer[] outputs = new Gson().fromJson(new FileReader("/root/data/dsl_buffer/answers.json"), Integer[].class);
    
        RedirectDSL rdsl = new RedirectDSL(inputs, new ArrayList<Integer>(Arrays.asList(outputs)), redirects);
        rdsl.check_redirects();
      } else if (args[0].equals("log")) {
        String client = args[2];
        int client_id =  Integer.parseInt(args[3]);
        String command = args[1];
        
        ArrayList<Event> events = new ArrayList<Event>();
        BufferedReader br = new BufferedReader(new FileReader("/root/NeuralProgramSynthesis/dsl/data/logs/"+client+".json"));
        for(String line; (line = br.readLine()) != null; ) {
          Event e = new Gson().fromJson(line.replace("@timestamp", "timestamp"), Event.class);
          if (e._source != null) {
            if (count > limit)
                break;
            events.add(e);
            e.setTimes();
            e.setId(count);
          }
          count++;
        }
        br.close();
        
        HashMap<EventAtMinute, EventAtMinute> inputs = new HashMap<EventAtMinute, EventAtMinute>();
        
        for (Event e : events) {
          EventAtMinute ev = new EventAtMinute(e);
          if (!inputs.containsKey(ev))
            inputs.put(ev, ev);
          else
            inputs.get(ev).update(ev);
        }

        ArrayList<Integer> outputs = null;
                
        LogDSL log_dsl = new LogDSL(new ArrayList<EventAtMinute>(inputs.values()), outputs, client, command, client_id);
        log_dsl.execute(command);
      } else if (args[0].equals("context")) {
        int max = 0;

        ArrayList<HashMap<Integer, Step>> output = new ArrayList<>();
        
        HashMap<Integer, ArrayList<Vector>> sequences = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader("/root/ContextToCode/output/funcs/vectors"));
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
        System.err.println("*"+max);
        for (Entry<Integer, ArrayList<Vector>> s : sequences.entrySet()) {
          cntx_dsl = new ContextDSL(s.getValue());  
          cntx_dsl.execute();
          for (HashMap<Integer, Step> v : cntx_dsl.getData()){
            count += v.keySet().size();
          }
          output.addAll(cntx_dsl.getData());
        }
        
        /*HashMap<EventAtMinute, EventAtMinute> inputs = new HashMap<EventAtMinute, EventAtMinute>();
        
        for (Event e : events) {
          EventAtMinute ev = new EventAtMinute(e);
          if (!inputs.containsKey(ev))
            inputs.put(ev, ev);
          else
            inputs.get(ev).update(ev);
        }*/

//        ArrayList<Integer> outputs = null;
//         
        cntx_dsl.send(new Gson().toJson(output), "");
        System.err.println(output.size()+" * "+          count);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
}