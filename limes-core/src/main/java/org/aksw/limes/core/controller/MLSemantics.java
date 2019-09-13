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
import org.aksw.limes.core.io.cache.Instance;
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
import org.aksw.limes.core.ml.algorithm.dragon.Dragon;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.FitnessFunctionDTL;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.GiniIndex;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.GlobalFMeasurePruning;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.PruningFunctionDTL;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Evaluate Refinement based LGG for benchmark datasets DBLP-ACM,
 * Abt-Buy,Amazon-GoogleProducts, DBLP-Scholar, Person1, Person2, Restaurants,
 * DBLP-LinkedMDB and Dailymed-DrugBank
 *
 * @author sherif
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 */

public class MLSemantics {
    /**
     *
     */
    private static final Logger logger = Logger.getLogger(MLSemantics.class);

    public AMapping fullReferenceMapping;
    public String mlAlgorithm = null;
    public int iteration = 1;
    public String datasetName;
    public String resultsFileTest = null;
    public String resultsFileTraining = null;
    public File mappingFolder = null;
    public EvaluationData data = null;
    public CSVWriter csvWriterTest = null;
    public CSVWriter csvWriterTrain = null;
    public int experiment = 0;
    public List<AMapping> debugMappings = null;
    public AMapping positives = null;
    public AMapping negatives = null;
    public AMapping together = null;
    // public int f = 0;

    public void init(String[] args) {

        datasetName = args[0];
        experiment = Integer.valueOf(args[1]);
        mlAlgorithm = args[2];
        // f = Integer.valueOf(args[3]);

        data = DataSetChooser.getData(datasetName);

        resultsFileTest = data.getDatasetFolder() + "Test" + experiment + mlAlgorithm + "7extra"
                + data.getEvaluationResultFileName();
        resultsFileTraining = data.getDatasetFolder() + "Training" + experiment + mlAlgorithm + "7extra"
                + data.getEvaluationResultFileName();

        createResultsFile(resultsFileTest, csvWriterTest);
        createResultsFile(resultsFileTraining, csvWriterTrain);

        mappingFolder = new File(data.getDatasetFolder() + "Mappings7extra/");
        if (!mappingFolder.exists()) {
            logger.info("creating directory: " + mappingFolder.getName());
            boolean result = false;
            try {
                mappingFolder.mkdir();
                result = true;
            } catch (SecurityException se) {
                // handle it
            }
            if (result) {
                logger.info("DIR created");
            }
        }

    }

    public void createMappingFile(String fileName, AMapping mapping, double confidence) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(fileName, true);
            bw = new BufferedWriter(fw);
            for (String key : mapping.getMap().keySet()) {
                for (String value : mapping.getMap().get(key).keySet()) {
                    bw.write(key + "\t" + value + "\t" + String.valueOf(confidence) + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void createResultsFile(String fileName, CSVWriter writer) {
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

    public List<LearningParameter> setLearningParameters(Set<String> measures) {

        List<LearningParameter> learningParameters = new ArrayList<LearningParameter>();

        if (mlAlgorithm.equalsIgnoreCase("wombat simple")) {

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
            learningParameters
                    .add(new LearningParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, maxExecutionTimeInMin,
                            Integer.class, 1d, Integer.MAX_VALUE, 1, AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES));
            learningParameters.add(new LearningParameter(AWombat.PARAMETER_MAX_FITNESS_THRESHOLD, maxFitnessThreshold,
                    Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_MAX_FITNESS_THRESHOLD));
            learningParameters.add(new LearningParameter(AWombat.PARAMETER_MIN_PROPERTY_COVERAGE, minPropertyCoverage,
                    Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_MIN_PROPERTY_COVERAGE));
            learningParameters.add(new LearningParameter(AWombat.PARAMETER_PROPERTY_LEARNING_RATE, propertyLearningRate,
                    Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_PROPERTY_LEARNING_RATE));
            learningParameters.add(new LearningParameter(AWombat.PARAMETER_OVERALL_PENALTY_WEIGHT, overallPenaltyWeight,
                    Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_OVERALL_PENALTY_WEIGHT));
            learningParameters.add(new LearningParameter(AWombat.PARAMETER_CHILDREN_PENALTY_WEIGHT,
                    childrenPenaltyWeight, Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_CHILDREN_PENALTY_WEIGHT));
            learningParameters.add(new LearningParameter(AWombat.PARAMETER_COMPLEXITY_PENALTY_WEIGHT,
                    complexityPenaltyWeight, Double.class, 0d, 1d, 0.01d, AWombat.PARAMETER_COMPLEXITY_PENALTY_WEIGHT));
            learningParameters.add(new LearningParameter(AWombat.PARAMETER_VERBOSE, verbose, Boolean.class, 0, 1, 0,
                    AWombat.PARAMETER_VERBOSE));
            learningParameters.add(new LearningParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measures, MeasureType.class,
                    0, 0, 0, AWombat.PARAMETER_ATOMIC_MEASURES));
            learningParameters.add(new LearningParameter(AWombat.PARAMETER_SAVE_MAPPING, saveMapping, Boolean.class, 0,
                    1, 0, AWombat.PARAMETER_SAVE_MAPPING));
        }

        else if (mlAlgorithm.equalsIgnoreCase("dragon")) {
            learningParameters.add(new LearningParameter(Dragon.PARAMETER_PRUNING_CONFIDENCE, 0.25d, Double.class, 0d,
                    1d, 0.01d, Dragon.PARAMETER_PRUNING_CONFIDENCE));
            learningParameters.add(new LearningParameter(Dragon.PARAMETER_PROPERTY_MAPPING, data.getPropertyMapping(),
                    PropertyMapping.class, Double.NaN, Double.NaN, Double.NaN, Dragon.PARAMETER_PROPERTY_MAPPING));
            learningParameters
                    .add(new LearningParameter(Dragon.PARAMETER_MAPPING, MappingFactory.createDefaultMapping(),
                            AMapping.class, Double.NaN, Double.NaN, Double.NaN, Dragon.PARAMETER_MAPPING));
            learningParameters.add(new LearningParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3, Integer.class, 1,
                    100000, 1, Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT));
            learningParameters.add(new LearningParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.4, Double.class, 0d,
                    1d, 0.01d, Dragon.PARAMETER_MIN_PROPERTY_COVERAGE));
            learningParameters.add(new LearningParameter(Dragon.PARAMETER_PROPERTY_LEARNING_RATE, 0.95, Double.class,
                    0d, 1d, 0.01d, Dragon.PARAMETER_PROPERTY_LEARNING_RATE));
            learningParameters.add(
                    new LearningParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex(), FitnessFunctionDTL.class,
                            new String[] { Dragon.FITNESS_NAME_GINI_INDEX, Dragon.FITNESS_NAME_GLOBAL_FMEASURE },
                            Dragon.PARAMETER_FITNESS_FUNCTION));
            learningParameters.add(new LearningParameter(Dragon.PARAMETER_PRUNING_FUNCTION, new GlobalFMeasurePruning(),
                    PruningFunctionDTL.class,
                    new String[] { Dragon.PRUNING_NAME_ERROR_ESTIMATE_PRUNING, Dragon.PRUNING_NAME_GLOBAL_FMEASURE },
                    Dragon.PARAMETER_FITNESS_FUNCTION));

            learningParameters.add(new LearningParameter(Dragon.PARAMETER_ATOMIC_MEASURES, measures, MeasureType.class,
                    0, 0, 0, Dragon.PARAMETER_ATOMIC_MEASURES));
        }
        return learningParameters;
    }

    public void saveMappings() {
        int posSize = fullReferenceMapping.size();
        AMapping negativeExamples = MappingFactory.createDefaultMapping();
        ArrayList<Instance> allTargets = data.getTargetCache().getAllInstances();

        // for each positive pair, find a negative one
        int negSize = 0;
        for (Entry<String, HashMap<String, Double>> entry : fullReferenceMapping.getMap().entrySet()) {
            String sourceURI = entry.getKey();
            HashMap<String, Double> matchingTargets = entry.getValue();

            Set<Integer> index = new HashSet<>();
            // get random indexes
            for (int i = 0; i < matchingTargets.size(); i++) {
                int number;
                do {
                    double rand = Math.random();
                    number = (int) (allTargets.size() * rand);
                } while (index.contains(number) || matchingTargets.containsKey(allTargets.get(number).getUri()));
                index.add(number);
            }
            for (int number : index) {
                negativeExamples.add(sourceURI, allTargets.get(number).getUri(), 0.0d);
                negSize++;
            }

        }

        logger.info("Full size: "
                + (data.getSourceCache().getAllInstances().size() * data.getTargetCache().getAllInstances().size())
                + " positives: " + posSize + " negatives: " + negSize + " together: " + (posSize + negSize));

        
        createMappingFile(data.getDatasetFolder() + "Mappings7extra/positives.tsv", fullReferenceMapping, 1.0d);
        createMappingFile(data.getDatasetFolder() + "Mappings7extra/negatives.tsv", negativeExamples, 0.0d);

        // positive subsets
        List<AMapping> positiveSubsets = generateEvaluationSets(fullReferenceMapping, posSize);
        int totalP = 0;
        for (int i = 0; i < positiveSubsets.size(); i++) {
            AMapping mapping = positiveSubsets.get(i);
            logger.info(i + " size: " + mapping.size());
            totalP += mapping.size();
            createMappingFile(data.getDatasetFolder() + "Mappings7extra/mapping" + i + ".tsv", mapping, 1.0d);
        }
        logger.info("Total positive: " + totalP);
        // negative subsets
        List<AMapping> negativeSubsets = generateEvaluationSets(negativeExamples, negSize);
        int totalN = 0;
        for (int i = 0; i < negativeSubsets.size(); i++) {
            AMapping mapping = negativeSubsets.get(i);
            logger.info(i + " size: " + mapping.size());
            totalN += mapping.size();
            createMappingFile(data.getDatasetFolder() + "Mappings7extra/mapping" + i + ".tsv", mapping, 0.0d);
        }
        logger.info("Total negative: " + totalN);

    }

    public List<AMapping> loadMappings() {
        List<AMapping> subSets = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            AMapping mapping = MappingFactory.createDefaultMapping();
            String file = data.getDatasetFolder() + "Mappings7extra/mapping" + i + ".tsv";
            String line = "";
            String cvsSplitBy = "\t";

            BufferedReader br = null;
            FileReader fr = null;
            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] m = line.split(cvsSplitBy);
                    if ((mlAlgorithm.equalsIgnoreCase("wombat simple")) && Double.valueOf(m[2]) == 0.0d)
                        continue;
                    mapping.add(m[0], m[1], Double.valueOf(m[2]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)
                        br.close();
                    if (fr != null)
                        fr.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            subSets.add(mapping);
        }

        String filePositives = data.getDatasetFolder() + "Mappings7extra/positives.tsv";
        positives = MappingFactory.createDefaultMapping();
        String line = "";
        String cvsSplitBy = "\t";
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(filePositives);
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] m = line.split(cvsSplitBy);
                positives.add(m[0], m[1], Double.valueOf(m[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        negatives = MappingFactory.createDefaultMapping();
        if (!mlAlgorithm.equals("wombat simple")) {
            String fileNegatives = data.getDatasetFolder() + "Mappings7extra/negatives.tsv";
            
            line = "";
            cvsSplitBy = "\t";
            br = null;
            fr = null;
            try {
                fr = new FileReader(fileNegatives);
                br = new BufferedReader(fr);
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] m = line.split(cvsSplitBy);
                    negatives.add(m[0], m[1], Double.valueOf(m[2]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)
                        br.close();
                    if (fr != null)
                        fr.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        together = MappingOperations.union(positives, negatives);

        return subSets;
    }

    public Set<String> getMeasures() {
        Set<String> measures = null;
        switch (experiment) {
        case 1:
            String[] strings = new String[] { "cosine", "qgrams", "levenshtein" };
            measures = new HashSet<>(Arrays.asList(strings));
            break;
        case 2:
            String[] semantic = new String[] { "shortest_path", "wupalmer", "li", "lch" };
            measures = new HashSet<>(Arrays.asList(semantic));

            break;
        case 3:
            String[] measureSet = new String[] { "cosine", "qgrams", "levenshtein", "shortest_path", "wupalmer", "li",
                    "lch" };
            measures = new HashSet<>(Arrays.asList(measureSet));
            break;
        }
        return measures;
    }

    public static void main(String[] args) throws UnsupportedMLImplementationException {
        MLSemantics controller = new MLSemantics();
        controller.run(args);
    }

    /**
     * @param args
     * @throws UnsupportedMLImplementationException
     * @author sherif
     */
    public void run(String[] args) throws UnsupportedMLImplementationException {

        init(args);
        ACache fullSourceCache = data.getSourceCache();
        ACache fullTargetCache = data.getTargetCache();
        fullReferenceMapping = removeLinksWithNoInstances(data.getReferenceMapping(), fullSourceCache, fullTargetCache);

        if (args.length == 4 && args[3].equals("init")) {
            // create and save mappings
            saveMappings();

        } else if (args.length == 4 && args[3].equals("debug")) {
            debugMappings = loadMappings();
        } else {
            List<AMapping> subSets = loadMappings();
            // load all mappings
            // but change the iteration
            for (int i = 0; i < 7; i++) {
                iteration = i + 1;
                logger.info("Iteration: " + iteration);
                // training with only 2%
                AMapping trainingSet = subSets.get(i);
                trainingSet.getReversedMap();

                // testing on the remaining 98%
                AMapping testSet = getLearningPool(trainingSet);
                testSet.getReversedMap();

                fullReferenceMapping = trainingSet;
                // give this to wombat
                List<ACache> trainingCaches = reduceCaches(trainingSet, fullSourceCache, fullTargetCache);
                List<ACache> testingCaches = reduceCaches(testSet, fullSourceCache, fullTargetCache);

                Set<String> measures = getMeasures();

                // create algorithm and let it learn
                SupervisedMLAlgorithm ml = MLAlgorithmFactory
                        .createMLAlgorithm(MLAlgorithmFactory.getAlgorithmType(mlAlgorithm),
                                MLImplementationType.SUPERVISED_BATCH)
                        .asSupervised();

                List<LearningParameter> lp = setLearningParameters(measures);

                logger.info("Init ML");
                ml.init(lp, trainingCaches.get(0), trainingCaches.get(1));
                logger.info("ML is learning..");
                MLResults mlResults = ml.learn(trainingSet);
                logger.info("ML is done!");

                logger.info("LS: \n" + mlResults.getLinkSpecification().toString());
                // execute ls
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                AMapping predictions = executeLinkSpecs(mlResults.getLinkSpecification(), testingCaches.get(0),
                        testingCaches.get(1));
                long runtime = stopWatch.getTime();
                //logger.info("Mapping size: " + mlResults.getMapping());
                // evaluate
                // compare results from limes to test set
                logger.info("Evaluating results");
                evaluate(predictions, testSet, runtime, mlResults.getLinkSpecification(), resultsFileTest,
                        csvWriterTest, testingCaches.get(0), testingCaches.get(1));
                // compare mapping from MLResults with the training set
                if (!this.mlAlgorithm.equals("dragon")) {
                    evaluate(mlResults.getMapping(), trainingSet, 0, mlResults.getLinkSpecification(),
                            resultsFileTraining, csvWriterTrain, trainingCaches.get(0), trainingCaches.get(1));
                }
            }
        }
    }

    protected List<ACache> reduceCaches(AMapping refMap, ACache fullSourceCache, ACache fullTargetCache) {
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
            } 
        }
        return Lists.newArrayList(sourceTestCache, targetTestCache);
    }

    public AMapping removeLinksWithNoInstances(AMapping map, ACache fullSourceCache, ACache fullTargetCache) {
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

    public AMapping executeLinkSpecs(LinkSpecification linkSpecification, ACache sourceCache, ACache targetCache) {

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

    public void evaluate(AMapping predictions, AMapping referenceSet, long runtime, LinkSpecification linkSpecification,
            String file, CSVWriter writer, ACache source, ACache target) {
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

    public void writeResults(Map<EvaluatorType, Double> evaluations, long runtime, int predictions, int reference,
            LinkSpecification linkSpecification, String file, CSVWriter writer) {

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

    public AMapping sampleReferenceMap(AMapping reference, int size) {
        Set<Integer> index = new HashSet<>();
        // get random indexes
        for (int i = 0; i < size; i++) {
            int number;
            do {
                double rand = Math.random();
                number = (int) (reference.size() * rand);
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

    private List<AMapping> generateEvaluationSets(AMapping referenceMap, int totalSize) {

        int fraction = (int) (totalSize * 0.02d);
        logger.info("Number of sub-results: " + fraction);
        AMapping localRef = referenceMap.getSubMap(0.0d);
        List<AMapping> result = new ArrayList<>(7);

        for (int i = 0; i < 7; i++) {
            AMapping subSet = sampleReferenceMap(localRef, fraction);
            result.add(subSet);
            localRef = MappingOperations.difference(localRef, subSet);
        }
        
        return result;
    }

    public AMapping getLearningPool(AMapping trainingSet) {
        AMapping result = MappingFactory.createDefaultMapping();

        for (String key : together.getMap().keySet()) {
            for (String value : together.getMap().get(key).keySet()) {
                if (!trainingSet.contains(key, value)) {
                    result.add(key, value, together.getMap().get(key).get(value));
                }
            }
        }
        return result;
    }

}