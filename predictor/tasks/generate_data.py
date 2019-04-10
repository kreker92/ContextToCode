"""
generate_data.py

Core script for generating training/test addition data. First, generates random pairs of numbers,
then steps through an execution trace, computing the exact order of subroutines that need to be
called.
"""
import pickle
import pandas as pd
import numpy as np

import datetime
import tensorflow as tf
import re
import os
import json
from tasks.env.config import DSL_DATA_PATH, DATA_PATH_ENCODE_MASK
from pprint import pprint
import collections
from random import shuffle
import sys


def explode (str):
    return str.replace(':', ' ').replace(', ', ' ').replace('-', ' ').split(' ')
	
def expect_to_prog( dir, count ):
    with open("/root/ContextToCode/predictor/log/1class/expect_to_prog", 'r') as handle:
        parsed = json.load(handle)
    f = open("/root/ContextToCode/predictor/log/1class/"+dir+"/info", "r")
    result = re.search('{(.*)}', f.read())
    info = json.loads("{"+result.group(1)+"}")
    parsed[count] = {'prog': info['prog'], 'dir': dir, 'key':count}
    with open("/root/ContextToCode/predictor/log/1class/expect_to_prog", 'w') as handle:
        json.dump(parsed, handle)
    
		
def generate_addition( dir ):
    """
    Generates addition data with the given string prefix (i.e. 'train', 'test') and the specified
    number of examples.

    :param prefix: String prefix for saving the file ('train', 'test')
    :param num_examples: Number of examples to generate.
    """

    with open(DSL_DATA_PATH+dir+"/context.json", 'r') as handle:
        parsed = json.load(handle)
    with open(DSL_DATA_PATH+dir+"/domain.json", 'r') as handle:
        domain_path = "log/pipeline/"+json.load(handle)
    shuffle(parsed)
    # times = pd.date_range('2000-10-01', end='2017-12-31', freq='5min').tolist()
    #
    train_data = []
    test_data = []
    count_ = []	
    count = 0

    progs = {};
    progs["target"] = 0;
    progs["nontarget"] = 0;
	
    mask = {}
    # dates = []
    # members_set = set()
    # for i in np.random.choice(times, size=num_examples, replace=False):
    #     # key = i.strftime("%Y-%m-%d %H:%M:%S")
    #     # value = i.strftime("%H:%M:%S %A, %d %B %Y")
    #
    #     key = i.strftime("y%Y m%m d%d")
    #     value = i.strftime("d%d m%B y%Y")
    #
    #     # key = i.strftime("m%m 0 0")
    #     # value = i.strftime("m%B 0 0")
    #
    #     dates.append({"k":key, "v":value})
    #
    #     for m in explode(value):
    #         members_set.add(m)
    #     for m in explode(key):
    #         members_set.add(m)
    # members_list = list(members_set)
    # count = 0
    for row_r in parsed:
        temp = []
        pr = transform(row_r, temp, "", mask, count_)
        if len(mask) > 290995:
            break

        if pr != "1" or progs["target"] > (progs["nontarget"]*2.5):
            count += 1
            if pr == "1":
                progs["nontarget"] += 1;
            else:
                progs["target"] += 1;
                progs["prog"] = pr
            if (count % 20 == 0):
                test_data = test_data + temp
            else:
                train_data = train_data + temp
 
    progs["mask"] = len(mask)
    print(progs)
    print("##"+str(count)+" - "+dir)
    os.mkdir( domain_path );
    with open(domain_path+'/test.pik', 'wb') as f:
        pickle.dump(test_data, f)
    with open(domain_path+'/train.pik', 'wb') as f:
        pickle.dump(train_data, f)
    if mask:
        with open(domain_path+'/mask', 'w') as outfile:
            json.dump(mask, outfile)
    with open(domain_path+'/test', 'w') as outfile:
       json.dump(test_data, outfile)
    with open(domain_path+'/info', 'w') as outfile:
       json.dump(progs, outfile)
    # with open('tasks/env/data/train.pik1', 'a') as f:
    #     for c in train_data:
    #         f.write(str(c))

def transform(row_r, dataset, mask_file, mask, count_):
    row_ = {}
    for key, values in row_r.items():
       row_[int(key)] = values
    row = collections.OrderedDict(sorted(row_.items()))
    with_mask_file = False
    trace = []
    cur_prog = 0
    contains_connect = False;

    if mask_file:
        with_mask_file = True
        with open(mask_file) as f:
            mask = json.load(f)
    else:
        one_hot_count = len(mask)+1
    for key, values in row.items():
        for k, v in values.items():
            if k == 'program':
                for e_k, e_v in v.items():
                    if e_k == 'id':
                        cur_prog = e_v.get('value')
    for key, values in row.items():
        step = {}
        for k, v in values.items():
            if k == 'supervised_env':
                environment = {}
                for e_k, e_v in v.items():
                    if e_v.get('value') in mask:
                        environment[e_k] = mask.get(e_v.get('value'))
                    elif with_mask_file:
                        environment[e_k] = 0
                    elif cur_prog != "1":
                        count_.append(1)
                        print(len(count_))
                        one_hot_count += 1
                        mask[e_v.get('value')] = one_hot_count
                        environment[e_k] = one_hot_count
                environment['terminate'] = "false"
                step['environment'] = environment
            elif k == 'argument':
                args = {}
                # for e_k, e_v in v.items():
                #   if e_k == 'id':
                args['id'] = '1'
                step['args'] = args
            elif k == 'program':
                program = {}
                for e_k, e_v in v.items():
                    if e_k == 'program':
                        program['program'] = e_v.get('value')
                    if e_k == 'id':
                        program['id'] = e_v.get('value')
                        #cur_prog = e_v.get('value')

                step['program'] = program
            elif k == 'additional_info':
                step['addinfo'] = v
        trace.append(step)

    dataset.append(trace)
	
    return cur_prog