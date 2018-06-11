package tmt.snippets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tmt.stackoverflow.Row;
import tmt.utils.Conf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

public class SaveToFile {

  private static HashSet<Integer> ids = new HashSet<Integer>();

  public SaveToFile() {
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
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
          row.parse();

          if (!row.getTags().isEmpty() && row.getTags().contains("java")){
            Conf.posts.add(row);
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
            save(posts/Conf.chunk);
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
  
  private static void save(int i) throws IOException {
    try (Writer writer = new FileWriter(Conf.posts_output.replace("?", i+""))) {
      Gson gson = new GsonBuilder().create();
      gson.toJson(Conf.posts, writer);
    }
    
    try (Writer writer = new FileWriter(Conf.answers_output.replace("?", i+""))) {
      Gson gson = new GsonBuilder().create();
      gson.toJson(Conf.answers, writer);
    }
  }
}




