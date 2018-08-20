package org.aksw.limes.core.exceptions;

public class InvalidPreprocessingFunctionException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3378727153103619947L;
	
	
    /**
     * Constructor of InvalidPreprocessingFunctionException class.
     * 
     * @param name,
     *            Name of the wrong preprocessing function
     */
    public InvalidPreprocessingFunctionException(String name) {
        super("Unknown preprocessing function " + name + ".");
    }

}
