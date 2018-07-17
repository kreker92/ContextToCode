package tmt.search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Locale;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

class SearchServerImpl extends RouterNanoHTTPD {

  public SearchServerImpl(int port) throws IOException {
    super(port);
  }

  public void addMappings() {
    super.addMappings();
    addRoute("/generate", SearchDefaultHandler.class);
  }

  public void init() {
    try {
      addMappings();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void start() throws IOException {
    super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
  }
}

public class SearchServer {
  private SearchServerImpl ssi;

  public void init(String args[]) {
    try {
      Locale.setDefault(new Locale("en", "US", "UTF-8"));
      Properties props = new Properties();
      props.load(new FileReader(new File(args[0])));
      ssi = new SearchServerImpl(Integer.parseInt(System.getProperty("com.detectum.server.port")));
      ssi.init();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }    
  }

  public void start() throws Exception {
    ssi.start();
  }

  public void stop() {
    ssi.stop();
  }
  
  public void destroy() {
    ssi.stop();
  }
  
  public static void main(String[] args) {
    Properties props = new Properties();
    try {
//      props.load(new FileReader(new File(args[0])));
//      Locale.setDefault(new Locale("en", "US", "UTF-8"));
      SearchServerImpl ssi = new SearchServerImpl(Integer.parseInt(System.getProperty("server.port")));
//      ssi.init(props);
      ssi.init();
      ssi.start();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
