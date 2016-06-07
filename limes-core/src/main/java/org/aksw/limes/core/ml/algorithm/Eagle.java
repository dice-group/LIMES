package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningParameters;

public class Eagle extends ACoreMLAlgorithm {
    protected static final String ALGORITHM_NAME = "Eagle";

    protected Eagle() {
        //
    }

    @Override
    protected String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    protected void init(LearningParameters lp, Cache source, Cache target) {
        super.init(lp, source, target);
        // TODO Auto-generated method stub
    }

    @Override
    protected MLModel learn(AMapping trainingData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected MLModel learn(PseudoFMeasure pfm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AMapping predict(Cache source, Cache target, MLModel mlModel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
        return mlType == MLImplementationType.SUPERVISED_BATCH || mlType == MLImplementationType.UNSUPERVISED;
    }

    @Override
    protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

    @Override
    protected MLModel activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

    @Override
    protected void setDefaultParameters() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected MLModel activeLearn() throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

}
