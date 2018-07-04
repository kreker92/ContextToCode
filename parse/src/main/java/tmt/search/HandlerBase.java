package tmt.search;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;

public abstract class HandlerBase extends DefaultHandler {
      
  public static String getClientIP(NanoHTTPD.IHTTPSession session) {
      
    String clientIP = session.getHeaders().get("http-client-ip");
    String xff = session.getHeaders().get("x-forwarded-for");
    if ( xff != null ) {
      xff = xff.replace(" ", "");
      clientIP = xff + "," + clientIP;
    }
    return clientIP;
  }

  @Override
  public String getText() {
    return "not implemented";
  }

  public NanoHTTPD.Response.IStatus getStatus() {
    return NanoHTTPD.Response.Status.OK;
  }

  public String getMimeType() {
    return "application/json;charset=UTF-8";
  }    
}
