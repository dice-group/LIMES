package org.aksw.limes.core.ml.algorithm.dragon.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.*;
import org.aksw.limes.core.ml.algorithm.dragon.Dragon;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.GiniIndex;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.GlobalFMeasure;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.ErrorEstimatePruning;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.GlobalFMeasurePruning;
import org.aksw.limes.core.ml.algorithm.euclid.BooleanEuclid;
import org.aksw.limes.core.ml.algorithm.euclid.LinearEuclid;
import org.aksw.limes.core.ml.algorithm.euclid.MeshEuclid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DTLEvaluation {

    private static List<FoldData> folds = new ArrayList<>();
    private static int FOLDS_COUNT = 10;
    protected static Logger logger = LoggerFactory.getLogger(DTLEvaluation.class);
    private static Random randnum = new Random();

    public static List<FoldData> generateFolds(EvaluationData data) {
        folds = new ArrayList<>();

        // Fill caches
        ACache source = data.getSourceCache();
        ACache target = data.getTargetCache();
        AMapping refMap = data.getReferenceMapping();

        // remove error mappings (if any)
        refMap = removeLinksWithNoInstances(refMap, source, target);

        // generate AMapping folds
        List<AMapping> foldMaps = generateMappingFolds(refMap, source, target);

        // fill fold caches
        for (AMapping foldMap : foldMaps) {
            ACache sourceFoldCache = new HybridCache();
            ACache targetFoldCache = new HybridCache();
            for (String s : foldMap.getMap().keySet()) {
                if (source.containsUri(s)) {
                    sourceFoldCache.addInstance(source.getInstance(s));
                    for (String t : foldMap.getMap().get(s).keySet()) {
                        if (target.containsUri(t)) {
                            targetFoldCache.addInstance(target.getInstance(t));
                        } else {
                            // logger.warn("Instance " + t +
                            // " not exist in the target dataset");
                        }
                    }
                } else {
                    // logger.warn("Instance " + s +
                    // " not exist in the source dataset");
                }
            }
            folds.add(new FoldData(foldMap, sourceFoldCache, targetFoldCache));
        }
        return folds;
    }

    public static List<FoldData> generateFoldsDeterministically(EvaluationData data) {
        randnum.setSeed(423112);
        return generateFolds(data);
    }

    /**
     * Labels prediction mapping according referencemapping
     *
     * @param predictionMapping
     * @param referenceMapping
     * @return result labeled mapping
     */
    public static AMapping oracleFeedback(AMapping predictionMapping, AMapping referenceMapping) {
        AMapping result = MappingFactory.createDefaultMapping();

        for (String s : predictionMapping.getMap().keySet()) {
            for (String t : predictionMapping.getMap().get(s).keySet()) {
                if (referenceMapping.contains(s, t)) {
                    // result.add(s, t,
                    // predictionMapping.getMap().get(s).get(t));
                    result.add(s, t, 1.0);
                } else {
                    result.add(s, t, 0.0);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        try {
//            newEval(args[0]);
            cleanEval();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void createDirectoriesIfNecessarry(String... folders) {
        for (String folder : folders) {
            File f = new File(folder);
            if (!f.exists()) {
                boolean success = f.mkdir();
                if (success) {
                    logger.info("Successfully created directory: " + f.getPath());
                } else {
                    logger.error("Error while trying to create: " + f.getPath());
                }
            } else {
                logger.info(f.getPath() + " already exists");
            }
        }
    }

    public static void cleanEval() throws FileNotFoundException {
        // ================================================================================================================
        // Set up output
        // ================================================================================================================
        String baseFolder = "/tmp/";
        long start;
        long end;
        String fMeasureBase = baseFolder + "FMeasure/";
        String precisionBase = baseFolder + "Precision/";
        String recallBase = baseFolder + "Recall/";
        String timeBase = baseFolder + "Time/";
        String sizeBase = baseFolder + "Size/";
        String unprunedfMeasureBase = baseFolder + "UnprunedFMeasure/";
        String unprunedprecisionBase = baseFolder + "UnprunedPrecision/";
        String unprunedrecallBase = baseFolder + "UnprunedRecall/";
        String unprunedsizeBase = baseFolder + "UnprunedSize/";
        String trainfMeasureBase = baseFolder + "TrainFMeasure/";
        String trainprecisionBase = baseFolder + "TrainPrecision/";
        String trainrecallBase = baseFolder + "TrainRecall/";
        createDirectoriesIfNecessarry(baseFolder, fMeasureBase, precisionBase, recallBase, timeBase, sizeBase, unprunedfMeasureBase, unprunedprecisionBase, unprunedrecallBase, unprunedsizeBase, trainfMeasureBase, trainprecisionBase, trainrecallBase);

        String[] algos = {"GiniGlobal","GiniError","GlobalGlobal","GlobalError","Wombat","Eagle","LinearEuclid","BooleanEuclid","MeshEuclid","j48","j48opt"};

        String[] datasets = {"drugs"};
        StringBuilder headerBuilder = new StringBuilder("Data");
        for(String a: algos){
            headerBuilder.append("\t").append(a);
        }
        headerBuilder.append("\n");
        String header = headerBuilder.toString();

        for (int k = 0; k < 1; k++) {

            PrintWriter writerFMeasure = new PrintWriter(new FileOutputStream(fMeasureBase + k + ".csv", false));
            writerFMeasure.write(header);
            String datalineFMeasure = "";
            PrintWriter writerPrecision = new PrintWriter(new FileOutputStream(precisionBase + k + ".csv", false));
            writerPrecision.write(header);
            String datalinePrecision = "";
            PrintWriter writerRecall = new PrintWriter(new FileOutputStream(recallBase + k + ".csv", false));
            writerRecall.write(header);
            String datalineRecall = "";
            PrintWriter writerTime = new PrintWriter(new FileOutputStream(timeBase + k + ".csv", false));
            writerTime.write(header);
            String datalineTime = "";
            PrintWriter writerSize = new PrintWriter(new FileOutputStream(sizeBase + k + ".csv", false));
            writerSize.write(header);
            String datalineSize = "";

            PrintWriter unprunedwriterFMeasure = new PrintWriter(new FileOutputStream(unprunedfMeasureBase + k + ".csv", false));
            unprunedwriterFMeasure.write(header);
            String unpruneddatalineFMeasure = "";
            PrintWriter unprunedwriterPrecision = new PrintWriter(new FileOutputStream(unprunedprecisionBase + k + ".csv", false));
            unprunedwriterPrecision.write(header);
            String unpruneddatalinePrecision = "";
            PrintWriter unprunedwriterRecall = new PrintWriter(new FileOutputStream(unprunedrecallBase + k + ".csv", false));
            unprunedwriterRecall.write(header);
            String unpruneddatalineRecall = "";
            PrintWriter unprunedwriterSize = new PrintWriter(new FileOutputStream(unprunedsizeBase + k + ".csv", false));
            unprunedwriterSize.write(header);
            String unpruneddatalineSize = "";

            PrintWriter trainwriterFMeasure = new PrintWriter(new FileOutputStream(trainfMeasureBase + k + ".csv", false));
            trainwriterFMeasure.write(header);
            String traindatalineFMeasure = "";
            PrintWriter trainwriterPrecision = new PrintWriter(new FileOutputStream(trainprecisionBase + k + ".csv", false));
            trainwriterPrecision.write(header);
            String traindatalinePrecision = "";
            PrintWriter trainwriterRecall = new PrintWriter(new FileOutputStream(trainrecallBase + k + ".csv", false));
            trainwriterRecall.write(header);
            String traindatalineRecall = "";

            // ================================================================================================================
            // Set up training data folds and caches
            // ================================================================================================================

            for (String dataName : datasets) {
                logger.info("\n\n >>>>>>>>>>>>> " + dataName.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
                Dragon.useJ48optimized = false;
                Dragon.useJ48 = false;
                EvaluationData c = DataSetChooser.getData(dataName);
                folds = generateFolds(c);

                FoldData trainData = new FoldData();
                FoldData testData = folds.get(FOLDS_COUNT - 1);
                // perform union on test folds
                for (int i = 0; i < FOLDS_COUNT; i++) {
                    if (i != 9) {
                        trainData.map = MappingOperations.union(trainData.map, folds.get(i).map);
                        trainData.sourceCache = cacheUnion(trainData.sourceCache, folds.get(i).sourceCache);
                        trainData.targetCache = cacheUnion(trainData.targetCache, folds.get(i).targetCache);
                    }
                }
                // fix caches if necessary
                for (String s : trainData.map.getMap().keySet()) {
                    for (String t : trainData.map.getMap().get(s).keySet()) {
                        if (!trainData.targetCache.containsUri(t)) {
                            // logger.info("target: " + t);
                            trainData.targetCache.addInstance(c.getTargetCache().getInstance(t));
                        }
                    }
                    if (!trainData.sourceCache.containsUri(s)) {
                        // logger.info("source: " + s);
                        trainData.sourceCache.addInstance(c.getSourceCache().getInstance(s));
                    }
                }

                AMapping trainingData = trainData.map;
                ACache trainSourceCache = trainData.sourceCache;
                ACache trainTargetCache = trainData.targetCache;
                ACache testSourceCache = testData.sourceCache;
                ACache testTargetCache = testData.targetCache;

                // ================================================================================================================
                // Learning Phase
                // ================================================================================================================
                try {
                    Map<String, CleanerEval.EvalResult> results = new HashMap<>();
                    results.put(algos[0], CleanerEval.evaluate(Dragon.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 3, new GiniIndex(), new GlobalFMeasurePruning(), 0.4));
                    results.put(algos[1], CleanerEval.evaluate(Dragon.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 3, new GiniIndex(), new ErrorEstimatePruning(), 0.4));
                    results.put(algos[2], CleanerEval.evaluate(Dragon.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 3, new GlobalFMeasure(), new GlobalFMeasurePruning(), 0.4));
                    results.put(algos[3], CleanerEval.evaluate(Dragon.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 3, new GlobalFMeasure(), new ErrorEstimatePruning(), 0.4));
                    results.put(algos[4], CleanerEval.evaluate(WombatSimple.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 0, null, null, 0));
                    results.put(algos[5], CleanerEval.evaluate(Eagle.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 0, null, null, 0));
                    results.put(algos[6], CleanerEval.evaluate(LinearEuclid.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 0, null, null, 0));
                    results.put(algos[7], CleanerEval.evaluate(BooleanEuclid.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 0, null, null, 0));
                    results.put(algos[8], CleanerEval.evaluate(MeshEuclid.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 0, null, null, 0));
                    Dragon.useJ48 = true;
                    results.put(algos[9], CleanerEval.evaluate(Dragon.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 3, new GiniIndex(), new GlobalFMeasurePruning(), 0.4));
                    Dragon.useJ48optimized = true;
                    results.put(algos[10], CleanerEval.evaluate(Dragon.class, MLImplementationType.SUPERVISED_BATCH, testSourceCache, testTargetCache, trainSourceCache, trainTargetCache, c, trainingData, testData.map, logger, 3, new GiniIndex(), new GlobalFMeasurePruning(), 0.4));

                    // ================================================================================================================
                    // Print results for iteration
                    // ================================================================================================================

                    writeResults(datalineFMeasure, algos, dataName, writerFMeasure, results, CleanerEval.EvalResult.FMEASURE_KEY);
                    writeResults(datalinePrecision, algos, dataName, writerPrecision, results, CleanerEval.EvalResult.PRECISION_KEY);
                    writeResults(datalineRecall, algos, dataName, writerRecall, results, CleanerEval.EvalResult.RECALL_KEY);
                    writeResults(datalineTime, algos, dataName, writerTime, results, CleanerEval.EvalResult.TIME_KEY);
                    writeResults(datalineSize, algos, dataName, writerSize, results, CleanerEval.EvalResult.SIZE_KEY);

                    writeResults(unpruneddatalineFMeasure, algos, dataName, unprunedwriterFMeasure, results, CleanerEval.EvalResult.UNPRUNED_FMEASURE_KEY);
                    writeResults(unpruneddatalinePrecision, algos, dataName, unprunedwriterPrecision, results, CleanerEval.EvalResult.UNPRUNED_PRECISION_KEY);
                    writeResults(unpruneddatalineRecall, algos, dataName, unprunedwriterRecall, results, CleanerEval.EvalResult.UNPRUNED_RECALL_KEY);
                    writeResults(unpruneddatalineSize, algos, dataName, unprunedwriterSize, results, CleanerEval.EvalResult.UNPRUNED_SIZE_KEY);

                    writeResults(traindatalineFMeasure, algos, dataName, trainwriterFMeasure, results, CleanerEval.EvalResult.TRAIN_FMEASURE_KEY);
                    writeResults(traindatalinePrecision, algos, dataName, trainwriterPrecision, results, CleanerEval.EvalResult.TRAIN_PRECISION_KEY);
                    writeResults(traindatalineRecall, algos, dataName, trainwriterRecall, results, CleanerEval.EvalResult.TRAIN_RECALL_KEY);

                } catch (UnsupportedMLImplementationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            writerFMeasure.close();
            writerPrecision.close();
            writerRecall.close();
            writerTime.close();
            writerSize.close();

            unprunedwriterFMeasure.close();
            unprunedwriterPrecision.close();
            unprunedwriterRecall.close();
            unprunedwriterSize.close();

            trainwriterFMeasure.close();
            trainwriterPrecision.close();
            trainwriterRecall.close();
        }
    }

    private static String getDataSetName(String dataName) {
        return dataName.replace("dbplinkedmdb", "Movies").replace("person1", "Person1").replace("person2", "Person2").replace("drugs", "Drugs").replace("restaurants", "Restaurants").replace("dblpacm", "DBLP-ACM").replace("abtbuy", "Abtbuy").replace("dblpscholar", "DBLP-GS").replace("amazongoogle", "Amazon-GP");
    }

    private static void writeResults(String dataline, String[] algos, String dataName, PrintWriter writer, Map<String, CleanerEval.EvalResult> results, String metricKey) {
        dataline += getDataSetName(dataName);
        for (String a : algos) {
            dataline += "\t" + results.get(a).get(metricKey);
        }
        dataline += "\n";
        writer.write(dataline);
        dataline = "";
    }

    public static void performCrossValidationj48() throws FileNotFoundException {
        // ================================================================================================================
        // Set up output
        // ================================================================================================================
        String baseFolder = "/tmp/";
        long start;
        long end;
        String fMeasureBase = baseFolder + "FMeasure/";
        String precisionBase = baseFolder + "Precision/";
        String recallBase = baseFolder + "Recall/";
        String timeBase = baseFolder + "Time/";
        String sizeBase = baseFolder + "Size/";
        createDirectoriesIfNecessarry(baseFolder, fMeasureBase, precisionBase, recallBase, timeBase, sizeBase);

        String[] datasets = {
                "abtbuy", "dblpscholar", "amazongoogleproducts"};
        String header = "Data\tj48\tj48opt\n";

        for (int k = 0; k < 1; k++) {

            PrintWriter writerFMeasure = new PrintWriter(new FileOutputStream(fMeasureBase + k + ".csv", false));
            writerFMeasure.write(header);
            String datalineFMeasure = "";
            PrintWriter writerPrecision = new PrintWriter(new FileOutputStream(precisionBase + k + ".csv", false));
            writerPrecision.write(header);
            String datalinePrecision = "";
            PrintWriter writerRecall = new PrintWriter(new FileOutputStream(recallBase + k + ".csv", false));
            writerRecall.write(header);
            String datalineRecall = "";
            PrintWriter writerTime = new PrintWriter(new FileOutputStream(timeBase + k + ".csv", false));
            writerTime.write(header);
            String datalineTime = "";
            PrintWriter writerSize = new PrintWriter(new FileOutputStream(sizeBase + k + ".csv", false));
            writerSize.write(header);
            String datalineSize = "";

            // ================================================================================================================
            // Set up training data folds and caches
            // ================================================================================================================

            for (String dataName : datasets) {
                logger.info("\n\n >>>>>>>>>>>>> " + dataName.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
                Dragon.useJ48optimized = false;
                Dragon.useJ48 = false;
                EvaluationData c = DataSetChooser.getData(dataName);
                folds = generateFolds(c);

                FoldData trainData = new FoldData();
                FoldData testData = folds.get(FOLDS_COUNT - 1);
                // perform union on test folds
                for (int i = 0; i < FOLDS_COUNT; i++) {
                    if (i != 9) {
                        trainData.map = MappingOperations.union(trainData.map, folds.get(i).map);
                        trainData.sourceCache = cacheUnion(trainData.sourceCache, folds.get(i).sourceCache);
                        trainData.targetCache = cacheUnion(trainData.targetCache, folds.get(i).targetCache);
                    }
                }
                // fix caches if necessary
                for (String s : trainData.map.getMap().keySet()) {
                    for (String t : trainData.map.getMap().get(s).keySet()) {
                        if (!trainData.targetCache.containsUri(t)) {
                            // logger.info("target: " + t);
                            trainData.targetCache.addInstance(c.getTargetCache().getInstance(t));
                        }
                    }
                    if (!trainData.sourceCache.containsUri(s)) {
                        // logger.info("source: " + s);
                        trainData.sourceCache.addInstance(c.getSourceCache().getInstance(s));
                    }
                }

                AMapping trainingData = trainData.map;
                ACache trainSourceCache = trainData.sourceCache;
                ACache trainTargetCache = trainData.targetCache;
                ACache testSourceCache = testData.sourceCache;
                ACache testTargetCache = testData.targetCache;

                // ================================================================================================================
                // Learning Phase
                // ================================================================================================================
                try {
                    AMLAlgorithm dtl = null;
                    Configuration config = null;
                    MLResults res = null;
                    AMapping mapping = null;


                    logger.info("========J48 FOLDs 5==========");


                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    dtl.setParameter(Dragon.PARAMETER_REDUCED_ERROR_PRUNING, true);
                    dtl.setParameter(Dragon.PARAMETER_FOLD_NUMBER, 5);

                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    Dragon.useJ48 = true;
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double j48FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double j48Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double j48Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int j48Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + j48FM);
                    logger.info("Precision: " + j48Precision);
                    logger.info("Recall: " + j48Recall);
                    long j48Time = (end - start);
                    logger.info("Time: " + j48Time);
                    logger.info("Size: " + j48Size);


                    logger.info("========J48 opt==========");


                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    dtl.setParameter(Dragon.PARAMETER_REDUCED_ERROR_PRUNING, true);
                    dtl.setParameter(Dragon.PARAMETER_FOLD_NUMBER, 5);

                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    Dragon.useJ48 = true;
                    Dragon.useJ48optimized = false;
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double j48optFM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double j48optPrecision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double j48optRecall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int j48optSize = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + j48optFM);
                    logger.info("Precision: " + j48optPrecision);
                    logger.info("Recall: " + j48optRecall);
                    long j48optTime = (end - start);
                    logger.info("Time: " + j48optTime);
                    logger.info("Size: " + j48optSize);

//					// ========================================
//					logger.info("========Global + Gini + UP + 0.4==========");
//					Dragon.useJ48 = false;
//
//					dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
//							MLImplementationType.SUPERVISED_BATCH);
//					logger.info("source size: " + testSourceCache.size());
//					logger.info("target size: " + testTargetCache.size());
//					dtl.init(null, trainSourceCache, trainTargetCache);
//					config = c.getConfigReader().read();
//					dtl.getMl().setConfiguration(config);
//					((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
//					start = System.currentTimeMillis();
//					dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
//					dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
//					dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
//							new GlobalFMeasurePruning());
//					dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.4);
//					res = dtl.asSupervised().learn(trainingData);
//					end = System.currentTimeMillis();
//					logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
//					mapping = dtl.predict(testSourceCache, testTargetCache, res);
//					MLResults resUnpruned = new MLResults((LinkSpecification) res.getDetails().get("unpruned"), null, 0, null);
//					AMapping mappingUnpruned = dtl.predict(testSourceCache, testTargetCache, resUnpruned);
//					double giniGlobalUp04FM = new FMeasure().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double giniGlobalUp04Precision = new Precision().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double giniGlobalUp04Recall = new Recall().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					int giniGlobalUp04Size = res.getLinkSpecification().size();
//					int giniGlobalUp04UnprunedSize = resUnpruned.getLinkSpecification().size();
//					double giniGlobalUpUnpruned04FM = new FMeasure().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double giniGlobalUpUnpruned04Precision = new Precision().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double giniGlobalUpUnpruned04Recall = new Recall().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					logger.info("FMeasure: " + giniGlobalUp04FM);
//					logger.info("Precision: " + giniGlobalUp04Precision);
//					logger.info("Recall: " + giniGlobalUp04Recall);
//					long giniGlobalUp04Time = (end - start);
//					logger.info("Time: " + giniGlobalUp04Time);
//					logger.info("Size: " + giniGlobalUp04Size);
//					logger.info("Unpruned Size: " + giniGlobalUp04Size);

                    // ================================================================================================================
                    // Print results for iteration
                    // ================================================================================================================

                    datalineFMeasure += dataName.replace("full", "").replace("products", "") + "\t" + j48FM + "\t" + j48optFM + "\n";
                    writerFMeasure.write(datalineFMeasure);
                    datalineFMeasure = "";

                    datalinePrecision += dataName.replace("full", "").replace("products", "") + "\t" + j48Precision + "\t" + j48optPrecision + "\n";
                    writerPrecision.write(datalinePrecision);
                    datalinePrecision = "";

                    datalineRecall += dataName.replace("full", "").replace("products", "") + "\t" + j48Recall + "\t" + j48optRecall + "\n";
                    writerRecall.write(datalineRecall);
                    datalineRecall = "";

                    datalineTime += dataName.replace("full", "").replace("products", "") + "\t" + j48Time + "\t" + j48optTime + "\n";
                    writerTime.write(datalineTime);
                    datalineTime = "";

                    datalineSize += dataName.replace("full", "").replace("products", "") + "\t" + j48Size + "\t" + j48optSize + "\n";
                    writerSize.write(datalineSize);
                    datalineSize = "";

//					datalineFMeasure += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04FM + "\t" + j48FM + "\n";
//					writerFMeasure.write(datalineFMeasure);
//					datalineFMeasure = "";
//
//					datalinePrecision += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Precision + "\t" + j48Precision + "\n";
//					writerPrecision.write(datalinePrecision);
//					datalinePrecision = "";
//
//					datalineRecall += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Recall + "\t" + j48Recall + "\n";
//					writerRecall.write(datalineRecall);
//					datalineRecall = "";
//
//					datalineTime += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Time + "\t" + j48Time + "\n";
//					writerTime.write(datalineTime);
//					datalineTime = "";
//
//					datalineSize += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Size + "\t" + j48Size +  "\n";
//					writerSize.write(datalineSize);
//					datalineSize = "";

//					datalineFMeasure += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04FM + "\t" + giniGlobalUpUnpruned04FM + "\n";
//					writerFMeasure.write(datalineFMeasure);
//					datalineFMeasure = "";
//
//					datalinePrecision += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Precision + "\t" + giniGlobalUpUnpruned04Precision + "\n";
//					writerPrecision.write(datalinePrecision);
//					datalinePrecision = "";
//
//					datalineRecall += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Recall + "\t" + giniGlobalUpUnpruned04Recall + "\n";
//					writerRecall.write(datalineRecall);
//					datalineRecall = "";
//
//					datalineTime += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Time + "\t" + 0 + "\n";
//					writerTime.write(datalineTime);
//					datalineTime = "";
//
//					datalineSize += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Size + "\t" + giniGlobalUp04UnprunedSize +  "\n";
//					writerSize.write(datalineSize);
//					datalineSize = "";

                } catch (UnsupportedMLImplementationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            writerFMeasure.close();
            writerPrecision.close();
            writerRecall.close();
            writerTime.close();
            writerSize.close();
        }
    }

    public static void small(String baseFolder) throws FileNotFoundException {
        // ================================================================================================================
        // Set up output
        // ================================================================================================================
        long start;
        long end;
        String fMeasureBase = baseFolder + "FMeasure/";
        String precisionBase = baseFolder + "Precision/";
        String recallBase = baseFolder + "Recall/";
        String timeBase = baseFolder + "Time/";
        String sizeBase = baseFolder + "Size/";

        String unprunedfMeasureBase = baseFolder + "UnprunedFMeasure/";
        String unprunedprecisionBase = baseFolder + "UnprunedPrecision/";
        String unprunedrecallBase = baseFolder + "UnprunedRecall/";
        String unprunedsizeBase = baseFolder + "UnprunedSize/";
        createDirectoriesIfNecessarry(baseFolder, fMeasureBase, precisionBase, recallBase, timeBase, sizeBase, unprunedfMeasureBase, unprunedprecisionBase, unprunedrecallBase, unprunedsizeBase);

        String[] datasets = {"dbplinkedmdb", "person1", "person2", "drugs", "restaurants", "dblpacm",
                "abtbuy", "dblpscholar", "amazongoogleproducts"};
        String header = "Data\tDragon\twombat\n";

        for (int k = 0; k < 10; k++) {

            PrintWriter writerFMeasure = new PrintWriter(new FileOutputStream(fMeasureBase + k + ".csv", false));
            writerFMeasure.write(header);
            String datalineFMeasure = "";
            PrintWriter writerPrecision = new PrintWriter(new FileOutputStream(precisionBase + k + ".csv", false));
            writerPrecision.write(header);
            String datalinePrecision = "";
            PrintWriter writerRecall = new PrintWriter(new FileOutputStream(recallBase + k + ".csv", false));
            writerRecall.write(header);
            String datalineRecall = "";
            PrintWriter writerTime = new PrintWriter(new FileOutputStream(timeBase + k + ".csv", false));
            writerTime.write(header);
            String datalineTime = "";
            PrintWriter writerSize = new PrintWriter(new FileOutputStream(sizeBase + k + ".csv", false));
            writerSize.write(header);
            String datalineSize = "";

            PrintWriter unprunedWriterFMeasure = new PrintWriter(new FileOutputStream(unprunedfMeasureBase + k + ".csv", false));
            unprunedWriterFMeasure.write(header);
            String unprunedDatalineFMeasure = "";
            PrintWriter unprunedWriterPrecision = new PrintWriter(new FileOutputStream(unprunedprecisionBase + k + ".csv", false));
            unprunedWriterPrecision.write(header);
            String unprunedDatalinePrecision = "";
            PrintWriter unprunedWriterRecall = new PrintWriter(new FileOutputStream(unprunedrecallBase + k + ".csv", false));
            unprunedWriterRecall.write(header);
            String unprunedDatalineRecall = "";
            PrintWriter unprunedWriterSize = new PrintWriter(new FileOutputStream(unprunedsizeBase + k + ".csv", false));
            unprunedWriterSize.write(header);
            String unprunedDatalineSize = "";

            // ================================================================================================================
            // Set up training data folds and caches
            // ================================================================================================================

            for (String dataName : datasets) {
                logger.info("\n\n >>>>>>>>>>>>> " + dataName.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
                Dragon.useJ48optimized = false;
                Dragon.useJ48 = false;
                EvaluationData c = DataSetChooser.getData(dataName);
                folds = generateFolds(c);

                FoldData trainData = new FoldData();
                FoldData testData = folds.get(FOLDS_COUNT - 1);
                // perform union on test folds
                for (int i = 0; i < FOLDS_COUNT; i++) {
                    if (i != 9) {
                        trainData.map = MappingOperations.union(trainData.map, folds.get(i).map);
                        trainData.sourceCache = cacheUnion(trainData.sourceCache, folds.get(i).sourceCache);
                        trainData.targetCache = cacheUnion(trainData.targetCache, folds.get(i).targetCache);
                    }
                }
                // fix caches if necessary
                for (String s : trainData.map.getMap().keySet()) {
                    for (String t : trainData.map.getMap().get(s).keySet()) {
                        if (!trainData.targetCache.containsUri(t)) {
                            // logger.info("target: " + t);
                            trainData.targetCache.addInstance(c.getTargetCache().getInstance(t));
                        }
                    }
                    if (!trainData.sourceCache.containsUri(s)) {
                        // logger.info("source: " + s);
                        trainData.sourceCache.addInstance(c.getSourceCache().getInstance(s));
                    }
                }

                AMapping trainingData = trainData.map;
                ACache trainSourceCache = trainData.sourceCache;
                ACache trainTargetCache = trainData.targetCache;
                ACache testSourceCache = testData.sourceCache;
                ACache testTargetCache = testData.targetCache;

                // ================================================================================================================
                // Learning Phase
                // ================================================================================================================
                try {
                    AMLAlgorithm dtl = null;
                    Configuration config = null;
                    MLResults res = null;
                    AMapping mapping = null;


                    // ========================================
                    logger.info("========Global + Gini + UP + 0.4==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new GlobalFMeasurePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.4);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniGlobalUp04FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp04Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp04Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniGlobalUp04Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniGlobalUp04FM);
                    logger.info("Precision: " + giniGlobalUp04Precision);
                    logger.info("Recall: " + giniGlobalUp04Recall);
                    long giniGlobalUp04Time = (end - start);
                    logger.info("Time: " + giniGlobalUp04Time);
                    logger.info("Size: " + giniGlobalUp04Size);
                    logger.info("Unpruned Size: " + giniGlobalUp04Size);


                    logger.info("\n========WOMBAT==========");
                    AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    wombat.init(null, trainSourceCache, trainTargetCache);
                    wombat.getMl().setConfiguration(c.getConfigReader().read());
                    ((WombatSimple) wombat.getMl()).propMap = c.getPropertyMapping();
                    start = System.currentTimeMillis();
                    res = wombat.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();

                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = wombat.predict(testSourceCache, testTargetCache, res);
                    double wombatFM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache, testTargetCache));
                    double wombatPrecision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache, testTargetCache));
                    double wombatRecall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache, testTargetCache));
                    int wombatSize = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + wombatFM);
                    logger.info("Precision: " + wombatPrecision);
                    logger.info("Recall: " + wombatRecall);
                    long wombatTime = (end - start);
                    logger.info("Time: " + wombatTime);
                    logger.info("Size: " + wombatSize);

                    // ================================================================================================================
                    // Print results for iteration
                    // ================================================================================================================

                    datalineFMeasure += dataName.replace("full", "").replace("products", "") + "\t" + giniGlobalUp04FM + "\t" + wombatFM + "\n";
                    writerFMeasure.write(datalineFMeasure);
                    datalineFMeasure = "";

                    datalinePrecision += dataName.replace("full", "").replace("products", "") + "\t" + giniGlobalUp04Precision + "\t" + wombatPrecision + "\n";
                    writerPrecision.write(datalinePrecision);
                    datalinePrecision = "";

                    datalineRecall += dataName.replace("full", "").replace("products", "") + "\t" + giniGlobalUp04Recall + "\t" + wombatRecall + "\n";
                    writerRecall.write(datalineRecall);
                    datalineRecall = "";

                } catch (UnsupportedMLImplementationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            writerFMeasure.close();
            writerPrecision.close();
            writerRecall.close();
            writerTime.close();
            writerSize.close();
            unprunedWriterFMeasure.close();
            unprunedWriterPrecision.close();
            unprunedWriterRecall.close();
            unprunedWriterSize.close();
        }
    }

    public static void newEval(String baseFolder) throws FileNotFoundException {
        // ================================================================================================================
        // Set up output
        // ================================================================================================================
        long start;
        long end;
        String fMeasureBase = baseFolder + "FMeasure/";
        String precisionBase = baseFolder + "Precision/";
        String recallBase = baseFolder + "Recall/";
        String timeBase = baseFolder + "Time/";
        String sizeBase = baseFolder + "Size/";

        String unprunedfMeasureBase = baseFolder + "UnprunedFMeasure/";
        String unprunedprecisionBase = baseFolder + "UnprunedPrecision/";
        String unprunedrecallBase = baseFolder + "UnprunedRecall/";
        String unprunedsizeBase = baseFolder + "UnprunedSize/";
        createDirectoriesIfNecessarry(baseFolder, fMeasureBase, precisionBase, recallBase, timeBase, sizeBase, unprunedfMeasureBase, unprunedprecisionBase, unprunedrecallBase, unprunedsizeBase);

        String[] datasets = {"dbplinkedmdb", "person1", "person2", "drugs", "restaurants", "dblpacm",
                "abtbuy", "dblpscholar", "amazongoogleproducts"};
        String header = "Data\tginiGlobal\tginiError\tglobalglobal\tglobalError\twombat\tj48\tj48opt\n";

        for (int k = 0; k < 10; k++) {

            PrintWriter writerFMeasure = new PrintWriter(new FileOutputStream(fMeasureBase + k + ".csv", false));
            writerFMeasure.write(header);
            String datalineFMeasure = "";
            PrintWriter writerPrecision = new PrintWriter(new FileOutputStream(precisionBase + k + ".csv", false));
            writerPrecision.write(header);
            String datalinePrecision = "";
            PrintWriter writerRecall = new PrintWriter(new FileOutputStream(recallBase + k + ".csv", false));
            writerRecall.write(header);
            String datalineRecall = "";
            PrintWriter writerTime = new PrintWriter(new FileOutputStream(timeBase + k + ".csv", false));
            writerTime.write(header);
            String datalineTime = "";
            PrintWriter writerSize = new PrintWriter(new FileOutputStream(sizeBase + k + ".csv", false));
            writerSize.write(header);
            String datalineSize = "";

            PrintWriter unprunedWriterFMeasure = new PrintWriter(new FileOutputStream(unprunedfMeasureBase + k + ".csv", false));
            unprunedWriterFMeasure.write(header);
            String unprunedDatalineFMeasure = "";
            PrintWriter unprunedWriterPrecision = new PrintWriter(new FileOutputStream(unprunedprecisionBase + k + ".csv", false));
            unprunedWriterPrecision.write(header);
            String unprunedDatalinePrecision = "";
            PrintWriter unprunedWriterRecall = new PrintWriter(new FileOutputStream(unprunedrecallBase + k + ".csv", false));
            unprunedWriterRecall.write(header);
            String unprunedDatalineRecall = "";
            PrintWriter unprunedWriterSize = new PrintWriter(new FileOutputStream(unprunedsizeBase + k + ".csv", false));
            unprunedWriterSize.write(header);
            String unprunedDatalineSize = "";

            // ================================================================================================================
            // Set up training data folds and caches
            // ================================================================================================================

            for (String dataName : datasets) {
                logger.info("\n\n >>>>>>>>>>>>> " + dataName.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
                Dragon.useJ48optimized = false;
                Dragon.useJ48 = false;
                EvaluationData c = DataSetChooser.getData(dataName);
                folds = generateFolds(c);

                FoldData trainData = new FoldData();
                FoldData testData = folds.get(FOLDS_COUNT - 1);
                // perform union on test folds
                for (int i = 0; i < FOLDS_COUNT; i++) {
                    if (i != 9) {
                        trainData.map = MappingOperations.union(trainData.map, folds.get(i).map);
                        trainData.sourceCache = cacheUnion(trainData.sourceCache, folds.get(i).sourceCache);
                        trainData.targetCache = cacheUnion(trainData.targetCache, folds.get(i).targetCache);
                    }
                }
                // fix caches if necessary
                for (String s : trainData.map.getMap().keySet()) {
                    for (String t : trainData.map.getMap().get(s).keySet()) {
                        if (!trainData.targetCache.containsUri(t)) {
                            // logger.info("target: " + t);
                            trainData.targetCache.addInstance(c.getTargetCache().getInstance(t));
                        }
                    }
                    if (!trainData.sourceCache.containsUri(s)) {
                        // logger.info("source: " + s);
                        trainData.sourceCache.addInstance(c.getSourceCache().getInstance(s));
                    }
                }

                AMapping trainingData = trainData.map;
                ACache trainSourceCache = trainData.sourceCache;
                ACache trainTargetCache = trainData.targetCache;
                ACache testSourceCache = testData.sourceCache;
                ACache testTargetCache = testData.targetCache;

                // ================================================================================================================
                // Learning Phase
                // ================================================================================================================
                try {
                    AMLAlgorithm dtl = null;
                    Configuration config = null;
                    MLResults res = null;
                    AMapping mapping = null;


                    // ========================================
                    logger.info("========Global + Gini + UP + 0.4==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(LinearEuclid.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
//					((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
//					dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
//					dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
//					dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
//							new GlobalFMeasurePruning());
//					dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.4);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniGlobalUp04FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp04Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp04Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniGlobalUp04Size = res.getLinkSpecification().size();
                    MLResults resUnpruned = new MLResults((LinkSpecification) res.getDetails().get("unpruned"), null, 0, null);
                    AMapping mappingUnpruned = dtl.predict(testSourceCache, testTargetCache, resUnpruned);
                    int giniGlobalUp04UnprunedSize = resUnpruned.getLinkSpecification().size();
                    double giniGlobalUp04UnprunedFM = new FMeasure().calculate(mappingUnpruned,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp04UnprunedPrecision = new Precision().calculate(mappingUnpruned,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp04UnprunedRecall = new Recall().calculate(mappingUnpruned,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    logger.info("FMeasure: " + giniGlobalUp04FM);
                    logger.info("Precision: " + giniGlobalUp04Precision);
                    logger.info("Recall: " + giniGlobalUp04Recall);
                    long giniGlobalUp04Time = (end - start);
                    logger.info("Time: " + giniGlobalUp04Time);
                    logger.info("Size: " + giniGlobalUp04Size);
                    logger.info("Unpruned Size: " + giniGlobalUp04Size);

//					// ========================================
//					logger.info("========ErrorEstimate + Gini + UP + 0.4==========");
//
//					dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
//							MLImplementationType.SUPERVISED_BATCH);
//					logger.info("source size: " + testSourceCache.size());
//					logger.info("target size: " + testTargetCache.size());
//					dtl.init(null, trainSourceCache, trainTargetCache);
//					config = c.getConfigReader().read();
//					dtl.getMl().setConfiguration(config);
//					((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
//					start = System.currentTimeMillis();
//					dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
//					dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
//					dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
//							new ErrorEstimatePruning());
//					dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.4);
//					res = dtl.asSupervised().learn(trainingData);
//					end = System.currentTimeMillis();
//					logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
//					mapping = dtl.predict(testSourceCache, testTargetCache, res);
//					double giniErrorUp04FM = new FMeasure().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double giniErrorUp04Precision = new Precision().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double giniErrorUp04Recall = new Recall().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					int giniErrorUp04Size = res.getLinkSpecification().size();
//					resUnpruned = new MLResults((LinkSpecification) res.getDetails().get("unpruned"), null, 0, null);
//					mappingUnpruned = dtl.predict(testSourceCache, testTargetCache, resUnpruned);
//					int giniErrorUp04UnprunedSize = resUnpruned.getLinkSpecification().size();
//					double giniErrorUp04UnprunedFM = new FMeasure().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double giniErrorUp04UnprunedPrecision = new Precision().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double giniErrorUp04UnprunedRecall = new Recall().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					logger.info("FMeasure: " + giniErrorUp04FM);
//					logger.info("Precision: " + giniErrorUp04Precision);
//					logger.info("Recall: " + giniErrorUp04Recall);
//					long giniErrorUp04Time = (end - start);
//					logger.info("Time: " + giniErrorUp04Time);
//					logger.info("Size: " + giniErrorUp04Size);
//
//					logger.info("\n========WOMBAT==========");
//					AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
//							MLImplementationType.SUPERVISED_BATCH);
//					wombat.init(null, trainSourceCache, trainTargetCache);
//					wombat.getMl().setConfiguration(c.getConfigReader().read());
//					((WombatSimple) wombat.getMl()).propMap = c.getPropertyMapping();
//					start = System.currentTimeMillis();
//					res = wombat.asSupervised().learn(trainingData);
//					end = System.currentTimeMillis();
//
//					logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
//					mapping = wombat.predict(testSourceCache, testTargetCache, res);
//					double wombatFM = new FMeasure().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache, testTargetCache));
//					double wombatPrecision = new Precision().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache, testTargetCache));
//					double wombatRecall = new Recall().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache, testTargetCache));
//					int wombatSize = res.getLinkSpecification().size();
//					logger.info("FMeasure: " + wombatFM);
//					logger.info("Precision: " + wombatPrecision);
//					logger.info("Recall: " + wombatRecall);
//					long wombatTime = (end - start);
//					logger.info("Time: " + wombatTime);
//					logger.info("Size: " + wombatSize);
//
//					Dragon.useMergeAndConquer = false;
//					// ==================================
//
//					logger.info("========Global + ErrorEstimate==========");
//
//					dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
//							MLImplementationType.SUPERVISED_BATCH);
//					logger.info("source size: " + testSourceCache.size());
//					logger.info("target size: " + testTargetCache.size());
//					dtl.init(null, trainSourceCache, trainTargetCache);
//					config = c.getConfigReader().read();
//					dtl.getMl().setConfiguration(config);
//					((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
//					start = System.currentTimeMillis();
//					dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
//					dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GlobalFMeasure());
//					dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
//							new ErrorEstimatePruning());
//					res = dtl.asSupervised().learn(trainingData);
//					end = System.currentTimeMillis();
//					logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
//					mapping = dtl.predict(testSourceCache, testTargetCache, res);
//					double globalErrorFM = new FMeasure().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double globalErrorPrecision = new Precision().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double globalErrorRecall = new Recall().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					int globalErrorSize = res.getLinkSpecification().size();
//					resUnpruned = new MLResults((LinkSpecification) res.getDetails().get("unpruned"), null, 0, null);
//					mappingUnpruned = dtl.predict(testSourceCache, testTargetCache, resUnpruned);
//					int globalErrorUp04UnprunedSize = resUnpruned.getLinkSpecification().size();
//					double globalErrorUp04UnprunedFM = new FMeasure().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double globalErrorUp04UnprunedPrecision = new Precision().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double globalErrorUp04UnprunedRecall = new Recall().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					logger.info("FMeasure: " + globalErrorFM);
//					logger.info("Precision: " + globalErrorPrecision);
//					logger.info("Recall: " + globalErrorRecall);
//					long globalErrorTime = (end - start);
//					logger.info("Time: " + globalErrorTime);
//					logger.info("Size: " + globalErrorSize);
//					// ========================================
//
//					logger.info("========Global + Global==========");
//
//					dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
//							MLImplementationType.SUPERVISED_BATCH);
//					logger.info("source size: " + testSourceCache.size());
//					logger.info("target size: " + testTargetCache.size());
//					dtl.init(null, trainSourceCache, trainTargetCache);
//					config = c.getConfigReader().read();
//					dtl.getMl().setConfiguration(config);
//					((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
//					start = System.currentTimeMillis();
//					dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
//					dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GlobalFMeasure());
//					dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
//							new GlobalFMeasurePruning());
//					res = dtl.asSupervised().learn(trainingData);
//					end = System.currentTimeMillis();
//					logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
//					mapping = dtl.predict(testSourceCache, testTargetCache, res);
//					double globalglobalFM = new FMeasure().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double globalglobalPrecision = new Precision().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double globalglobalRecall = new Recall().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					int globalglobalSize = res.getLinkSpecification().size();
//					resUnpruned = new MLResults((LinkSpecification) res.getDetails().get("unpruned"), null, 0, null);
//					mappingUnpruned = dtl.predict(testSourceCache, testTargetCache, resUnpruned);
//					int globalglobalUp04UnprunedSize = resUnpruned.getLinkSpecification().size();
//					double globalglobalUp04UnprunedFM = new FMeasure().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double globalglobalUp04UnprunedPrecision = new Precision().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double globalglobalUp04UnprunedRecall = new Recall().calculate(mappingUnpruned,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					logger.info("FMeasure: " + globalglobalFM);
//					logger.info("Precision: " + globalglobalPrecision);
//					logger.info("Recall: " + globalglobalRecall);
//					long globalglobalTime = (end - start);
//					logger.info("Time: " + globalglobalTime);
//					logger.info("Size: " + globalglobalSize);

                    // ========================================
                    logger.info("========J48==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    dtl.setParameter(Dragon.PARAMETER_REDUCED_ERROR_PRUNING, true);
                    dtl.setParameter(Dragon.PARAMETER_FOLD_NUMBER, 5);
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    Dragon.useJ48 = true;
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double j48FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double j48Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double j48Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int j48Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + j48FM);
                    logger.info("Precision: " + j48Precision);
                    logger.info("Recall: " + j48Recall);
                    long j48Time = (end - start);
                    logger.info("Time: " + j48Time);
                    logger.info("Size: " + j48Size);
                    // ========================================

//					logger.info("========J48 optimized==========");
//					dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
//							MLImplementationType.SUPERVISED_BATCH);
//					logger.info("source size: " + testSourceCache.size());
//					logger.info("target size: " + testTargetCache.size());
//					dtl.init(null, trainSourceCache, trainTargetCache);
//					config = c.getConfigReader().read();
//					dtl.getMl().setConfiguration(config);
//					((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
//					dtl.setParameter(Dragon.PARAMETER_REDUCED_ERROR_PRUNING, true);
//					dtl.setParameter(Dragon.PARAMETER_FOLD_NUMBER, 5);
//					start = System.currentTimeMillis();
//					dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
//					Dragon.useJ48 = true;
//					Dragon.useJ48optimized = true;
//					res = dtl.asSupervised().learn(trainingData);
//					end = System.currentTimeMillis();
//					logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
//					mapping = dtl.predict(testSourceCache, testTargetCache, res);
//					double j48optFM = new FMeasure().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double j48optPrecision = new Precision().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					double j48optRecall = new Recall().calculate(mapping,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
//					int j48optSize = res.getLinkSpecification().size();
//					logger.info("FMeasure: " + j48optFM);
//					logger.info("Precision: " + j48optPrecision);
//					logger.info("Recall: " + j48optRecall);
//					long j48optTime = (end - start);
//					logger.info("Time: " + j48optTime);
//					logger.info("Size: " + j48optSize);
//					Dragon.useJ48 = false;
//					Dragon.useJ48optimized = false;
//
//					// ================================================================================================================
//					// Print results for iteration
//					// ================================================================================================================
//
//					datalineFMeasure += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04FM + "\t" + giniErrorUp04FM+ "\t" + globalglobalFM+ "\t" + globalErrorFM+ "\t" + wombatFM+ "\t" + j48FM+ "\t" + j48optFM+ "\n";
//					writerFMeasure.write(datalineFMeasure);
//					datalineFMeasure = "";
//
//					datalinePrecision += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Precision + "\t" + giniErrorUp04Precision+ "\t" + globalglobalPrecision+ "\t" + globalErrorPrecision+ "\t" + wombatPrecision+ "\t" + j48Precision+ "\t" + j48optPrecision+ "\n";
//					writerPrecision.write(datalinePrecision);
//					datalinePrecision = "";
//
//					datalineRecall += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Recall + "\t" + giniErrorUp04Recall+ "\t" + globalglobalRecall+ "\t" + globalErrorRecall+ "\t" + wombatRecall+ "\t" + j48Recall+ "\t" + j48optRecall+ "\n";
//					writerRecall.write(datalineRecall);
//					datalineRecall = "";
//
//					datalineTime += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Time + "\t" + giniErrorUp04Time+ "\t" + globalglobalTime+ "\t" + globalErrorTime+ "\t" + wombatTime+ "\t" + j48Time+ "\t" + j48optTime+ "\n";
//					writerTime.write(datalineTime);
//					datalineTime = "";
//
//					datalineSize += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04Size + "\t" + giniErrorUp04Size+ "\t" + globalglobalSize+ "\t" + globalErrorSize+ "\t" + wombatSize+ "\t" + j48Size+ "\t" + j48optSize+  "\n";
//					writerSize.write(datalineSize);
//					datalineSize = "";
//
//					unprunedDatalineFMeasure += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04UnprunedFM + "\t" + giniErrorUp04UnprunedFM+ "\t" + globalglobalUp04UnprunedFM+ "\t" + globalErrorUp04UnprunedFM+ "\t" + 0 + "\t" + 0 + "\t" + 0+ "\n";
//					unprunedWriterFMeasure.write(unprunedDatalineFMeasure);
//					unprunedDatalineFMeasure = "";
//
//					unprunedDatalinePrecision += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04UnprunedPrecision + "\t" + giniErrorUp04UnprunedPrecision+ "\t" + globalglobalUp04UnprunedPrecision+ "\t" + globalErrorUp04UnprunedPrecision+ "\t" + 0 + "\t" + 0 + "\t" + 0+ "\n";
//					unprunedWriterPrecision.write(unprunedDatalinePrecision);
//					unprunedDatalinePrecision = "";
//
//					unprunedDatalineRecall += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04UnprunedRecall + "\t" + giniErrorUp04UnprunedRecall+ "\t" + globalglobalUp04UnprunedRecall+ "\t" + globalErrorUp04UnprunedRecall+ "\t" + 0 + "\t" + 0 + "\t" + 0+ "\n";
//					unprunedWriterRecall.write(unprunedDatalineRecall);
//					unprunedDatalineRecall = "";
//
//					unprunedDatalineSize += dataName.replace("full","").replace("products","") + "\t" + giniGlobalUp04UnprunedSize + "\t" + giniErrorUp04UnprunedSize+ "\t" + globalglobalUp04UnprunedSize+ "\t" + globalErrorUp04UnprunedSize+ "\t" + 0 + "\t" + 0 + "\t" + 0+  "\n";
//					unprunedWriterSize.write(unprunedDatalineSize);
//					unprunedDatalineSize = "";

                } catch (UnsupportedMLImplementationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            writerFMeasure.close();
            writerPrecision.close();
            writerRecall.close();
            writerTime.close();
            writerSize.close();
            unprunedWriterFMeasure.close();
            unprunedWriterPrecision.close();
            unprunedWriterRecall.close();
            unprunedWriterSize.close();
        }
    }

    public static void performCrossValidationEverything(String baseFolder) throws FileNotFoundException {
        // ================================================================================================================
        // Set up output
        // ================================================================================================================
        long start;
        long end;
        String fMeasureBase = baseFolder + "FMeasure/";
        String precisionBase = baseFolder + "Precision/";
        String recallBase = baseFolder + "Recall/";
        String timeBase = baseFolder + "Time/";
        String sizeBase = baseFolder + "Size/";
        createDirectoriesIfNecessarry(baseFolder, fMeasureBase, precisionBase, recallBase, timeBase, sizeBase);

        String[] datasets = {"dbplinkedmdb", "person1full", "person2full", "drugs", "restaurantsfull", "dblpacm",
                "abtbuy", "dblpscholar", "amazongoogleproducts"};
        String header = "Data\tginiGlobalUp005FM\tginiErrorUp005FM\tginiGlobalUp02FM\tginiErrorUp02FM\tginiGlobalUp04FM\tginiErrorUp04FM\tginiGlobalUp08FM\tginiErrorUp08FM\tginiGlobalMiddle005FM\tginiErrorMiddle005FM\tginiGlobalMiddle02FM\tginiErrorMiddle02FM\tginiGlobalMiddle04FM\tginiErrorMiddle04FM\tiniGlobalMiddle08FM\tginiErrorMiddle08FM\tglobalglobalFM\tglobalErrorFM\twombatFM\tj48FM\tj48optFM\n";

        for (int k = 0; k < 10; k++) {

            PrintWriter writerFMeasure = new PrintWriter(new FileOutputStream(fMeasureBase + k + ".csv", false));
            writerFMeasure.write(header);
            String datalineFMeasure = "";
            PrintWriter writerPrecision = new PrintWriter(new FileOutputStream(precisionBase + k + ".csv", false));
            writerPrecision.write(header);
            String datalinePrecision = "";
            PrintWriter writerRecall = new PrintWriter(new FileOutputStream(recallBase + k + ".csv", false));
            writerRecall.write(header);
            String datalineRecall = "";
            PrintWriter writerTime = new PrintWriter(new FileOutputStream(timeBase + k + ".csv", false));
            writerTime.write(header);
            String datalineTime = "";
            PrintWriter writerSize = new PrintWriter(new FileOutputStream(sizeBase + k + ".csv", false));
            writerSize.write(header);
            String datalineSize = "";

            // ================================================================================================================
            // Set up training data folds and caches
            // ================================================================================================================

            for (String dataName : datasets) {
                logger.info("\n\n >>>>>>>>>>>>> " + dataName.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
                Dragon.useJ48optimized = false;
                Dragon.useJ48 = false;
                EvaluationData c = DataSetChooser.getData(dataName);
                folds = generateFolds(c);

                FoldData trainData = new FoldData();
                FoldData testData = folds.get(FOLDS_COUNT - 1);
                // perform union on test folds
                for (int i = 0; i < FOLDS_COUNT; i++) {
                    if (i != 9) {
                        trainData.map = MappingOperations.union(trainData.map, folds.get(i).map);
                        trainData.sourceCache = cacheUnion(trainData.sourceCache, folds.get(i).sourceCache);
                        trainData.targetCache = cacheUnion(trainData.targetCache, folds.get(i).targetCache);
                    }
                }
                // fix caches if necessary
                for (String s : trainData.map.getMap().keySet()) {
                    for (String t : trainData.map.getMap().get(s).keySet()) {
                        if (!trainData.targetCache.containsUri(t)) {
                            // logger.info("target: " + t);
                            trainData.targetCache.addInstance(c.getTargetCache().getInstance(t));
                        }
                    }
                    if (!trainData.sourceCache.containsUri(s)) {
                        // logger.info("source: " + s);
                        trainData.sourceCache.addInstance(c.getSourceCache().getInstance(s));
                    }
                }

                AMapping trainingData = trainData.map;
                ACache trainSourceCache = trainData.sourceCache;
                ACache trainTargetCache = trainData.targetCache;
                ACache testSourceCache = testData.sourceCache;
                ACache testTargetCache = testData.targetCache;

                // ================================================================================================================
                // Learning Phase
                // ================================================================================================================
                try {
                    AMLAlgorithm dtl = null;
                    Configuration config = null;
                    MLResults res = null;
                    AMapping mapping = null;

                    GiniIndex.middlePoints = false;

                    // ========================================
                    logger.info("========Global + Gini + UP + 0.05==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new GlobalFMeasurePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.05);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniGlobalUp005FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp005Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp005Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniGlobalUp005Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniGlobalUp005FM);
                    logger.info("Precision: " + giniGlobalUp005Precision);
                    logger.info("Recall: " + giniGlobalUp005Recall);
                    long giniGlobalUp005Time = (end - start);
                    logger.info("Time: " + giniGlobalUp005Time);
                    logger.info("Size: " + giniGlobalUp005Size);

                    // ========================================
                    logger.info("========ErrorEstimate + Gini + UP + 0.05==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new ErrorEstimatePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.05);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniErrorUp005FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorUp005Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorUp005Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniErrorUp005Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniErrorUp005FM);
                    logger.info("Precision: " + giniErrorUp005Precision);
                    logger.info("Recall: " + giniErrorUp005Recall);
                    long giniErrorUp005Time = (end - start);
                    logger.info("Time: " + giniErrorUp005Time);
                    logger.info("Size: " + giniErrorUp005Size);

                    // ========================================
                    logger.info("========Global + Gini + UP + 0.2==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new GlobalFMeasurePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.2);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniGlobalUp02FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp02Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp02Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniGlobalUp02Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniGlobalUp02FM);
                    logger.info("Precision: " + giniGlobalUp02Precision);
                    logger.info("Recall: " + giniGlobalUp02Recall);
                    long giniGlobalUp02Time = (end - start);
                    logger.info("Time: " + giniGlobalUp02Time);
                    logger.info("Size: " + giniGlobalUp02Size);

                    // ========================================
                    logger.info("========ErrorEstimate + Gini + UP + 0.2==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new ErrorEstimatePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.2);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniErrorUp02FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorUp02Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorUp02Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniErrorUp02Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniErrorUp02FM);
                    logger.info("Precision: " + giniErrorUp02Precision);
                    logger.info("Recall: " + giniErrorUp02Recall);
                    long giniErrorUp02Time = (end - start);
                    logger.info("Time: " + giniErrorUp02Time);
                    logger.info("Size: " + giniErrorUp02Size);

                    // ========================================
                    logger.info("========Global + Gini + UP + 0.4==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new GlobalFMeasurePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.4);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniGlobalUp04FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp04Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp04Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniGlobalUp04Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniGlobalUp04FM);
                    logger.info("Precision: " + giniGlobalUp04Precision);
                    logger.info("Recall: " + giniGlobalUp04Recall);
                    long giniGlobalUp04Time = (end - start);
                    logger.info("Time: " + giniGlobalUp04Time);
                    logger.info("Size: " + giniGlobalUp04Size);

                    // ========================================
                    logger.info("========ErrorEstimate + Gini + UP + 0.4==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new ErrorEstimatePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.4);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniErrorUp04FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorUp04Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorUp04Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniErrorUp04Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniErrorUp04FM);
                    logger.info("Precision: " + giniErrorUp04Precision);
                    logger.info("Recall: " + giniErrorUp04Recall);
                    long giniErrorUp04Time = (end - start);
                    logger.info("Time: " + giniErrorUp04Time);
                    logger.info("Size: " + giniErrorUp04Size);

                    // ========================================
                    logger.info("========Global + Gini + UP + 0.8==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new GlobalFMeasurePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.8);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniGlobalUp08FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp08Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalUp08Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniGlobalUp08Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniGlobalUp08FM);
                    logger.info("Precision: " + giniGlobalUp08Precision);
                    logger.info("Recall: " + giniGlobalUp08Recall);
                    long giniGlobalUp08Time = (end - start);
                    logger.info("Time: " + giniGlobalUp08Time);
                    logger.info("Size: " + giniGlobalUp08Size);

                    // ========================================
                    logger.info("========ErrorEstimate + Gini + UP + 0.8==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new ErrorEstimatePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.8);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniErrorUp08FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorUp08Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorUp08Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniErrorUp08Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniErrorUp08FM);
                    logger.info("Precision: " + giniErrorUp08Precision);
                    logger.info("Recall: " + giniErrorUp08Recall);
                    long giniErrorUp08Time = (end - start);
                    logger.info("Time: " + giniErrorUp08Time);
                    logger.info("Size: " + giniErrorUp08Size);

                    GiniIndex.middlePoints = true;

                    // ========================================
                    logger.info("========Global + Gini + Middle + 0.05==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new GlobalFMeasurePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.05);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniGlobalMiddle005FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalMiddle005Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalMiddle005Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniGlobalMiddle005Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniGlobalMiddle005FM);
                    logger.info("Precision: " + giniGlobalMiddle005Precision);
                    logger.info("Recall: " + giniGlobalMiddle005Recall);
                    long giniGlobalMiddle005Time = (end - start);
                    logger.info("Time: " + giniGlobalMiddle005Time);
                    logger.info("Size: " + giniGlobalMiddle005Size);

                    // ========================================
                    logger.info("========ErrorEstimate + Gini + Middle + 0.05==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new ErrorEstimatePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.05);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniErrorMiddle005FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorMiddle005Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorMiddle005Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniErrorMiddle005Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniErrorMiddle005FM);
                    logger.info("Precision: " + giniErrorMiddle005Precision);
                    logger.info("Recall: " + giniErrorMiddle005Recall);
                    long giniErrorMiddle005Time = (end - start);
                    logger.info("Time: " + giniErrorMiddle005Time);
                    logger.info("Size: " + giniErrorMiddle005Size);

                    // ========================================
                    logger.info("========Global + Gini + Middle + 0.2==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new GlobalFMeasurePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.2);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniGlobalMiddle02FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalMiddle02Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalMiddle02Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniGlobalMiddle02Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniGlobalMiddle02FM);
                    logger.info("Precision: " + giniGlobalMiddle02Precision);
                    logger.info("Recall: " + giniGlobalMiddle02Recall);
                    long giniGlobalMiddle02Time = (end - start);
                    logger.info("Time: " + giniGlobalMiddle02Time);
                    logger.info("Size: " + giniGlobalMiddle02Size);

                    // ========================================
                    logger.info("========ErrorEstimate + Gini + Middle + 0.2==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new ErrorEstimatePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.2);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniErrorMiddle02FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorMiddle02Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorMiddle02Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniErrorMiddle02Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniErrorMiddle02FM);
                    logger.info("Precision: " + giniErrorMiddle02Precision);
                    logger.info("Recall: " + giniErrorMiddle02Recall);
                    long giniErrorMiddle02Time = (end - start);
                    logger.info("Time: " + giniErrorMiddle02Time);
                    logger.info("Size: " + giniErrorMiddle02Size);

                    // ========================================
                    logger.info("========Global + Gini + Middle + 0.4==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new GlobalFMeasurePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.4);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniGlobalMiddle04FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalMiddle04Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalMiddle04Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniGlobalMiddle04Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniGlobalMiddle04FM);
                    logger.info("Precision: " + giniGlobalMiddle04Precision);
                    logger.info("Recall: " + giniGlobalMiddle04Recall);
                    long giniGlobalMiddle04Time = (end - start);
                    logger.info("Time: " + giniGlobalMiddle04Time);
                    logger.info("Size: " + giniGlobalMiddle04Size);

                    // ========================================
                    logger.info("========ErrorEstimate + Gini + Middle + 0.4==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new ErrorEstimatePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.4);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniErrorMiddle04FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorMiddle04Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorMiddle04Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniErrorMiddle04Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniErrorMiddle04FM);
                    logger.info("Precision: " + giniErrorMiddle04Precision);
                    logger.info("Recall: " + giniErrorMiddle04Recall);
                    long giniErrorMiddle04Time = (end - start);
                    logger.info("Time: " + giniErrorMiddle04Time);
                    logger.info("Size: " + giniErrorMiddle04Size);

                    // ========================================
                    logger.info("========Global + Gini + Middle + 0.8==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new GlobalFMeasurePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.8);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniGlobalMiddle08FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalMiddle08Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniGlobalMiddle08Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniGlobalMiddle08Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniGlobalMiddle08FM);
                    logger.info("Precision: " + giniGlobalMiddle08Precision);
                    logger.info("Recall: " + giniGlobalMiddle08Recall);
                    long giniGlobalMiddle08Time = (end - start);
                    logger.info("Time: " + giniGlobalMiddle08Time);
                    logger.info("Size: " + giniGlobalMiddle08Size);

                    // ========================================
                    logger.info("========ErrorEstimate + Gini + Middle + 0.8==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new ErrorEstimatePruning());
                    dtl.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, 0.8);
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double giniErrorMiddle08FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorMiddle08Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double giniErrorMiddle08Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int giniErrorMiddle08Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + giniErrorMiddle08FM);
                    logger.info("Precision: " + giniErrorMiddle08Precision);
                    logger.info("Recall: " + giniErrorMiddle08Recall);
                    long giniErrorMiddle08Time = (end - start);
                    logger.info("Time: " + giniErrorMiddle08Time);
                    logger.info("Size: " + giniErrorMiddle08Size);

                    GiniIndex.middlePoints = true;
                    logger.info("\n========WOMBAT==========");
                    AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    wombat.init(null, trainSourceCache, trainTargetCache);
                    wombat.getMl().setConfiguration(c.getConfigReader().read());
                    ((WombatSimple) wombat.getMl()).propMap = c.getPropertyMapping();
                    start = System.currentTimeMillis();
                    res = wombat.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();

                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = wombat.predict(testSourceCache, testTargetCache, res);
                    double wombatFM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache, testTargetCache));
                    double wombatPrecision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache, testTargetCache));
                    double wombatRecall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache, testTargetCache));
                    int wombatSize = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + wombatFM);
                    logger.info("Precision: " + wombatPrecision);
                    logger.info("Recall: " + wombatRecall);
                    long wombatTime = (end - start);
                    logger.info("Time: " + wombatTime);
                    logger.info("Size: " + wombatSize);

                    Dragon.useMergeAndConquer = false;
                    // ==================================

                    logger.info("========Global + ErrorEstimate==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GlobalFMeasure());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new ErrorEstimatePruning());
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double globalErrorFM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double globalErrorPrecision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double globalErrorRecall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int globalErrorSize = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + globalErrorFM);
                    logger.info("Precision: " + globalErrorPrecision);
                    logger.info("Recall: " + globalErrorRecall);
                    long globalErrorTime = (end - start);
                    logger.info("Time: " + globalErrorTime);
                    logger.info("Size: " + globalErrorSize);
                    // ========================================

                    logger.info("========Global + Global==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    dtl.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, new GlobalFMeasure());
                    dtl.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION,
                            new GlobalFMeasurePruning());
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double globalglobalFM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double globalglobalPrecision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double globalglobalRecall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int globalglobalSize = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + globalglobalFM);
                    logger.info("Precision: " + globalglobalPrecision);
                    logger.info("Recall: " + globalglobalRecall);
                    long globalglobalTime = (end - start);
                    logger.info("Time: " + globalglobalTime);
                    logger.info("Size: " + globalglobalSize);

                    // ========================================
                    logger.info("========J48==========");

                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    Dragon.useJ48 = true;
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double j48FM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double j48Precision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double j48Recall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int j48Size = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + j48FM);
                    logger.info("Precision: " + j48Precision);
                    logger.info("Recall: " + j48Recall);
                    long j48Time = (end - start);
                    logger.info("Time: " + j48Time);
                    logger.info("Size: " + j48Size);
                    // ========================================

                    logger.info("========J48 optimized==========");
                    dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
                            MLImplementationType.SUPERVISED_BATCH);
                    logger.info("source size: " + testSourceCache.size());
                    logger.info("target size: " + testTargetCache.size());
                    dtl.init(null, trainSourceCache, trainTargetCache);
                    config = c.getConfigReader().read();
                    dtl.getMl().setConfiguration(config);
                    ((Dragon) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
                    start = System.currentTimeMillis();
                    dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
                    Dragon.useJ48 = true;
                    Dragon.useJ48optimized = true;
                    res = dtl.asSupervised().learn(trainingData);
                    end = System.currentTimeMillis();
                    logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
                    mapping = dtl.predict(testSourceCache, testTargetCache, res);
                    double j48optFM = new FMeasure().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double j48optPrecision = new Precision().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    double j48optRecall = new Recall().calculate(mapping,
                            new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
                    int j48optSize = res.getLinkSpecification().size();
                    logger.info("FMeasure: " + j48optFM);
                    logger.info("Precision: " + j48optPrecision);
                    logger.info("Recall: " + j48optRecall);
                    long j48optTime = (end - start);
                    logger.info("Time: " + j48optTime);
                    logger.info("Size: " + j48optSize);
                    Dragon.useJ48 = false;
                    Dragon.useJ48optimized = false;


                    // ================================================================================================================
                    // Print results for iteration
                    // ================================================================================================================

                    datalineFMeasure += dataName.replace("full", "").replace("products", "") + "\t" + giniGlobalUp005FM + "\t" + giniErrorUp005FM + "\t" + giniGlobalUp02FM + "\t" + giniErrorUp02FM + "\t" + giniGlobalUp04FM + "\t" + giniErrorUp04FM + "\t" + giniGlobalUp08FM + "\t" + giniErrorUp08FM + "\t" + giniGlobalMiddle005FM + "\t" + giniErrorMiddle005FM + "\t" + giniGlobalMiddle02FM + "\t" + giniErrorMiddle02FM + "\t" + giniGlobalMiddle04FM + "\t" + giniErrorMiddle04FM + "\t" + giniGlobalMiddle08FM + "\t" + giniErrorMiddle08FM + "\t" + globalglobalFM + "\t" + globalErrorFM + "\t" + wombatFM + "\t" + j48FM + "\t" + j48optFM + "\n";
                    writerFMeasure.write(datalineFMeasure);
                    datalineFMeasure = "";

                    datalinePrecision += dataName.replace("full", "").replace("products", "") + "\t" + giniGlobalUp005Precision + "\t" + giniErrorUp005Precision + "\t" + giniGlobalUp02Precision + "\t" + giniErrorUp02Precision + "\t" + giniGlobalUp04Precision + "\t" + giniErrorUp04Precision + "\t" + giniGlobalUp08Precision + "\t" + giniErrorUp08Precision + "\t" + giniGlobalMiddle005Precision + "\t" + giniErrorMiddle005Precision + "\t" + giniGlobalMiddle02Precision + "\t" + giniErrorMiddle02Precision + "\t" + giniGlobalMiddle04Precision + "\t" + giniErrorMiddle04Precision + "\t" + giniGlobalMiddle08Precision + "\t" + giniErrorMiddle08Precision + "\t" + globalglobalPrecision + "\t" + globalErrorPrecision + "\t" + wombatPrecision + "\t" + j48Precision + "\t" + j48optPrecision + "\n";
                    writerPrecision.write(datalinePrecision);
                    datalinePrecision = "";

                    datalineRecall += dataName.replace("full", "").replace("products", "") + "\t" + giniGlobalUp005Recall + "\t" + giniErrorUp005Recall + "\t" + giniGlobalUp02Recall + "\t" + giniErrorUp02Recall + "\t" + giniGlobalUp04Recall + "\t" + giniErrorUp04Recall + "\t" + giniGlobalUp08Recall + "\t" + giniErrorUp08Recall + "\t" + giniGlobalMiddle005Recall + "\t" + giniErrorMiddle005Recall + "\t" + giniGlobalMiddle02Recall + "\t" + giniErrorMiddle02Recall + "\t" + giniGlobalMiddle04Recall + "\t" + giniErrorMiddle04Recall + "\t" + giniGlobalMiddle08Recall + "\t" + giniErrorMiddle08Recall + "\t" + globalglobalRecall + "\t" + globalErrorRecall + "\t" + wombatRecall + "\t" + j48Recall + "\t" + j48optRecall + "\n";
                    writerRecall.write(datalineRecall);
                    datalineRecall = "";

                    datalineTime += dataName.replace("full", "").replace("products", "") + "\t" + giniGlobalUp005Time + "\t" + giniErrorUp005Time + "\t" + giniGlobalUp02Time + "\t" + giniErrorUp02Time + "\t" + giniGlobalUp04Time + "\t" + giniErrorUp04Time + "\t" + giniGlobalUp08Time + "\t" + giniErrorUp08Time + "\t" + giniGlobalMiddle005Time + "\t" + giniErrorMiddle005Time + "\t" + giniGlobalMiddle02Time + "\t" + giniErrorMiddle02Time + "\t" + giniGlobalMiddle04Time + "\t" + giniErrorMiddle04Time + "\t" + giniGlobalMiddle08Time + "\t" + giniErrorMiddle08Time + "\t" + globalglobalTime + "\t" + globalErrorTime + "\t" + wombatTime + "\t" + j48Time + "\t" + j48optTime + "\n";
                    writerTime.write(datalineTime);
                    datalineTime = "";

                    datalineSize += dataName.replace("full", "").replace("products", "") + "\t" + giniGlobalUp005Size + "\t" + giniErrorUp005Size + "\t" + giniGlobalUp02Size + "\t" + giniErrorUp02Size + "\t" + giniGlobalUp04Size + "\t" + giniErrorUp04Size + "\t" + giniGlobalUp08Size + "\t" + giniErrorUp08Size + "\t" + giniGlobalMiddle005Size + "\t" + giniErrorMiddle005Size + "\t" + giniGlobalMiddle02Size + "\t" + giniErrorMiddle02Size + "\t" + giniGlobalMiddle04Size + "\t" + giniErrorMiddle04Size + "\t" + giniGlobalMiddle08Size + "\t" + giniErrorMiddle08Size + "\t" + globalglobalSize + "\t" + globalErrorSize + "\t" + wombatSize + "\t" + j48Size + "\t" + j48optSize + "\n";
                    writerSize.write(datalineSize);
                    datalineSize = "";

                } catch (UnsupportedMLImplementationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            writerFMeasure.close();
            writerPrecision.close();
            writerRecall.close();
            writerTime.close();
            writerSize.close();
        }
    }


    public static ACache cacheUnion(ACache a, ACache b) {
        ACache result = new HybridCache();
        for (Instance i : a.getAllInstances()) {
            result.addInstance(i);
        }
        for (Instance i : b.getAllInstances()) {
            result.addInstance(i);
        }
        return result;
    }

    public static String getRandomTargetInstance(ACache source, ACache target, List<String> values, Random random,
                                                 HashMap<String, HashMap<String, Double>> refMap, String sourceInstance, int previousRandom) {
        int randomInt;
        do {
            randomInt = random.nextInt(values.size());
        } while (randomInt == previousRandom);

        String tmpTarget = values.get(randomInt);
        if (refMap.get(sourceInstance).get(tmpTarget) == null && target.getInstance(tmpTarget) != null) {
            return tmpTarget;
        }
        return getRandomTargetInstance(source, target, values, random, refMap, sourceInstance, randomInt);
    }


    public static List<AMapping> generateMappingFolds(AMapping refMap, ACache source, ACache target) {
        return generateMappingFolds(refMap, source, target, true);
    }

    public static List<AMapping> generateMappingFolds(AMapping refMap, ACache source, ACache target, boolean random) {
        Random rand;
        if (random) {
            rand = new Random();
        } else {
            rand = randnum;
        }
        List<AMapping> foldMaps = new ArrayList<>();
        int mapSize = refMap.getMap().keySet().size();
        int foldSize = (int) (mapSize / FOLDS_COUNT);

        Iterator<HashMap<String, Double>> it = refMap.getMap().values().iterator();
        ArrayList<String> values = new ArrayList<String>();
        while (it.hasNext()) {
            for (String t : it.next().keySet()) {
                values.add(t);
            }
        }
        for (int foldIndex = 0; foldIndex < FOLDS_COUNT; foldIndex++) {
            Set<Integer> index = new HashSet<>();
            // get random indexes
            while (index.size() < foldSize) {
                int number;
                do {
                    if (random) {
                        number = (int) (mapSize * Math.random());
                    } else {
                        number = randnum.nextInt(mapSize - 1);
                    }
                } while (index.contains(number));
                index.add(number);
            }
            // get data
            AMapping foldMap = MappingFactory.createDefaultMapping();
            int count = 0;
            for (String key : refMap.getMap().keySet()) {
                if (foldIndex != FOLDS_COUNT - 1) {
                    if (index.contains(count) && count % 2 == 0) {
                        HashMap<String, Double> help = new HashMap<String, Double>();
                        for (String k : refMap.getMap().get(key).keySet()) {
                            help.put(k, 1.0);
                        }
                        foldMap.getMap().put(key, help);
                    } else if (index.contains(count)) {
                        HashMap<String, Double> help = new HashMap<String, Double>();
                        help.put(getRandomTargetInstance(source, target, values, rand, refMap.getMap(), key, -1), 0.0);
                        foldMap.getMap().put(key, help);
                    }
                } else {
                    if (index.contains(count)) {
                        HashMap<String, Double> help = new HashMap<String, Double>();
                        for (String k : refMap.getMap().get(key).keySet()) {
                            help.put(k, 1.0);
                        }
                        foldMap.getMap().put(key, help);
                    }
                }
                count++;
            }

            foldMaps.add(foldMap);
            refMap = removeSubMap(refMap, foldMap);
        }
        int i = 0;
        int odd = 0;
        // if any remaining links in the refMap, then distribute them to all
        // folds
        for (String key : refMap.getMap().keySet()) {
            if (i != FOLDS_COUNT - 1) {
                if (odd % 2 == 0) {
                    HashMap<String, Double> help = new HashMap<String, Double>();
                    for (String k : refMap.getMap().get(key).keySet()) {
                        help.put(k, 1.0);
                    }
                    foldMaps.get(i).add(key, help);
                } else {

                    HashMap<String, Double> help = new HashMap<String, Double>();
                    help.put(getRandomTargetInstance(source, target, values, rand, refMap.getMap(), key, -1), 0.0);
                    foldMaps.get(i).add(key, help);
                }
            } else {
                HashMap<String, Double> help = new HashMap<String, Double>();
                for (String k : refMap.getMap().get(key).keySet()) {
                    help.put(k, 1.0);
                }
                foldMaps.get(i).add(key, help);

            }
            odd++;
            i = (i + 1) % FOLDS_COUNT;
        }
        return foldMaps;
    }

    public static AMapping removeSubMap(AMapping mainMap, AMapping subMap) {
        AMapping result = MappingFactory.createDefaultMapping();
        double value = 0;
        for (String mainMapSourceUri : mainMap.getMap().keySet()) {
            for (String mainMapTargetUri : mainMap.getMap().get(mainMapSourceUri).keySet()) {
                if (!subMap.contains(mainMapSourceUri, mainMapTargetUri)) {
                    result.add(mainMapSourceUri, mainMapTargetUri, value);
                }
            }
        }
        return result;
    }

    protected static AMapping removeLinksWithNoInstances(AMapping map, ACache source, ACache target) {
        AMapping result = MappingFactory.createDefaultMapping();
        for (String s : map.getMap().keySet()) {
            for (String t : map.getMap().get(s).keySet()) {
                if (source.containsUri(s) && target.containsUri(t)) {
                    result.add(s, t, map.getMap().get(s).get(t));
                }
            }
        }
        return result;
    }

}
