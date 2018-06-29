package tmt.export;

import tmt.code.snippets.stackoverflow.Row;
import tmt.conf.Conf;

public class Detectum {
  public Detectum() {
    
  }
  
  public void export () {
    Row p = Conf.posts.get(1000);
    Row a = Conf.answers.get(p.getId()).get(1);

    System.err.println("!!!p.body: "+p.getBody()+" p.title: "+p.getTitle()+" a.body "+a.getBody()+" a.stripped "+a.getStripped()+" a.tags "+a.getTags()+" a.code "+a.getCode()+" a.score "+a.getScore());
  }

}
