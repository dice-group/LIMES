package org.aksw.limes.core.controller;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.opencsv.CSVWriter;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
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
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Evaluate Refinement based LGG for benchmark datasets DBLP-ACM,
 * Abt-Buy,Amazon-GoogleProducts, DBLP-Scholar, Person1, Person2, Restaurants,
 * DBLP-LinkedMDB and Dailymed-DrugBank
 *
 * @author sherif
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 */

public class Semantics {
    /**
     *
     */
    private static final Logger logger = Logger.getLogger(Semantics.class);

    public static AMapping fullReferenceMapping;
    public static int fold = 1;
    public static String datasetName;
    public static String resultsFile = null;
    public static EvaluationData data = null;
    public static CSVWriter csvWriter = null;
    public static int experiment = 0;

    public static void init(String[] args) {

        datasetName = args[0];
        experiment = Integer.valueOf(args[1]);

        data = DataSetChooser.getData(datasetName);

        resultsFile = data.getDatasetFolder() + data.getEvaluationResultFileName();

        // create file if it doesn't exist
        File f = new File(resultsFile);
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
            csvWriter = new CSVWriter(new FileWriter(resultsFile, true));
        } catch (IOException e) {
            logger.error("Can't create csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }
        csvWriter.writeNext(new String[] { "GoldStandard", "Predictions", "Precision", "Recall", "F-measure",
                "Accuracy", "Runtime", "LS"}, false);
        try {
            csvWriter.close();
        } catch (IOException e) {
            logger.error("Couldn't close csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    /**
     * @param args
     * @throws UnsupportedMLImplementationException
     * @author sherif
     */
    public static void main(String[] args) throws UnsupportedMLImplementationException {
        int mostInformativeExaplesCount = 10;
        // get training data

        Semantics.init(args);
        ACache fullSourceCache = data.getSourceCache();
        ACache fullTargetCache = data.getTargetCache();
        fullReferenceMapping = removeLinksWithNoInstances(data.getReferenceMapping(), fullSourceCache, fullTargetCache);
        List<AMapping> subSets = generateEvaluationSets(fullReferenceMapping);

        for (int i = 0; i < 10; i++) {
            fold = i + 1;
            AMapping testSet = subSets.get(i);
            testSet.getReversedMap();
            AMapping learningPool = getLearningPool(subSets, i);// training set
            learningPool.getReversedMap();
            fullReferenceMapping = learningPool;
            List<ACache> learning = reduceCaches(learningPool, fullSourceCache, fullTargetCache);// give
                                                                                                 // this
                                                                                                 // to
                                                                                                 // wombat
            List<ACache> testing = reduceCaches(testSet, fullSourceCache, fullTargetCache);
            AMapping trainingSet = learningPool;
            // compare testSet with result from limes
            // compare mapping from MLResults with the training set

            switch (experiment) {
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;

            }
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

    static void executeLinkSpecs(LinkSpecification linkSpecification, ACache sourceCache, ACache targetCache,
            AMapping referenceSet) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
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
        long runtime = stopWatch.getTime();

        evaluate(resultMap, referenceSet, runtime, linkSpecification);

    }

    public static void evaluate(AMapping predictions, AMapping referenceSet, long runtime, LinkSpecification linkSpecification) {
        QualitativeMeasuresEvaluator evaluator = new QualitativeMeasuresEvaluator();
        Set<EvaluatorType> evaluationMeasures = new LinkedHashSet<EvaluatorType>();
        evaluationMeasures.add(EvaluatorType.PRECISION);
        evaluationMeasures.add(EvaluatorType.RECALL);
        evaluationMeasures.add(EvaluatorType.F_MEASURE);
        evaluationMeasures.add(EvaluatorType.ACCURACY);
        Map<EvaluatorType, Double> evaluations = evaluator.evaluate(predictions, new GoldStandard(referenceSet),
                evaluationMeasures);

        writeResults(evaluations, runtime, predictions.getNumberofMappings(), referenceSet.getNumberofMappings(),linkSpecification);
    }

    public static void writeResults(Map<EvaluatorType, Double> evaluations, long runtime, int predictions,
            int reference, LinkSpecification linkSpecification) {

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
        values[index] = linkSpecification.getFullExpression()+">="+linkSpecification.getThreshold();

        try {
            csvWriter = new CSVWriter(new FileWriter(resultsFile, true));
        } catch (IOException e) {
            logger.error("Can't create csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }
        csvWriter.writeNext(values, false);

        try {
            csvWriter.close();
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