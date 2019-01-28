package org.aksw.limes.core.ml.algorithm.dragon.evaluation;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.*;
import org.aksw.limes.core.ml.algorithm.dragon.Dragon;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.FitnessFunctionDTL;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.GiniIndex;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.GlobalFMeasurePruning;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.PruningFunctionDTL;
import org.aksw.limes.core.ml.algorithm.euclid.LinearEuclid;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CleanerEval {

    public static EvalResult evaluate(Class algorithm, MLImplementationType type, ACache testSourceCache, ACache testTargetCache, ACache trainSourceCache, ACache trainTargetCache, EvaluationData c, AMapping trainingData, AMapping testData, Logger logger,
                                      int maxlinkspecheight, FitnessFunctionDTL fitnessfunction, PruningFunctionDTL pruningfunction, double minpropertycoverage) throws UnsupportedMLImplementationException {
        AMLAlgorithm ml;
        Configuration config;
        MLResults res;
        AMapping mapping = null;
        EvalResult evalResults = new EvalResult();
        ml = MLAlgorithmFactory.createMLAlgorithm(algorithm, type);
        logger.info("source size: " + testSourceCache.size());
        logger.info("target size: " + testTargetCache.size());
        ml.init(null, trainSourceCache, trainTargetCache);
        config = c.getConfigReader().read();
        ml.getMl().setConfiguration(config);
        if(algorithm == Dragon.class) {
            ((Dragon) ml.getMl()).setPropertyMapping(c.getPropertyMapping());
            if(Dragon.useJ48 || Dragon.useJ48optimized){
                ml.setParameter(Dragon.PARAMETER_REDUCED_ERROR_PRUNING, true);
                ml.setParameter(Dragon.PARAMETER_FOLD_NUMBER, 5);
            }else {
                ml.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, maxlinkspecheight);
                ml.getMl().setParameter(Dragon.PARAMETER_FITNESS_FUNCTION, fitnessfunction);
                ml.getMl().setParameter(Dragon.PARAMETER_PRUNING_FUNCTION, pruningfunction);
                ml.getMl().setParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE, minpropertycoverage);
            }
        }
        if(algorithm == Eagle.class){
            ml.getMl().setParameter(Eagle.PROPERTY_MAPPING, c.getPropertyMapping());
        }
        long start = System.currentTimeMillis();
        res = ml.asSupervised().learn(trainingData);
        long end = System.currentTimeMillis();
        long Time = (end - start);
        logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
        Object[] metrics = getMetrics(ml, testSourceCache, testTargetCache, testData, res);
        evalResults.setfMeasure((double)metrics[0]);
        evalResults.setPrecision((double)metrics[1]);
        evalResults.setRecall((double)metrics[2]);
        evalResults.setSize((int)metrics[3]);
        evalResults.setMlResults(res);
        logger.info("FMeasure: " + (double)metrics[0]);
        logger.info("Precision: " + (double)metrics[1]);
        logger.info("Recall: " + (double)metrics[2]);
        evalResults.setTime(Time);
        logger.info("Time: " + Time);
        logger.info("Size: " + (int)metrics[3]);
        if(algorithm == Dragon.class && !(Dragon.useJ48 || Dragon.useJ48optimized)) {
            MLResults resUnpruned = new MLResults((LinkSpecification) res.getDetails().get("unpruned"), null, 0, null);
            Object[] unprunedMetrics = getMetrics(ml, testSourceCache, testTargetCache, testData, resUnpruned);
            logger.info("Unpruned Size: " + unprunedMetrics[3]);
            evalResults.setUnprunedFMeasure((double)unprunedMetrics[0]);
            evalResults.setUnprunedPrecision((double)unprunedMetrics[1]);
            evalResults.setUnprunedRecall((double)unprunedMetrics[2]);
            evalResults.setUnprunedSize((int)unprunedMetrics[3]);
        }
        Object[] trainmetrics = getMetrics(ml, trainSourceCache, trainTargetCache, trainingWithoutNegative(trainingData), res);
        evalResults.setTrainFMeasure((double)trainmetrics[0]);
        evalResults.setTrainPrecision((double)trainmetrics[1]);
        evalResults.setTrainRecall((double)trainmetrics[2]);

        Dragon.useJ48 = false;
        Dragon.useMergeAndConquer = false;
        Dragon.useJ48optimized = false;
        Dragon.buildTestCaches = false;
        return evalResults;
    }

    private static AMapping trainingWithoutNegative(AMapping trainingData){
        AMapping tPositive = MappingFactory.createDefaultMapping();
        for(String s: trainingData.getMap().keySet()){
            for(String t: trainingData.getMap().get(s).keySet()){
               if(trainingData.getMap().get(s).get(t) > 0){
                   tPositive.add(s,t,trainingData.getMap().get(s).get(t));
               }
            }
        }
        return tPositive;
    }

    private static Object[] getMetrics(AMLAlgorithm algo, ACache source, ACache target, AMapping gold, MLResults res){
        AMapping mapping = algo.predict(source, target, res);
        double FM = new FMeasure().calculate(mapping,
                new GoldStandard(gold, source.getAllUris(), target.getAllUris()));
        double Precision = new Precision().calculate(mapping,
                new GoldStandard(gold, source.getAllUris(), target.getAllUris()));
        double Recall = new Recall().calculate(mapping,
                new GoldStandard(gold, source.getAllUris(), target.getAllUris()));
        int Size = res.getLinkSpecification().size();
        return new Object[]{FM, Precision, Recall, Size};
    }

    public static class EvalResult {
        public static final String ML_RESULTS_KEY = "ML_RESULTS";
        public static final String FMEASURE_KEY = "FMEASURE";
        public static final String PRECISION_KEY = "PRECISION";
        public static final String RECALL_KEY = "RECALL";
        public static final String TIME_KEY = "TIME";
        public static final String SIZE_KEY = "SIZE";
        public static final String TRAIN_FMEASURE_KEY = "TRAIN_FMEASURE";
        public static final String TRAIN_PRECISION_KEY = "TRAIN_PRECISION";
        public static final String TRAIN_RECALL_KEY = "TRAIN_RECALL";
        public static final String UNPRUNED_FMEASURE_KEY = "UNPRUNED_FMEASURE";
        public static final String UNPRUNED_PRECISION_KEY = "UNPRUNED_PRECISION";
        public static final String UNPRUNED_RECALL_KEY = "UNPRUNED_RECALL";
        public static final String UNPRUNED_SIZE_KEY = "UNPRUNED_SIZE";

        private Map<String, Object> results;

        public EvalResult() {
            results = new HashMap<>();
        }

        public Object get(String KEY){
            return results.get(KEY);
        }

        public void setMlResults(MLResults mlResults) {
            results.put(ML_RESULTS_KEY, mlResults);
        }

        public void setfMeasure(double fMeasure) {
            results.put(FMEASURE_KEY,fMeasure);
        }

        public void setPrecision(double precision) {
            results.put(PRECISION_KEY ,precision);
        }

        public void setRecall(double recall) {
            results.put(RECALL_KEY ,recall);
        }

        public void setTrainFMeasure(double trainFMeasure) {
            results.put(TRAIN_FMEASURE_KEY ,trainFMeasure);
        }


        public void setTrainPrecision(double trainPrecision) {
            results.put(TRAIN_PRECISION_KEY ,trainPrecision);
        }

        public void setTrainRecall(double trainRecall) {
            results.put(TRAIN_RECALL_KEY ,trainRecall);
        }

        public void setUnprunedFMeasure(double unprunedFMeasure) {
            results.put(UNPRUNED_FMEASURE_KEY ,unprunedFMeasure);
        }

        public void setUnprunedPrecision(double unprunedPrecision) {
            results.put(UNPRUNED_PRECISION_KEY ,unprunedPrecision);
        }

        public void setUnprunedRecall(double unprunedRecall) {
            results.put(UNPRUNED_RECALL_KEY ,unprunedRecall);
        }

        public void setTime(long time) {
            results.put(TIME_KEY ,time);
        }

        public void setSize(long size) {
            results.put(SIZE_KEY ,size);
        }

        public void setUnprunedSize(long unprunedSize) {
            results.put(UNPRUNED_SIZE_KEY ,unprunedSize);
        }
    }
}
