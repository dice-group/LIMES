/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.measures.measure.space;

import org.aksw.limes.core.io.cache.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a spatial similarity based on the Euclidean distance. The
 * similarity is computed as 1/(1+d), where d is the distance between the two
 * points. Consequently d = 0, sim = 1 and d = Infinity, sim = 0
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class EuclideanMeasure extends ASpaceMeasure {
    static Logger logger = LoggerFactory.getLogger(EuclideanMeasure.class);
    public double dim = 2;

    public void setDimension(int dimension) {
        dim = dimension;
    }

    public double getSimilarity(Object object1, Object object2) {
        String split1[] = ((String) object1).split("\\|");
        String split2[] = ((String) object2).split("\\|");
        double sim = 0;
        double entry;
        for (int i = 0; i < dim; i++) {
            entry = new Double(split1[i]) - new Double(split2[i]);
            sim = sim + entry * entry;
        }
        return 1.0 / (1 + Math.sqrt(sim));
    }

    public String getType() {
        return "spatial";
    }

    public double getSimilarity(Instance instance1, Instance instance2, String properties1, String properties2) {
        String p1[] = properties1.split("\\|");
        String p2[] = properties2.split("\\|");
        double sim = 0;
        double min;
        double entry;
        for (int i = 0; i < p1.length; i++) {
            min = Integer.MAX_VALUE;
            for (String value1 : instance1.getProperty(p1[i])) {
                for (String value2 : instance2.getProperty(p2[i])) {
                    try {
                        entry = new Double(value1) - new Double(value2);
                        entry = entry * entry;
                        if (min > entry)
                            min = entry;
                    } catch (Exception e) {
                    }
                }
            }
            sim = sim + min;
        }
        return 1.0 / (1 + Math.sqrt(sim));
    }

    public String getName() {
        return "euclidean";
    }

    /**
     * Return the threshold for a dimension. This is used for blocking. Given
     * that the Euclidean metric does not squeeze space as the Mahalanobis does,
     * we simply return the simThreshold
     *
     * @param dimension
     * @param simThreshold
     */
    public double getThreshold(int dimension, double simThreshold) {
        return (1 - simThreshold) / simThreshold;
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}
