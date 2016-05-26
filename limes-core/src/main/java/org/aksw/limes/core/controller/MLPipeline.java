package org.aksw.limes.core.controller;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;
import org.aksw.limes.core.ml.algorithm.*;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningParameters;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Execution pipeline for generating mappings using ML.
 * Provides overloaded convenience methods.
 *
 * @author Kevin Dreßler
 */
public class MLPipeline {

    public static final Logger logger = Logger.getLogger(MLPipeline.class);

    public static AMapping execute(Cache source, Cache target, String mlAlgrorithmName, MLImplementationType mlImplementationType,
            LearningParameters learningParameters, String trainingDataFile, PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
        Class<? extends ACoreMLAlgorithm> clazz = MLAlgorithmFactory.getAlgorithmType(mlAlgrorithmName);
        MLModel mlm;
        AMapping trainingDataMap = MappingFactory.createDefaultMapping();
        if(mlImplementationType == MLImplementationType.SUPERVISED_ACTIVE ||
                mlImplementationType == MLImplementationType.SUPERVISED_BATCH){
         // TODO make it check for different readers 
            RDFMappingReader mappingReader = new RDFMappingReader(trainingDataFile);
            trainingDataMap = mappingReader.read();
        }
        switch (mlImplementationType) {
        case SUPERVISED_BATCH:
            SupervisedMLAlgorithm mls = new SupervisedMLAlgorithm(clazz);
            mls.init(learningParameters, source, target);
            mlm = mls.learn(trainingDataMap);
            return mls.predict(source, target, mlm);
        case SUPERVISED_ACTIVE:
            // for active learning, need to reiterate and prompt the user for evaluation of examples:
            boolean stopLearning = false;
            ActiveMLAlgorithm mla = new ActiveMLAlgorithm(clazz);
            mla.init(learningParameters, source, target);
            Scanner scan = new Scanner(System.in);
            Random random = new Random();
            double rating;
            String evaluationMsg;
            logger.info("Please rate the following examples with a number in [-1,+1].\n\t" +
                    "(-1 =: strong negative example, +1 =: strong positive example)");
            for (int c = 0; c < 10; c++) {
                mlm = mla.activeLearn(trainingDataMap);
                AMapping m = mla.predict(source, target, mlm);
                //Set<Map.Entry<String, HashMap<String, Double>>> entries = mlm.getMapping().getMap().entrySet();
                Set<Map.Entry<String, HashMap<String, Double>>> entries = m.getMap().entrySet();
                int i = 0;
                int j = random.nextInt(entries.size());
                for (Map.Entry<String, HashMap<String, Double>> entry : entries) {
                    if (i == j) {
                        int k = 0;
                        int l = random.nextInt(entry.getValue().size());
                        for (Map.Entry<String, Double> innerEntry : entry.getValue().entrySet()) {
                            if (k == l) {
                                boolean rated = false;
                                do {
                                    evaluationMsg = "(" + entry.getKey() + ", " + innerEntry.getKey() + ")";
                                    try {
                                        logger.info(evaluationMsg);
                                        rating = scan.nextDouble();
                                        trainingDataMap.add(entry.getKey(), innerEntry.getKey(), rating);
                                        rated = true;
                                    } catch (NoSuchElementException e) {
                                        logger.error("Input did not match floating point number, please try again...");
                                    }
                                } while (!rated);
                                break;
                            }
                            k++;
                        }
                        break;
                    }
                    i++;
                }
            }
            mlm = mla.activeLearn(trainingDataMap);
            return mla.predict(source, target, mlm);
        case UNSUPERVISED:
            UnsupervisedMLAlgorithm mlu = new UnsupervisedMLAlgorithm(clazz);
            mlu.init(learningParameters, source, target);
            mlm = mlu.learn(pfm);
            return mlu.predict(source, target, mlm);
        default:
            throw new UnsupportedMLImplementationException(clazz.getName());
        }
    }
}
