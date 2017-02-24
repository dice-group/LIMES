package org.aksw.limes.core.exceptions;

/**
 * Exception class for 0 or invalid threshold of measure.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class InvalidThresholdException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 2309448747871110206L;

    public InvalidThresholdException(double threshold) {
        super("Invalid " + threshold + ".");
    }
}
