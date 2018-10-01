package tmt.search.github;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import tmt.code.snippets.stackoverflow.Row;
import tmt.conf.Utils;

public class Github {

	ArrayList<Response> resp;
	public HashMap<String, Item> merged;

	public Github () {
		resp = new ArrayList<>();
		merged = new HashMap<>();
	}

	public void addResp(String query, String user) throws UnsupportedEncodingException, IOException {
		try {
      resp.add(new Gson().fromJson(Utils.readStringFromURL("https://api.github.com/search/code?q="+URLEncoder.encode(query, "UTF-8")+"+language:java+user:"+user+"&client_id=1d37f4c170ab6b715f4b1d37f4c170ab6b715f4b&client_secret=3b877aec7dd7ba8fe51f2b8d717a074e3b540bf3"),
      		Response.class));
    } catch (JsonSyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (KeyManagementException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	}

	//  public String getItemsPath() {
	//    return resp.getItemsPath();
	//  }

	public void merge() throws IOException {
		for (Response r : resp)
			for (Item i : r.items) {
				if (!merged.containsKey(i.sha)) {
					merged.put(i.sha, i);   
					i.getRawHTML();
					Utils.savePlainFile("/root/GherkinToCode/output/"+i.sha, new HtmlToPlainText().getPlainText(Jsoup.parse(i.raw_html)));
					//          i.html = Utils.readStringFromURL(i.html_url);
				}
			}
	}

	public String countRelevance(ArrayList<String> code) throws Exception {
		for (Entry<String, Item> i : merged.entrySet()) {
			HashSet<String> temp = new HashSet<>();
			HashSet<String> temp1 = new HashSet<>();

			for ( String t : i.getValue().raw_html.replaceAll("[^a-zA-Z0-9]", " ").split(" ")) {
				if (!t.isEmpty())
					temp.add(t);
			}
			for ( String t : code) 
				for ( String c : t.replaceAll("[^a-zA-Z0-9]", " ").split(" ")) {
					if (!c.isEmpty())
						temp1.add(c);
				}
			temp1.retainAll(temp);
			i.getValue().intersect = temp1;
			i.getValue().relevance = temp1.size();
		}
		ArrayList<Item> vals = new ArrayList<Item>(merged.values());
		Collections.sort(vals, comparator_relevance);
//	    return Utils.readFile("/root/GherkinToCode/output/funcs/"+vals.get(0).sha);
		return "1";
	}
	
	  public static Comparator<Item> comparator_relevance = new Comparator<Item>() {
		    public int compare(Item o1, Item o2) {
		      return o2.relevance.compareTo(o1.relevance);
		    }
		  };
}

class Response {
	int total_count;
	boolean incomplete_results;
	ArrayList<Item> items;
	public String getItemsPath() {
		String res = "";
		if (items != null)
			for  (Item i : items)
				res += i.html_url;
		return res;
	}
}

class Item {
	String name;
	String path;
	String sha;
	String url;
	String git_url;
	String html_url;
	Repository repository;
	String score;
	String html;
	String raw_html;
	Integer relevance;
	HashSet<String> intersect;

	public void getRawHTML() throws IOException {
		try {
      raw_html = Utils.readStringFromURL(html_url.replace("github.com", "raw.githubusercontent.com").replace("blob/", ""));
    } catch (KeyManagementException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
		//    System.err.println(raw_html);
		//System.exit(1);
	}

	public String toString() {
		return html_url + ":" + relevance;
	}
}

class Repository {
	/*": {
  "id": 41032178,
  "node_id": "MDEwOlJlcG9zaXRvcnk0MTAzMjE3OA==",
  "name": "binnavi",
  "full_name": "google/binnavi",
  "owner": {
    "login": "google",
    "id": 1342004,
    "node_id": "MDEyOk9yZ2FuaXphdGlvbjEzNDIwMDQ=",
    "avatar_url": "https://avatars1.githubusercontent.com/u/1342004?v=4",
    "gravatar_id": "",
    "url": "https://api.github.com/users/google",
    "html_url": "https://github.com/google",
    "followers_url": "https://api.github.com/users/google/followers",
    "following_url": "https://api.github.com/users/google/following{/other_user}",
    "gists_url": "https://api.github.com/users/google/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/google/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/google/subscriptions",
    "organizations_url": "https://api.github.com/users/google/orgs",
    "repos_url": "https://api.github.com/users/google/repos",
    "events_url": "https://api.github.com/users/google/events{/privacy}",
    "received_events_url": "https://api.github.com/users/google/received_events",
    "type": "Organization",
    "site_admin": false
  },
  "private": false,
  "html_url": "https://github.com/google/binnavi",
  "description": "BinNavi is a binary analysis IDE that allows to inspect, navigate, edit and annotate control flow graphs and call graphs of disassembled code.",
  "fork": false,
  "url": "https://api.github.com/repos/google/binnavi",
  "forks_url": "https://api.github.com/repos/google/binnavi/forks",
  "keys_url": "https://api.github.com/repos/google/binnavi/keys{/key_id}",
  "collaborators_url": "https://api.github.com/repos/google/binnavi/collaborators{/collaborator}",
  "teams_url": "https://api.github.com/repos/google/binnavi/teams",
  "hooks_url": "https://api.github.com/repos/google/binnavi/hooks",
  "issue_events_url": "https://api.github.com/repos/google/binnavi/issues/events{/number}",
  "events_url": "https://api.github.com/repos/google/binnavi/events",
  "assignees_url": "https://api.github.com/repos/google/binnavi/assignees{/user}",
  "branches_url": "https://api.github.com/repos/google/binnavi/branches{/branch}",
  "tags_url": "https://api.github.com/repos/google/binnavi/tags",
  "blobs_url": "https://api.github.com/repos/google/binnavi/git/blobs{/sha}",
  "git_tags_url": "https://api.github.com/repos/google/binnavi/git/tags{/sha}",
  "git_refs_url": "https://api.github.com/repos/google/binnavi/git/refs{/sha}",
  "trees_url": "https://api.github.com/repos/google/binnavi/git/trees{/sha}",
  "statuses_url": "https://api.github.com/repos/google/binnavi/statuses/{sha}",
  "languages_url": "https://api.github.com/repos/google/binnavi/languages",
  "stargazers_url": "https://api.github.com/repos/google/binnavi/stargazers",
  "contributors_url": "https://api.github.com/repos/google/binnavi/contributors",
  "subscribers_url": "https://api.github.com/repos/google/binnavi/subscribers",
  "subscription_url": "https://api.github.com/repos/google/binnavi/subscription",
  "commits_url": "https://api.github.com/repos/google/binnavi/commits{/sha}",
  "git_commits_url": "https://api.github.com/repos/google/binnavi/git/commits{/sha}",
  "comments_url": "https://api.github.com/repos/google/binnavi/comments{/number}",
  "issue_comment_url": "https://api.github.com/repos/google/binnavi/issues/comments{/number}",
  "contents_url": "https://api.github.com/repos/google/binnavi/contents/{+path}",
  "compare_url": "https://api.github.com/repos/google/binnavi/compare/{base}...{head}",
  "merges_url": "https://api.github.com/repos/google/binnavi/merges",
  "archive_url": "https://api.github.com/repos/google/binnavi/{archive_format}{/ref}",
  "downloads_url": "https://api.github.com/repos/google/binnavi/downloads",
  "issues_url": "https://api.github.com/repos/google/binnavi/issues{/number}",
  "pulls_url": "https://api.github.com/repos/google/binnavi/pulls{/number}",
  "milestones_url": "https://api.github.com/repos/google/binnavi/milestones{/number}",
  "notifications_url": "https://api.github.com/repos/google/binnavi/notifications{?since,all,participating}",
  "labels_url": "https://api.github.com/repos/google/binnavi/labels{/name}",
  "releases_url": "https://api.github.com/repos/google/binnavi/releases{/id}",
  "deployments_url": "https://api.github.com/repos/google/binnavi/deployments"*/
}
