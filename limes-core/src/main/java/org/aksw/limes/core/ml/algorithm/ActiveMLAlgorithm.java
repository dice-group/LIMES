package org.aksw.limes.core.ml.algorithm;

import java.lang.reflect.Constructor;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 *
 */
public class ActiveMLAlgorithm extends AMLAlgorithm {

    public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.SUPERVISED_ACTIVE;

    /**
     * @param clazz the CoreMLAlgorithm class
     * @throws UnsupportedMLImplementationException if ML implementation is not supported
     */
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

    /**
     * @param size number of examples to return
     * @return the mapping
     * @throws UnsupportedMLImplementationException if ML implementation is not supported
     */
    public AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
        return getMl().getNextExamples(size);
    }

    /**
     * @return wrap with results
     * @throws UnsupportedMLImplementationException Exception
     */
    public MLResults activeLearn() throws UnsupportedMLImplementationException {
        return getMl().activeLearn();
    }
    
    /**
     * @param oracleMapping mapping from the oracle
     * @return wrap with results
     * @throws UnsupportedMLImplementationException if ML implementation is not supported
     */
    public MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        return getMl().activeLearn(oracleMapping);
    }


}
