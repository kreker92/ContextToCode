# modern ecmascript parser

import sys
import os.path
import subprocess
import json

def _print(obj):
    print(json.dumps(obj,indent=2))

from ref import js_fuctions

types = {
  str(type(1)): 'Number',
  str(type('')): 'String',
  str(type(True)): 'Boolean',
  str(type(None)): 'Null',
  str(type({})): 'Object',
  str(type([])): 'Array'
}

def set_types(ast):

  vartypes = {}

  for v in ast['body']:
      if (v['type'] == 'VariableDeclaration'):
          for d in v['declarations']:
              if (d['type'] == 'VariableDeclarator'):
                varname = d['id']['name']

                if (d['init']['type'] == 'Literal'):
                  vartypes[varname] = {
                    'primitive': types[str(type(d['init']['value']))]
                  }
                elif (d['init']['type'] == 'ObjectExpression'):
                  vartypes[varname] = {
                    'primitive': 'Object'
                  }
                elif (d['init']['type'] == 'ArrayExpression'):
                  vartypes[varname] = {
                    'primitive': 'Array'
                  }
                elif (d['init']['type'] == 'CallExpression'):

                  if (d['init']['callee']['type'] == 'Identifier'):
                    if (d['init']['callee']['name'] == 'require'):
                    # loading module
                      vartypes[varname] = {
                        'module': d['init']['arguments'][0]['value']
                      }

                    elif (d['init']['callee']['name'] in js_fuctions.keys()):
                    # global function
                      vartypes[varname] = {
                        'primitive': js_fuctions[d['init']['callee']['name']]
                      }

                    else:
                      vartypes[varname] = {
                        'unknown': d['init']
                      }
                else:
                  vartypes[varname] = {
                    'unknown': d['init']
                  }


  return vartypes


def parse_file(ast):

    # _print(ast)

    # ast_raw = {}
    # for node in ast['body']:
    #     if node != 0:
    #         _id = '{start}_{end}'.format(start=node['start'], end=node['end'])
    #         ast_raw[_id] = node

    vartypes = set_types(ast)

    _print(vartypes)



_DIR = os.path.dirname(__file__)

def _path(relative):
    return os.path.join(_DIR, relative)

def main(arg):
    if arg[0] == "file":

        PARSER = _path('./es_parse.js') # install with npm install
        INPUT  = _path('input/test.js')

        try:
            data = subprocess.check_output([PARSER, INPUT], stderr=subprocess.STDOUT)

            parsed = json.loads(data)
            # _print(parsed)

        except:
            print('PARSE ERROR:')
            print(str(sys.exc_info()[1].output))
            return

        parse_file(parsed)

if __name__ == "__main__":
   main(sys.argv[1:])

