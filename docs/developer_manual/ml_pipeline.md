# MLPipeline

The `MLPipeline` acts as a simple facade for the three `MLImplementationType`s of `MLAlgorithms` LIMES 1.0 offers: `UNSUPERVISED`, `SUPERVISED_BATCH`, `SUPERVISED_ACTIVE`.
In the following the pattern for invoking a `MLAlgorithm` is presented.
Because many algorithms can support more than one `MLImplementationType`, to minimize redundant code, `MLAlgorithm`'s should be implemented by extending the abstract class `ACoreMLAlgorithm`. `ACoreMLAlgorithm`'s interface covers all three types, but not all three need to be implemented.
For each `MLImplementationType` there is a decorator class extending `AMLAlgorithm`. To use any `MLAlgorithm` in a safe way, pass its class type to the constructor of the decorator class corresponding to the `MLImplementationType`, e.g.:

	Class<? extends ACoreMLAlgorithm> clazz = WombatComplete.class;
	UnsupervisedMLAlgorithm mlu = new UnsupervisedMLAlgorithm(clazz);

There also exists an `MLAlgorithmFactory` which offers a method `getAlgorithmType(String name)` to retrieve class types of algorithms from their names .
The usage patterns for the `MLImplementationType`s are as follows:

## Unsupervised Learning

In unsupervised learning, predictions are made based on the structure of existing data. A helpful concept for unsupervised learning of links between knowledge bases is the so called `Pseudo-F-measure` (PFM). PFMs can be defined in various ways, but generally they are used to evaluate the intermediary results of the unsupervised learning algorithm, based only on its structure.
Initialization works the same across all cases of ML. For unsupervised learning, we then pass a PFM to the `learn()` method, which returns an instance of class `MLResults`. We can use this result as a parameter to the `predict` function, which allows us to, for example, learn on smaller subsets of our data and then apply the learned classificators to the whole dataset.

	UnsupervisedMLAlgorithm mlu = new UnsupervisedMLAlgorithm(clazz);
	mlu.init(learningParameters, source, target);
	mlm = mlu.learn(pfm);
	mlu.predict(source, target, mlm);

## Supervised Batch Learning

In supervised batch learning, you learn to classify resp. evaluate future (a priori unknown) observations by looking at sufficiently many training examples, which are all known at the start of the algorithm.
For this to work really good, a lot of training data is needed.
The usage in limes is analogous to the unsupervised case, except this time, we do not pass a PFM to the `learn()` method. Instead, we pass it an instance of `AMapping` which stores our training data.

	SupervisedMLAlgorithm mls = new SupervisedMLAlgorithm(clazz);
	mls.init(learningParameters, source, target);
	mlm = mls.learn(trainingDataMap);
	mls.predict(source, target, mlm);

## Supervised Active Learning

Active learning is like batch learning, except that the training examples are generated on the fly, by selecting example observations upon which different intermediary results of the algorithm disagree the most. An oracle (mostly a human expert) is then prompted to evaluate the selected examples, which are then fed back into the algorithm as training examples.
This is why in LIMES an active learning algorithm's `activeLearn` method can be called without parameters, e.g. for the first step of selecting appropriate examples for user evaluation (which is actually unsupervised), or it can be called with an instance of `AMapping`, storing the training examples.
For retrieving the examples for user evaluation, the `getNextExamples` method returns an instance of `AMapping`.

	ActiveMLAlgorithm mla = new ActiveMLAlgorithm(clazz);
	mla.init(learningParameters, source, target);
	mlm = mla.activeLearn(); 
	AMapping nextExamples = mla.getNextExamples(maxIterations);
	// retrieve assessments from oracle and update scores in nextExamples accordingly
	mlm = mla.activeLearn(nextExamples);
	mla.predict(source, target, mlm);