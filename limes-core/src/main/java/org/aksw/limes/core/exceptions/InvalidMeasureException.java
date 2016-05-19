package org.aksw.limes.core.exceptions;

public class InvalidMeasureException extends Exception {
    /**
     * Exception class for wrong or not existing metric expression.
     */
    private static final long serialVersionUID = 6971779912538326113L;

    public InvalidMeasureException(String name) {
	super("Unknown measure " + name + ". Exiting..");
    }
}
