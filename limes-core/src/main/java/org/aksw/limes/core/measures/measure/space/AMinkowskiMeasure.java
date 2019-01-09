package org.aksw.limes.core.measures.measure.space;

import org.aksw.limes.core.io.cache.Instance;

/**
 * The Minkowski measure is a parametrized metric. For more information about this metric
 * see <a href="https://en.wikipedia.org/wiki/Minkowski_distance">this wikipedia article</a>.
 *
 * It is defined as (\sum_{i=1}^n |x_i-y_i|^p)^{1/p} for X=(x_1,x_2,...,x_n) and Y=(y_1,y_2,...,y_n) \in R^n
 *
 * There are two popular special cases of the Minkowski distance are the {@link EuclideanMeasure} (p=2)
 * and {@link ManhattanMeasure} (p=1).
 *
 * The similarity of two points is computed as 1/(1+d), where d is the distance between the two
 * points. Consequently d = 0, sim = 1 and d = Infinity, sim = 0.
 *
 * @author Jan Lippert (lippertsjan@gmail.com)
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
abstract class AMinkowskiMeasure extends ASpaceMeasure {
    @Override
    public String getType() {
        return "spatial";
    }

    @Override
    public double getSimilarity(Object object1, Object object2) {
        String split1[] = ((String) object1).split("\\|");
        String split2[] = ((String) object2).split("\\|");

        double distance = 0.0;
        for (int i=0; i<dimension; i++){
            distance += innerPTerm(split1[i], split2[i]);
        }
        distance = outerPTerm(distance);

        return 1.0/(1+distance);
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String properties1, String properties2) {
        String p1[] = properties1.split("\\|");
        String p2[] = properties2.split("\\|");

        double distance = 0;
        for (int i = 0; i < p1.length; i++) {
            double min = Double.MAX_VALUE;
            for (String value1 : instance1.getProperty(p1[i])) {
                for (String value2 : instance2.getProperty(p2[i])) {
                    try {
                        min = Math.min(min, innerPTerm(value1, value2));
                    } catch (Exception e) {
                    }
                }
            }
            distance = distance + min;
        }
        return 1.0 / (1.0 + outerPTerm(distance));
    }

    /**
     * Implementation of the outer p-term.
     * @param sum the sum of over |x_i-y_i|^p for i in 0...n
     * @return the p-th root of the sum
     */
    abstract double outerPTerm(double sum);

    /**
     * Implementation for the inner p-term.
     * @param xi value of x_i
     * @param yi value of y_i
     * @return |x_i-y_i|^p
     */
    abstract double innerPTerm(String xi, String yi);

}
