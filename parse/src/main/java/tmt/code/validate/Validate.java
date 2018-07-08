package tmt.code.validate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import tmt.conf.Utils;
import tmt.search.github.Github;

public class Validate {

  public void validate(ArrayList<String> code) throws InterruptedException, UnsupportedEncodingException, IOException {
    Github gh = new Github();
    for (String c : code) 
      for (String l : c.split("\\r?\\n")) {
        if ((l.replace(":", "").replace("=", "").trim()).length() > 2)
        gh.addResp((l.replace(":", "").replace("=", "").trim()), "google");
//        System.err.println(gh.getItemsPath());
        Thread.sleep(2000);
      }
    
    gh.merge();
    gh.countRelevance(code);
    System.exit(1);
  }

}
