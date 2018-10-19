package tmt.dsl;

import java.util.ArrayList;

import tmt.dsl.formats.context.Vector;

public class Template {
	public ArrayList<String> keys;
	public String description;
	public String snippet;

	public String folder;
	public String vectors;
	public String model;
	public ArrayList<Vector> vs = new ArrayList<>();
	public String executor_comand;
	
	public Template(ArrayList<String> keys_, String description_, String folder_, String executor_comand_) {
		keys = keys_;
		description = description_;
		folder = folder_;
		executor_comand = executor_comand_;
	}

	public Template() {
		// TODO Auto-generated constructor stub
	}
}
