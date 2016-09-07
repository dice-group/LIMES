package org.aksw.limes.core.ml.algorithm;

import java.lang.reflect.Constructor;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 *
 */
public class SupervisedMLAlgorithm extends AMLAlgorithm {

    public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.SUPERVISED_BATCH;

    /**
     * @param clazz the core ML algorithm class
     * @throws UnsupportedMLImplementationException Exception
     */
    public SupervisedMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) throws UnsupportedMLImplementationException {

        try {
            Constructor<? extends ACoreMLAlgorithm> ctor = clazz.getDeclaredConstructor();
            setMl(ctor.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedMLImplementationException(getMl().getName());
        }

        if (!getMl().supports(ML_IMPLEMENTATION_TYPE)) {
            throw new UnsupportedMLImplementationException(getMl().getName());
        }

    }

    /**
     * @param trainingData training data
     * @return wrap with results
     * @throws UnsupportedMLImplementationException Exception
     */
    public MLResults learn(AMapping trainingData) throws UnsupportedMLImplementationException {
        return getMl().learn(trainingData);
    }


}
