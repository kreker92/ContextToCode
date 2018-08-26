package tmt.code.snippets.stackoverflow;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import tmt.conf.Utils;
import tmt.search.github.Github;

public class Validate {

  public String validate(ArrayList<String> code) throws Exception {
    Github gh = new Github();
    for (String c : code) 
      for (String l : c.split("\\r?\\n")) {
        if ((l.replace(":", "").replace("=", "").trim()).length() > 2)
        gh.addResp((l.replace(":", "").replace("=", "").trim()), "google");
//        System.err.println(gh.getItemsPath());
        Thread.sleep(2000);
      }
    
    gh.merge();
    System.err.println(gh.countRelevance(code));
    return gh.countRelevance(code);
  }

}
