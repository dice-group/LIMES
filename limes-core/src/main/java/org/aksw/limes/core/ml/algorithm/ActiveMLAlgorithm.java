package org.aksw.limes.core.ml.algorithm;

import java.lang.reflect.Constructor;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;

public class ActiveMLAlgorithm extends AMLAlgorithm {

    public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.SUPERVISED_ACTIVE;

    public ActiveMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) throws UnsupportedMLImplementationException {

        try {
            Constructor<? extends ACoreMLAlgorithm> ctor = clazz.getDeclaredConstructor();
            setMl(ctor.newInstance());
        } catch (Exception e) {
            throw new UnsupportedMLImplementationException(getMl().getName());
        }

        if (!getMl().supports(ML_IMPLEMENTATION_TYPE)) {
            throw new UnsupportedMLImplementationException(getMl().getName());
        }

    }

    public AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
        return getMl().getNextExamples(size);
    }

    public MLModel activeLearn() throws UnsupportedMLImplementationException {
        return getMl().activeLearn();
    }
    
    public MLModel activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        return getMl().activeLearn(oracleMapping);
    }


}
