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
import re
import tensorflow as tf
from itertools import chain

CONFIG_GENERAL = ['model', 'latent_size', 'batch_size', 'num_epochs',
                  'learning_rate', 'print_step', 'alpha', 'beta']
CONFIG_ENCODER = ['name', 'units', 'num_layers', 'tile']
CONFIG_DECODER = ['units', 'num_layers', 'max_ast_depth']
CONFIG_DECODER_INFER = ['chars', 'vocab', 'vocab_size']

C0 = 'CLASS0'
UNK = '_UNK_'
CHILD_EDGE = 'V'
SIBLING_EDGE = 'H'


def length(tensor):
    elems = tf.sign(tf.reduce_max(tensor, axis=2))
    return tf.reduce_sum(elems, axis=1)


# split s based on camel case and lower everything (uses '#' for split)
def split_camel(s):
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1#\2', s)  # UC followed by LC
    s1 = re.sub('([a-z0-9])([A-Z])', r'\1#\2', s1)  # LC followed by UC
    split = s1.split('#')
    return [s.lower() for s in split]


# Do not move these imports to the top, it will introduce a cyclic dependency
import bayou.models.core.evidence


# convert JSON to config
def read_config(js, save_dir, infer=False):
    config = argparse.Namespace()

    for attr in CONFIG_GENERAL:
        config.__setattr__(attr, js[attr])
    
    config.evidence = bayou.models.core.evidence.Evidence.read_config(js['evidence'], save_dir)
    config.decoder = argparse.Namespace()
    for attr in CONFIG_DECODER:
        config.decoder.__setattr__(attr, js['decoder'][attr])
    if infer:
        for attr in CONFIG_DECODER_INFER:
            config.decoder.__setattr__(attr, js['decoder'][attr])

    return config


# convert config to JSON
def dump_config(config):
    js = {}

    for attr in CONFIG_GENERAL:
        js[attr] = config.__getattribute__(attr)

    js['evidence'] = [ev.dump_config() for ev in config.evidence]
    js['decoder'] = {attr: config.decoder.__getattribute__(attr) for attr in
                     CONFIG_DECODER + CONFIG_DECODER_INFER}

    return js


def gather_calls(node):
    """
    Gathers all call nodes (recursively) in a given AST node

    :param node: the node to gather calls from
    :return: list of call nodes
    """

    if type(node) is list:
        return list(chain.from_iterable([gather_calls(n) for n in node]))
    node_type = node['node']
    if node_type == 'DSubTree':
        return gather_calls(node['_nodes'])
    elif node_type == 'DBranch':
        return gather_calls(node['_cond']) + gather_calls(node['_then']) + gather_calls(node['_else'])
    elif node_type == 'DExcept':
        return gather_calls(node['_try']) + gather_calls(node['_catch'])
    elif node_type == 'DLoop':
        return gather_calls(node['_cond']) + gather_calls(node['_body'])
    else:  # this node itself is a call
        return [node]
