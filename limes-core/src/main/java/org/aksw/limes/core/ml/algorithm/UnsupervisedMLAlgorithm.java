package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;

import java.lang.reflect.Constructor;

public class UnsupervisedMLAlgorithm extends AMLAlgorithm {

    public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.UNSUPERVISED;

    public UnsupervisedMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) throws UnsupportedMLImplementationException {

        try {
            Constructor<? extends ACoreMLAlgorithm> ctor = clazz.getDeclaredConstructor();
            ml = ctor.newInstance();
        } catch (Exception e) {
            throw new UnsupportedMLImplementationException(ml.getName());
        }

        if (!ml.supports(ML_IMPLEMENTATION_TYPE)) {
            throw new UnsupportedMLImplementationException(ml.getName());
        }

    }

    public MLModel learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
        return ml.learn(pfm);
    }


}
