package org.aksw.limes.core.io.preprocessing;

import org.aksw.limes.core.exceptions.IllegalNumberOfParametersException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;

public interface IPreprocessingFunction {

//	/**
//	 * Applies the function to the properties of the instances in the cache
//	 * @param c cache to apply function on
//	 * @param arguments arguments the function will use
//	 * @param properties properties on which values the function should be applied
//	 * @throws IllegalNumberOfParametersException thrown if an illegal number of properties is given
//	 */
//	public void process(ACache c, String[] properties ,String ... arguments) throws IllegalNumberOfParametersException;
	
	public Instance applyFunction(Instance i, String property, String... arguments);

	
	public int minNumberOfArguments();
	
	public int maxNumberOfArguments();
	
}
