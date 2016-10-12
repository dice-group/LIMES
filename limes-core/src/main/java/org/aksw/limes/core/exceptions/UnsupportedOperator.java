package org.aksw.limes.core.exceptions;
/**
 * Exception class for wrong specification operator.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class UnsupportedOperator extends RuntimeException{
    /**
     * 
     */
    private static final long serialVersionUID = 2025338559543577903L;

    /**
     * Constructor of UnsupportedOperator class.
     * 
     * @param name,
     *            Name of the wrong operator
     */
    public UnsupportedOperator(String name) {
        super("Unknown operator " + name + ".");
    }
}
