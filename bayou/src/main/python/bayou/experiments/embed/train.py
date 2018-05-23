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
import collections
import json
import math
import os
import textwrap
import time
from itertools import chain

import numpy as np
import tensorflow as tf

from bayou.experiments.embed.utils import read_config, dump_config
from bayou.models.core.utils import C0, UNK

HELP = """\
Config options should be given as a JSON file (see config.json for example):
{                            |
    "embedding_size": 8,     | Size of the embedding
    "window_size": 1,        | Window size to use for skip-grams
    "num_sampled": 64,       | Number of samples for softmax (NCE) loss
    "batch_size": 50,        | Minibatch size
    "num_epochs": 100,       | Number of training epochs
    "learning_rate": 1.0,    | Learning rate
    "print_step": 1          | Print training output every given steps
}                            |
"""


def get_data_javadoc(js):
    data = []
    for program in js['programs']:
        javadoc = program['javadoc'] if 'javadoc' in program else None
        if javadoc:
            data.append(javadoc.split())
    return data


def to_skip_grams(data, window_size):
    inputs, targets = [], []
    for point in data:
        for i, word in enumerate(point):
            left = max(i - window_size, 0)
            right = min(i + window_size + 1, len(point))
            window = list(range(left, right))
            if i in window:
                window.remove(i)
            for j in window:
                inputs.append(word)
                targets.append(point[j])
    return inputs, targets


def wrangle(raw_inputs, raw_targets, config):
    sz = config.num_batches * config.batch_size
    raw_inputs = raw_inputs[:sz]
    raw_targets = raw_targets[:sz]

    inputs = np.array(list(map(config.vocab.get, raw_inputs)))
    targets = np.array(list(map(config.vocab.get, raw_targets)))

    inputs = np.split(inputs, config.num_batches)
    targets = np.split(np.reshape(targets, [-1, 1]), config.num_batches)

    return inputs, targets


def model(config):
    inputs = tf.placeholder(tf.int32, [config.batch_size])
    targets = tf.placeholder(tf.int32, [config.batch_size, 1])

    embeddings = tf.get_variable('embedding', initializer=tf.random_uniform(
                        [config.vocab_size, config.embedding_size], -1.0, 1.0))
    w = tf.get_variable('w', initializer=tf.truncated_normal([config.vocab_size,
                        config.embedding_size], stddev=1.0 / math.sqrt(config.embedding_size)))
    b = tf.get_variable('b', initializer=tf.zeros([config.vocab_size]))

    embed = tf.nn.embedding_lookup(embeddings, inputs)
    loss = tf.reduce_mean(tf.nn.nce_loss(weights=w, biases=b,
                                         inputs=embed, labels=targets,
                                         num_sampled=config.num_sampled,
                                         num_classes=config.vocab_size))
    optimizer = tf.train.AdagradOptimizer(config.learning_rate).minimize(loss)

    return inputs, targets, loss, optimizer


def train(clargs):
    with open(clargs.config) as f:
        config = read_config(json.load(f), False)
    with open(clargs.input_file[0]) as f:
        js = json.load(f)

    data = get_data_javadoc(js)

    chars = collections.Counter(chain.from_iterable(data))
    chars[C0] = 1
    chars[UNK] = 1
    config.chars = sorted(chars.keys(), key=lambda c: -chars[c])
    config.vocab = dict(zip(config.chars, range(len(config.chars))))
    config.vocab_size = len(config.vocab)

    jsconfig = dump_config(config)
    with open(os.path.join(clargs.save, 'config.json'), 'w') as f:
        json.dump(jsconfig, fp=f, indent=2)

    raw_inputs, raw_targets = to_skip_grams(data, config.window_size)
    config.num_batches = int(len(raw_inputs) / config.batch_size)
    assert config.num_batches > 0, 'Not enough data'
    inputs, targets = wrangle(raw_inputs, raw_targets, config)
    print('Training data: {} pairs, {} batches, {} vocab size'.format
          (len(raw_inputs), len(inputs), config.vocab_size))

    tf_inputs, tf_targets, loss, optimizer = model(config)

    with tf.Session() as sess:
        tf.global_variables_initializer().run()
        saver = tf.train.Saver(tf.global_variables())
        sum_cost = 0
        for i in range(config.num_epochs):
            for j in range(config.num_batches):
                start = time.time()
                batch_inputs, batch_targets = inputs[j], targets[j]
                feed = { tf_inputs: batch_inputs, tf_targets: batch_targets }
                _, cost = sess.run([optimizer, loss], feed_dict=feed)
                end = time.time()
                sum_cost += np.mean(cost)
                if j % config.print_step == 0:
                    print('{}/{} (epoch {}), loss: {:2.3f} time: {:.3f}'.format(
                        i * config.num_batches + j, config.num_epochs * config.num_batches, i, 
                        sum_cost / config.print_step, end - start))
                    sum_cost = 0
            checkpoint_dir = os.path.join(clargs.save, 'model.ckpt')
            saver.save(sess, checkpoint_dir)
            print('Model checkpointed: {}'.format(checkpoint_dir))

if __name__ == '__main__':
    parser = argparse.ArgumentParser(formatter_class=argparse.RawDescriptionHelpFormatter,
                                     description=textwrap.dedent(HELP))
    parser.add_argument('input_file', type=str, nargs=1,
                        help='input data file')
    parser.add_argument('--config', type=str, required=True,
                        help='config file (see description above for help)')
    parser.add_argument('--save', type=str, default='save',
                        help='checkpoint model during training here')
    clargs = parser.parse_args()
    train(clargs)
