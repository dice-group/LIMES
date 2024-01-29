package org.aksw.limes.core.measures.mapper.topology.im;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;

public class GridSizeHeuristics {

    public final static String AVG = "avg";
    public final static String MIN = "min";
    public final static String MAX = "max";
    public final static String MED = "median";
    public static boolean swap = false;

    public static double[] decideForTheta(GridSizeHeuristics s, GridSizeHeuristics t, String measure) {
        double[] stats;
        switch (measure) {
        case MAX:
            stats = new double[] { s.maxX, s.maxY, t.maxX, t.maxY };
            break;
        case AVG:
            stats = new double[] { s.avgX, s.avgY, t.avgX, t.avgY };
            break;
        case MED:
            stats = new double[] { s.medX, s.medY, t.medX, t.medY };
            break;
        case MIN:
        default:
            stats = new double[] { s.minX, s.minY, t.minX, t.minY };
        }
        double estAreaS = stats[0] * stats[1] * s.size;
        double estAreaT = stats[2] * stats[3] * t.size;
        // we want to swap towards the smallest area coverage to optimizethe
        // number of comparisons
        swap = estAreaS > estAreaT;
        return new double[] { (2.0d) / (stats[0] + stats[2]), (2.0d) / (stats[1] + stats[3]) };
    }

    private double size;
    private double minX;
    private double maxX;
    private double avgX;
    private double medX;
    private double minY;
    private double maxY;
    private double avgY;
    private double medY;

    public GridSizeHeuristics(Collection<Geometry> input) {
        double[] x = new double[input.size()];
        double[] y = new double[input.size()];
        int i = 0;
        for (Geometry geometry : input) {
            Envelope e = geometry.getEnvelopeInternal();
            y[i] = e.getHeight();
            x[i] = e.getWidth();
            i++;
        }
        this.size = input.size();
        Arrays.sort(x);
        this.minX = x[0];
        this.maxX = x[x.length - 1];
        this.avgX = Arrays.stream(x).average().getAsDouble();
        this.medX = x.length % 2 == 0 ? (x[x.length / 2 - 1] + x[x.length / 2]) / 2.0d : x[x.length / 2];
        Arrays.sort(y);
        this.minY = y[0];
        this.maxY = y[y.length - 1];
        this.avgY = Arrays.stream(y).average().getAsDouble();
        this.medY = y.length % 2 == 0 ? (y[y.length / 2 - 1] + y[y.length / 2]) / 2.0d : y[y.length / 2];
    }

    public double getSize() {
        return size;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getAvgX() {
        return avgX;
    }

    public double getMedX() {
        return medX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getAvgY() {
        return avgY;
    }

    public double getMedY() {
        return medY;
    }

    public String toString() {
        DecimalFormat df = new DecimalFormat("0.0000");
        return "[MIN(" + df.format(minX) + ";" + df.format(minY) + ");MAX(" + df.format(maxX) + ";"
        + df.format(maxY) + ";AVG(" + df.format(avgX) + ";" + df.format(avgY) + ");MED(" + df.format(medX)
        + ";" + df.format(medY) + ")]";
    }

}
