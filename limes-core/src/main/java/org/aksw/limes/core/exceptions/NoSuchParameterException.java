package org.aksw.limes.core.exceptions;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 *
 */
public class NoSuchParameterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -54456956700647426L;

	public NoSuchParameterException(String name) {
		super("No such parameter: " + name);
	}
	
}
