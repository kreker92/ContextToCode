package tmt.snippets;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.Normalizer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.CharBuffer;

import org.apache.commons.collections4.iterators.IteratorChain;

import com.google.gson.Gson;

public class Snippets {
  
  String  file = "/root/stackoverflow/Posts.xml";
  
  public Snippets() throws Exception {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
         // process the line.
      }
  }
  }
}




