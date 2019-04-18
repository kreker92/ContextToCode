package tmt.util;

import tmt.dsl.formats.context.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class Cache {
    private HashMap<ArrayList<Vector>, ArrayList<HashMap<String, String>>> cache = new HashMap<ArrayList<Vector>, ArrayList<HashMap<String, String>>>();

    public ArrayList<HashMap<String, String>> validate(ArrayList<Vector> vs) {
        System.err.println("$read: "+vs.size());
        return cache.getOrDefault(vs, null);
    }

    public void add(ArrayList<Vector> vs, ArrayList<HashMap<String, String>> snpt) {
        System.err.println("$add: "+vs.size());
        cache.put(vs, snpt);
    }
}

class CacheEntry {
    ArrayList<HashMap<String, String>> snippets;
    Boolean valid;
}
