package org.aksw.limes.core.io.preprocessing;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.preprocessing.AProcessingFunction.IllegalNumberOfPropertiesException;

public interface IProcessingFunction {

	/**
	 * Applies the function to the properties of the instances in the cache
	 * Classes that implement this function are heavily encouraged to use 
	 * {@link AProcessingFunction#testIfPropertyNumberIsLegal(String...)} function.
	 * @param c cache to apply function on
	 * @param property properties on which values the function should be applied
	 * @throws IllegalNumberOfPropertiesException thrown if an illegal number of properties is given
	 */
	public void applyFunction(ACache c, String ... property) throws IllegalNumberOfPropertiesException;
	
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
}
