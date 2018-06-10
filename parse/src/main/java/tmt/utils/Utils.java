package tmt.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Utils {

  public static ArrayList<String> parse (String heystack, String delim1, String delim2){
    if (heystack != null) {
      String[] res = StringUtils.substringsBetween(heystack, delim1, delim2);

      if (res != null)
        return new ArrayList<String>(Arrays.asList(res));
    }

    return new ArrayList<String>();
  }
}
