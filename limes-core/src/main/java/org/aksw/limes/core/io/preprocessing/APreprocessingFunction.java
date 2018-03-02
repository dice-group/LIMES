package org.aksw.limes.core.io.preprocessing;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;

public abstract class AProcessingFunction implements IProcessingFunction {

    public static final String AT               = "@";

	@Override
	public void process(ACache c, String[] properties, String... arguments) throws IllegalNumberOfParametersException {
		testIfPropertyNumberIsLegal(properties);
		testIfNumberOfArgumentsIsLegal(arguments);
            c.getAllInstances().stream()
            		//Only get instances with that properties
                    .filter(AProcessingFunction.hasProperty(properties))
                    //Apply function to each instance
                    .map(instance -> this.applyFunction(instance, properties, arguments)
                    ).collect(Collectors.toList());
	}

	public void testIfPropertyNumberIsLegal(String[] properties) throws IllegalNumberOfParametersException{
		if(properties.length < minNumberOfProperties()){
			throw new IllegalNumberOfParametersException("This functions takes at least " + minNumberOfProperties() + " number of properties!");
		}
		if(properties.length > maxNumberOfProperties() && maxNumberOfProperties() != -1){
			throw new IllegalNumberOfParametersException("This functions takes at most " + maxNumberOfProperties() + " number of properties!");
		}
	}

	public void testIfNumberOfArgumentsIsLegal(String... arguments) throws IllegalNumberOfParametersException{
		if(arguments.length < minNumberOfArguments()){
			throw new IllegalNumberOfParametersException("This functions takes at least " + minNumberOfArguments() + " number of properties!");
		}
		if(arguments.length > maxNumberOfArguments() && maxNumberOfArguments() != -1){
			throw new IllegalNumberOfParametersException("This functions takes at most " + maxNumberOfArguments() + " number of properties!");
		}
	}

	
	public class IllegalNumberOfParametersException extends Exception {

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
	
	public static Predicate<Instance> hasProperty(String ... property){
		return i -> !Collections.disjoint(i.getAllProperties(), Arrays.asList(property));
	}
	
}
