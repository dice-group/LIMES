package org.aksw.limes.core.measures.measure;

import org.aksw.limes.core.io.cache.Instance;

/**
 * Implements the measure interface.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public interface IMeasure {
    /**
     * Returns the similarity between two objects.
     *
     * @param object1,
     *            the source object
     * @param object2,
     *            the target object
     * 
     * @return The similarity of the objects
     */
    public double getSimilarity(Object object1, Object object2);

    /**
     * Returns the similarity between two instances, given their corresponding
     * properties.
     *
     * @param instance1,
     *            the source instance
     * @param instance2,
     *            the target instance
     * @param property1,
     *            the source property
     * @param property2,
     *            the target property
     * @return The similarity of the instances
     */
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2);

    /**
     * Returns the runtime approximation of a measure.
     *
     * @param mappingSize,
     *            the mapping size returned by the measure
     *
     * @return The runtime of the measure
     */
    public double getRuntimeApproximation(double mappingSize);

    /**
     * Returns name of a measure.
     *
     * 
     * @return Measure name as a string
     */
    public String getName();

    /**
     * Returns type of a measure.
     *
     * 
     * @return The runtime of the measure
     */
    public String getType();

}
