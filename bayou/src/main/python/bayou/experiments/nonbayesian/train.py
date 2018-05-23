# Copyright 2017 Rice University
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from __future__ import print_function

import argparse
import json
import os
import textwrap
import time

import numpy as np
import tensorflow as tf

from bayou.experiments.nonbayesian.model import Model
from bayou.experiments.nonbayesian.data_reader import Reader
from bayou.experiments.nonbayesian.utils import read_config, dump_config

HELP = """\
Config options should be given as a JSON file (see config.json for example):
{                                         |
    "batch_size": 50,                     | Minibatch size
    "num_epochs": 100,                    | Number of training epochs
    "learning_rate": 0.02,                | Learning rate
    "print_step": 1,                      | Print training output every given steps
    "units": 256,                         | Size of the hidden states of encoder and decoder
    "evidence": [                         | Provide each evidence type in this list
        {                                 |
            "name": "apicalls",           | Name of evidence ("apicalls")
        },                                |
        {                                 |
            "name": "types",              | Name of evidence ("types")
        }                                 |
    ],                                    |
    "decoder": {                          | Provide parameters for the decoder here
        "max_ast_depth": 32               | Maximum depth of the AST (length of the longest path)
    }                                     |
}                                         |
"""


def train(clargs):
    config_file = clargs.config if clargs.continue_from is None \
                                else os.path.join(clargs.continue_from, 'config.json')
    with open(config_file) as f:
        config = read_config(json.load(f), save_dir=clargs.save)
    reader = Reader(clargs, config)
    
    jsconfig = dump_config(config)
    print(clargs)
    print(json.dumps(jsconfig, indent=2))
    with open(os.path.join(clargs.save, 'config.json'), 'w') as f:
        json.dump(jsconfig, fp=f, indent=2)

    model = Model(config)

    with tf.Session() as sess:
        tf.global_variables_initializer().run()
        saver = tf.train.Saver(tf.global_variables(), max_to_keep=None)
        tf.train.write_graph(sess.graph_def, clargs.save, 'model.pbtxt')
        tf.train.write_graph(sess.graph_def, clargs.save, 'model.pb', as_text=False)

        # restore model
        if clargs.continue_from is not None:
            ckpt = tf.train.get_checkpoint_state(clargs.continue_from)
            saver.restore(sess, ckpt.model_checkpoint_path)

        # training
        for i in range(config.num_epochs):
            reader.reset_batches()
            avg_loss = 0
            for b in range(config.num_batches):
                start = time.time()

                # setup the feed dict
                ev_data, n, e, y = reader.next_batch()
                feed = {model.targets: y}
                for j, ev in enumerate(config.evidence):
                    feed[model.encoder.inputs[j].name] = ev_data[j]
                for j in range(config.decoder.max_ast_depth):
                    feed[model.decoder.nodes[j].name] = n[j]
                    feed[model.decoder.edges[j].name] = e[j]

                # run the optimizer
                loss, _ = sess.run([model.loss, model.train_op], feed)
                end = time.time()
                avg_loss += np.mean(loss)
                step = i * config.num_batches + b
                if step % config.print_step == 0:
                    print('{}/{} (epoch {}), loss: {:.3f}, time: {:.3f}'.format
                          (step, config.num_epochs * config.num_batches, i,
                           np.mean(loss),
                           end - start))
            checkpoint_dir = os.path.join(clargs.save, 'model{}.ckpt'.format(i))
            saver.save(sess, checkpoint_dir)
            print('Model checkpointed: {}. Average for epoch loss: {:.3f}'.format
                  (checkpoint_dir,
                   avg_loss / config.num_batches))

if __name__ == '__main__':
    parser = argparse.ArgumentParser(formatter_class=argparse.RawDescriptionHelpFormatter,
                                     description=textwrap.dedent(HELP))
    parser.add_argument('input_file', type=str, nargs=1,
                        help='input data file')
    parser.add_argument('--save', type=str, default='save',
                        help='checkpoint model during training here')
    parser.add_argument('--config', type=str, default=None,
                        help='config file (see description above for help)')
    parser.add_argument('--continue_from', type=str, default=None,
                        help='ignore config options and continue training model checkpointed here')
    clargs = parser.parse_args()
    if clargs.config and clargs.continue_from:
        parser.error('Do not provide --config if you are continuing from checkpointed model')
    if not clargs.config and not clargs.continue_from:
        parser.error('Provide at least one option: --config or --continue_from')
    train(clargs)
