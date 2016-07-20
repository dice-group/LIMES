package org.aksw.limes.core.measures.measure.space;

import org.aksw.limes.core.measures.measure.pointsets.GeoDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class SpaceMeasureFactory {

    static Logger logger = LoggerFactory.getLogger(SpaceMeasureFactory.class);

    public static ISpaceMeasure getMeasure(String name, int dimension) {
        // System.out.println("SpaceMesure.getMeasure("+name+")");
        if (name.toLowerCase().startsWith("geo")) {
            if (dimension != 2) {
                logger.warn("Erroneous dimension settings for GeoDistance (" + dimension + ").");
            }
            return new GeoDistance();
        } else {
            EuclideanMetric measure = new EuclideanMetric();
            measure.setDimension(dimension);
            return measure;
        }
    }
}
