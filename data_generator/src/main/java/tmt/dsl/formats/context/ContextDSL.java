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

  public ContextDSL(ArrayList<Vector> inputs) {
    super("context");
    ExecData d = new ExecData();

    int commands = 0;
    for (Vector v : inputs) {
      int count = 0;
      if (commands > 0)
        d.next_step();
      
      for (Integer i : v.vector) {
        d.toEnvironment("param_"+count, i+"", true);
        System.err.println("param_"+count+", "+i+"");
        count++;
      }
      commands++;
      
      if (v.level==0) {
        d.toProgram("id", Executor.CONNECT);
        d.toProgram("program", "connect");
      } else {
        d.toProgram("id", Executor.NOT_CONNECT);
        d.toProgram("program", "not_connect");
      }
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

