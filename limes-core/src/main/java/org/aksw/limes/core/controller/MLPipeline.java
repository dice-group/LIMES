package org.aksw.limes.core.controller;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorFactory;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;
import org.aksw.limes.core.ml.algorithm.*;
import org.aksw.limes.core.ml.setting.LearningParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Execution pipeline for generating mappings using ML.
 * Provides overloaded convenience methods.
 *
 * @author Kevin Dreßler
 */
public class MLPipeline {

    public static final Logger logger = LoggerFactory.getLogger(MLPipeline.class);

    public static AMapping execute(
            Cache source,
            Cache target,
            String mlAlgrorithmName,
            MLImplementationType mlImplementationType,
            List<LearningParameter> learningParameters,
            String trainingDataFile,
            EvaluatorType pfmType,
            int maxIt
    ) throws UnsupportedMLImplementationException {
        Class<? extends ACoreMLAlgorithm> clazz = MLAlgorithmFactory.getAlgorithmType(mlAlgrorithmName);
        MLResults mlm;
        AMapping trainingDataMap = MappingFactory.createDefaultMapping();
        if (
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
                //            boolean stopLearning = false;
                ActiveMLAlgorithm mla = new ActiveMLAlgorithm(clazz);
                mla.init(learningParameters, source, target);
                mlm = mla.activeLearn();
                Scanner scan = new Scanner(System.in);
                double rating;
                String reply, evaluationMsg;
                int i = 0;
                while (true) {
                    i++;
                    logger.info("To rate the " + i + ". set of examples, write 'r' and press enter.\n" +
                            "To quit learning at this point and write out the mapping, write 'q' and press enter.\n" +
                            "For rating examples, use numbers in [-1,+1].\n" +
                            "\t(-1 := strong negative example, +1 := strong positive example)");
                    reply = scan.next();
                    if (reply.trim().equals("q"))
                        break;
                    AMapping nextExamples = mla.getNextExamples(maxIt);
                    int j = 0;
                    for (String s : nextExamples.getMap().keySet()) {
                        for (String t : nextExamples.getMap().get(s).keySet()) {
                            boolean rated = false;
                            j++;
                            do {
                                evaluationMsg = "Exemplar #" + i + "." + j + ": (" + s + ", " + t + ")";
                                try {
                                    logger.info(evaluationMsg);
                                    rating = scan.nextDouble();
                                    if (rating >= -1.0d && rating <= 1.0d) {
                                        nextExamples.getMap().get(s).put(t, rating);
                                        rated = true;
                                    } else {
                                        logger.error("Input number out of range [-1,+1], please try again...");
                                    }
                                } catch (NoSuchElementException e) {
                                    logger.error("Input did not match floating point number, please try again...");
                                    scan.next();
                                }
                            } while (!rated);
                        }
                    }
                    mlm = mla.activeLearn(nextExamples);
                }
                return mla.predict(source, target, mlm);
            case UNSUPERVISED:
                UnsupervisedMLAlgorithm mlu = new UnsupervisedMLAlgorithm(clazz);
                mlu.init(learningParameters, source, target);
                PseudoFMeasure pfm = null;
                if(pfmType != null){
                    pfm = (PseudoFMeasure) EvaluatorFactory.create(pfmType);
                }
                mlm = mlu.learn(pfm);
                return mlu.predict(source, target, mlm);
            default:
                throw new UnsupportedMLImplementationException(clazz.getName());
        }
    }
}
