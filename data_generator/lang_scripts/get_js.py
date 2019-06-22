import glob
import sys
import os
from es import main as parse




def count_unknown(_vars):
  counter = 0
  for k,v in _vars.items():
    print(k,v)
    if 'unknown' in v:
      counter += 1
  return counter


all_vars = 0
unknown_types = 0

files = list(filter(os.path.isfile, glob.glob('repos/**/*.js', recursive=True)))

print('parsing {count} files'.format(count=len(files)))

for f in files:

  vartypes = parse(f)

  print(f)
  all_vars += len(vartypes)
  unknown_types += count_unknown(vartypes)

  # print()
  print(unknown_types, all_vars)

  # sys.stdout.write('.')
  # sys.stdout.flush()

print(unknown_types, all_vars)

