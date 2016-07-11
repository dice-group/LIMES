package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;

import java.lang.reflect.Constructor;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 *
 */
public class UnsupervisedMLAlgorithm extends AMLAlgorithm {

    public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.UNSUPERVISED;

    /**
     * @param clazz the core ML algorithm class
     * @throws UnsupportedMLImplementationException
     */
    public UnsupervisedMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) throws UnsupportedMLImplementationException {

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
     * @param pfm the pseudo f-measure
     * @return wrap with results
     * @throws UnsupportedMLImplementationException
     */
    public MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
        return getMl().learn(pfm);
    }


}
