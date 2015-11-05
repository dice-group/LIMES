package org.aksw.limes.core.util;

import org.aksw.limes.core.io.cache.Cache;

/**
 *
 * @author ngonga
 */
public interface DataGenerator {
    public static String LABEL = "label";
    public Cache generateData(int size);
    public String getName();
    //return average string length or value generated
    public double getMean();
    public double getStandardDeviation();
}
