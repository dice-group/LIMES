package org.aksw.limes.core.io.preprocessing;

import org.aksw.limes.core.io.cache.Instance;

public interface IPreprocessingFunction {
	
	/**
	 * Applies the function to this instance
	 * @param i the instance that will be preprocessed
	 * @param property for unary operators this is the property on which the function will be applied,
	 *  for n-ary fucntions this is the name of the new property where the result of the preprocessing will be stored
	 * @param arguments some functions take arguments
	 * @return the preprocessed instance
	 */
	public Instance applyFunction(Instance i, String property, String... arguments);

	
	public int minNumberOfArguments();
	
	public int maxNumberOfArguments();

	boolean isComplex();
	
}
