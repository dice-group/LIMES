package org.aksw.limes.core.exceptions;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 *
 */
public class UnsupportedMLImplementationException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 4789420769908480902L;

    public UnsupportedMLImplementationException(String mlAlgorithmName) {
        super(mlAlgorithmName + " does not support this implementation.");
    }

}
