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

import argparse
import json
import os
from sklearn.manifold import TSNE
from matplotlib import pylab

from bayou.experiments.embed.utils import read_config


def infer(clargs):
    with open(os.path.join(clargs.save, 'config.json')) as f:
        config = read_config(json.load(f), True)

    with tf.Session() as sess:
        embedding = tf.get_variable('embedding', [config.vocab_size, config.embedding_size], 
                                    dtype=tf.float32, trainable=False)
        norm = tf.sqrt(tf.reduce_sum(tf.square(embedding), 1, keep_dims=True))
        normalized_embedding = embedding / norm

        saver = tf.train.Saver({'embedding': embedding})
        ckpt = tf.train.get_checkpoint_state(clargs.save)
        saver.restore(sess, ckpt.model_checkpoint_path)
        final_embedding = normalized_embedding.eval()

    tsne = TSNE(perplexity=30, n_components=2, init='pca', n_iter=5000)
    two_d_embeddings = tsne.fit_transform(final_embedding[:clargs.num_points, :])
    words = [config.chars[i] for i in range(clargs.num_points)]
    plot(two_d_embeddings, words, clargs.out)


def plot(embeddings, labels, out):
    assert embeddings.shape[0] >= len(labels), 'More labels than embeddings'
    pylab.figure(figsize=(15,15))
    for i, label in enumerate(labels):
        x, y = embeddings[i,:]
        pylab.scatter(x, y)
        pylab.annotate(label, xy=(x, y), xytext=(5, 2), textcoords='offset points',
                       ha='right', va='bottom')
    pylab.savefig(out)
    print('Saved plot to {}'.format(out))

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--save', type=str, default='save',
                        help='directory to load model from')
    parser.add_argument('--num_points', type=int, default=500,
                        help='number of top-k words to plot')
    parser.add_argument('--out', type=str, default='plot.png',
                        help='output .png file to save plot to')
    clargs = parser.parse_args()
    infer(clargs)
