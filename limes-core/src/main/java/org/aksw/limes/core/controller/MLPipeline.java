package org.aksw.limes.core.controller;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.*;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningParameters;

/**
 * Execution pipeline for generating mappings using ML.
 * Provides overloaded convenience methods.
 * @author Kevin Dreßler
 */
public class MLPipeline {

    public static Mapping execute (Cache source, Cache target, String mlAlgrorithmName, String mlImplementationType,
                                   LearningParameters learningParameters, Mapping trainingData, PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
        MLImplementationType mlType = MLAlgorithmFactory.getImplementationType(mlImplementationType);
        Class<? extends ACoreMLAlgorithm> clazz = MLAlgorithmFactory.getAlgorithmType(mlAlgrorithmName);
        MLModel mlm;
        switch(mlType) {
            case SUPERVISED_BATCH:
                SupervisedMLAlgorithm mls = new SupervisedMLAlgorithm(clazz);
                mls.init(learningParameters, source, target);
                mlm = mls.learn(trainingData);
                mls.predict(source, target, mlm);
            case SUPERVISED_ACTIVE:
                ActiveMLAlgorithm mla = new ActiveMLAlgorithm(clazz);
                mlm = mla.activeLearn(trainingData);
                mla.predict(source, target, mlm);
            case UNSUPERVISED:
                UnsupervisedMLAlgorithm mlu = new UnsupervisedMLAlgorithm(clazz);
                mlu.init(learningParameters, source, target);
                mlm = mlu.learn(pfm);
                mlu.predict(source, target, mlm);
            default:
                throw new UnsupportedMLImplementationException(clazz.getName());
        }
    }
}
