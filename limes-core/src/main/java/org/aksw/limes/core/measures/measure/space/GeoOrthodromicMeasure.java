/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.space;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.space.ASpaceMeasure;

/**
 * Computes a similarity based on the geo distance of two points. Assumes that
 * the input consists of latitute and longitude of two points. The similarity is
 * computed as 1/(1+d) where d is the distance between the two points.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
@Deprecated
public class GeoOrthodromicMeasure extends ASpaceMeasure {



    public double getThreshold(int dimension, double simThreshold) {
        // transforms the similarity threshold into an angular threshold
        return (1 - simThreshold) / (radius * simThreshold);
    }

    // assume lat|long
    public double getSimilarity(Object object1, Object object2) {
        String p1[] = ((String) object1).split("\\|");
        String p2[] = ((String) object2).split("\\|");

        double lat1 = Double.parseDouble(p1[0]);
        double lon1 = Double.parseDouble(p1[1]);
        double lat2 = Double.parseDouble(p1[0]);
        double lon2 = Double.parseDouble(p2[1]);

        double d = distance(lat1, lon1, lat2, lon2);
        return 1 / (1 + d);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double value1 = Math.pow(Math.sin((lat1 - lat2) / 2.0)* D2R, 2)
                + Math.cos(lat1 * D2R) * Math.cos(lat2 * D2R) * Math.pow(Math.sin((lon1 - lon2) / 2.0)* D2R, 2);
        double c = 2 * Math.atan2(Math.sqrt(value1), Math.sqrt(1 - value1));
        double d = radius * c;
        return d;
    }

    public String getType() {
        return "spatial";
    }

    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        String p1[] = property1.split("\\|");
        String p2[] = property1.split("\\|");
        double lon1, lon2, lat1, lat2;

        if (p1[0].toLowerCase().startsWith("lo")) {
            lon1 = Double.parseDouble(instance1.getProperty(p1[0]).first());
            lat1 = Double.parseDouble(instance1.getProperty(p1[1]).first());
        } else {
            lat1 = Double.parseDouble(instance1.getProperty(p1[0]).first());
            lon1 = Double.parseDouble(instance1.getProperty(p1[1]).first());
        }

        if (p2[0].toLowerCase().startsWith("lo")) {
            lon2 = Double.parseDouble(instance1.getProperty(p2[0]).first());
            lat2 = Double.parseDouble(instance1.getProperty(p2[1]).first());
        } else {
            lat2 = Double.parseDouble(instance1.getProperty(p2[0]).first());
            lon2 = Double.parseDouble(instance1.getProperty(p2[1]).first());
        }

        double d = distance(lat1, lon1, lat2, lon2);
        return 1 / (1 + d);
    }

    public String getName() {
        return "GeoOrthodromicMeasure";
    }

    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}
