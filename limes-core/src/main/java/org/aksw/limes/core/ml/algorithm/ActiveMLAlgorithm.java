package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;

import java.lang.reflect.Constructor;

public class ActiveMLAlgorithm extends AMLAlgorithm {

    public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.SUPERVISED_ACTIVE;

    public ActiveMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) throws UnsupportedMLImplementationException {

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

    public AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
        return ml.getNextExamples(size);
    }

    public MLModel activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        return ml.activeLearn(oracleMapping);
    }


}
