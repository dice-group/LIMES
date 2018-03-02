package org.aksw.limes.core.io.preprocessing;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.AProcessingFunction.IllegalNumberOfParametersException;

public interface IProcessingFunction {

	/**
	 * Applies the function to the properties of the instances in the cache
	 * @param c cache to apply function on
	 * @param arguments arguments the function will use
	 * @param properties properties on which values the function should be applied
	 * @throws IllegalNumberOfParametersException thrown if an illegal number of properties is given
	 */
	public void process(ACache c, String[] properties ,String ... arguments) throws IllegalNumberOfParametersException;
	
	public Instance applyFunction(Instance i, String[] properties, String... arguments);

	/**
	 * Minimum of needed properties
	 * @return minimum number of properties this function needs
	 */
	public int minNumberOfProperties();

	/**
	 * maximum of needed properties
	 * @return maximum number of properties this function needs or -1 if there is no upper limit
	 */
	public int maxNumberOfProperties();
	
	public int minNumberOfArguments();
	
	public int maxNumberOfArguments();
}
