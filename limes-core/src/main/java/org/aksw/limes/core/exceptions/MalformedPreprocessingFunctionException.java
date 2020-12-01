package org.aksw.limes.core.exceptions;

public class MalformedPreprocessingFunctionException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3378727153103619947L;
	
	
    public MalformedPreprocessingFunctionException() {
        super("Preprocessing functions take the form \"function(arg1,arg2,...,keyword1=value,keyword2=value,...)\"");
    }

    public MalformedPreprocessingFunctionException(String func) {
        super("Preprocessing functions take the form \"function(arg1,arg2,...,keyword1=value,keyword2=value,...)\""
        		+ "\n Something is wrong with the function \"" + func + "\" you provided");
    }
}
