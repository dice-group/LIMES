package org.aksw.limes.core.ml.algorithm.ligon;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

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
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement the idea of active learning with a trusted oracle
 * Just for evaluation purposes 
 * 
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 *
 */
@SuppressWarnings("Duplicates")
public class TrustedLigon {
    static Logger logger = LoggerFactory.getLogger(TrustedLigon.class);

    String resultStr =  "" ;

    protected ReliableOracle oracle;

    protected AMapping posExamplesMap = MappingFactory.createDefaultMapping();   
    protected AMapping negExamplesMap = MappingFactory.createDefaultMapping();

    public enum ODDS {
        HARD, RANDOM, EQUIVALENCE, APPROXIMATE
    }


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
     */
    public TrustedLigon(AMapping trainigExamplesMap, 
            ACache sourceTrainCache, 
            ACache targetTrainCache, 
            ReliableOracle oracle) {
        super();
        this.oracle = oracle;
        //        this.estimatedOracles = FixedSizeList.decorate(Arrays.asList(new NoisyOracle[blackBoxOracles.size()]));
        this.sourceTrainCache = sourceTrainCache;
        this.targetTrainCache = targetTrainCache;
    }

    /**
     * Complex constructor for evaluation
     * 
     * @param trainigExamplesMap
     * @param sourceTrainCache
     * @param targetTrainCache
     */
    public TrustedLigon(AMapping trainigExamplesMap, 
            ACache sourceTrainCache, 
            ACache targetTrainCache, 
            ReliableOracle oracle,
            ACache fullSourceCache, 
            ACache fullTargetCache, 
            AMapping fullReferenceMapping) {
        this(trainigExamplesMap, sourceTrainCache, targetTrainCache, oracle);
        this.fullSourceCache = fullSourceCache;
        this.fullTargetCache = fullTargetCache;
        this.fullReferenceMap = fullReferenceMapping;
    }


    public TrustedLigon(AMapping trainigExamplesMap,
                 ACache sourceTrainCache,
                 ACache targetTrainCache,
                 ReliableOracle oracle,
                 ACache fullSourceCache,
                 ACache fullTargetCache,
                 AMapping fullReferenceMapping,
                 ACache sourceTestingCache,
                 ACache targetTestingCache,
                 AMapping testReferenceMap) {
        this(trainigExamplesMap, sourceTrainCache, targetTrainCache, oracle, fullSourceCache, fullTargetCache, fullReferenceMapping);
        this.sourceTestingCache = sourceTestingCache;
        this.targetTestingCache = targetTestingCache;
        this.testReferenceMap = testReferenceMap;
    }



    /**
     * Classify unlabeled examples to be positive or negative ones
     * 
     * @param unlabeledexamples
     * @return mapping of positive and negative examples, where
     *         positive examples have confidence score of 1d and
     *         negative examples have a confidence score of 0d 
     */
    protected AMapping classifyUnlabeledExamples(AMapping unlabeledexamples){
        AMapping labeledExamples = MappingFactory.createDefaultMapping(); 
        for (String subject : unlabeledexamples.getMap().keySet()) {
            for (String object : unlabeledexamples.getMap().get(subject).keySet()) {
                if(oracle.predict(subject, object)){
                    labeledExamples.add(subject, object, 1.0d);
                }else {
                    //                    labeledExamples.add(subject, object, 0.0d); //TODO add later
                }
            }
        }
        return labeledExamples;
    }


    //    public AMapping learn(AMapping labeledExamples,double k, ODDS odds,
    //            int mostInformativeExamplesCount) throws UnsupportedMLImplementationException{
    public String learn(AMapping labeledExamples, int mostInformativeExamplesCount) throws UnsupportedMLImplementationException{
        fillTrainingCaches(labeledExamples);
        initActiveWombat(sourceTrainCache, targetTrainCache);
        int i = 0;
        do{
            AMapping mostInformativeExamples = getWombatMostInformativeExamples(labeledExamples, mostInformativeExamplesCount);
            System.out.println("mostInformativeExamples: " + mostInformativeExamples);
            AMapping newLabeledExamples = classifyUnlabeledExamples(mostInformativeExamples);
            labeledExamples = MappingOperations.union(labeledExamples, newLabeledExamples);
            fillTrainingCaches(labeledExamples);
            i++;
        }while(i < 10);
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
    protected MLResults initActiveWombat(ACache sourceCache , ACache  targetCache) throws UnsupportedMLImplementationException {

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
        AMapping learnedMap = activeWombat.predict(sourceTrainCache, targetTrainCache, mlModel);
        computePerformanceIndicatorsWombat(labeledExamples, learnedMap, mlModel);
        ((AWombat)activeWombat.getMl()).updateActiveLearningCaches(sourceTrainCache, targetTrainCache, fullSourceCache, fullTargetCache);
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

//        //PIs for training data
//        resultStr += String.format("%.2f", new Precision().calculate(learnedMap, new GoldStandard(labeledExamples)))+ "\t" +
//                String.format("%.2f",new Recall().calculate(learnedMap, new GoldStandard(labeledExamples)))   + "\t" +
//                String.format("%.2f",new FMeasure().calculate(learnedMap, new GoldStandard(labeledExamples)))   + "\t" ;
//
//        //PIs for whole KB
//        long start = System.currentTimeMillis();
//        AMapping kbMap;
//        LinkSpecification linkSpecification = mlModel.getLinkSpecification();
//
//        Rewriter rw = RewriterFactory.getDefaultRewriter();
//        LinkSpecification rwLs = rw.rewrite(linkSpecification);
//        IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, fullSourceCache, fullTargetCache);
//        assert planner != null;
//        ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, fullSourceCache, fullTargetCache, "?x", "?y");
//        assert engine != null;
//        AMapping fullResultMap = engine.execute(rwLs, planner);
//        kbMap = fullResultMap.getSubMap(linkSpecification.getThreshold());
//        GoldStandard fullRefgoldStandard = new GoldStandard(fullReferenceMap);
//        resultStr += linkSpecification.toStringOneLine() + "\t" +
//                String.format("%.2f", new Precision().calculate(fullResultMap, fullRefgoldStandard))+ "\t" +
//                String.format("%.2f",new Recall().calculate(fullResultMap, fullRefgoldStandard))   + "\t" +
//                String.format("%.2f",new FMeasure().calculate(fullResultMap, fullRefgoldStandard))   + "\t" +
//                //                (System.currentTimeMillis() - start)        +
//                "\n" ;
//        System.out.println(resultStr);
//        return resultStr;


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
                String.format("%.2f", new FMeasure().calculate(kbMap, fullRefgoldStandard)) + "\t" +
                linkSpecification.toStringOneLine() +
                //                (System.currentTimeMillis() - start)        +
                "\n";

        return resultStr;
    }


    /**
     * Extract the source and target training cache instances based on the input learnMap
     * @param trainMap to be used for training caches filling
     * @author sherif
     */
    protected void fillTrainingCaches(AMapping trainMap) {
        sourceTrainCache = new HybridCache();
        targetTrainCache = new HybridCache();
        for (String s : trainMap.getMap().keySet()) {
            if(fullSourceCache.containsUri(s)){
                sourceTrainCache.addInstance(fullSourceCache.getInstance(s));
                for (String t : trainMap.getMap().get(s).keySet()) {
                    if(fullTargetCache.containsUri(t)){
                        targetTrainCache.addInstance(fullTargetCache.getInstance(t));
                    }else{
                        logger.warn("Instance " + t + " not exist in the target dataset");
                    }
                }
            }else{
                logger.warn("Instance " + s + " not exist in the source dataset");
            }
        }
    }




}
