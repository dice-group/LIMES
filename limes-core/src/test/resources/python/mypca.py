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



lines = [it.strip() for it in open("python/wiki-abstracts-vectors.txt")]
x = []
labels = []
for i in range(0, len(lines), 3):
	a, b, c = lines[i:i + 3]
	labels.append(a)
	simple = [float(it) for it in b.split()]
	normal = [float(it) for it in c.split()]
	x.append(simple)
	x.append(normal)

def plotFit(x_fit, title):
	fig = plt.figure(figsize = (8,8))
	ax = fig.add_subplot(1,1,1)
	ax.set_xlabel('Principal Component 1', fontsize = 15)
	ax.set_ylabel('Principal Component 2', fontsize = 15)
	ax.set_title(title, fontsize = 16)


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
	myshow(block=False)

def my_cosine_distance(a,b):
	return 1-0.5*np.dot(a,b)/(np.linalg.norm(a)*np.linalg.norm(b))

from sklearn.preprocessing import StandardScaler
x = StandardScaler().fit_transform(x)
# from sklearn.decomposition import PCA
# x_fit = PCA(n_components=2).fit_transform(x)
from sklearn.manifold import TSNE
from sklearn.metrics import pairwise_distances
distance_matrix = pairwise_distances(x, x, metric='cosine', n_jobs=-1)
distance_matrix = np.maximum(np.zeros(shape=distance_matrix.shape), distance_matrix)
xs = []
ys = []
for lr in range(50,1000,50):
	x_fit = TSNE(n_components=2, metric="precomputed", learning_rate=lr).fit_transform(distance_matrix)
	mean_all_pairs_distance = 1/(len(x_fit)**2)*sum(sum(np.linalg.norm(x_fit[a] - x_fit[b]) for b in range(len(x_fit))) for a in range(len(x_fit)))
	mean_correct_pair_distance = 1 / (len(x_fit) // 2) * sum(np.linalg.norm(x_fit[i] - x_fit[i + 1]) for i in range(0, len(x_fit), 2))
	xs.append(lr)
	quality = mean_all_pairs_distance / mean_correct_pair_distance
	ys.append(quality)
	print(lr, mean_correct_pair_distance, mean_all_pairs_distance, quality)
	title = '2 component reduction, learning-rate=' + str(lr)
	# plotFit(x_fit, title)

plt.plot(xs, ys)
plt.title("mean relative distance for learning rate")
myshow(block=True)