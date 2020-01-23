package org.aksw.limes.core.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorFactory;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.AMappingReader;
import org.aksw.limes.core.io.mapping.reader.CSVMappingReader;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.UnsupervisedMLAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execution pipeline for generating mappings using ML.
 * Provides overloaded convenience methods.
 *
 * @author Kevin Dreßler
 */
public class MLPipeline {

    public static final Logger logger = LoggerFactory.getLogger(MLPipeline.class);

    public static AMapping execute(
            ACache source,
            ACache target,
            Configuration configuration,
            String mlAlgorithmName,
            MLImplementationType mlImplementationType,
            List<LearningParameter> learningParameters,
            String trainingDataFile,
            EvaluatorType pfmType,
            int maxIt,
            ActiveLearningOracle oracle
    ) throws UnsupportedMLImplementationException {
        Class<? extends ACoreMLAlgorithm> clazz = MLAlgorithmFactory.getAlgorithmType(mlAlgorithmName);
        MLResults mlm;
        AMapping trainingDataMap = MappingFactory.createDefaultMapping();
        if (mlImplementationType == MLImplementationType.SUPERVISED_BATCH){
        	AMappingReader mappingReader;
        	if(trainingDataFile.endsWith(".csv")){
        		mappingReader = new CSVMappingReader(trainingDataFile);
        	}else{
        		mappingReader = new RDFMappingReader(trainingDataFile);
        	}
            trainingDataMap = mappingReader.read();
        }

        switch (mlImplementationType) {
            case SUPERVISED_BATCH:
                SupervisedMLAlgorithm mls = new SupervisedMLAlgorithm(clazz);
                mls.init(learningParameters, source, target);
                mls.getMl().setConfiguration(configuration);
                mlm = mls.learn(trainingDataMap);
                logger.info("Learned: " + mlm.getLinkSpecification().getFullExpression() + " with threshold: " + mlm.getLinkSpecification().getThreshold());
                return mls.predict(source, target, mlm);
            case SUPERVISED_ACTIVE:
                // for active learning, need to reiterate and prompt the user for evaluation of examples:
                //            boolean stopLearning = false;
                ActiveMLAlgorithm mla = new ActiveMLAlgorithm(clazz);
                mla.init(learningParameters, source, target);
                mla.getMl().setConfiguration(configuration);
                mlm = mla.activeLearn();
                while (!oracle.isStopped()) {
                    AMapping nextExamplesMapping = mla.getNextExamples(maxIt);
                    if (nextExamplesMapping.getMap().isEmpty()) {
                        oracle.stop();
                        break;
                    }
                    logger.info(nextExamplesMapping.toString());
                    ActiveLearningExamples activeLearningExamples = new ActiveLearningExamples(nextExamplesMapping, source, target);
                    AMapping classify = oracle.classify(activeLearningExamples);
                    logger.info(classify.toString());
                    mlm = mla.activeLearn(classify);
                }
                logger.info("Learned: " + mlm.getLinkSpecification().getFullExpression() + " with threshold: " + mlm.getLinkSpecification().getThreshold());
                return mla.predict(source, target, mlm);
            case UNSUPERVISED:
                UnsupervisedMLAlgorithm mlu = new UnsupervisedMLAlgorithm(clazz);
                mlu.init(learningParameters, source, target);
                mlu.getMl().setConfiguration(configuration);
                PseudoFMeasure pfm = null;
                if(pfmType != null){
                    pfm = (PseudoFMeasure) EvaluatorFactory.create(pfmType);
                }
                mlm = mlu.learn(pfm);
                logger.info("Learned: " + mlm.getLinkSpecification().getFullExpression() + " with threshold: " + mlm.getLinkSpecification().getThreshold());
                return mlu.predict(source, target, mlm);
            default:
                throw new UnsupportedMLImplementationException(clazz.getName());
        }
    }
}
