package tmt.snippets;

import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tmt.stackoverflow.Row;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

public class Snippets {

  static String  file = "/root/stackoverflow/Posts.xml";
  private static ArrayList<Row> java_rows = new ArrayList<Row>();

  public Snippets() {
  }
  
  public static void main(String[] args) throws FileNotFoundException, IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      int count = 0;
      while ((line = br.readLine()) != null) {// && count < 30) {
        try {
          JAXBContext jaxbContext = JAXBContext.newInstance(Row.class);

          Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
          StringReader reader = new StringReader(line);

          Row row = (Row) jaxbUnmarshaller.unmarshal(reader);
          row.parseTags();
          
          if (!row.getTags().isEmpty() && row.getTags().contains("java")){
            java_rows.add(row);
            count ++;
            System.err.println(count);
          }
        } catch (JAXBException e) {
          e.printStackTrace();
        }
      }
      
      try (Writer writer = new FileWriter("/root/stackoverflow/output.json")) {
        Gson gson = new GsonBuilder().create();
        gson.toJson(java_rows, writer);
    }
    }
  }
}




