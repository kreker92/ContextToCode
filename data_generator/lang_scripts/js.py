import json
import os.path
from shutil import copyfile

#def carret_return ():

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


def parse_file (src, dst, data):
    ast = {}
    with open(data, 'r') as handle:
        parsed = json.load(handle)
    for node in parsed:
        if node != 0:
            ast[node['id']] = node
    for key, value in ast.items():
        if "children" in value:
            value["links"] = []
            for child in value["children"]:
                if "BlockStatement" != ast[child]["type"]:
                    value["links"].append(ast[child])
    for key, value in ast.items():
        if not "value" in value and value["type"] == "VariableDeclaration":
            ast["value"] = get_text(value)
            print(ast["value"])
    with open('/root/ContextToCode/data_generator/lang_scripts/data.json', 'w') as outfile:
        json.dump(ast, outfile)
    sss()
            

			
#with open("/root/js/programs_eval.json", 'r') as f: 
#    jsons = f.readlines()

with open("/root/js/programs_training.txt", 'r') as f: 
    files = f.readlines()

#print(len(jsons))
print(len(files))
for i in range(len(files)):
    name = files[i].split('/')[-1]
    if os.path.isfile("/root/js/data_picks/1/"+name.strip()) and "semantic-layout" in name:
        #f = open("/root/js/data_picks/sandbox/"+name.strip().replace(".js", "-json.js"), "w")
        #data = json.loads(jsons[i])
        #f.write(jsons[i])
        parse_file("/root/js/data_picks/1/"+name.strip(), "/root/js/data_picks/sandbox/"+name.strip().replace(".js", "-json.js"), "/root/js/data_picks/sandbox/"+name.strip().replace(".js", "-raw.js"))
					