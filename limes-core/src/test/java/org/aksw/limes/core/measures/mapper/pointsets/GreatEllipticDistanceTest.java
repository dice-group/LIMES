package org.aksw.limes.core.measures.mapper.pointsets;


import org.aksw.limes.core.measures.measure.pointsets.GeoGreatEllipticMeasure;
import org.junit.Test;

public class GreatEllipticDistanceTest {

    @Test
    public void test() {
        System.out
                .println("Distance between Tokyo (35.765278, 140.385556) and San Francisco (37.618889, -122.618889):");

        double ortho = OrthodromicDistance.getDistanceInDegrees(35.765278, 140.385556, 37.618889, -122.618889);
        System.out.println("Orthodromic Distance:\t" + ortho + " km");

        double eliptic = GeoGreatEllipticMeasure.getDistanceInDegrees(35.765278, 140.385556, 37.618889, -122.618889);
        System.out.println("Elliptic Distance:\t" + eliptic + " km");

        // double geodesic = Geodesic.WGS84.Inverse(35.765278, 140.385556,
        // 37.618889, -122.618889).s12/1000;
        // System.out.println("From Geodesic lib:\t" + geodesic + " km");

        System.out.println("Diff:\t" + (eliptic - ortho) + " km");
    }

}
