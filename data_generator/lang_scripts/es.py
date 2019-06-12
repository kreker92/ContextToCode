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
  str(type(0.1)): 'Number',
  str(type('')): 'String',
  str(type(True)): 'Boolean',
  str(type(None)): 'Null',
  str(type({})): 'Object',
  str(type([])): 'Array'
}


def check_type(d): # d is declarator

  if ('init' in d):

    res = {'unknown': d['init']}

    if (not d['init']):
      return res;

    elif ('type' not in d['init']):
      return res

    elif (d['init']['type'] == 'Literal'):
      return {
        'primitive': types[str(type(d['init']['value']))]
      }
    elif (d['init']['type'] == 'ObjectExpression'):
      return {
        'primitive': 'Object'
      }
    elif (d['init']['type'] == 'ArrayExpression'):
      return {
        'primitive': 'Array'
      }
    elif (d['init']['type'] == 'ArrowFunctionExpression'):
      return {
        'primitive': 'Function'
      }
    elif (d['init']['type'] == 'CallExpression'):

      if (d['init']['callee']['type'] == 'Identifier'):
        if (d['init']['callee']['name'] == 'require'):
        # loading module
          if (d['init']['arguments'][0]['type'] == 'Literal'):
            return {
              'module': d['init']['arguments'][0]['value']
            }

        elif (d['init']['callee']['name'] in js_fuctions.keys()):
        # global function
          return {
            'primitive': js_fuctions[d['init']['callee']['name']]
          }


    return res
  else:
    return {
      'unknown': True
    }



def set_types(ast):

  vartypes = {}

  # print(ast['body'])

  for v in ast['body']:
      if (v['type'] == 'VariableDeclaration'):
        for d in v['declarations']:
              if (d['type'] == 'VariableDeclarator'):

                # print(d)
                if ('name' in d['id']): # skip this: const {rerender} = ...


                  varname = d['id']['name']

                  # print('********')
                  # print(varname)

                  vartypes[varname] = check_type(d)

  return vartypes


def parse_file(ast):

    # _print(ast)

    # ast_raw = {}
    # for node in ast['body']:
    #     if node != 0:
    #         _id = '{start}_{end}'.format(start=node['start'], end=node['end'])
    #         ast_raw[_id] = node

    return set_types(ast)


_DIR = os.path.dirname(__file__)

def _path(relative):
    return os.path.join(_DIR, relative)

def main(input_file):
  PARSER = _path('./es_parse.js') # install with npm install
  INPUT  = _path(input_file)

  try:
      data = subprocess.check_output([PARSER, INPUT], stderr=subprocess.STDOUT)

      parsed = json.loads(data)
      # _print(parsed)

      return parse_file(parsed)

  except:
      print('ERROR in {INPUT}:'.format(INPUT=INPUT))
      print(str(sys.exc_info()[1].output))
      return


if __name__ == "__main__":
  res = main(sys.argv[1])
  print(res)

