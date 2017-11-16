package org.aksw.limes.core.ml.algorithm.ligon;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
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
import org.aksw.limes.core.ml.algorithm.ligon.Ligon.ODDS;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Evaluate Refinement based LGG for benchmark datasets
 * DBLP-ACM, Abt-Buy,Amazon-GoogleProducts, DBLP-Scholar,
 * Person1, Person2, Restaurants, DBLP-LinkedMDB and Dailymed-DrugBank
 *
 * @author sherif
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 */

@SuppressWarnings("Duplicates")
public class EvaluateLigon {
    /**
     *
     */
    private static final Logger logger = Logger.getLogger(EvaluateLigon.class);

    public static AMapping fullReferenceMapping;
    public static String resultStr = "fold\tk\toracles\todds\titNr\tpTr\trTr\tfTr\tpF\trF\tfF\tpTe\trTe\tfTe\tTT_MSE\tTF_MSE\tFT_MSE\tFF_MSE\tls";
    public static String reliableResultStr = "fold\titNr\tpTr\trTr\tfTr\tpF\trF\tfF\tpTe\trTe\tfTe\tls";
    public static int fold = 1;


    /**
     * @param args
     * @throws UnsupportedMLImplementationException
     * @author sherif
     */
    public static void main(String[] args) throws UnsupportedMLImplementationException {
        List<ODDS> oddsList = Arrays.asList(ODDS.HARD, ODDS.EQUIVALENCE, ODDS.APPROXIMATE);
        int mostInformativeExaplesCount = 10;
        // get training data
        String datasetName = args[1];
        EvaluationData data = DataSetChooser.getData(datasetName);
        ACache fullSourceCache = data.getSourceCache();
        ACache fullTargetCache = data.getTargetCache();
        fullReferenceMapping = removeLinksWithNoInstances(data.getReferenceMapping(), fullSourceCache, fullTargetCache);
        List<AMapping> subSets = generateEvaluationSets(fullReferenceMapping);
        for (int i = 0; i < 10; i++) {
            fold = i+1;
            AMapping testSet = subSets.get(i);
            testSet.getReversedMap();
            AMapping learningPool = getLearningPool(subSets, i);
            learningPool.getReversedMap();
            fullReferenceMapping = learningPool;
            List<ACache> learning = reduceCaches(learningPool, fullSourceCache, fullTargetCache);
            List<ACache> testing = reduceCaches(testSet, fullSourceCache, fullTargetCache);
            AMapping trainingMap = sampleReferenceMap(learningPool, 10);

            switch (Integer.valueOf(args[0])) {
                case 1:
                    // 1. series of experiments: find best k
                    evaluateLigonWithReliableOracleForDataset(trainingMap, testing.get(0), testing.get(1), learning.get(0), learning.get(1), testSet, learningPool);
                    for (int oracles = 2; oracles <= 16; oracles *= 2) {
                        for (int k = 2; k <= 16; k *= 2) {
                            evaluateLigonForDataset(k, getNoisyOracles(oracles, 0.75d, 1.0d), ODDS.EQUIVALENCE, trainingMap, testing.get(0), testing.get(1), learning.get(0), learning.get(1), testSet, learningPool);
                        }
                    }
                    break;
                case 2:
                    // 2. series of experiments: find best model
                    evaluateLigonWithReliableOracleForDataset(trainingMap, testing.get(0), testing.get(1), learning.get(0), learning.get(1), testSet, learningPool);
                    for (ODDS odds : oddsList) {
                        for (int oracles = 2; oracles <= 16; oracles *= 2) {
                            evaluateLigonForDataset(Integer.valueOf(args[2]), getNoisyOracles(oracles, 0.75d, 1.0d), odds, trainingMap, testing.get(0), testing.get(1), learning.get(0), learning.get(1), testSet, learningPool);
                        }
                    }
                    break;
                case 3:
                    // 3. series of experiment: measure robustness
                    // baseline:
                    evaluateLigonWithReliableOracleForDataset(trainingMap, testing.get(0), testing.get(1), learning.get(0), learning.get(1), testSet, learningPool);
                    int k = Integer.valueOf(args[2]);
                    ODDS odds = oddsList.get(Integer.valueOf(args[3]));
                    List<Double> meanList = Arrays.asList(0.75d, 0.5d, 0.25d);
                    List<Double> stddevList = Arrays.asList(0.5d, 1.0d);
                    for (Double mean : meanList) {
                        for (Double stddev : stddevList) {
                            for (int oracles = 2; oracles <= 16; oracles *= 2) {
                                evaluateLigonForDataset(k, getNoisyOracles(oracles, mean, stddev), odds, trainingMap, testing.get(0), testing.get(1), learning.get(0), learning.get(1), testSet, learningPool);
                            }
                        }
                    }
                    break;
                case 4:
//                    evaluateLigonWithReliableOracleForDataset(trainingMap, testing.get(0), testing.get(1), learning.get(0), learning.get(1), testSet, learningPool);
                    List<NoisyOracle> oracles = new ArrayList<>(1);
                    oracles.add(new NoisyOracle(fullReferenceMapping, new ConfusionMatrix(new double[][]{new double[]{0.4, 0.4}, new double[]{0.1, 0.1}})));
                    oracles.add(new NoisyOracle(fullReferenceMapping, new ConfusionMatrix(new double[][]{new double[]{0.4, 0.4}, new double[]{0.1, 0.1}})));
                    oracles.add(new NoisyOracle(fullReferenceMapping, new ConfusionMatrix(new double[][]{new double[]{0.4, 0.4}, new double[]{0.1, 0.1}})));
                    oracles.add(new NoisyOracle(fullReferenceMapping, new ConfusionMatrix(new double[][]{new double[]{0.4, 0.4}, new double[]{0.1, 0.1}})));
                    oracles = getNoisyOracles(4, 0.75d, 0.5d);
                    evaluateLigonForDataset(Integer.valueOf(args[2]), oracles, ODDS.EQUIVALENCE, trainingMap, testing.get(0), testing.get(1), learning.get(0), learning.get(1), testSet, learningPool);
            }
            System.out.println("----- partly results -----\n" + resultStr);
        }
        System.out.println("----- final results -----\n" + resultStr);
        try {
            Files.write(resultStr, new File("./" + datasetName + "_" + args[0] + "_result.txt"), Charset.defaultCharset());
            Files.write(reliableResultStr, new File("./" + datasetName + "_" + args[0] + "_result_reliable.txt"), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void evaluateLigonWithReliableOracleForDataset(AMapping trainingMap, ACache sourceTestingCache, ACache targetTestingCache, ACache fullSourceCache, ACache fullTargetCache, AMapping testReferenceMap, AMapping referenceMapping) throws UnsupportedMLImplementationException {

        int mostInformativeExaplesCount = 10;
        trainingMap.getReversedMap();

        ReliableOracle oracle = new ReliableOracle(fullReferenceMapping);

        TrustedLigon ligon = new TrustedLigon(trainingMap, null, null, oracle,
                fullSourceCache, fullTargetCache, referenceMapping, sourceTestingCache, targetTestingCache, testReferenceMap);

        String run = ligon.learn(trainingMap, mostInformativeExaplesCount);
        String[] runs = run.split("\n");
        int i = 1;
        for (String s : runs) {
            if (!s.trim().equals("")) {
                reliableResultStr += fold + "\t" + i + "\t" + s + "\n";
                i++;
            }
        }
    }

    public static void evaluateLigonForDataset(int k, List<NoisyOracle> noisyOracles, ODDS odds, AMapping trainingMap, ACache sourceTestingCache, ACache targetTestingCache, ACache fullSourceCache, ACache fullTargetCache, AMapping testReferenceMap, AMapping referenceMapping) throws UnsupportedMLImplementationException {
        // evaluation parameters
        int mostInformativeExaplesCount = 10;
        trainingMap.getReversedMap();

        System.out.println("\n\n ---- noisyOracles ----\n" + noisyOracles);

        // initialize ligon
        Ligon ligon = new Ligon(trainingMap, null, null, noisyOracles,
                fullSourceCache, fullTargetCache, referenceMapping, sourceTestingCache, targetTestingCache, testReferenceMap);

        String run = ligon.learn(trainingMap, k, odds, mostInformativeExaplesCount);
        String[] runs = run.split("\n");
        int i = 1;
        for (String s : runs) {
            if (!s.trim().equals("")) {
                resultStr += fold + "\t" + k + "\t" + noisyOracles.size() + "\t" + odds.name() + "\t" + i + "\t" + s + "\n";
                i++;
            }
        }


    }

    private static List<NoisyOracle> getNoisyOracles(int noisyOracleCount, double mean, double stddev) {
        // create noisy oracles with normal distribution
        List<NoisyOracle> noisyOracles = new ArrayList<>();
        Random pTT = new Random();
        Random pTF = new Random();
        Random pFT = new Random();
        Random pFF = new Random();
        double rPTT, rPTF, rPFT, rPFF;
        for (int i = 0; i < noisyOracleCount; i++) {
            do {
                rPTT = mean + (pTT.nextGaussian() * stddev);
            }
            while (rPTT < 0 || rPTT > 1);
            do {
                rPTF = mean + (pTF.nextGaussian() * stddev);
            }
            while (rPTF < 0 || rPTF > 1);
            do {
                rPFT = mean + (pFT.nextGaussian() * stddev);
            }
            while (rPFT < 0 || rPFT > 1);
            do {
                rPFF = mean + (pFF.nextGaussian() * stddev);
            }
            while (rPFF < 0 || rPFF > 1);
            double sumR = rPTT + rPTF + rPFT + rPFF;
            noisyOracles.add(new NoisyOracle(fullReferenceMapping,
                    new ConfusionMatrix(new double[][]{{rPTT / sumR, rPTF / sumR}, {rPFT / sumR, rPFF / sumR}})));
        }
        return noisyOracles;
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



    static String executeLinkSpecs(LinkSpecification linkSpecification, ACache sourceCache, ACache targetCache) {
        long start = System.currentTimeMillis();
        AMapping kbMap;
        Rewriter rw = RewriterFactory.getDefaultRewriter();
        LinkSpecification rwLs = rw.rewrite(linkSpecification);
        IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sourceCache, targetCache);
        assert planner != null;
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache, targetCache, "?x", "?y");
        assert engine != null;
        AMapping resultMap = engine.execute(rwLs, planner);
        kbMap = resultMap.getSubMap(linkSpecification.getThreshold());
        resultStr = precision(kbMap, fullReferenceMapping) + "\t" +
                recall(kbMap, fullReferenceMapping) + "\t" +
                fScore(kbMap, fullReferenceMapping) + "\t" +
                (System.currentTimeMillis() - start) + "\n";
        return resultStr;
    }

    public static AMapping generateNegativeExamples(AMapping posExamplesMapping, int size) {
        AMapping negativeExampleMapping = MappingFactory.createDefaultMapping();
        int i = 0;
        List<String> sourceUris = new ArrayList<>(posExamplesMapping.getMap().keySet());
        List<String> targetUris = new ArrayList<>();
        for (String s : posExamplesMapping.getMap().keySet()) {
            targetUris.addAll(posExamplesMapping.getMap().get(s).keySet());
        }
        Random random = new Random();
        do {
            String randomSourceUri, randomTargetUri;

            do {
                randomSourceUri = sourceUris.get(random.nextInt(sourceUris.size()));
                randomTargetUri = targetUris.get(random.nextInt(targetUris.size()));
            } while (posExamplesMapping.contains(randomSourceUri, randomTargetUri));
            negativeExampleMapping.add(randomSourceUri, randomTargetUri, 0.0);
            i++;
        } while (i < size);
        return negativeExampleMapping;
    }


    public static AMapping sampleReferenceMap(AMapping reference, double fraction) {
        if (fraction == 1) {
            return reference;
        }
        //		int mapSize = reference.size();
        if (fraction > 1) {
            fraction = 1 / fraction;
        }
        int size = (int) (reference.getMap().keySet().size() * fraction);
        return sampleReferenceMap(reference, size);
    }


    public static AMapping sampleReferenceMap(AMapping reference, int size) {
        Set<Integer> index = new HashSet<>();
        //get random indexes
        for (int i = 0; i < size; i++) {
            int number;
            do {
                number = (int) (reference.getMap().keySet().size() * Math.random());
            } while (index.contains(number));
            index.add(number);
        }

        //get data
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

    public static DataSets toDataset(String d) {
        if (d.equalsIgnoreCase("DBLP-ACM")) {
            return (DataSets.DBLPACM);
        } else if (d.equalsIgnoreCase("Abt-Buy")) {
            return (DataSets.ABTBUY);
        } else if (d.equalsIgnoreCase("Amazon-GoogleProducts")) {
            return (DataSets.AMAZONGOOGLEPRODUCTS);
        } else if (d.equalsIgnoreCase("DBLP-Scholar")) {
            return (DataSets.DBLPSCHOLAR);
        } else if (d.equalsIgnoreCase("Person1")) {
            return (DataSets.PERSON1);
        } else if (d.equalsIgnoreCase("Person2")) {
            return (DataSets.PERSON2);
        } else if (d.equalsIgnoreCase("Restaurants")) {
            return (DataSets.RESTAURANTS);
        } else if (d.equalsIgnoreCase("Restaurants_CSV")) {
            return (DataSets.RESTAURANTS_CSV);
        } else if (d.equalsIgnoreCase("DBPLINKEDMDB")) {
            return (DataSets.DBPLINKEDMDB);
        } else if (d.equalsIgnoreCase("Dailymed-DrugBank")) {
            return (DataSets.DRUGS);
        } else {
            System.out.println("Experiment " + d + " Not implemented yet");
            System.exit(1);
        }
        return null;
    }


    protected static double recall(AMapping map, AMapping ref) {
        return new Recall().calculate(map, new GoldStandard(ref));
    }

    static double fScore(AMapping map, AMapping ref) {
        return new FMeasure().calculate(map, new GoldStandard(ref));
    }


    protected static double precision(AMapping map, AMapping ref) {
        return new Precision().calculate(map, new GoldStandard(ref));
    }


    private static List<AMapping> generateEvaluationSets(AMapping fullReferenceMapping) {
        AMapping localRef = fullReferenceMapping.getSubMap(0.0d);
        List<AMapping> result = new ArrayList<>(10);
        for (int i=0; i<9; i++) {
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
