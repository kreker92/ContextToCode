import json
import os.path
from shutil import copyfile
import subprocess
import sys

def is_valid (value):
    if value["type"] == "BlockStatement":
        return False
    else:
        return True
		
def is_with_links (value):
    if value["type"] == "FunctionExpression":
        return False
    else:
        return True
		
def contains_key (key, node):
    if node["type"] == key["type"] and (not "value" in key or node["value"] == key["value"]) and (not "source" in key or ("source" in node and node["source"] == key["source"])):
        return True;
    elif "children" in node:
        for child in node["links"]:
            if (contains_key(key, child)):
                return True
				
    return False
	
def get_complex_types(node):
    with open("/root/ContextToCode/data/datasets/js/templates", 'r') as handle:
        anchors = json.load(handle)
    for anchor in anchors:
        contains_anchor = True;
        for key in anchor["els"]:
            if key["type"] != "VariableDeclarator":
                if not contains_key(key, node):
                    contains_anchor = False;
                    break;
        if contains_anchor:
            return anchor["ast_type"]
			
    return ""
		
def get_type (node):
    if node["type"] == "CallExpression":
        print(str(get_complex_types(node)))
        return str(get_complex_types(node))
    else:		
        return node["type"]
		
#def in_memberexpression(node, ast_raw):
#    id = ast_raw[node]["id"]
	
#    for key, value in ast_raw.items():
#        if "children" in value and id in value["children"] and value["type"] == :
#            return true;

#    return false;
		
def get_parent_in_scope(parent, value, ast_raw, vars, level):
    if value+":"+str(parent) in vars:
        return value+":"+str(parent)

    if level > 6 or parent == 0:
        return "";

    return get_parent_in_scope(ast_raw[parent]["parent"], value, ast_raw, vars, level+1)
		
def set_parent(ast_raw, id, parent):
    ast_raw[id]["parent"] = parent
    if not is_valid(ast_raw[id]):
        parent = id  
    if "children" in ast_raw[id]:
        for child in ast_raw[id]["children"]:
            set_parent(ast_raw, ast_raw[child]["id"], parent)
			
def get_inner_types(node, tmp):
    if "CallExpression" != node["type"]:
        if "value" in node:
	        tmp.append({"type":node["type"], "value":node["value"]})
        else:
            tmp.append({"type":node["type"], "value":""})

    if "links" in node:
        for child in node["links"]:
            get_inner_types(child, tmp)

    return tmp
			
def set_vars(vars, ast_raw):
    assigned = False;
    for _, value in ast_raw.items():
        if "children" in value and value["type"] == "VariableDeclaration":
            for child in value["children"]:
                 key = ast_raw[child]["value"]+":"+str(ast_raw[child]["parent"])
                 if not key in vars or not vars[key]["type"]:
                     if ast_raw[child]["type"] == "VariableDeclarator":	
                    # if not key in vars:				 
                         if ast_raw[child]["value"] in vars:
                             print(ast_raw[child]["value"])
                             #ddd()
                         if "children" in ast_raw[child]:
                             for inner_child in ast_raw[child]["children"]:
                                 if ast_raw[inner_child]["type"] != "Identifier":
                                     vars[key] = {"type":get_type(ast_raw[inner_child]), "links":get_inner_types(ast_raw[inner_child], [])}
                                     if get_type(ast_raw[inner_child]):
                                         assigned = True;
    count = 0;										 
    for key, value in ast_raw.items():
        if value["type"] == "Identifier" or value["type"] == "VariableDeclarator":
            parent = get_parent_in_scope(value["parent"], value["value"], ast_raw, vars, 0)
            if parent in vars:
                value["source"] = vars[parent]["type"]
                count += 1
                value["source_info"] = vars[parent]["links"]
    print(count)
    return assigned
		
def is_end_node (value, ids):
    if value["type"] == "LiteralString":
        value["value"] = "-"
    if value["type"] != "FunctionExpression" and (value["type"] == "Program" or value["type"] == "VariableDeclarator" or value["type"] == "BlockStatement" or value["type"] == "CallExpression" or value["id"] in ids):
        return False
    else:
        return True

def get_text(node):
    if not "value" in node:
        node["value"] = " ";
    if not "children" in node:
        return node["value"]
    else:
        if "links" in node:
            for child in node["links"]:
                node["value"] += " "+get_text(child) 
        return node["value"]

def get_ids(ids, value):
    ids.append(value["id"])
    for child in value["links"]:
       if "links" in child:
           get_ids(ids, child)
       else:
           ids.append(child["id"])
		   
def set_links(ast_raw, in_block):
    for key, value in ast_raw.items():
        if "children" in value:
            value["links"] = []
            for child in value["children"]:
                 if is_valid(ast_raw[child]) and is_with_links(ast_raw[child]):				 
                     value["links"].append(ast_raw[child])
                 elif "children" in ast_raw[child]:
                     in_block += ast_raw[child]["children"]

def parse_file (src, dst, o_dst, data):
#and value["type"] == "VariableDeclaration"
    ast_raw = {}
    vars = {}
    ast_out = []
    ids = []
    in_block = []
    #with open(data, 'r') as handle:
    parsed = json.loads(data)
    for node in parsed:
        if node != 0:
            ast_raw[node['id']] = node
    set_links(ast_raw, in_block)
    set_parent(ast_raw, 0, 0)
    while set_vars(vars, ast_raw):
        continue
    for key, value in ast_raw.items():
        if not "parent" in value:
            ddd()
        if is_end_node(value, ids):
           # get_text(value)
            if "links" in value:
       	        get_ids(ids, value)	
            if value["id"] in in_block and value["type"] == "FunctionDeclaration":
                value["type"] = "FunctionDeclarationInBlock"
            ast_out.append(value)
   # with open('/root/ContextToCode/data_generator/lang_scripts/data_raw.json', 'w') as outfile:
   #     json.dump(ast_raw, outfile)
    with open(dst.strip(), 'w') as outfile:
        json.dump(ast_out, outfile)
    with open(o_dst.strip(), 'w') as outfile:
        outfile.write(data)

def main(arg):	
    if arg[0] == "file":
        parsed = subprocess.check_output(["/root/ContextToCode/data_generator/lang_scripts/js_parser/bin/js_parser.js", "/root/ContextToCode/data_generator/lang_scripts/input/input.js"])
        parse_file("", "/root/ContextToCode/data_generator/lang_scripts/parsed", "/root/ContextToCode/data_generator/lang_scripts/o_parsed", parsed)#"/root/js/data_picks/sandbox/"+name.strip().replace(".js", "-raw.js"))
    elif arg[0] == "folder":
        #parse_file(file, "/root/js/data_picks/sandbox/"+str(count), jsons[i])#"/root/js/data_picks/sandbox/"+name.strip().replace(".js", "-raw.js"))
		
        #f = open("/root/js/data_picks/picked", "r")
        #check = f.read()
			
        with open("/root/js/programs_eval.json", 'r') as f: 
            jsons = f.readlines()

        with open("/root/js/programs_eval.txt", 'r') as f: 
            files = f.readlines()

        count = 0
        #print(len(jsons))
        #print(len(files))
        for i in range(len(files)):
            i+=1
            name = files[i].split('/')[-1]
            k = files[i].rfind("data/")
            file = files[i][:k] + "/root/js/data/" + files[i][k+5:]
            if True:#"/root/js/data/jsdelivr/jsdelivr/files/reactive.js/0.3.9/Ractive-legacy.runtime.js" in file:#files[i] in check:#os.path.isfile("/root/js/data_picks/1/"+name.strip()):
                if (count > 0):
                    ddd()
                #data = json.loads(jsons[i])
                f = open("/root/js/data_picks/mask", "a")
                f.write(str(count)+", "+file)
              #  try:
                parse_file(file, "/root/js/data_picks/sandbox_test/"+str(count), "/root/js/data_picks/originals_test/"+str(count), jsons[i])#"/root/js/data_picks/sandbox/"+name.strip().replace(".js", #"-raw.js"))
               # except:
                #    print("Can't parse file"+file)
                count += 1
					
if __name__ == "__main__":
   main(sys.argv[1:])

