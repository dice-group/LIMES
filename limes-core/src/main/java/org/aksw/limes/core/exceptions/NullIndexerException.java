package org.aksw.limes.core.exceptions;

public class NullIndexerException extends RuntimeException {

    /**
     * Exception class for null index instance.
     * 
     * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
     * @version 1.0
     */
    private static final long serialVersionUID = 1549349430103275276L;

    public NullIndexerException(String message) {
        super(message);
    }

}
