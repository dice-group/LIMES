package org.aksw.limes.core.exceptions;

public class ParameterOutOfRangeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1840423718467941605L;

	public ParameterOutOfRangeException(String message){
		super(message);
	}

	public ParameterOutOfRangeException(String message, Throwable throwable){
		super(message, throwable);
	}
}

