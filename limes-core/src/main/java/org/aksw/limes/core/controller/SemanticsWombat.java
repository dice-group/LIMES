package org.aksw.limes.core.controller;

import com.google.common.collect.Lists;
import com.opencsv.CSVWriter;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Evaluate Refinement based LGG for benchmark datasets DBLP-ACM,
 * Abt-Buy,Amazon-GoogleProducts, DBLP-Scholar, Person1, Person2, Restaurants,
 * DBLP-LinkedMDB and Dailymed-DrugBank
 *
 * @author sherif
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 */

public class SemanticsWombat {
    /**
     *
     */
    private static final Logger logger = Logger.getLogger(SemanticsWombat.class);

    public static AMapping fullReferenceMapping;
    public static int fold = 1;
    public static String datasetName;
    public static String resultsFileTest = null;
    public static String resultsFileTraining = null;
    public static EvaluationData data = null;
    public static CSVWriter csvWriterTest = null;
    public static CSVWriter csvWriterTrain = null;
    public static int experiment = 0;

    public static void init(String[] args) {

        datasetName = args[0];
        experiment = Integer.valueOf(args[1]);

        data = DataSetChooser.getData(datasetName);

        resultsFileTest = data.getDatasetFolder() + "Test" + experiment + data.getEvaluationResultFileName();
        resultsFileTraining = data.getDatasetFolder() + "Training" + experiment + data.getEvaluationResultFileName();

        createFile(resultsFileTest, csvWriterTest);
        createFile(resultsFileTraining, csvWriterTrain);

    }

    public static void createFile(String fileName, CSVWriter writer) {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                logger.error("File can't be created");
                e.printStackTrace();
                throw new RuntimeException();
            }
        }

        // write header
        try {
            writer = new CSVWriter(new FileWriter(fileName, true));
        } catch (IOException e) {
            logger.error("Can't create csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }
        writer.writeNext(new String[] { "Experiment " + experiment }, false);
        writer.writeNext(new String[] { "GoldStandard", "Predictions", "Precision", "Recall", "F-measure", "Accuracy",
                "Runtime", "LS" }, false);
        try {
            writer.close();
        } catch (IOException e) {
            logger.error("Couldn't close csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static List<LearningParameter> setLearningParameters(Set<String> measures) {
        List<LearningParameter> learningParameters = new ArrayList<LearningParameter>();
        long maxRefineTreeSize = 100;
        int maxIterationNumber = 10;
        int maxIterationTimeInMin = 5;
        int maxExecutionTimeInMin = 50;
        double maxFitnessThreshold = 1;
        double childrenPenaltyWeight = 1;
        double complexityPenaltyWeight = 1;
        boolean saveMapping = true;
        double minPropertyCoverage = 0.6;
        double propertyLearningRate = 0.9;
        double overallPenaltyWeight = 0.5d;
        boolean verbose = false;

        learningParameters = new ArrayList<>();
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE, maxRefineTreeSize,
                Long.class, 10d, Long.MAX_VALUE, 10d, AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, maxIterationNumber,
                Integer.class, 1d, Integer.MAX_VALUE, 10d, AWombat.PARAMETER_MAX_ITERATIONS_NUMBER));
        learningParameters
                .add(new LearningParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, maxIterationTimeInMin,
                        Integer.class, 1d, Integer.MAX_VALUE, 1, AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, maxExecutionTimeInMin,
                Integer.class, 1d, Integer.MAX_VALUE, 1, AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_MAX_FITNESS_THRESHOLD, maxFitnessThreshold,
                Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_MAX_FITNESS_THRESHOLD));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_MIN_PROPERTY_COVERAGE, minPropertyCoverage,
                Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_MIN_PROPERTY_COVERAGE));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_PROPERTY_LEARNING_RATE, propertyLearningRate,
                Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_PROPERTY_LEARNING_RATE));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_OVERALL_PENALTY_WEIGHT, overallPenaltyWeight,
                Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_OVERALL_PENALTY_WEIGHT));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_CHILDREN_PENALTY_WEIGHT, childrenPenaltyWeight,
                Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_CHILDREN_PENALTY_WEIGHT));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_COMPLEXITY_PENALTY_WEIGHT,
                complexityPenaltyWeight, Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_COMPLEXITY_PENALTY_WEIGHT));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_VERBOSE, verbose, Boolean.class, 0, 1, 0,
                AWombat.PARAMETER_VERBOSE));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measures, MeasureType.class, 0,
                0, 0, AWombat.PARAMETER_ATOMIC_MEASURES));
        learningParameters.add(new LearningParameter(AWombat.PARAMETER_SAVE_MAPPING, saveMapping, Boolean.class, 0, 1,
                0, AWombat.PARAMETER_SAVE_MAPPING));

        return learningParameters;
    }

    /**
     * @param args
     * @throws UnsupportedMLImplementationException
     * @author sherif
     */
    public static void main(String[] args) throws UnsupportedMLImplementationException {
        // get training data
        if (args.length != 2) {
            logger.error("Not enough arguments");
            System.exit(1);
        }
        SemanticsWombat.init(args);
        ACache fullSourceCache = data.getSourceCache();
        ACache fullTargetCache = data.getTargetCache();
        fullReferenceMapping = removeLinksWithNoInstances(data.getReferenceMapping(), fullSourceCache, fullTargetCache);
        List<AMapping> subSets = generateEvaluationSets(fullReferenceMapping);

        for (int i = 0; i < 10; i++) {
            fold = i + 1;
            logger.info("Fold: " + fold);
            AMapping testSet = subSets.get(i);
            testSet.getReversedMap();
            AMapping trainingSet = getLearningPool(subSets, i);// training set
            trainingSet.getReversedMap();
            fullReferenceMapping = trainingSet;
            // give this to wombat
            List<ACache> trainingCaches = reduceCaches(trainingSet, fullSourceCache, fullTargetCache);
            List<ACache> testingCaches = reduceCaches(testSet, fullSourceCache, fullTargetCache);

            Set<String> measures = null;
            List<LearningParameter> lp = null;
            switch (experiment) {

            case 1:
                String[] strings = new String[] { "jaccard", "trigrams", "cosine", "qgrams", "overlap", "levenshtein" };
                measures = new HashSet<>(Arrays.asList(strings));
                break;
            case 2:
                String[] semantic = new String[] { "shortest_path", "wupalmer", "li", "lch" };
                measures = new HashSet<>(Arrays.asList(semantic));

                break;
            case 3:
                String[] measureSet = new String[] { "jaccard", "trigrams", "cosine", "qgrams", "overlap",
                        "levenshtein", "shortest_path", "wupalmer", "li", "lch" };
                measures = new HashSet<>(Arrays.asList(measureSet));
                break;

            }
            lp = setLearningParameters(measures);

            // create wombat and let it learn
            SupervisedMLAlgorithm wombat = MLAlgorithmFactory
                    .createMLAlgorithm(WombatSimple.class, MLImplementationType.SUPERVISED_BATCH).asSupervised();
            logger.info("Init WOMBAT");
            wombat.init(lp, trainingCaches.get(0), trainingCaches.get(1));
            logger.info("WOMBAT is learning..");
            MLResults mlResults = wombat.learn(trainingSet);
            logger.info("WOMBAT is done!");

            logger.info("LS: \n" + mlResults.getLinkSpecification().toString());
            // execute ls
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            AMapping predictions = executeLinkSpecs(mlResults.getLinkSpecification(), testingCaches.get(0),
                    testingCaches.get(1));
            long runtime = stopWatch.getTime();

            // evaluate
            // compare results from limes to test set
            logger.info("Evaluating results");
            evaluate(predictions, testSet, runtime, mlResults.getLinkSpecification(), resultsFileTest, csvWriterTest,
                    testingCaches.get(0), testingCaches.get(1));
            // compare mapping from MLResults with the training set
            evaluate(mlResults.getMapping(), trainingSet, 0, mlResults.getLinkSpecification(), resultsFileTraining,
                    csvWriterTrain, trainingCaches.get(0), trainingCaches.get(1));
        }

    }

    protected static List<ACache> reduceCaches(AMapping refMap, ACache fullSourceCache, ACache fullTargetCache) {
        ACache sourceTestCache = new HybridCache();
        ACache targetTestCache = new HybridCache();
        for (String s : refMap.getMap().keySet()) {
            if (fullSourceCache.containsUri(s)) {
                sourceTestCache.addInstance(fullSourceCache.getInstance(s));
                for (String t : refMap.getMap().get(s).keySet()) {
                    if (fullTargetCache.containsUri(t)) {
                        targetTestCache.addInstance(fullTargetCache.getInstance(t));
                    } else {
                        logger.warn("Instance " + t + " not exist in the target dataset");
                    }
                }
            } else {
                logger.warn("Instance " + s + " not exist in the source dataset");
            }
        }
        return Lists.newArrayList(sourceTestCache, targetTestCache);
    }

    protected static AMapping removeLinksWithNoInstances(AMapping map, ACache fullSourceCache, ACache fullTargetCache) {
        AMapping result = MappingFactory.createDefaultMapping();
        for (String s : map.getMap().keySet()) {
            for (String t : map.getMap().get(s).keySet()) {
                if (fullSourceCache.containsUri(s) && fullTargetCache.containsUri(t)) {
                    result.add(s, t, map.getMap().get(s).get(t));
                }
            }
        }
        return result;
    }

    static AMapping executeLinkSpecs(LinkSpecification linkSpecification, ACache sourceCache, ACache targetCache) {

        // AMapping kbMap;
        Rewriter rw = RewriterFactory.getDefaultRewriter();
        LinkSpecification rwLs = rw.rewrite(linkSpecification);
        IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sourceCache, targetCache);
        assert planner != null;
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache, targetCache,
                "?x", "?y");
        assert engine != null;
        AMapping resultMap = engine.execute(rwLs, planner);
        // kbMap = resultMap.getSubMap(linkSpecification.getThreshold());
        return resultMap;

    }

    public static void evaluate(AMapping predictions, AMapping referenceSet, long runtime,
            LinkSpecification linkSpecification, String file, CSVWriter writer, ACache source, ACache target) {
        QualitativeMeasuresEvaluator evaluator = new QualitativeMeasuresEvaluator();
        Set<EvaluatorType> evaluationMeasures = new LinkedHashSet<EvaluatorType>();
        evaluationMeasures.add(EvaluatorType.PRECISION);
        evaluationMeasures.add(EvaluatorType.RECALL);
        evaluationMeasures.add(EvaluatorType.F_MEASURE);
        evaluationMeasures.add(EvaluatorType.ACCURACY);
        Map<EvaluatorType, Double> evaluations = evaluator.evaluate(predictions,
                new GoldStandard(referenceSet, source, target), evaluationMeasures);

        writeResults(evaluations, runtime, predictions.getNumberofMappings(), referenceSet.getNumberofMappings(),
                linkSpecification, file, writer);
    }

    public static void writeResults(Map<EvaluatorType, Double> evaluations, long runtime, int predictions,
            int reference, LinkSpecification linkSpecification, String file, CSVWriter writer) {

        String[] values = new String[8];
        values[0] = String.valueOf(reference);
        values[1] = String.valueOf(predictions);

        int index = 2;
        for (Map.Entry<EvaluatorType, Double> entry : evaluations.entrySet()) {
            Double value = entry.getValue();
            values[index] = String.format("%.3f", value);
            index++;
        }
        values[index] = String.valueOf(runtime);
        index++;
        values[index] = linkSpecification.getFullExpression() + ">=" + linkSpecification.getThreshold();

        try {
            writer = new CSVWriter(new FileWriter(file, true));
        } catch (IOException e) {
            logger.error("Can't create csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }
        writer.writeNext(values, false);

        try {
            writer.close();
        } catch (IOException e) {
            logger.error("Couldn't close csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static AMapping sampleReferenceMap(AMapping reference, double fraction) {
        if (fraction == 1) {
            return reference;
        }
        // int mapSize = reference.size();
        if (fraction > 1) {
            fraction = 1 / fraction;
        }
        int size = (int) (reference.getMap().keySet().size() * fraction);
        return sampleReferenceMap(reference, size);
    }

    public static AMapping sampleReferenceMap(AMapping reference, int size) {
        Set<Integer> index = new HashSet<>();
        // get random indexes
        for (int i = 0; i < size; i++) {
            int number;
            do {
                number = (int) (reference.getMap().keySet().size() * Math.random());
            } while (index.contains(number));
            index.add(number);
        }

        // get data
        AMapping sample = MappingFactory.createDefaultMapping();

        int count = 0;
        for (String key : reference.getMap().keySet()) {
            for (String value : reference.getMap().get(key).keySet()) {
                if (index.contains(count++)) {
                    sample.add(key, value, reference.getConfidence(key, value));
                }
            }
        }

        return sample;
    }

    private static List<AMapping> generateEvaluationSets(AMapping fullReferenceMapping) {
        AMapping localRef = fullReferenceMapping.getSubMap(0.0d);
        List<AMapping> result = new ArrayList<>(10);
        for (int i = 0; i < 9; i++) {
            AMapping subSet = sampleReferenceMap(localRef, 0.1d);
            result.add(subSet);
            localRef = MappingOperations.difference(localRef, subSet);
        }
        result.add(localRef);
        return result;
    }

    private static AMapping getLearningPool(List<AMapping> subSets, int evaluationIndex) {
        AMapping result = MappingFactory.createDefaultMapping();
        for (int i = 0; i < 10; i++) {
            if (i != evaluationIndex) {
                result = MappingOperations.union(result, subSets.get(i));
            }
        }
        return result;
    }

}