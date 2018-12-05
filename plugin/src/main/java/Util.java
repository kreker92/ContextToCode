
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.io.*;

import com.google.gson.Gson;

public class Util {

    public static String sendGet(String string) throws Exception {

        URL url = new URL("http://78.46.103.68:1958/generate");

        StringBuilder stringBuilder;

        byte[] postDataBytes = string.getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        int resp = conn.getResponseCode();
        String res = "^";

//        BufferedReader responseStream;
//        if (((HttpURLConnection) conn).getResponseCode() == 200) {
//            responseStream = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//        } else {
//            responseStream = new BufferedReader(new InputStreamReader(((HttpURLConnection) conn).getErrorStream(), "UTF-8"));
//        }
//
//        System.err.println("$"+org.apache.commons.io.IOUtils.toString(res));
        // read the output from the server
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        stringBuilder = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null)
        {
            stringBuilder.append(line + "\n");
        }
        return stringBuilder.toString();
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
