from http.server import BaseHTTPRequestHandler, HTTPServer
import tensorflow as tf
# from tasks.env.config

from tasks.eval import repl
from model.npi import NPI
from tasks.generate_data import transform
from tasks.env.addition import AdditionCore
from tasks.env.config import CONFIG, get_args, LOG_PATH, DATA_PATH_TEST, CKPT_PATH
from tasks.env.config import get_env
import numpy as np
import pickle
import tensorflow as tf
from tensorflow.python.platform import gfile
import json
import sys
import tflearn
from urllib.parse import unquote
import threading

class test:
    def __init__(self):
      with open("/root/ContextToCode/predictor/log/1class/expect_to_prog", 'r') as handle:
          self.sessions = json.load(handle)
      for key, value in self.sessions.items():
          value['session'] = tf.Session()
	  
	  # Initialize Addition Core
      core = AdditionCore(CONFIG)
 
      # Initialize NPI Model
      self.npi = NPI(core, CONFIG, LOG_PATH)
      # Restore from Checkpoint
      saver = tf.train.Saver()
	  
      for key, value in self.sessions.items():
          saver.restore(value['session'], CKPT_PATH+value['dir']+"/models/model-0006.ckpt")
          print(value)

class http_server:
    def __init__(self, t1):
        def handler(*args):
            myHandler(t1, *args)
        server = HTTPServer(('', 8081), handler)
        server.serve_forever()

class myHandler(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
		
    def get_predictions(self, sess, mask, res, data, expected, prog):
        predict = {};
        dataset = []
        count_ = []	
        transform(data, dataset, mask, "", count_)
        if int(repl(sess, self.t1.npi, dataset, 0, predict)[-1]) == expected:
            res.append(prog)

    def __init__(self, t1, *args):
        self.t1 = t1
        BaseHTTPRequestHandler.__init__(self, *args)

    def do_GET(self):
        self._set_headers()
        dataset = []
        res = []

        with open("/root/ContextToCode/data/datasets/context.json", 'r') as handle:
          data = json.load(handle)
		  
       # self.get_predictions(self.t1.sess2, MASK_PATH_CLASS2, res, data[3])	
       # self.get_predictions(self.t1.sess, MASK_PATH_CLASS3, res, data[3])	
       # self.get_predictions(self.t1.sess4, MASK_PATH_CLASS4, res, data[3])	
        self.get_predictions(self.t1.sess5, MASK_PATH_CLASS5, res, data[3])	

        self.wfile.write(bytes(json.dumps(res), 'utf-8'))		
        return
		
    def do_POST(self):
        # Doesn't do anything with posted data
        content_length = int(self.headers['Content-Length']) # <--- Gets the size of data
        post_data = self.rfile.read(content_length) # <--- Gets the data itself
        self._set_headers()

        res = []

        context_ = post_data.decode("utf-8").split("=")[1]
        context = unquote(context_)	
        data = json.loads(context)
      #  for key, value in self.t1.sessions.items():
       #     thr = threading.Thread(target=self.get_predictions, args=(value['session'], CKPT_PATH+value['dir']+"/mask", res, json.loads(context)[0], int(value['prog']), value['key']), kwargs={})
        #    thr.start() # Will run "foo"
        jobs = []
        for key, value in self.t1.sessions.items():
            for j in data[0]:
                if j in value['dir']:
                    thr = threading.Thread(target=self.get_predictions, args=(value['session'], CKPT_PATH+value['dir']+"/mask", res, data[1][0], int(value['prog']), value['key']), kwargs={})
                    jobs.append(thr)

	    # Start the threads (i.e. calculate the random number lists)
        for j in jobs:
            j.start()

	    # Ensure all of the threads have finished
        for j in jobs:
            j.join()

        print ("List processing complete.")
           # thr.is_alive() # Will return whether foo is running currently

           # thr.join() # Will wait till "foo" is done
            #result = pool.apply_async(self.get_predictions, (value['session'], CKPT_PATH+value['dir']+"/mask", res, json.loads(context)[0], int(value['prog']), value['key'])) # Evaluate "f(10)" asynchronously calling callback when finished.
        print (res)
        #tf.reset_default_graph()
        self.wfile.write(bytes(json.dumps(res), 'utf-8'))			

class main:
    def __init__(self):
        self.t1 = test()

        self.server = http_server(self.t1)

if __name__ == '__main__':
    m = main()