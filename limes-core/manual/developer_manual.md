# LIMES 1.0 Developer Manual

## Introduction

This short manual is intended for developers who want to extend LIMES 1.0 and/or include it into their own software products. It aims to deliver an overview of the architecture underlying our framework, explain the core concepts and give developers entry points into the java docs for further reading.

## Basic Data Structure

There are two classes that are fundamental for representing linked data within LIMES:

- **ACache** stores *input data*, i.e. linked data retrieved from a triple store or file. In the most cases, there are two caches, one for the source *knowledge base* (KB) and one for the target KB.

- **AMapping** stores *output data*, i.e. the mapping (from source resources to target resources) that is generated as the result of running LIMES. An entry in a mapping is a triple of source URI, linking predicate and target URI. Additionally, Mapping could store the confidence score.

## Overview
![LIMES workflow](images/uml.png "fig:")
[fig:workflow] 

As you can see from the flow diagram above, LIMES 1.0 mainly consists of 2 separate execution pipelines:

- **LSPipeline** executes a given *Link Specification* on input KB `S` and `T`, resulting in a *Mapping*
- **MLPipeline** uses *Machine Learning* algorithms on KB `S` and `T`, also resulting in a *Mapping*

## LSPipeline

The **LSPipeline** consists of three basic building blocks: **Rewriter**, **Planner** and **ExecutionEngine**. 

- A **Rewriter** aims to simplify a given LS to remove potential redundancy, eventually speeding up its execution.
- A **Planner** determines which *atomic LS* should be executed first and how to process intermediary results, e.g. it could plan to first run some *atomic LS* and then filter the results using another *atomic LS* instead of running it independently and merging the results.
- An **ExecutionEngine** executes the *Plan* which results from running the *Planner*. 

All of these building blocks have several implementations which can be instantiated using the respective factories, i.e. *RewriterFactory*, *PlannerFactory*, *ExecutionEngineFactory*.
This code example, taken from the **LSPipeline** class, demonstrates the usage pattern:

	Rewriter rw = RewriterFactory.getRewriter(rewriterType);
	LinkSpecification rwLs = rw.rewrite(ls);
	// Planning execution of the LS
	Planner planner = ExecutionPlannerFactory.getPlanner(executionPlannerType, sourceCache, targetCache);
	// Execute the ExecutionPlan obtained from the LS
	ExecutionEngine engine = ExecutionEngineFactory.getEngine(executionEngineType, sourceCache, targetCache, sourceVar, targetVar);
	return engine.execute(rwLs, planner);

## MLPipeline

The **MLPipeline** acts as a simple facade for the three *MLImplementationType*s of *MLAlgorithms* LIMES 1.0 offers: *UNSUPERVISED*, *SUPERVISED\_BATCH*, *SUPERVISED\_ACTIVE*.
In the following the patten for invoking a *MLAlgorithm* is presented.
Because many algorithms can support more than one *MLImplementationType*, to minimize redundant code, *MLAlgorithm*s should be implemented by extending the abstract class *ACoreMLAlgorithm*. *ACoreMLAlgorithm*'s interface covers all three types, but not all three need to be implemented.
For each *MLImplementationType* there is a decorator class extending *AMLAlgorithm*. To use any *MLAlgorithm* in a safe way, pass its class type to the constructor of the decorator class corresponding to the *MLImplementationType*, e.g.:

	Class<? extends ACoreMLAlgorithm> clazz = WombatComplete.class;
	UnsupervisedMLAlgorithm mlu = new UnsupervisedMLAlgorithm(clazz);

There also exists an *MLAlgorithmFactory* which offers a method `getAlgorithmType(String name)` to retrieve class types of algorithms from their names .
The usage patterns for the *MLImplementationType*s are as follows:

### Unsupervised Learning

In unsupervised learning, predictions are made based on the structure of existing data. A helpful concept for unsupervised learning of links between knowledge bases is the so called *Pseudo-F-measure* (PFM). *PFM*s can be defined in various ways, but generally they are used to evaluate the intermediary results of the unsupervised learning algorithm, based only on its structure.
Initialization works the same across all cases of ML. For unsupervised learning, we then pass a *PFM* to the `learn` method, which returns an instance of class `MLResults`. We can use this result as a parameter to the `predict` function, which allows us to, for example, learn on smaller subsets of our data and then apply the learned classificators to the whole dataset.

	UnsupervisedMLAlgorithm mlu = new UnsupervisedMLAlgorithm(clazz);
	mlu.init(learningParameters, source, target);
	mlm = mlu.learn(pfm);
	mlu.predict(source, target, mlm);

### Supervised Batch Learning

In supervised batch learning, you learn to classify resp. evaluate future (a priori unknown) observations by looking at sufficiently many training examples, which are all known at the start of the algorithm.
For this to work really good, a lot of training data is needed.
The usage in limes is analogous to the unsupervised case, except this time, we do not pass a *PFM* to the `learn` method. Instead, we pass it an instance of *AMapping* which stores our training data.

	SupervisedMLAlgorithm mls = new SupervisedMLAlgorithm(clazz);
	mls.init(learningParameters, source, target);
	mlm = mls.learn(trainingDataMap);
	mls.predict(source, target, mlm);

### Supervised Active Learning

Active learning is like batch learning, except that the training examples are generated on the fly, by selecting example observations upon which different intermediary results of the algorithm disagree the most. An oracle (mostly a human expert) is then prompted to evaluate the selected examples, which are then fed back into the algorithm as training examples.
This is why in LIMES an active learning algorithm's `activeLearn` method can be called without parameters, e.g. for the first step of selecting appropriate examples for user evaluation (which is actually unsupervised), or it can be called with an instance of *AMapping*, storing the training examples.
For retrieving the examples for user evaluation, the `getNextExamples` method returns an instance of *AMapping*.

	ActiveMLAlgorithm mla = new ActiveMLAlgorithm(clazz);
	mla.init(learningParameters, source, target);
	mlm = mla.activeLearn(); 
	AMapping nextExamples = mla.getNextExamples(maxIterations);
	// retrieve assessments from oracle and update scores in nextExamples accordingly
	mlm = mla.activeLearn(nextExamples);
	mla.predict(source, target, mlm);
