package tmt.code.validate;

import java.io.IOException;
import java.util.ArrayList;

import tmt.conf.Utils;

public class Validate {

  public void validate(ArrayList<String> code) {
    for (String c : code) 
      for (String l : c.split("\\r?\\n")) {
        System.err.println(Utils.searchGhub(l.replace(":", "").replace("=", "").trim()));
      }
    System.err.println("!!!"+code);
    System.exit(1);
  }

}
