package tmt.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;

import tmt.code.snippets.stackoverflow.Row;
import tmt.conf.Conf;

public class Dt {
  public Dt() {

  }

  public void export () {
    HashMap<Integer, ArrayList<Row>> coded_answers = new HashMap<Integer, ArrayList<Row>>();

    int count = 0;
    for (Row p : Conf.posts.values()) {
      if (Conf.answers.containsKey(p.getId())) {
        ArrayList<Row> temp = new ArrayList<Row>();
        for (Row a : Conf.answers.get(p.getId())) {
          if (!a.getCode().isEmpty() && a.getScore() >= 0)
            temp.add(a);
        }
        if (!temp.isEmpty()) {
          coded_answers.put(p.getId(), temp);
          count ++;
        }
      }
    }
    
    for (Entry<Integer, ArrayList<Row>> for_export : coded_answers.entrySet()) {
      dump_file(for_export.getValue());
    }

    System.err.println(count+"!!!");
  }

  private void dump_file(ArrayList<Row> answers) {
    Row a = answers.get(0);
    DtJson dtj = new DtJson(a.getId(), a.getStripped().replace("\n", "").replace("\r", "").replace("\t", "")); 
    
    for (String t : a.getTags())
      dtj.addParam("tag", t);

    dtj.addParam("model", a.getTitle());
    
    for (String t : a.getCode())
      dtj.addParam("code", t);
    
    System.err.println(new Gson().toJson(dtj)+" - "+a);
    System.exit(1);
  }

}

class DtJson {
  int item_id;
  String descr;
  int quantity = 1;
  String price = "1";
  ArrayList<ArrayList<String>> params = new ArrayList<>();
  String shop_item_id = "";
  String img_url = "";
  ArrayList<String> categories = new ArrayList<>();
  
  public DtJson(int id, String stripped) {
    item_id = id;
    descr = stripped;
  }

  public void addParam(String key, String val) {
    ArrayList<String> temp = new ArrayList<>();
    temp.add(key);
    temp.add(val);
    params.add(temp);
  }
}
