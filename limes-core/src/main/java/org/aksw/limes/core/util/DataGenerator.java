package org.aksw.limes.core.util;

import org.aksw.limes.core.io.cache.ACache;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public interface DataGenerator {
    public static String LABEL = "label";

    public ACache generateData(int size);

    public String getName();

    //return average string length or value generated
    public double getMean();

    public double getStandardDeviation();
}
