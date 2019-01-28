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
