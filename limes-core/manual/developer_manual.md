# LIMES 1.0 Developer Manual

## Introduction

This short manual is intended for developers who want to extend LIMES 1.0 and/or include it into their own software products. It aims to deliver an overview of the architecture underlying our framework, explain the core concepts and give developers entry points into the java docs for further reading.

## Basic Data Structure

There are two classes that are fundamental for representing linked data within LIMES:

- **ACache** stores *input data*, i.e. linked data retrieved from a triple store or file. In the most cases, there are two caches, one for the source *knowledge base* (KB) and one for the target KB.

- **AMapping** stores *output data*, i.e. the mapping (from source resources to target resources) that is generated as the result of running LIMES. An entry in a mapping is a triple of source URI, linking predicate and target URI. Additionally, Mapping could store the confidence score.

## Overview
![LIMES workflow](images/uml.png "fig:")


As you can see from the flow diagram above, LIMES 1.0 mainly consists of 2 separate execution pipelines:

- **LSPipeline** executes a given *Link Specification* on input KB `S` and `T`, resulting in a *Mapping*
- **MLPipeline** uses *Machine Learning* algorithms on KB `S` and `T`, also resulting in a *Mapping*

## LSPipeline

The **LSPipeline** consists of three basic building blocks: **Rewriter**, **Planner** and **ExecutionEngine**. 

- A **Rewriter** aims to simplify a given LS by removing potential redundancy, that eventually speeds up its execution.
- A **Planner** generates the plan of an input LS. For an *atomic LS*, it generates a simple *Plan* that consists of a *RUN* command. For a *complex LS*, it determines which *atomic LS* should be executed first, how to process intermediary results and identifies dependencies between atomic LSs. For example, a *Planner* can decide to first run some *atomic LS* and then filter the results using another *atomic LS* instead of running it independently and merging the results.
- An **ExecutionEngine** is responsible for executing the *Plan* of an input LS. It takes as input a LS and a *Planner*, it executes  the *Plan* and returns the set of links as a *AMapping*.

All of these building blocks have several implementations which can be instantiated using the respective factories, i.e. *RewriterFactory*, *PlannerFactory*, *ExecutionEngineFactory*.
This code example, taken from the **LSPipeline** class, demonstrates the usage pattern:

	Rewriter rw = RewriterFactory.getRewriter(rewriterType);
	LinkSpecification rwLs = rw.rewrite(ls);
	// Planning execution of the LS
	Planner planner = ExecutionPlannerFactory.getPlanner(executionPlannerType, sourceCache, targetCache);
	// Execute the ExecutionPlan obtained from the LS
	ExecutionEngine engine = ExecutionEngineFactory.getEngine(executionEngineType, sourceCache, targetCache, sourceVar, targetVar);
	return engine.execute(rwLs, planner);
	
###Measures
LIMES supports a set of metrics that can be used inside a LS to link resources (see [user_manual.md](user_manual.md) for more details). Each metric corresponds to one mapper. One mapper can correspond to more that one metric. The **Measures** package is divided into two sub-packages: **Measure** and **Mapper**:

- The **Measure** packages includes the interface **IMeasure**, the abstract class **AMeasure**, the set of metrics implemented in LIMES, a **MeasureType** class, a **MeasureFactory** class and a **MeasureProcessor** class:
	
	* The **IMeasure** interface includes all the basic functions that any metric class must implement.
	* The **AMeasure** class implements the **IMeasure** interface.
	* The **MeasureType** class includes all the labels of the metrics implemented in LIMES. If a developer wants to include a new metric, they have to first add it in this class using a representative name.
	* The metrics are divided into type packages based on the type of property they are comparing (*string*, *pointsets*, *space*, *topology* and *space*). Each type package includes an interface and an abstract class, i.e. the *string* type package includes the **IStringMeasure** interface and the **IStringMeasure** abstract class. All metrics that belong to a particual type package must implement the corresponding interface and abstract class. Additionally, the type package abstract class must extend **AMeasure** and implement the corresponding type interface. The type interface must extend the **IMeasure** interface. If a developer wants to include a new metric, they have to first find the correct package or create a new type package if there is none representative. If a corresponding package exists, then the new metric class must extend the type abstract class and as a result, it must implement all overriden methods of the type interface. No unsupported functions are allowed. If the developer wants to create a new type package, he has to create a corresponding abstract type class and interface and follow the instructions listed above about extending and implementing existing interfaces and classes.
	* The **MeasureFactory** class returns an object of a measure given an input measure name. If the developer wants to add a new metric, he has to create a *public static final* field of String type using the same name of metric as the one used in the **MeasureType** class i.e. *public static final String COSINE = "cosine";* and then add the choice of returing this measure at the *getMeasureType* and *createMeasure* functions following the pattern used for other metrics.
	* The **MeasureProcessor** class is responsible for returning the similarity between two instances, given a simple or complex metric expression and a threshold.

- The **Mapper** has almost the same hierarchy as the **Measure** package. The **Mapper** packages includes the interface **IMapper**, the abstract class **AMapper**, a **MapperFactory** class and a **MappingOperations** class:
	
	* The **IMapper** interface includes all the basic functions that any mapper class must implement.
	* The **AMapper** class implements the **IMapper** interface.
	* The mappers are divided into type packages based on the type of property their corresponding metrics are comparing (*string*, *pointsets*, *space*, *topology* and *space*).  All mappers must extend the **AMapper** abstrast class. If a developer wants to include a new mapper, they have to first find the correct package or create a new type package if there is none representative. If a corresponding package exists, then the new mapper class must extend the **AMapper** abstrast class as a result, it must implement all overriden methods of the **IMapper** interface. No unsupported functions are allowed. If the developer wants to create a new type package, he should follow the previous instructions regarding extending and implementing existing interfaces and classes. 
	* The **MapperFactory** class returns an object of a mapper given an input measure name. If the developer wants to add a new mapper, he has add the choice of returing this mapper at the *createMapper* function following the pattern used for other mappers.
	* The **MappingOperations** includes the basic functions between mappings.
	
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
