package tmt.code.snippets.stackoverflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;

import tmt.conf.Utils;

public class Dt {
  private HashMap<String, String> ids = new HashMap<>();
  public Dt() {

  }

  public void export () {
    HashMap<Integer, ArrayList<Row>> coded_answers = new HashMap<Integer, ArrayList<Row>>();

    int count = 0;
    ArrayList<Row> best = new ArrayList<Row>(Conf.posts.values());
    Collections.sort(best, Utils.comparator_score_desc);

    for (Row p : best) {
      if (Conf.answers.containsKey(p.getId())) {
        ArrayList<Row> temp = new ArrayList<Row>();
        for (Row a : Conf.answers.get(p.getId())) {
          if (!a.getCode().isEmpty() && a.getScore() >= 0)
            temp.add(a);
        }
        if (!temp.isEmpty()) {
          Collections.sort(temp, Utils.comparator_score_desc);
          coded_answers.put(p.getId(), temp);
        }
      }
    }

    for (Row p : best) {
      if (count > 100000)
        break;
      if (coded_answers.containsKey(p.getId())) {
        String id = dump_file(coded_answers.get(p.getId()).get(0));
        ids.put(id, id);
        count ++;
        System.err.println(count+"!!!");
      }
    }
    try {
      Utils.saveJsonFile ("/root/detectum-java/search/moshoztorg/item_id_to_shop_id.json", ids);
    } catch (Exception e) {
      e.printStackTrace();
      //throw new RuntimeException(e);
    }
  }

  private String dump_file(Row a) {
    DtJson dtj = new DtJson(a.getId(), a.getPost().getStripped().replace("\n", "").replace("\r", "").replace("\t", "")); 

    for (String t : a.getPost().getTagsArr())
      dtj.addParam("tag", t);

//    dtj.addParam("model", a.getPost().getTitle());

    dtj.addParam("code", a.getFunc());

    try {
      Utils.saveJsonFile ("/root/detectum-java/search/moshoztorg/articles_new/"+a.getId()+".json", dtj);
    } catch (Exception e) {
      e.printStackTrace();
      //throw new RuntimeException(e);
    }
    return a.getId()+"";
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
  ArrayList<Cat> categories = new ArrayList<Cat>();

  public DtJson(int id, String stripped) {
    item_id = id;
    descr = stripped;
    categories.add(new Cat());
  }

  public void addParam(String key, String val) {
    ArrayList<String> temp = new ArrayList<>();
    temp.add(key);
    temp.add(val);
    params.add(temp);
  }
}

class Cat {
  int id = 694;
  String name = "";
  int level = 1;
  String shop_category_id = "471";
}
