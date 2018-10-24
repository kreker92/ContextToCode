package tmt.dsl;

import java.util.ArrayList;

import tmt.dsl.formats.context.Vector;
import tmt.dsl.formats.context.in.InnerContext;

public class Template {
	public ArrayList<InnerContext> keys = new ArrayList<InnerContext>();
	public String description;
	public String snippet;
	public InnerContext true_key;

	public String folder;
	public String vectors;
	public String model;
	public ArrayList<Vector> vs = new ArrayList<>();
	
	public Template(String description_, String folder_) {
	  description = description_;
	  folder = folder_;
	}
	
	public Template() {
		// TODO Auto-generated constructor stub
	}
}