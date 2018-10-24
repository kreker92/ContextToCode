package tmt.dsl.formats.context;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

import tmt.dsl.DSL;
import tmt.dsl.executor.Executor;
import tmt.dsl.executor.info.ExecData;
import tmt.dsl.executor.info.Step;

public class ContextDSL extends DSL {

  HashMap<Integer, String> qoh = new HashMap<Integer, String>();
  ArrayList<ExecData> data = new ArrayList<ExecData>();
  Executor exec = new Executor();

  public ContextDSL(ArrayList<Vector> inputs, String path) {
    super(path);
    ExecData d = new ExecData();

    int commands = 0;
    for (Vector v : inputs) {
      int count = 0;
      if (commands > 0)
        d.next_step();
      
      for (Integer i : v.vector) {
        d.toEnvironment("param_"+count, i+"", true, v.getParent());
        System.err.println("param_"+count+", "+i+"");
        count++;
      }
      commands++;
      
      for (int i = 0; i < v.vector.size(); i++) {  //Integer i : v.vector) {
    	  d.toAddInfo(v.vector.get(i)+"", v.strings.get(i));
      }
	  d.toAddInfo("label", inputs.hashCode()+"");
	  d.toAddInfo("path", v.path);
	  d.toAddInfo("line", v.line_num+"");
	  d.toAddInfo("text", v.getOrign());
      
      d.toProgram("id", v.getProgram()+"");
      System.err.println("#"+v.getProgram());
      if (!v.getProgram().equals("1")) 
        d.toProgram("program", "connect");
      else 
        d.toProgram("program", "not_connect");
    }
    data.add(d);
  }

  public void execute()throws Exception {
    for (ExecData d : data) {
     // exec.prepare_data(d);
     // exec.validate(d);

      d.flush_buffer(out);
      d.clear();
    }
  }
  
  public ArrayList<HashMap<Integer, Step>> getData() {
    return out;
  }
}

