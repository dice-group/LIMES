package org.aksw.limes.core.exceptions;

/**
 * Exception class for wrong or not existing metric expression.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class InvalidMeasureException extends RuntimeException {

    private static final long serialVersionUID = 6971779912538326113L;

    /**
     * Constructor of InvalidMeasureException class.
     * 
     * @param name,
     *            Name of the wrong measure
     */
    public InvalidMeasureException(String name) {
        super("Unknown measure " + name + ".");
    }
}
