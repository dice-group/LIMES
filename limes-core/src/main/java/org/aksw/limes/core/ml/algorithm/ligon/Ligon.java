package org.aksw.limes.core.ml.algorithm.ligon;

import com.google.common.io.Files;
import org.aksw.limes.core.datastrutures.GoldStandard;
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
import org.aksw.limes.core.ml.algorithm.*;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

@SuppressWarnings("Duplicates")
public class Ligon {
    static Logger logger = LoggerFactory.getLogger(Ligon.class);

    String resultStr = "";

    protected List<NoisyOracle> blackBoxOracles;
    protected List<NoisyOracle> estimatedOracles;

    protected AMapping posExamplesMap = MappingFactory.createDefaultMapping();
    protected AMapping negExamplesMap = MappingFactory.createDefaultMapping();
    private File logOut = null;
    private int iteration = 1;

    public enum ODDS {
        HARD, RANDOM, EQUIVALENCE, APPROXIMATE
    }

    double randomOddL = 0.5;

    ACache sourceTrainCache = null;
    ACache targetTrainCache = null;

    // Olny for evaluation
    ACache fullSourceCache = null;
    ACache fullTargetCache = null;

    ACache sourceTestingCache = null;
    ACache targetTestingCache = null;

    AMapping testReferenceMap = null;
    AMapping fullReferenceMap = null;

    protected ActiveMLAlgorithm activeWombat = null;
    protected double wombatBestFmeasure = 1.0;


    /**
     * Simplest contractor
     *
     * @param trainigExamplesMap
     * @param sourceTrainCache
     * @param targetTrainCache
     * @param blackBoxOracles
     */
    public Ligon(AMapping trainigExamplesMap,
                 ACache sourceTrainCache,
                 ACache targetTrainCache,
                 List<NoisyOracle> blackBoxOracles) {
        super();
        this.blackBoxOracles = blackBoxOracles;
        //        this.estimatedOracles = FixedSizeList.decorate(Arrays.asList(new NoisyOracle[blackBoxOracles.size()]));
        this.sourceTrainCache = sourceTrainCache;
        this.targetTrainCache = targetTrainCache;
        this.estimatedOracles = new ArrayList<>();
        for (NoisyOracle o : blackBoxOracles) {
            this.estimatedOracles.add(new NoisyOracle(null, new ConfusionMatrix(0.5d)));
        }
    }

    /**
     * Complex constructor for evaluation
     *
     * @param trainigExamplesMap
     * @param sourceTrainCache
     * @param targetTrainCache
     * @param blackBoxOracles
     */
    public Ligon(AMapping trainigExamplesMap,
                 ACache sourceTrainCache,
                 ACache targetTrainCache,
                 List<NoisyOracle> blackBoxOracles,
                 ACache fullSourceCache,
                 ACache fullTargetCache,
                 AMapping fullReferenceMapping) {
        this(trainigExamplesMap, sourceTrainCache, targetTrainCache, blackBoxOracles);
        this.fullSourceCache = fullSourceCache;
        this.fullTargetCache = fullTargetCache;
        this.fullReferenceMap = fullReferenceMapping;
    }


    public Ligon(AMapping trainigExamplesMap,
                 ACache sourceTrainCache,
                 ACache targetTrainCache,
                 List<NoisyOracle> blackBoxOracles,
                 ACache fullSourceCache,
                 ACache fullTargetCache,
                 AMapping fullReferenceMapping,
                 ACache sourceTestingCache,
                 ACache targetTestingCache,
                 AMapping testReferenceMap) {
        this(trainigExamplesMap, sourceTrainCache, targetTrainCache, blackBoxOracles, fullSourceCache, fullTargetCache, fullReferenceMapping);
        this.sourceTestingCache = sourceTestingCache;
        this.targetTestingCache = targetTestingCache;
        this.testReferenceMap = testReferenceMap;
    }


    /**
     * Process 1 link by all black box oracles
     *
     * @param subject
     * @param object
     * @return vector of all black box oracles results
     */
    protected List<Boolean> getOracleFeedback(String subject, String object) {
        List<Boolean> result = new ArrayList<>();
        for (int i = 0; i < blackBoxOracles.size(); i++) {
            result.set(i, blackBoxOracles.get(i).predict(subject, object));
        }
        return result;
    }


    public void updateOraclesConfusionMatrices(AMapping labeledExamples) {
        for (String subject : labeledExamples.getMap().keySet()) {
            for (String object : labeledExamples.getMap().get(subject).keySet()) {
                double confidence = labeledExamples.getConfidence(subject, object);
                for (int i = 0; i < blackBoxOracles.size(); i++) {
                    if (confidence == 1.0d) { //positive example
                        if (blackBoxOracles.get(i).predict(subject, object)) { //true prediction
                            estimatedOracles.get(i).confusionMatrix.incrementRightClassifiedPositiveExamplesCount();
                        } else { //false prediction
                            estimatedOracles.get(i).confusionMatrix.incrementWrongClassifiedPositiveExamplesCount();
                        }
                    } else if (confidence == 0.0d) { //negative example
                        if (blackBoxOracles.get(i).predict(subject, object)) { //true prediction
                            estimatedOracles.get(i).confusionMatrix.incrementWrongClassifiedNegativeExamplesCount();
                        } else { //false prediction
                            estimatedOracles.get(i).confusionMatrix.incrementRightClassifiedNegativeExamplesCount();
                        }
                    }
                }
            }
        }
        logConfusionMatrixes();
    }

    private void logConfusionMatrixes() {
        try {
            if (logOut == null) {
                logOut = new File("./" + EvaluateLigon.datasetName + "_" + blackBoxOracles.size() + "_cm.txt");
                String out = "it";
                for (int i = 0; i < blackBoxOracles.size(); i++) {
                    out += "\tbb"+i+"-TT\tbb"+i+"-TF\tbb"+i+"-FT\tbb"+i+"-FF\test"+i+"-TT\test"+i+"-TF\test"+i+"-FT\test"+i+"-FF";
                }
                Files.write(out + "\n", logOut, Charset.defaultCharset());
            } else {
                String out = iteration + "";
                for (int i = 0; i < blackBoxOracles.size(); i++) {
                    ConfusionMatrix bb = blackBoxOracles.get(i).confusionMatrix;
                    ConfusionMatrix est = estimatedOracles.get(i).confusionMatrix;
                    out += "\t" + bb.getRightClassifiedPositiveExamplesProbability()
                            + "\t" + bb.getWrongClassifiedPositiveExamplesProbability()
                            + "\t" + bb.getWrongClassifiedNegativeExamplesProbability()
                            + "\t" + bb.getRightClassifiedNegativeExamplesProbability()
                            + "\t" + est.getRightClassifiedPositiveExamplesProbability()
                            + "\t" + est.getWrongClassifiedPositiveExamplesProbability()
                            + "\t" + est.getWrongClassifiedNegativeExamplesProbability()
                            + "\t" + est.getRightClassifiedNegativeExamplesProbability();
                }
                Files.append(out + "\n", logOut, Charset.defaultCharset());
                iteration++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double computeOdds(String subject, String object, ODDS odds) {
        double result = 0.0d;
        for (int i = 0; i < estimatedOracles.size(); i++) {
            result +=
                    Math.log(estimatedOracles.get(i).confusionMatrix.getRightClassifiedPositiveExamplesProbability() +
                            estimatedOracles.get(i).confusionMatrix.getWrongClassifiedPositiveExamplesProbability()) -
                            Math.log(estimatedOracles.get(i).confusionMatrix.getRightClassifiedNegativeExamplesProbability() +
                            estimatedOracles.get(i).confusionMatrix.getWrongClassifiedNegativeExamplesProbability());
            logger.info("T=" + (estimatedOracles.get(i).confusionMatrix.getRightClassifiedPositiveExamplesProbability() + estimatedOracles.get(i).confusionMatrix.getWrongClassifiedPositiveExamplesProbability()));
            logger.info("F=" + (estimatedOracles.get(i).confusionMatrix.getWrongClassifiedNegativeExamplesProbability() + estimatedOracles.get(i).confusionMatrix.getRightClassifiedNegativeExamplesProbability()));
            logger.info("result=" + result);
        }
//        result = result / (double) estimatedOracles.size();
        double oddsL = 1.0d;
        switch (odds) {
            case APPROXIMATE:
                oddsL = wombatBestFmeasure;
                break;
            case EQUIVALENCE:
                double minKbSize = (sourceTrainCache.size() < targetTrainCache.size()) ? sourceTrainCache.size() : targetTrainCache.size();
                oddsL = minKbSize / (double) (sourceTrainCache.size() * targetTrainCache.size() - minKbSize);
                break;
            case RANDOM:
                oddsL = randomOddL;
                break;
            case HARD:
            default:
                oddsL = 1.0d;
                break;
        }
        return result + Math.log(oddsL);
    }

    /**
     * Classify unlabeled examples to be positive or negative ones
     *
     * @param unlabeledexamples
     * @param k
     * @param odds
     * @return mapping of positive and negative examples, where
     * positive examples have confidence score of 1d and
     * negative examples have a confidence score of 0d
     */
    protected AMapping classifyUnlabeledExamples(AMapping unlabeledexamples, double k, ODDS odds) {
        AMapping labeledExamples = MappingFactory.createDefaultMapping();
        for (String subject : unlabeledexamples.getMap().keySet()) {
            for (String object : unlabeledexamples.getMap().get(subject).keySet()) {
                double OddsValue = computeOdds(subject, object, odds);
                logger.info("odds=" + OddsValue + ", k=" + Math.log(k) + ", " + (OddsValue > Math.log(k)));
                    if (OddsValue > Math.log(k)) {
                    labeledExamples.add(subject, object, 1.0d);
                } else if (OddsValue < (1 / Math.log(k))) {
                    labeledExamples.add(subject, object, 0.0d);
                }
            }
        }
        return labeledExamples;
    }


    //    public AMapping learn(AMapping labeledExamples,double k, ODDS odds,
    //            int mostInformativeExamplesCount) throws UnsupportedMLImplementationException{
    public String learn(AMapping labeledExamples, double k, ODDS odds,
                        int mostInformativeExamplesCount) throws UnsupportedMLImplementationException {
        fillTrainingCaches(labeledExamples);
        initActiveWombat(sourceTrainCache, targetTrainCache);
        int i = 0;
        AMapping newLabeledExamples = labeledExamples;
        do {
            updateOraclesConfusionMatrices(newLabeledExamples);
            AMapping mostInformativeExamples = getWombatMostInformativeExamples(labeledExamples, mostInformativeExamplesCount);
            newLabeledExamples = classifyUnlabeledExamples(mostInformativeExamples, k, odds);
            labeledExamples = MappingOperations.union(labeledExamples, newLabeledExamples);
            fillTrainingCaches(labeledExamples);
            i++;
        } while (i < 10);
        //        System.out.println(resultStr);
        return resultStr;
        //        return labeledExamples;
    }

    /**
     * Initializes active Wombat
     *
     * @return MLResults
     * @throws UnsupportedMLImplementationException
     */
    protected MLResults initActiveWombat(ACache sourceCache, ACache targetCache) throws UnsupportedMLImplementationException {

        try {
            activeWombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
                    MLImplementationType.SUPERVISED_ACTIVE).asActive();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (activeWombat.getClass().equals(ActiveMLAlgorithm.class));
        activeWombat.init(null, sourceCache, targetCache);
        //        activeWombat.init(null, sourcePredictionCache, targetPredictionCache);
        return null;
        //                activeWombat.activeLearn();
    }

    /**
     * @param labeledExamples
     * @param mostInformativeExamplesCount
     * @return
     * @throws UnsupportedMLImplementationException
     */
    protected AMapping getWombatMostInformativeExamples(AMapping labeledExamples, int mostInformativeExamplesCount) throws UnsupportedMLImplementationException {
        MLResults mlModel = activeWombat.activeLearn(labeledExamples);
        wombatBestFmeasure = mlModel.getQuality();
        AMapping learnedMap = activeWombat.predict(sourceTrainCache, targetTrainCache, mlModel);
        computePerformanceIndicatorsWombat(labeledExamples, learnedMap, mlModel);
        ((AWombat) activeWombat.getMl()).updateActiveLearningCaches(sourceTrainCache, targetTrainCache, fullSourceCache, fullTargetCache);
        return activeWombat.getNextExamples(mostInformativeExamplesCount);
    }


    /**
     * Computes performance indicators for Active Wombat
     * i.e. compute precision, recall and F-measure for training and testing phases
     * NOTE: for evaluation purpose only
     *
     * @param labeledExamples
     * @param learnedMap
     * @param mlModel
     * @return result String
     */
    private String computePerformanceIndicatorsWombat(AMapping labeledExamples, AMapping learnedMap, MLResults mlModel) {

        //PIs for training data
        resultStr += String.format("%.2f", new Precision().calculate(learnedMap, new GoldStandard(labeledExamples))) + "\t" +
                String.format("%.2f", new Recall().calculate(learnedMap, new GoldStandard(labeledExamples))) + "\t" +
                String.format("%.2f", new FMeasure().calculate(learnedMap, new GoldStandard(labeledExamples))) + "\t";

        //PIs for whole KB
        long start = System.currentTimeMillis();
        AMapping kbMap;
        LinkSpecification linkSpecification = mlModel.getLinkSpecification();

        Rewriter rw = RewriterFactory.getDefaultRewriter();
        LinkSpecification rwLs = rw.rewrite(linkSpecification);
        IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, fullSourceCache, fullTargetCache);
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, fullSourceCache, fullTargetCache, "?x", "?y");
        AMapping fullResultMap = engine.execute(rwLs, planner);
        kbMap = fullResultMap.getSubMap(linkSpecification.getThreshold());
        GoldStandard fullRefgoldStandard = new GoldStandard(fullReferenceMap);
        resultStr +=
                String.format("%.2f", new Precision().calculate(kbMap, fullRefgoldStandard)) + "\t" +
                        String.format("%.2f", new Recall().calculate(kbMap, fullRefgoldStandard)) + "\t" +
                        String.format("%.2f", new FMeasure().calculate(kbMap, fullRefgoldStandard)) + "\t";
        planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sourceTestingCache, targetTestingCache);
        engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceTestingCache, targetTestingCache, "?x", "?y");
        fullResultMap = engine.execute(rwLs, planner);
        kbMap = fullResultMap.getSubMap(linkSpecification.getThreshold());
        fullRefgoldStandard = new GoldStandard(testReferenceMap);
        resultStr += String.format("%.2f", new Precision().calculate(kbMap, fullRefgoldStandard)) + "\t" +
                String.format("%.2f", new Recall().calculate(kbMap, fullRefgoldStandard)) + "\t" +
                String.format("%.2f", new FMeasure().calculate(kbMap, fullRefgoldStandard)) + "\t" + computeMSE() + "\t" +
                linkSpecification.toStringOneLine() +
                "\n";

        return resultStr;
    }

    private String computeMSE() {
        double tt=0, tf=0, ft=0, ff=0;
        double x;
        ConfusionMatrix a,b;
        double size = estimatedOracles.size();
        for (int i=0; i<estimatedOracles.size(); i++) {
            a = estimatedOracles.get(i).confusionMatrix;
            b = blackBoxOracles.get(i).confusionMatrix;
            x = a.getRightClassifiedPositiveExamplesProbability() - b.getRightClassifiedPositiveExamplesProbability();
            tt += x*x;
            x = a.getWrongClassifiedPositiveExamplesProbability() - b.getWrongClassifiedPositiveExamplesProbability();
            ft += x*x;
            x = a.getRightClassifiedNegativeExamplesProbability() - b.getRightClassifiedNegativeExamplesProbability();
            tf += x*x;
            x = a.getWrongClassifiedNegativeExamplesProbability() - b.getWrongClassifiedNegativeExamplesProbability();
            ff += x*x;
        }
        return String.format("%.4f", tt/size) + "\t" +
                String.format("%.4f", tf/size) + "\t" +
                String.format("%.4f", ft/size) + "\t" +
                String.format("%.4f", ff/size);
    }


    /**
     * Extract the source and target training cache instances based on the input learnMap
     *
     * @param trainMap
     *         to be used for training caches filling
     * @author sherif
     */
    protected void fillTrainingCaches(AMapping trainMap) {
        sourceTrainCache = new HybridCache();
        targetTrainCache = new HybridCache();
        for (String s : trainMap.getMap().keySet()) {
            if (fullSourceCache.containsUri(s)) {
                sourceTrainCache.addInstance(fullSourceCache.getInstance(s));
                for (String t : trainMap.getMap().get(s).keySet()) {
                    if (fullTargetCache.containsUri(t)) {
                        targetTrainCache.addInstance(fullTargetCache.getInstance(t));
                    } else {
                        logger.warn("Instance " + t + " not exist in the target dataset");
                    }
                }
            } else {
                logger.warn("Instance " + s + " not exist in the source dataset");
            }
        }
    }


}
