package org.aksw.limes.core.measures.mapper.topology.cobalt;

import org.locationtech.jts.geom.Envelope;

public class CobaltMeasures {

    public static final String EQUALS = "equals";
    public static final String DISJOINT = "disjoint";
    public static final String INTERSECTS = "intersects";
    public static final String TOUCHES = "touches";
    public static final String WITHIN = "within";
    public static final String CONTAINS = "contains";
    public static final String OVERLAPS = "overlaps";
    public static final String COVERS = "covers";
    public static final String COVEREDBY = "coveredby";

    public static double area(Envelope mbb) {
        return mbb.getArea();
    }

    public static double diagonal(Envelope mbb) {
        return Math.sqrt(mbb.getHeight() * mbb.getHeight() + mbb.getWidth() * mbb.getWidth());
    }

    //Creates the union mbb of two mbbs
    public static Envelope union(Envelope mbbA, Envelope mbbB) {
        return new Envelope(
                Math.min(mbbA.getMinX(), mbbB.getMinX()),
                Math.max(mbbA.getMaxX(), mbbB.getMaxX()),
                Math.min(mbbA.getMinY(), mbbB.getMinY()),
                Math.max(mbbA.getMaxY(), mbbB.getMaxY())
        );
    }

    //Creates the intersection mbb of two non-disjoint mbbs
    public static Envelope intersection(Envelope mbbA, Envelope mbbB) {
        return mbbA.intersection(mbbB);
    }

    //Checks if the two envelopes overlap on the x-axis
    public static boolean projectionX(Envelope mbbA, Envelope mbbB) {
        if (mbbA.getMinX() > mbbB.getMaxX()) {
            return false;
        } else if (mbbA.getMaxX() < mbbB.getMinX()) {
            return false;
        } else {
            return true;
        }
    }

    //Checks if the two envelopes overlap on the y-axis
    public static boolean projectionY(Envelope mbbA, Envelope mbbB) {
        if (mbbA.getMinY() > mbbB.getMaxY()) {
            return false;
        } else if (mbbA.getMaxY() < mbbB.getMinY()) {
            return false;
        } else {
            return true;
        }
    }

    //Computes the distance between two mbbs
    public static double distance(Envelope mbbA, Envelope mbbB) {
        if (!projectionX(mbbA, mbbB) && !projectionY(mbbA, mbbB)) {
            return Math.sqrt(
                    Math.pow(
                            Math.min(
                                    Math.abs(mbbA.getMinX() - mbbB.getMaxX()),
                                    Math.abs(mbbA.getMaxX() - mbbB.getMinX())
                            ), 2
                    ) + Math.pow(
                            Math.min(
                                    Math.abs(mbbA.getMinY() - mbbB.getMaxY()),
                                    Math.abs(mbbA.getMaxY() - mbbB.getMinY())
                            ), 2
                    )
            );
        } else if (union(mbbA, mbbB).getArea() == Math.max(mbbA.getArea(), mbbB.getArea())) {
            return -Math.min(
                    Math.min(
                            Math.abs(mbbA.getMinX() - mbbB.getMinX()),
                            Math.abs(mbbA.getMaxX() - mbbB.getMaxX())
                    ),
                    Math.min(
                            Math.abs(mbbA.getMinY() - mbbB.getMinY()),
                            Math.abs(mbbA.getMaxY() - mbbB.getMaxY())
                    )
            );
        } else if (projectionX(mbbA, mbbB) && !projectionY(mbbA, mbbB)) {
            return Math.min(
                    Math.abs(mbbA.getMinY() - mbbB.getMaxY()),
                    Math.abs(mbbA.getMaxY() - mbbB.getMinY())
            );
        } else if (!projectionX(mbbA, mbbB) && projectionY(mbbA, mbbB)) {
            return Math.min(
                    Math.abs(mbbA.getMinX() - mbbB.getMaxX()),
                    Math.abs(mbbA.getMaxX() - mbbB.getMinX())
            );
        } else {
            return 0.0;
        }
    }

    //Computes the area based function of cobalt
    public static double fA(Envelope mbbA, Envelope mbbB){
        return area(mbbA) / (area(union(mbbA, mbbB)));
    }

    //Computes the diagonal based function of cobalt
    public static double fD(Envelope mbbA, Envelope mbbB){
        return diagonal(mbbA) / (diagonal(union(mbbA, mbbB)));
    }

    //Computes the mixed function of cobalt
    public static double fM(Envelope mbbA, Envelope mbbB){
        return ((area(mbbA) - 2 * area(intersection(mbbA, mbbB))) / area(mbbA))
                + (distance(mbbA, mbbB) / diagonal(mbbA));
    }

}
