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
		
def get_type (node):
    if node["type"] == "CallExpression":
        tmp = []
        return get_inner_types(node, tmp)
    else:		
        return node["type"]
		
#def in_memberexpression(node, ast_raw):
#    id = ast_raw[node]["id"]
	
#    for key, value in ast_raw.items():
#        if "children" in value and id in value["children"] and value["type"] == :
#            return true;

#    return false;
		
def get_parent_in_scope(node, value, ast_raw, vars, level):
    parent = get_parent(node, ast_raw)
    if value+":"+str(parent) in vars:
        return value+":"+str(parent)

    if level > 6 or parent == 0:
        return "";

    return get_parent_in_scope(parent, value, ast_raw, vars, level+1)
		
def get_parent(node, ast_raw):
    id = ast_raw[node]["id"]
    parent = 0
	
    for key, value in ast_raw.items():
        if "children" in value and id in value["children"]:
            parent = value["id"]
			
    if parent == 0 or not is_valid(ast_raw[parent]):
        return parent
    else:
        return get_parent(parent, ast_raw)

def fill_vars(vars, ast_raw):
    for key, value in ast_raw.items():
        if "children" in value and value["type"] == "VariableDeclaration":
            for child in value["children"]:
                 if ast_raw[child]["type"] == "VariableDeclarator":				 
                     if ast_raw[child]["value"] in vars:
                         print(ast_raw[child]["value"])
                         #ddd()
                     if "children" in ast_raw[child]:
                         for inner_child in ast_raw[child]["children"]:
                             if ast_raw[inner_child]["type"] != "Identifier":
                                 vars[ast_raw[child]["value"]+":"+str(get_parent(child, ast_raw))] = get_type(ast_raw[inner_child])
                 elif "children" in ast_raw[child]:
                     in_block += ast_raw[child]["children"]
   # if value["type"] == "Identifier" and not value["value"] in vars:
    #    for key, value in ast_raw.items():
     #       if value["type"] == "VariableDeclaration":
      #  vars[value["value"]] =
		
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

def parse_file (src, dst, o_dst, data):
#and value["type"] == "VariableDeclaration"
    print(src)
    vars = {}
    ast_raw = {}
    ast_out = []
    ids = []
    in_block = []
    #with open(data, 'r') as handle:
    parsed = json.loads(data)
    for node in parsed:
        if node != 0:
            ast_raw[node['id']] = node
    for key, value in ast_raw.items():
        if "children" in value:
            value["links"] = []
            for child in value["children"]:
                 if is_valid(ast_raw[child]) and is_with_links(ast_raw[child]):				 
                     value["links"].append(ast_raw[child])
                 elif "children" in ast_raw[child]:
                     in_block += ast_raw[child]["children"]
    fill_vars(vars, ast_raw)
    print(vars)
    for key, value in ast_raw.items():
        value["block"] = get_parent(key, ast_raw);
        if value["type"] == "Identifier" or value["type"] == "VariableDeclarator":
         #   if in_memberexpression(key, ast_raw):
         #       vars[value["value"]+":"+str(get_parent(key, ast_raw))] = "MemberExpression"
            parent = get_parent_in_scope(key, value["value"], ast_raw, vars, 0)
            if parent in vars:
                value["source"] = vars[parent]
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
            if True:#"magicmonkey" in file:#files[i] in check:#os.path.isfile("/root/js/data_picks/1/"+name.strip()):
                if (count > 20):
                    ddd()
                #data = json.loads(jsons[i])
                f = open("/root/js/data_picks/mask", "a")
                f.write(str(count)+", "+file)
                #try:
                parse_file(file, "/root/js/data_picks/sandbox_test/"+str(count), "/root/js/data_picks/originals_test/"+str(count), jsons[i])#"/root/js/data_picks/sandbox/"+name.strip().replace(".js", #"-raw.js"))
               # except:
                #    print("Can't parse file"+file)
                count += 1
					
if __name__ == "__main__":
   main(sys.argv[1:])