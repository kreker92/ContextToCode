import json
import os.path
from shutil import copyfile

#import subprocess
#subprocess.run(["ls", "-l"])

def is_valid (value):
    if value["type"] == "BlockStatement":
        return False
    else:
        return True

def is_end_node (value, ids):
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

def parse_file (src, dst, data):
#and value["type"] == "VariableDeclaration"
    print(src)
    ast_raw = {}
    ast_out = []
    ids = []
    #with open(data, 'r') as handle:
    parsed = json.loads(data)
    for node in parsed:
        if node != 0:
            ast_raw[node['id']] = node
    for key, value in ast_raw.items():
        if "children" in value:
            value["links"] = []
            for child in value["children"]:
                 if is_valid(ast_raw[child]):				 
                     value["links"].append(ast_raw[child])
    for key, value in ast_raw.items():
        if is_end_node(value, ids):
           # get_text(value)
            if "links" in value:
       	        get_ids(ids, value)	
            ast_out.append(value)

   # with open('/root/ContextToCode/data_generator/lang_scripts/data_raw.json', 'w') as outfile:
   #     json.dump(ast_raw, outfile)
    with open(dst.strip(), 'w') as outfile:
        json.dump(ast_out, outfile)
            
f = open("/root/js/data_picks/picked", "r")
check = f.read()
			
with open("/root/js/programs_eval.json", 'r') as f: 
    jsons = f.readlines()

with open("/root/js/programs_eval.txt", 'r') as f: 
    files = f.readlines()

count = 0
print(len(jsons))
print(len(files))
for i in range(len(files)):
    name = files[i].split('/')[-1]
    k = files[i].rfind("data/")
    file = files[i][:k] + "/root/js/data/" + files[i][k+5:]
    if files[i] in check:#os.path.isfile("/root/js/data_picks/1/"+name.strip()):
        #data = json.loads(jsons[i])
        f = open("/root/js/data_picks/mask", "a")
        f.write(str(count)+", "+file)
        parse_file(file, "/root/js/data_picks/sandbox/"+str(count), jsons[i])#"/root/js/data_picks/sandbox/"+name.strip().replace(".js", "-raw.js"))
        count += 1
					