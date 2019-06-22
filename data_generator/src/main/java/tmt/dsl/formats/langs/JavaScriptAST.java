package tmt.dsl.formats.langs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import tmt.conf.Utils;
import tmt.dsl.formats.context.in.ElementInfo;
import tmt.dsl.formats.context.in.InnerClass;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JavaScriptAST {
  private InnerClass[] out;
  
  public JavaScriptAST (String f) {
    System.err.println(f);
    
    HashMap<String, Integer> ast_counter = new HashMap<String, Integer>();
    ast_counter.put("complex", 0);
    ast_counter.put("simple", 0);
    
    Node[] nodes = null;
    try {
      nodes = new Gson().fromJson(Utils.readFile(f), Node[].class);
    } catch (JsonSyntaxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    ArrayList<InnerClass> cs = new ArrayList<>();
    
    int count = 1;
    for (Node n : nodes) {
      
      Collections.sort(n.links, comparator_id_desc);
      traverse(n, n);
      
      cs.add(new InnerClass(n.var_name, count, n.parent, n.source, n.elements));
      count ++;
    }
    out = cs.toArray(new InnerClass[cs.size()]);
  }

  private void traverse(Node n, Node parent) {
    if (n.type.equals("Identifier")) 
      n.var_name = n.value; 

    else if (!n.value.isEmpty())
      n.var_name = n.type+":"+n.value;
    
    parent.var_name += n.var_name;

    if (n.source != null) {
        if(n.source.contains("[{")) {
            ElementInfo[] els = new Gson().fromJson(n.source.replace("u'", "'"), ElementInfo[].class);
        } 
        else { 
            ElementInfo el = new ElementInfo("ast_type", n.source, n.value);
            parent.elements.add(el);
        }
    }
    else {
      ElementInfo el = new ElementInfo("type", n.type, n.type.equals("Identifier") ? n.value : n.var_name);
      el.ast_type = n.value;
      parent.elements.add(el);
    }
    
    if (n.links != null) 
      for (Node child : n.links)
        traverse(child, parent);
  }

  private static Comparator<Node> comparator_id_desc = new Comparator<Node>() {
    public int compare(Node o1, Node o2) {
      int c = o2.id.compareTo(o1.id);
      return c;
    }
  };
  
  public InnerClass[] getClasses() {
    return out;
  }
}

class Node {
  String type;
  Integer id;
  Integer parent;
  String source;
  public ArrayList<Node> links = new ArrayList<>();
  String value = "";
  int line_num;
  String var_name;
  ArrayList<ElementInfo> elements = new ArrayList<>();
  
  /*
   *   public ArrayList<ElementInfo> elements = new ArrayList<>();
  public int start;
  public int end;
  public int line_num;
  public String line_text;
  public String type;
  public String ast_type;
  //ACTUAL "PROGRAM" in traces
  public String executor_command = "1";
  
  [{"elements":[{"node":"Element(IMPORT_STATEMENT)","type":"IMPORT_STATEMENT","text":"import android.os.Bundle;","parent":"PsiImportList","line":13},
  {"node":"PsiKeyword","type":"IMPORT_KEYWORD","text":"import","parent":"PsiImportStatement","line":13},
  {"node":"PsiJavaCodeReferenceElement","type":"JAVA_CODE_REFERENCE","text":"android.os","parent":"PsiJavaCodeReferenceElement","line":13},
  {"node":"PsiJavaCodeReferenceElement","type":"JAVA_CODE_REFERENCE","text":"android","parent":"PsiJavaCodeReferenceElement","line":13},
  {"node":"PsiIdentifier","type":"IDENTIFIER","text":"android","parent":"PsiJavaCodeReferenceElement","line":13},
  {"node":"PsiReferenceParameterList","type":"REFERENCE_PARAMETER_LIST","text":"","parent":"PsiJavaCodeReferenceElement","line":13},
  {"node":"PsiIdentifier","type":"IDENTIFIER","text":"os","parent":"PsiJavaCodeReferenceElement","line":13},
  {"node":"PsiReferenceParameterList","type":"REFERENCE_PARAMETER_LIST","text":"","parent":"PsiJavaCodeReferenceElement","line":13},
  {"node":"PsiIdentifier","type":"IDENTIFIER","text":"Bundle","parent":"PsiJavaCodeReferenceElement","line":13},
  {"node":"PsiReferenceParameterList","type":"REFERENCE_PARAMETER_LIST","text":"","parent":"PsiJavaCodeReferenceElement","line":13}],
  "start":1348,"end":1374,"line_num":13,"line_text":"import android.os.Bundle;\n","clean_line_text":""}]
  
  {"type": "ExpressionStatement", "id": 2, "links": [{"type": "CallExpression", "id": 3,
   "links": [{"type": "FunctionExpression", "id": 4, "links": [{"type": "Identifier", "id": 5, "value": "window"},
    {"type": "Identifier", "id": 6, "value": "$"}], "children": [5, 6, 7]}, {"type": "ThisExpression", "id": 1362},
     {"parent": 30, "source": "MemberExpression", "type": "Identifier", "id": 61, "value": "editor"}
     ], "children": [4, 1362, 1363]}], "children": [3]}
  
   */
}
