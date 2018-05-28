import sys

import matplotlib


matplotlib.use("TkAgg")
import numpy as np
import matplotlib.pyplot as plt


def quit_figure(event):
	if event.key == 'escape':
		plt.close(event.canvas.figure)


def myshow(block=True):
	plt.gcf().canvas.mpl_connect('key_press_event', quit_figure)
	plt.show(block=block)



lines = [it.strip() for it in open("wiki-abstracts-vectors.txt")]
x = []
labels = []
for i in range(0, len(lines), 3):
	a, b, c = lines[i:i + 3]
	labels.append(a)
	simple = [float(it) for it in b.split()]
	normal = [float(it) for it in c.split()]
	x.append(simple)
	x.append(normal)

from sklearn.preprocessing import StandardScaler
x = StandardScaler().fit_transform(x)
# from sklearn.decomposition import PCA
# x_fit = PCA(n_components=2).fit_transform(x)
from sklearn.manifold import TSNE
x_fit = TSNE(n_components=2).fit_transform(x)

fig = plt.figure(figsize = (8,8))
ax = fig.add_subplot(1,1,1)
ax.set_xlabel('Principal Component 1', fontsize = 15)
ax.set_ylabel('Principal Component 2', fontsize = 15)
ax.set_title('2 component PCA', fontsize = 20)


ax.scatter(x_fit[::2, 0], x_fit[::2, 1], color="r")
ax.scatter(x_fit[1::2, 0], x_fit[1::2, 1], color="b")

from matplotlib import collections  as mc

lines = [[x_fit[it], x_fit[it + 1]] for it in
         range(0, len(x_fit), 2)]
lc = mc.LineCollection(lines, color="k")
ax.add_collection(lc)

for label, x, y in zip(labels, x_fit[::2, 0], x_fit[::2, 1]):
	plt.annotate(
		label+"S",
		xy=(x, y), xytext=(-5, 5),
		textcoords='offset points', ha='right', va='bottom')
for label, x, y in zip(labels, x_fit[1::2, 0], x_fit[1::2, 1]):
	plt.annotate(
		label+"N",
		xy=(x, y), xytext=(-5, 5),
		textcoords='offset points', ha='right', va='bottom')

ax.legend(["Simple", "Normal"])
myshow()