package tmt.dsl;

import java.util.ArrayList;

import tmt.dsl.formats.context.Vector;
import tmt.dsl.formats.context.in.InnerClass;

public class Classifier {
  public ArrayList<InnerClass> classes = new ArrayList<InnerClass>();

  public String folder;
  public String vectors;
  public String model;

  public boolean blocking = true;

  public ArrayList<Vector> vs = new ArrayList<>();

  public Classifier(String folder_) {
    folder = folder_;
  }

  public Classifier() {
    // TODO Auto-generated constructor stub
  }

  public void clear() { 
    vs.clear();
    vectors = "";
  }
}