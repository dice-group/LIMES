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


def plotFit(x_fit, title):
	fig = plt.figure(figsize=(8, 8))
	ax = fig.add_subplot(1, 1, 1)
	ax.set_xlabel('Principal Component 1', fontsize=15)
	ax.set_ylabel('Principal Component 2', fontsize=15)
	ax.set_title(title, fontsize=16)

	ax.scatter(x_fit[::2, 0], x_fit[::2, 1], color="r")
	ax.scatter(x_fit[1::2, 0], x_fit[1::2, 1], color="b")

	# from matplotlib import collections  as mc
	#
	# lines = [[x_fit[it], x_fit[it + 1]] for it in
	#          range(0, len(x_fit), 2)]
	# lc = mc.LineCollection(lines, color="k")
	# ax.add_collection(lc)

	for label, x, y in zip(labels, x_fit[::2, 0], x_fit[::2, 1]):
		plt.annotate(
			label + "S",
			xy=(x, y), xytext=(-5, 5),
			textcoords='offset points', ha='right', va='bottom')
	for label, x, y in zip(labels, x_fit[1::2, 0], x_fit[1::2, 1]):
		plt.annotate(
			label + "N",
			xy=(x, y), xytext=(-5, 5),
			textcoords='offset points', ha='right', va='bottom')

	ax.legend(["Simple", "Normal"])
	myshow(block=False)


def my_cosine_distance(a, b):
	return 1 - 0.5 * (
			1 + np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b)))


a = [1,1]
b = [-1,-1]
print(1-my_cosine_distance(a,b))
# python: 0.998964444867
# java: 0.9979289174079895
sys.exit()

x = np.array(x)
from sklearn.preprocessing import StandardScaler

# x = StandardScaler().fit_transform(x)
# from sklearn.decomposition import PCA
# x_fit = PCA(n_components=2).fit_transform(x)

from sklearn.metrics import pairwise_distances

distance_matrix = pairwise_distances(x, x, metric='cosine', n_jobs=-1)
distance_matrix = np.maximum(np.zeros(shape=distance_matrix.shape),
                             distance_matrix) * 0.5
print(distance_matrix)
print(np.mean(distance_matrix))
avg_dist_all = np.mean(distance_matrix)
avg_dist_simple = np.mean(distance_matrix[::2, ::2])
avg_dist_normal = np.mean(distance_matrix[1::2, 1::2])
avg_dist_pairs = 1 / (len(x) // 2) * sum(
	my_cosine_distance(x[i], x[i + 1]) for i in range(0, len(x), 2))
print(avg_dist_all, avg_dist_simple, avg_dist_normal, avg_dist_pairs)
# results: 0.499697531375 0.471654812898 0.471823169607 0.442123100908
confidences = []
for a in range(0, len(distance_matrix), 2):
	for b in range(1, len(distance_matrix), 2):
		confidence = 1 - distance_matrix[a, b]
		if a % 2 == 0 and a + 1 == b:  # golden standard
			confidences.append((confidence, True))
		else:
			confidences.append((confidence, False))
confidences.sort()
tp, tn, fn = sum(b for (c, b) in confidences), 0, 0
bests = 0, 0, 0, 0
fp = len(confidences) - tp
best_threshold = -0.001


def f_score():
	if tp == 0:
		return 0.0
	precision = tp / (tp + fp)
	recall = tp / (tp + fn)
	return 2 * (precision * recall) / (precision + recall)


best_f_score = f_score()
for c, b in confidences:
	if b:
		tp -= 1
		fn += 1
	else:
		tn += 1
		fp -= 1
	f = f_score()
	if f > best_f_score:
		best_f_score = f
		best_threshold = c + 0.001
		bests = (tp, tn, fn, fp)
print(
	"best threshold, best f score, tp,tn,fn,fp, (tp/(fn+tp))/(p/(p+n))('red / green'):")
tp,tn,fn,fp = bests
print(best_threshold, best_f_score, bests,
      (tp / (fn + tp)) / ((tp + fp) / (tp + fp + tn + fn)))
# =======================

"""
sim instead distance results:
best threshold, best f score, tp,tn,fn,fp, (tp/(fn+tp))/(p/(p+n))('red / green'):
0.945193769973 0.07253886010362695 (7, 7914, 83, 96) 6.116504854368932

first try:
0.499697531375
0.499697531375 0.471654812898 0.471823169607 0.442123100908
best threshold, best f score, tp,tn,fn,fp, (tp/(fn+tp))/(p/(p+n))('red / green'):
0.154033065905 0.011020284589766878 (91, 229, 0, 16333) 1.0139430102289333

bugfixed (not standardized):
0.163774287326
0.163774287326 0.192452176882 0.121326485291 0.143316474943
best threshold, best f score, tp,tn,fn,fp, (tp/(fn+tp))/(p/(p+n))('red / green'):
0.0495554248693 0.022123893805309738 (90, 54, 0, 7956) 1.0067114093959733

"""

sys.exit()
from sklearn.manifold import TSNE

xs = []
ys = []
for lr in range(5, 100, 5):
	x_fit = TSNE(n_components=2, metric="precomputed",
	             learning_rate=lr).fit_transform(distance_matrix)
	mean_all_pairs_distance = 1 / (len(x_fit) ** 2) * sum(
		sum(np.linalg.norm(x_fit[a] - x_fit[b]) for b in range(len(x_fit))) for
		a in range(len(x_fit)))
	mean_correct_pair_distance = 1 / (len(x_fit) // 2) * sum(
		np.linalg.norm(x_fit[i] - x_fit[i + 1]) for i in
		range(0, len(x_fit), 2))
	xs.append(lr)
	quality = mean_all_pairs_distance / mean_correct_pair_distance
	ys.append(quality)
	print(lr, mean_correct_pair_distance, mean_all_pairs_distance, quality)
	title = '2 component reduction, learning-rate=' + str(lr)
	plotFit(x_fit, title)

plt.plot(xs, ys)
plt.title("mean relative distance for learning rate")
myshow(block=True)
