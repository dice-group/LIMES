package org.aksw.limes.core.measures.measure.space;

/**
 * Implements a similarity measure based on the euclidean distance. A Minkowski
 * measure with p=1.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class EuclideanMeasure extends AMinkowskiMeasure {

    @Override
    double outerPTerm(double sum) {
        return Math.sqrt(sum);
    }

    @Override
    double innerPTerm(String xi, String yi) {
        double d = new Double(xi) - new Double(yi);
        return d * d;
    }

    @Override
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
