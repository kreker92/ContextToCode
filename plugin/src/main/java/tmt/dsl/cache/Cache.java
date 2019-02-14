package tmt.dsl.cache;

import java.util.HashMap;

public class Cache {
    HashMap<String, CacheEntry> cache = new HashMap<String, CacheEntry>();

    public String getResponse (String request) {
        return null;
    }
}

class CacheEntry {
    String response;
    Boolean valid;
}
