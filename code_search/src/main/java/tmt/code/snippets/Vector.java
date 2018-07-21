package tmt.code.snippets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Vector {
	private ArrayList<Integer> vector = new ArrayList<>();
	private ArrayList<String> strings = new ArrayList<>();
	public int counter = 0;
	private String origin;

	public Vector(String o, HashSet<String> commands) {
		origin = o;
		for ( String s : o.split(" ") )
			if (!s.trim().isEmpty()) {
				strings.add(s.trim());
				commands.add(s.trim());
			}
	}

	public boolean isEmpty() {
		return strings.isEmpty();
	}

	public void vectorize(HashMap<String, Integer> hot_ecnoding) {
		for (String s : strings) {
			vector.add(hot_ecnoding.get(s));
		}
		
		System.err.println(vector);
	}
}
