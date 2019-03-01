package tmt.dsl;

import java.util.ArrayList;

import tmt.dsl.formats.context.Vector;
import tmt.dsl.formats.context.in.InnerClass;

public class Classifier {
  public ArrayList<InnerClass> classes = new ArrayList<InnerClass>();

  public String folder;
  public String vectors;

  public boolean blocking = true;

  public ArrayList<Vector> vs = new ArrayList<>();

  public String domain;

  public Classifier(String folder_) {
    folder = folder_;
  }

  public Classifier() {
    // TODO Auto-generated constructor stub
  }

  public Classifier(Classifier t) {
    this.classes = new ArrayList<>(t.classes);
    this.folder = t.folder;
    this.vectors = t.vectors;
    this.blocking = t.blocking;
    this.vs = new ArrayList<>(t.vs);
    this.domain = t.domain;
  }

  public void clear() {
    vs.clear();
    vectors = "";
  }

  public String toString() {
    return "Domain: "+domain+", vs: "+vs+", classes: "+classes;
  }
}