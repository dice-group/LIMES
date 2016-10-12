# Evaluation Classes

The `Evaluation` package provides an environment to test and evaluate the learning algorithms to be applied in LIMES framework. The objective is to figure out how beneficial are the algorithms and to give an insight about their performances in both qualitative and quantitative measures.
 There are five packages that form the evaluation environment. These packages are:
 
- `QualitiativeMeasures` contains the classes that measure the learning algorithm in  terms of quality. The classes are arranged in a hierarchy where interface `IQualitiativeMeasure` that gives the signature of the method **calculate()** which will be implemented by all classes to calculate the required measure. Several classes implement the interface method these classes include:
    * `Accuracy`
    * `F-MEasure`
    * `Precision`
    * `Recall`
    * `APseudoPRF`
        * `PdeusoFMeasure`
                * `PdeusoRefFMeasure` 
        * `PdeusoPrecision`
                * `PdeusoRefPrecision`  
        * `PdeusoRecall`
                * `PdeusoRefRecall`                      


- `QuanitaiveMeasures` contains classes that measure the quantitative issues in machine learning performance such as the run time. The interface `IQuanitativeMeasure` describe set of methods **getRuns(), getRun(), addRun(), setRun(), getRunInfo()**. Two classes work together to do such task. These classes are:
    * `RunsData`
    * `RunRecord`
 
- `Oracle` contains the oracle classes that are useful in the supervised learning algorithms. Two classes exist where one implements the interface `IOracle` while the other class is a factory that uses the first one. The list of classes are:
    * `SimpleOracle`
    * `OracleFactory`

- `EvaluationDataLoader`  This package is responsible through its classes to load the datasets used to test the learning algorithms and to support the evaluation process with benchmarks and oracle data. Four classes comprise this package which are:
    * `DataSetChooser`
    * `EvaluationData`
    * `Experiment`
    * `OAEIMappingParser`
                
 

- `Evaluator` represents the main entry for running the evaluation against the machine learning. It uses an enumeration to specify the type of measure to use. A Factory class is used inside the main class in the package *Evaluator* to give the proper evaluation environment.
    * `EValuator`
    * `EvaluatorFactory`
    * `EvaluatorType` *(enum)*