package tmt.snippets;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import tmt.stackoverflow.Post;

import java.util.regex.Matcher;
import java.text.Normalizer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.CharBuffer;

public class Snippets {

	String  file = "/root/stackoverflow/Posts.xml";

	public Snippets() throws Exception {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Post.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Post customer = (Post) jaxbUnmarshaller.unmarshal(line);
				System.out.println(customer);

			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
	}
}




