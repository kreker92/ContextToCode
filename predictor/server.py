from http.server import BaseHTTPRequestHandler, HTTPServer
import tensorflow as tf
# from tasks.env.config

from tasks.eval import repl
from model.npi import NPI
from tasks.generate_data import transform
from tasks.env.addition import AdditionCore
from tasks.env.config import CONFIG, get_args, PROGRAM_SET, LOG_PATH, DATA_PATH_TEST, CKPT_PATH_CLASS3, CKPT_PATH_CLASS2, CKPT_PATH_CLASS4, CKPT_PATH_CLASS5, CKPT_PATH_CLASS1, MASK_PATH_CLASS3, MASK_PATH_CLASS2, MASK_PATH_CLASS4, MASK_PATH_CLASS5, MASK_PATH_CLASS1, TEST_CHUNK_PATH
from tasks.env.config import get_env
import numpy as np
import pickle
import tensorflow as tf
from tensorflow.python.platform import gfile
import json
from urllib.parse import unquote

class test:
    def __init__(self):
      self.sess1 = tf.Session()
      self.sess2 = tf.Session()
      self.sess3 = tf.Session()
      self.sess4 = tf.Session()
      self.sess5 = tf.Session()


       # Initialize Addition Core
      core = AdditionCore()
 
         # Initialize NPI Model
      self.npi = NPI(core, CONFIG, LOG_PATH)
 
         # Restore from Checkpoint
      saver = tf.train.Saver()
      saver.restore(self.sess1, CKPT_PATH_CLASS1)
      saver.restore(self.sess2, CKPT_PATH_CLASS2)
      saver.restore(self.sess3, CKPT_PATH_CLASS3)
      saver.restore(self.sess4, CKPT_PATH_CLASS4)
      saver.restore(self.sess5, CKPT_PATH_CLASS5)

 
      f = open('/root/ContextToCode/predictor/log/prog_produced.txt', 'r+')
      f.truncate()

      f = open('/root/ContextToCode/predictor/log/prog_orig.txt', 'r+')
      f.truncate()

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
		
    def get_predictions(self, sess, mask, res, data, dataset):
        predict = {};
		
        transform(data, dataset, mask)

       # res.append(int(repl(self.t1.sess1, self.t1.npi, dataset, 0, predict)[-1]))
        res.append(int(repl(sess, self.t1.npi, dataset, 0, predict)[-1]))
        #res.append(int(repl(self.t1.sess3, self.t1.npi, dataset, 0, predict)[-1]))
        #res.append(int(repl(self.t1.sess4, self.t1.npi, dataset, 0, predict)[-1]))
        #res.append(int(repl(self.t1.sess5, self.t1.npi, dataset, 0, predict)[-1]))


    def __init__(self, t1, *args):
        self.t1 = t1
        BaseHTTPRequestHandler.__init__(self, *args)

    def do_GET(self):
        self._set_headers()
        dataset = []
        res = []

        with open("/root/ContextToCode/data/datasets/context.json", 'r') as handle:
          data = json.load(handle)
		  
        self.get_predictions(self.t1.sess2, MASK_PATH_CLASS2, res, data[3], dataset)	
        self.get_predictions(self.t1.sess3, MASK_PATH_CLASS3, res, data[3], dataset)	
        self.get_predictions(self.t1.sess4, MASK_PATH_CLASS4, res, data[3], dataset)	
        self.get_predictions(self.t1.sess5, MASK_PATH_CLASS5, res, data[3], dataset)	

        self.wfile.write(bytes(json.dumps(res), 'utf-8'))		
        return
		
    def do_POST(self):
        # Doesn't do anything with posted data
        content_length = int(self.headers['Content-Length']) # <--- Gets the size of data
        post_data = self.rfile.read(content_length) # <--- Gets the data itself
        self._set_headers()

        dataset = []
        res = []

        context_ = post_data.decode("utf-8").split("=")[1]
        context = unquote(context_)	
        data = json.loads(context)
        
        self.get_predictions(self.t1.sess2, MASK_PATH_CLASS2, res, json.loads(context)[0], dataset)
        self.get_predictions(self.t1.sess3, MASK_PATH_CLASS3, res, json.loads(context)[0], dataset)	
        self.get_predictions(self.t1.sess4, MASK_PATH_CLASS4, res, json.loads(context)[0], dataset)	
        self.get_predictions(self.t1.sess5, MASK_PATH_CLASS5, res, json.loads(context)[0], dataset)	

        print(res)
        self.wfile.write(bytes(json.dumps(res), 'utf-8'))			

class main:
    def __init__(self):
        self.t1 = test()

        self.server = http_server(self.t1)

if __name__ == '__main__':
    m = main()