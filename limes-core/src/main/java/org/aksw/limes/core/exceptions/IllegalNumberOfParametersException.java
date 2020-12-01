package org.aksw.limes.core.exceptions;

	public class IllegalNumberOfParametersException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1581808737961911448L;
		
		public IllegalNumberOfParametersException(String message){
			super(message);
		}

		public IllegalNumberOfParametersException(String message, Throwable throwable){
			super(message, throwable);
		}
	}
	
