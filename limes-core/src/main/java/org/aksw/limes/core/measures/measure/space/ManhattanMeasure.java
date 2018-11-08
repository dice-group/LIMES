package org.aksw.limes.core.measures.measure.space;

/**
 * Implements a similarity measure based on the Manhattan distance. A Minkowski
 * measure with p=1.
 *
 * @author Jan Lippert (lippertsjan@gmail.com)
 */
public class ManhattanMeasure extends AMinkowskiMeasure {

    @Override
    double outerPTerm(double sum) {
        return sum;
    }

    @Override
    double innerPTerm(String xi, String yi) {
        return Math.abs(new Double(xi) - new Double(yi));
    }

    @Override
    public String getName() {
        return "manhattan";
    }

    @Override
    public double getThreshold(int dimension, double simThreshold) {
        return (1 - simThreshold) / simThreshold;
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}
