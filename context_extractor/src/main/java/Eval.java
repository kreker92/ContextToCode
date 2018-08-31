
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import com.google.gson.Gson;

public class Eval {

    public static String sendGet(String string) throws Exception {

        URL url = new URL("http://example.com");

        Map<String,Object> params = new LinkedHashMap<>();
        params.put("text",  string);
        params.put("analyzer",  "detectum_analyzer");

        String postData = new Gson().toJson(params);
        byte[] postDataBytes = postData.getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        int resp = conn.getResponseCode();
        String res;

        if (resp != 200)
            res = convertStreamToString(conn.getErrorStream());
        else
            res = convertStreamToString(conn.getInputStream());

        return res;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
