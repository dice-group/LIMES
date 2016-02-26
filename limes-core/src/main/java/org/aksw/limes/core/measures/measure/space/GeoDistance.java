/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.space;

import org.aksw.limes.core.io.cache.Instance;

/**
 * Computes a similarity based on the geo distance of two points. Assumes that the
 * input consists of latitute and longitude of two points. The similarity is computed
 * as 1/(1+d) where d is the distance between the two points.
 * @author ngonga
 */
public class GeoDistance extends SpaceMeasure {

    int dimension = 2;
    private static double D2R = Math.PI / 180;
    private static double radius = 6367;

    public void setDimension(int n) {
        dimension = n;
    }

    public double getThreshold(int dimension, double simThreshold) {
        //transforms the similarity threshold into an angular threshold
        return (1 - simThreshold)/(radius * simThreshold);
    }

    //assume lat|long
    public double getSimilarity(Object a, Object b) {
        String p1[] = ((String) a).split("\\|");
        String p2[] = ((String) b).split("\\|");

        double lat1 = Double.parseDouble(p1[0]);
        double lon1 = Double.parseDouble(p1[1]);
        double lat2 = Double.parseDouble(p1[0]);
        double lon2 = Double.parseDouble(p2[1]);

        double value1 = Math.pow(Math.sin((lat1 - lat2) / 2.0), 2)
                + Math.cos(lat1 * D2R) * Math.cos(lat2 * D2R) * Math.pow(Math.sin((lon1 - lon2) / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(value1), Math.sqrt(1 - value1));
        double d = radius * c;
        return 1 / (1 + d);
    }

    public String getType() {
        return "spatial";
    }

    public double getSimilarity(Instance a, Instance b, String property1, String property2) {
        String p1[] = property1.split("\\|");
        String p2[] = property1.split("\\|");
        double lon1, lon2, lat1, lat2;

        if (p1[0].toLowerCase().startsWith("lo")) {
            lon1 = Double.parseDouble(a.getProperty(p1[0]).first());
            lat1 = Double.parseDouble(a.getProperty(p1[1]).first());
        } else {
            lat1 = Double.parseDouble(a.getProperty(p1[0]).first());
            lon1 = Double.parseDouble(a.getProperty(p1[1]).first());
        }

        if (p2[0].toLowerCase().startsWith("lo")) {
            lon2 = Double.parseDouble(a.getProperty(p2[0]).first());
            lat2 = Double.parseDouble(a.getProperty(p2[1]).first());
        } else {
            lat2 = Double.parseDouble(a.getProperty(p2[0]).first());
            lon2 = Double.parseDouble(a.getProperty(p2[1]).first());
        }

        double value1 = Math.pow(Math.sin((lat1 - lat2) / 2.0), 2)
                + Math.cos(lat1 * D2R) * Math.cos(lat2 * D2R) * Math.pow(Math.sin((lon1 - lon2) / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(value1), Math.sqrt(1 - value1));
        double d = radius * c;
        return 1 / (1 + d);
    }

    public String getName() {
        return "geodistance";
    }

    public double getRuntimeApproximation(double mappingSize) {
	return mappingSize / 1000d;
    }
}
