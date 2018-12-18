"""
train.py

Core training script for the addition task-specific NPI. Instantiates a model, then trains using
the precomputed data.
"""
from model.npi import NPI
from tasks.env.addition import AdditionCore
from tasks.env.config import CONFIG, get_args, LOG_PATH, DATA_PATH_TRAIN, DATA_PATH_TEST, CKPT_PATH
from tasks.env.config import get_env
import pickle
import tensorflow as tf
import numpy as np
import json 

chunk = 50000
MOVE_PID, WRITE_PID = 0, 1
WRITE_OUT = 0
IN1_PTR, IN2_PTR, OUT_PTR = range(3)
LEFT, RIGHT = 0, 1

def data():
    """
    Instantiates an Addition Core, NPI, then loads and fits model to data.

    :param epochs: Number of epochs to train for.
    """
    print ("&")
    res = {}
	
    # Load Data
    with open(DATA_PATH_TRAIN, 'rb') as f:
        data = pickle.load(f)
		
    for d in data:
        for j in range(len(d)):
            if not d[j]["addinfo"]["path"] in res:
                res[d[j]["addinfo"]["path"]] = {}
            d[j]["environment"]["text"] = d[j]["addinfo"]["text"]
            res[d[j]["addinfo"]["path"]][d[j]["addinfo"]["line"]] = d[j]["environment"]
	    	
    with open(DATA_PATH_TEST, 'rb') as f:
        data = pickle.load(f)
		
    for d in data:
        for j in range(len(d)):
            if not d[j]["addinfo"]["path"] in res:
                res[d[j]["addinfo"]["path"]] = {}
            d[j]["environment"]["text"] = d[j]["addinfo"]["text"]
            res[d[j]["addinfo"]["path"]][d[j]["addinfo"]["line"]] = d[j]["environment"]
			
    with open('tasks/env/data/data.json', 'w') as outfile:
        json.dump(res, outfile)