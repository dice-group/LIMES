package org.aksw.limes.core.ml.algorithm.ligon_old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.evaluation.qualititativeMeasures.fuzzy.FuzzyFMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.fuzzy.FuzzyPrecision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.fuzzy.FuzzyRecall;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.FuzzyWombatSimple;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ligon {
    private static double TAU = 0.0;

    static Logger logger = LoggerFactory.getLogger(Ligon.class);

    protected AMapping trainigExamplesMap; // with probabilities
    protected AMapping posMap = MappingFactory.createDefaultMapping();     // with membership
    protected AMapping negMap = MappingFactory.createDefaultMapping();     // with membership


    ACache sourceTrainCache;
    ACache targetTrainCache;

    List<NoisyOracle> noisyOracles;


    public Ligon(AMapping trainigExamplesMap, ACache sourceTrainCache, ACache targetTrainCache, List<NoisyOracle> noisyOracles) {
        super();
        this.trainigExamplesMap = trainigExamplesMap;
        this.sourceTrainCache = sourceTrainCache;
        this.targetTrainCache = targetTrainCache;
        this.noisyOracles = noisyOracles;
        initPosNegTrainingExamples(trainigExamplesMap);
        updateNoisyOraclesTrust(trainigExamplesMap);
    }

    private void updateNoisyOraclesTrust(AMapping trainigExamplesMap) {
        for(NoisyOracle noisyOracle : noisyOracles){
            noisyOracle.setEstimatedTp(estimateTp(noisyOracle, trainigExamplesMap));
            noisyOracle.setEstimatedTn(estimateTn(noisyOracle, trainigExamplesMap));
        }
    }

    /**
     * Initialize positive and negative training example
     * Note that positive and negative training example mapping contain membership values not probability values
     * 
     * @param examplesMap (with probability values)
     */
    public void initPosNegTrainingExamples(AMapping examplesMap){
        for (String s : examplesMap.getMap().keySet()) {
            for (String t : examplesMap.getMap().get(s).keySet()) {
                double p = examplesMap.getConfidence(s,t);
                // convert probabilities to membership functions
                if(p >= 0.5){
                    posMap.add(s, t, 2.0 * p - 1.0);
                }else{
                    negMap.add(s, t, 1.0 - 2.0 * p);
                }
            }
        }
    }
    
    
    /**
     * convert input mapping with membership values to the same mapping with probabilities
     * @param inputMapping
     * @return
     */
    public static AMapping mu2p(AMapping inputMapping){
        AMapping result = MappingFactory.createDefaultMapping();
        for (String s : inputMapping.getMap().keySet()) {
            for (String t : inputMapping.getMap().get(s).keySet()) {
                double mu = inputMapping.getConfidence(s,t);
                if(mu >= 0.5){
                    result.add(s, t, 1.0);
                }
            }
        }
        return result;
    }




    /**
     * Estimate the TP of a given noisy oracle
     * 
     * @param noisyOracle
     * @param examplesMap
     * @return
     */
    public double estimateTp(NoisyOracle noisyOracle, AMapping examplesMap){
        double num =0.0d, denum =0.0d;
        for (String s : examplesMap.getMap().keySet()) {
            for (String t : examplesMap.getMap().get(s).keySet()) {
                if(posMap.contains(s, t)){
                    double mu = posMap.getConfidence(s, t);
                    num += (noisyOracle.predict(s, t)) ? mu : 0 ;
                    denum +=  mu;
                }
            }
        }
        return (num == 0.0 && denum == 0.0) ? 0.0 : num / denum;
    }

    /**
     * Estimate the TN of a given noisy oracle
     * 
     * @param noisyOracle
     * @param examplesMap
     * @return
     */
    public double estimateTn(NoisyOracle noisyOracle, AMapping examplesMap){
        double num =0.0d, denum =0.0d;
        for (String s : examplesMap.getMap().keySet()) {
            for (String t : examplesMap.getMap().get(s).keySet()) {
                if(negMap.contains(s, t)){
                    double mu = posMap.getConfidence(s, t);
                    num += (!noisyOracle.predict(s, t)) ? mu : 0 ;
                    denum += mu;
                }
            }
        }
        return (num == 0.0 && denum == 0.0) ? 0.0 : num / denum;
    }



    /**
     * Update positive and negative training example
     * Note that positive and negative training example mapping contain membership values not probability values
     * 
     * @param examplesMap (with probability values)
     */
    public void updatePosNegTrainingExamples(AMapping examplesMap){
        for (String s : examplesMap.getMap().keySet()) {
            for (String t : examplesMap.getMap().get(s).keySet()) {
                double pTrue = estimateTrue(s,t);
                double pFalse = estimateFalse(s,t);
                if(pTrue >= TAU * pFalse){
                    posMap.add(s, t, pTrue);
                }else{
                    negMap.add(s, t, pFalse);
                }
            }
        }
    }


    /**
     * Based on the noisy oracles answers, estimates the probability that the given (subject, object) pair being true  
     * 
     * @param subject
     * @param object
     * @return
     */
    protected double estimateTrue(String subject, String object){
        double result = 1; 
        for(NoisyOracle noisyOracle: noisyOracles){
            if(noisyOracle.predict(subject, object)){
                result *= 1.0 - (noisyOracle.predict(subject, object)? noisyOracle.estimatedTp: (1.0 - noisyOracle.getEstimatedTp()));
            }
        }
        return 1 - result;
    }


    /**
     * Based on the noisy oracles answers, estimates the probability that the given (subject, object) pair being false
     * 
     * @param subject
     * @param object
     * @return
     */
    protected double estimateFalse(String subject, String object){
        double result = 1; 
        for(NoisyOracle noisyOracle: noisyOracles){
            if(noisyOracle.predict(subject, object)){
                result *= 1 - (!noisyOracle.predict(subject, object)? noisyOracle.estimatedTn: (1.0 - noisyOracle.getEstimatedTn()));
            }
        }
        return 1 - result;
    }





    public void setTAU(double tAU) {
        TAU = tAU;
    }



    public AMapping getPosMap() {
        return posMap;
    }



    public void setPosMap(AMapping posMap) {
        this.posMap = posMap;
    }



    public AMapping getNegMap() {
        return negMap;
    }



    public void setNegMap(AMapping negMap) {
        this.negMap = negMap;
    }



    public AMapping getUnknownMap() {
        return trainigExamplesMap;
    }



    public void setUnknownMap(AMapping unknownMap) {
        this.trainigExamplesMap = unknownMap;
    }



    public List<NoisyOracle> getNoisyOracles() {
        return noisyOracles;
    }



    public void setNoisyOracles(List<NoisyOracle> noisyOracles) {
        this.noisyOracles = noisyOracles;
    }

    public MLResults learn(ACache sourceTestCache, ACache targetTestCache, AMapping fullReferenceMapping, int activeLearningRate) {
        MLResults mlModel = null;
        String resultStr =  "itr\tfP\tfR\tfF\tT\tMetricExpr\tP\tR\tF\n";
        String resultStr2 =  "";
        String resultStr3 =  "";

        AMapping examples = trainigExamplesMap;
        int intrCount = 10;
        long start = System.currentTimeMillis();

        for(int i = 0; i < intrCount  ; i++){

            // 1. Train fuzzy WOMBAT 
            FuzzyWombatSimple fuzzyWombat = new FuzzyWombatSimple(); 
//            fuzzyWombat.init(null, sourcePredictionCache, targetPredictionCache);
            fuzzyWombat.init(null, sourceTestCache, targetTestCache);
            mlModel = fuzzyWombat.learn(examples);
            AMapping learnedMap = fuzzyWombat.predict(sourceTrainCache, targetTrainCache, mlModel);
            //            learnedMap = AMapping.getBestOneToOneMappings(learnedMap);
            LinkSpecification linkSpecification = mlModel.getLinkSpecification();
            resultStr +=  (i + 1) + "\t" +
                    String.format("%.2f", new FuzzyPrecision().calculate(learnedMap, new GoldStandard(examples)))+ "\t" + 
                    String.format("%.2f",new FuzzyRecall().calculate(learnedMap, new GoldStandard(examples)))   + "\t" + 
                    String.format("%.2f",new FuzzyFMeasure().calculate(learnedMap, new GoldStandard(examples)))   + "\t" +
                    (System.currentTimeMillis() - start)            + "\t" +
                    linkSpecification.toStringOneLine()                   + "\t" +
                    executeLinkSpecs(mlModel.getLinkSpecification(), sourceTestCache, targetTestCache, fullReferenceMapping);
            //                    String.format("%.2f", new Precision().calculate(learnedMap, new GoldStandard(examples)))+ "\t" + 
            //                    String.format("%.2f",new Recall().calculate(learnedMap, new GoldStandard(examples)))   + "\t" + 
            //                    String.format("%.2f",new FMeasure().calculate(learnedMap, new GoldStandard(examples)))   + "\n" ;

            resultStr3 += (i + 1) + "\t(" + 
                    String.format("%.2f", computeTpMSE()) + "|" + 
                    String.format("%.2f", computeTnMSE()) +")\n";

            for(NoisyOracle o : noisyOracles){
                resultStr2 += "(" + String.format("%.2f",o.tp) + "-" + String.format("%.2f",o.estimatedTp) + ")(" 
                        + String.format("%.2f",o.tn) + "-" + String.format("%.2f",o.estimatedTn) + ")\t";
            }
            resultStr2 += "\n";


            // 2. get most informative examples
            AMapping mostInfMap = fuzzyWombat.findMostInformativeExamples(activeLearningRate, examples);
           
//            AMapping mostInfPosMap = fuzzyWombat.findMostInformativePositiveExamples(activeLearningRate, examples);
//            AMapping mostInfNegMap = fuzzyWombat.findMostInformativeNegativeExamples(activeLearningRate, examples);
            System.out.println("mostInfMap size: " + mostInfMap.size());
//            System.out.println("mostInfNegMap size: " + mostInfNegMap.size());

            // 3. update training examples
            examples = MappingOperations.union(examples, mostInfMap);
//            examples = MappingOperations.union(examples,mostInfPosMap);
//            examples = MappingOperations.union(examples, mostInfNegMap);
            updatePosNegTrainingExamples(examples);
//            System.out.println("Current example size: " + examples.size());

            //            AMapping nextExamples = null;
            //            try {
            //                int activeLearningRate = 10;
            //                nextExamples = fuzzyWombat.getNextExamples(activeLearningRate);
            //            } catch (Exception e) {
            //                // TODO Auto-generated catch block
            //                e.printStackTrace();
            //            }
            //            examples = MappingOperations.union(examples, nextExamples);
            //            System.out.println("Current example size: " + examples.size());
            //            updatePosNegTrainingExamples(examples);

            // 4. update noisy oracle trust values
            updateNoisyOraclesTrust(examples);
        }
        System.out.println("-------------- Fuzzy Wombat Results --------------" );
        System.out.println(resultStr);
        System.out.println("-------------- Noisy Oracle Trust --------------" );
        System.out.println(resultStr2);
        System.out.println("-------------- MSE (TP|TN) --------------" );
        System.out.println(resultStr3);

        return mlModel;
    }

    /**
     * @param d dataset 
     * @param linkSpecification to be applied to the whole dataset d 
     * @param reference 
     * @return
     */
    static String executeLinkSpecs(LinkSpecification linkSpecification, ACache sourceCache, ACache targetCache, AMapping reference){
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
        AMapping learnedMapWithProp = mu2p(kbMap);
        GoldStandard goldStandardWithProp = new GoldStandard(mu2p(reference));
        String resultStr = 
                String.format("%.2f", new Precision().calculate(learnedMapWithProp, goldStandardWithProp))+ "\t" + 
                        String.format("%.2f",new Recall().calculate(learnedMapWithProp, goldStandardWithProp))   + "\t" + 
                        String.format("%.2f",new FMeasure().calculate(learnedMapWithProp, goldStandardWithProp))   + "\t" +
                        (System.currentTimeMillis() - start)        + "\n" ;
        
        return resultStr;
    }

    public double computeTnMSE(){
        double mse = 0.0;
        double mean = 0.0;
        for(NoisyOracle o : noisyOracles){
            mean += Math.abs(o.tn - o.estimatedTn);
        }
        mean /= (double) noisyOracles.size();
        for(NoisyOracle o : noisyOracles){
            double tnDiff = mean - (o.tn - o.estimatedTn);
            mse += tnDiff * tnDiff;
        }
        return mse / (double) noisyOracles.size();
    }

    public double computeTpMSE(){
        double mse = 0.0;
        double mean = 0.0;
        for(NoisyOracle o : noisyOracles){
            mean += Math.abs(o.tp - o.estimatedTp);
        }
        mean /= (double) noisyOracles.size();
        for(NoisyOracle o : noisyOracles){
            double tpDiff = mean - (o.tp - o.estimatedTp);
            mse += tpDiff * tpDiff;
        }
        return mse / (double) noisyOracles.size();
    }

    void printNoisyOracles(){

    }



}
