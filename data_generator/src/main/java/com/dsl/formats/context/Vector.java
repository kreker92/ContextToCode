package com.dsl.formats.context;

import java.util.ArrayList;

public class Vector {
    public ArrayList<Integer> vector = new ArrayList<>();
    public ArrayList<String> strings = new ArrayList<>();
    public Integer row = null;
    public Integer level = null;
    public String origin;
    public int label = 0;
    public int parent_id;
    
    public String toString() {
      return strings+" - "+origin+" - "+level;
    }
}
