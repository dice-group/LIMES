package org.aksw.limes.core.io.preprocessing;

public abstract class AProcessingFunction implements IProcessingFunction {


	public void testIfPropertyNumberIsLegal(String ... property) throws IllegalNumberOfPropertiesException{
		if(property.length < minNumberOfProperties()){
			throw new IllegalNumberOfPropertiesException("This functions takes at least " + minNumberOfProperties() + " number of properties!");
		}
		if(property.length > maxNumberOfProperties() && maxNumberOfProperties() != -1){
			throw new IllegalNumberOfPropertiesException("This functions takes at most " + maxNumberOfProperties() + " number of properties!");
		}
	}

	
	public class IllegalNumberOfPropertiesException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1581808737961911448L;
		
		public IllegalNumberOfPropertiesException(String message){
			super(message);
		}

		public IllegalNumberOfPropertiesException(String message, Throwable throwable){
			super(message, throwable);
		}
	}

}
