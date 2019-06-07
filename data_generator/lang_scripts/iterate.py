import os
import re
import json

rootdir = '/root/w3schools/developer.mozilla.org_us/en-US/docs/Web/JavaScript/Reference/Global_Objects/'


def find_between( s, first, last ):
    try:
        start = s.index( first ) + len( first )
        end = s.index( last, start )
        return s[start:end]
    except ValueError:
        return ""
		
def filter_names(names, descrs):
    res = []
    for name in names:
        found = False;
        for descr in descrs:
            if "<dt>"+name+"</dt>" in descr:
                found = True
        if not found:
            res.append(name)
        else:
            res.append("*-FAIL-*"+name)
    return res

for subdir, dirs, files in os.walk(rootdir):
    for file in files:
        with open(os.path.join(subdir, file), 'r') as f: 
            if not "$" in os.path.join(subdir, file):
                html = f.read()
                parsed = find_between(html, '<h2 id="Syntax">Syntax</h2>','<h2 id')
                if parsed:
                    result = {}
                    result["all"] = parsed
                    param_section = find_between(parsed, '<dl>','</dl>')
                    params_descrs = re.findall(r'<dd>(.+?)</dd>', param_section.replace("\n", ""))
                    params_names = filter_names(re.findall(r'<dt>(.+?)</dt>', param_section.replace("\n", "")), re.findall(r'<dl>(.+?)</dl>', (param_section+"</dl>").replace("\n", "")))

                    count = 0					
                    i = len(params_names)
                    for j in range(i):
                        if not "*-FAIL-*" in params_names[j]:
                            try:
                                #print(os.path.join(subdir, file)+" - "+str(j))
                                result["params_n_"+str(count)] = params_names[j]
                                # print(params_names[j])
                                result["params_d_"+str(count)] = params_descrs[j]
                                # print(params_descrs[j])
                                count += 1
                            except:
                                print(os.path.join(subdir, file))
                                print(param_section)
                                print(params_names)
                                print(params_descrs)
					
                    with open("/root/w3schools/"+os.path.join(subdir, file).replace("/", "_"), 'w') as outfile:
                        json.dump(result, outfile)