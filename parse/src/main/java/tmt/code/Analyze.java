package tmt.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import tmt.code.snippets.stackoverflow.Row;
import tmt.conf.Conf;
import tmt.conf.Utils;

public class Analyze {
	public static void main(String[] args) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		Gson gson = new Gson();

		File f = new File(Conf.answers_output.replace("?", "_all"));
		Conf.answers = gson.fromJson(new FileReader(f), Conf.gson_answers);
		
		int count = 0;
		
		f = new File(Conf.posts_output.replace("?", "_all"));
		Row[] posts_ = gson.fromJson(new FileReader(f), Row[].class);
		
		for ( Entry<Integer, ArrayList<Row>> a : Conf.answers.entrySet() )
			count += a.getValue().size();
		System.err.println(posts_.length+" - "+count);
	}
}