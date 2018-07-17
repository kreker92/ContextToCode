package tmt.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import tmt.code.snippets.stackoverflow.Row;
import tmt.conf.Conf;
import tmt.conf.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

public class SaveToFile {

  private static HashSet<Integer> ids = new HashSet<Integer>();

  public static void main(String[] args) throws FileNotFoundException, IOException {
//    saveToChunks();
    mergeChunks();
  }
  
  private static void mergeChunks() throws JsonIOException, JsonSyntaxException, IOException {
    Gson gson = new Gson();
    int count = 1;
  /*  HashMap<Integer, ArrayList<Row>> answers = new HashMap<>();

    File f = new File(Conf.answers_output.replace("?", count+""));
    while(f.exists() && !f.isDirectory()) { 
      HashMap<Integer, ArrayList<Row>> temp = gson.fromJson(new FileReader(f), Conf.gson_answers);
      
      for ( Entry<Integer, ArrayList<Row>> t : temp.entrySet() ) {
        if (answers.containsKey(t.getKey()))
          answers.get(t.getKey()).addAll(t.getValue());
        else
          answers.put(t.getKey(), t.getValue());
      }

      count ++; 
      f = new File(Conf.answers_output.replace("?", count+""));
    }
    
    Utils.save(Conf.answers_output.replace("?", "_all"), answers); */
    ArrayList<Row> posts = new ArrayList<Row>();
    File f = new File(Conf.posts_output.replace("?", count+""));
    while(f.exists() && !f.isDirectory()) { 
      Row[] temp = gson.fromJson(new FileReader(f), Row[].class);
      
      for ( Row t : temp ) {
          posts.add(t);
      }

      count ++; 
      f = new File(Conf.posts_output.replace("?", count+""));
    }
    
    Utils.saveJsonFile(Conf.posts_output.replace("?", "_all"), posts);

  }

  public static void saveToChunks() throws FileNotFoundException, IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(Conf.input))) {
      String line;

      int posts = 0;
      int answs = 0;

      while (((line = br.readLine()) != null)){// && posts < 100000) {
        try {
          JAXBContext jaxbContext = JAXBContext.newInstance(Row.class);

          Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
          StringReader reader = new StringReader(line);

          Row row = (Row) jaxbUnmarshaller.unmarshal(reader);
          row.init(null);

          if (!row.getTags().isEmpty() && row.getTags().contains("java")){
            Conf.posts.put(row.getId(), row);
            ids.add(row.getId());
          } else if (ids.contains(row.getParentId())) {
            if (!Conf.answers.containsKey(row.getParentId())) {
              ArrayList<Row> temp = new ArrayList<Row>();
              Conf.answers.put(row.getParentId(), temp);
            }
            Conf.answers.get(row.getParentId()).add(row);
            answs ++;
          }
          System.err.println("Posts: "+posts+" answers: "+answs);
          
          if ( posts%Conf.chunk == 0 ) {
            chunk(posts/Conf.chunk);
            Conf.posts.clear();
            Conf.answers.clear();
          }
          posts ++;
        } catch (JAXBException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  private static void chunk(int i) throws IOException {
    Utils.saveJsonFile(Conf.posts_output.replace("?", i+""), Conf.posts);
    Utils.saveJsonFile(Conf.answers_output.replace("?", i+""), Conf.answers);
  }
}




