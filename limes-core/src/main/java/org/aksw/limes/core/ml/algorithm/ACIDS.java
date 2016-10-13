package org.aksw.limes.core.ml.algorithm;

import java.util.List;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.NotYetImplementedException;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;

public class ACIDS extends ACoreMLAlgorithm {

    @Override
    protected String getName() {
        return "ACIDS";
    }

    @Override
    protected void init(List<LearningParameter> lp, ACache source, ACache target) {
        super.init(lp, source, target);
        // TODO
        throw new NotYetImplementedException("ACIDS algorithm was not implemented into this version of LIMES.");
    }

    @Override
    protected MLResults learn(AMapping trainingData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

    @Override
    protected AMapping predict(ACache source, ACache target, MLResults mlModel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
        return mlType == MLImplementationType.SUPERVISED_BATCH || mlType == MLImplementationType.SUPERVISED_ACTIVE;
    }

    @Override
    protected AMapping getNextExamples(int size) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected MLResults activeLearn(AMapping oracleMapping) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDefaultParameters() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected MLResults activeLearn() throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }


}
