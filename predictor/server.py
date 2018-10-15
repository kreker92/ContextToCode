from http.server import BaseHTTPRequestHandler, HTTPServer
import tensorflow as tf
# from tasks.env.config

from tasks.eval import repl
from model.npi import NPI
from tasks.generate_data import transform
from tasks.env.addition import AdditionCore
from tasks.env.config import CONFIG, get_args, PROGRAM_SET, LOG_PATH, DATA_PATH_TEST, CKPT_PATH_STABLE, TEST_CHUNK_PATH
from tasks.env.config import get_env
import numpy as np
import pickle
import tensorflow as tf
from tensorflow.python.platform import gfile
import json

class test:
    def __init__(self):
      sess = tf.Session()
      # Initialize Addition Core
      core = AdditionCore()

        # Initialize NPI Model
      self.npi = NPI(core, CONFIG, LOG_PATH)

        # Restore from Checkpoint
      saver = tf.train.Saver()
      saver.restore(sess, CKPT_PATH_STABLE)

      self.sess1 = sess
        # Run REPL

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
    def __init__(self, t1, *args):
        self.t1 = t1
        BaseHTTPRequestHandler.__init__(self, *args)

    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type','text/html')
        self.end_headers()
        
        dataset = []

        with open("/root/ContextToCode/output/buffer/test/context.json", 'r') as handle:
          data = json.load(handle)
        transform(data[0], dataset)
        
        predict = {};
        predict["ncw"] = 0;
        predict["ncr"] = 0;
        predict["cw"] = 0;
        predict["cr"] = 0;

        self.wfile.write(repl(self.t1.sess1, self.t1.npi, dataset, 0, predict)) #Doesnt work
        return

class main:
    def __init__(self):
        self.t1 = test()

        self.server = http_server(self.t1)

if __name__ == '__main__':
    m = main()