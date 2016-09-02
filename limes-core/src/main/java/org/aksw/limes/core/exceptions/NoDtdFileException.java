package org.aksw.limes.core.exceptions;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 *
 */
public class NoDtdFileException extends RuntimeException {



	/**
     * 
     */
    private static final long serialVersionUID = 1819187223756493735L;

    public NoDtdFileException(String path) {
		super("No DTD file found at " + path);
	}
	
}
