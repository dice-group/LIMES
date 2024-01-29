package org.aksw.limes.core.measures.mapper.topology.cobalt.splitting;

import org.aksw.limes.core.measures.mapper.topology.cobalt.matcher.ICobaltMatcher;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CobaltSplitMatcher {

    private Map<String, Envelope[][]> splitA = new ConcurrentHashMap<>();
    private Map<String, Envelope[][]> splitB = new ConcurrentHashMap<>();
    private GeometryFactory factory = new GeometryFactory();
    private int splitTimes;
    private ICobaltSplitter splitter;
    private ICobaltMatcher matcher;


    public CobaltSplitMatcher(int splitTimes, ICobaltSplitter splitter, ICobaltMatcher matcher) {
        this.splitTimes = splitTimes;
        this.splitter = splitter;
        this.matcher = matcher;
    }

    public boolean relate(String uriA, Geometry geoA, String uriB, Geometry geoB, String relation) {
        Envelope[][] splitA;
        if (this.splitA.containsKey(uriA)) {
            splitA = this.splitA.get(uriA);
        } else {
            splitA = splitter.getSplit(geoA, splitTimes);
            this.splitA.put(uriA, splitA);
        }

        Envelope[][] splitB;
        if (this.splitB.containsKey(uriB)) {
            splitB = this.splitB.get(uriB);
        } else {
            splitB = splitter.getSplit(geoB, splitTimes);
            this.splitB.put(uriB, splitB);
        }


        switch (relation) {
            case ICobaltMatcher.EQUALS:
                for (int i = 0; i < splitA.length; i++) {
                    for (int j = 0; j < splitA[i].length; j++) {
                        Envelope eA = splitA[i][j];
                        Envelope eB = splitB[i][j];
                        if (!matcher.relate(eA, eB, ICobaltMatcher.EQUALS) && !(eA.isNull() && eB.isNull()) && !(eA.getArea() == 0 && eB.getArea() == 0)) {
                            return false;
                        }
                    }
                }
                return true;
            case ICobaltMatcher.INTERSECTS:
                for (int i1 = 0; i1 < splitA.length; i1++) {
                    for (int j1 = 0; j1 < splitA[i1].length; j1++) {
                        for (int i2 = 0; i2 < splitB.length; i2++) {
                            for (int j2 = 0; j2 < splitB[i2].length; j2++) {
                                Envelope eA = splitA[i1][j1];
                                Envelope eB = splitB[i2][j2];
                                if (!eA.isNull() && !eB.isNull() && matcher.relate(eA, eB, ICobaltMatcher.INTERSECTS)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;

            case ICobaltMatcher.TOUCHES: //meet
                boolean atLeastOneTouch = false;
                for (int i1 = 0; i1 < splitA.length; i1++) {
                    for (int j1 = 0; j1 < splitA[i1].length; j1++) {
                        for (int i2 = 0; i2 < splitB.length; i2++) {
                            for (int j2 = 0; j2 < splitB[i2].length; j2++) {
                                Envelope eA = splitA[i1][j1];
                                Envelope eB = splitB[i2][j2];
                                if (!eA.isNull() && !eB.isNull() && matcher.relate(eA, eB, ICobaltMatcher.TOUCHES)) {
                                    atLeastOneTouch = true;
                                } else {
                                    if (!eA.isNull() && !eB.isNull() && matcher.relate(eA, eB, ICobaltMatcher.INTERSECTS)) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
                return atLeastOneTouch;
            case ICobaltMatcher.CONTAINS: //All points of B are in some part of A
            case ICobaltMatcher.COVERS:
                return relate(uriB, geoB, uriA, geoA, ICobaltMatcher.WITHIN);

            case ICobaltMatcher.WITHIN: //All points of A are in some part of B
            case ICobaltMatcher.COVEREDBY:
                for (int i1 = 0; i1 < splitA.length; i1++) {
                    for (int j1 = 0; j1 < splitA[i1].length; j1++) {
                        Envelope eA = splitA[i1][j1]; //This piece of a should be contained in the pieces of b it intersects
                        if (!eA.isNull() && !(eA.getArea() == 0)) {
                            ArrayList<Envelope> intersecting = new ArrayList<>();
                            for (int i2 = 0; i2 < splitB.length; i2++) {
                                for (int j2 = 0; j2 < splitB[i2].length; j2++) {
                                    Envelope eB = splitB[i2][j2];
                                    if (matcher.relate(eA, eB, ICobaltMatcher.INTERSECTS)) {
                                        intersecting.add(eB);
                                    }
                                }
                            }
                            if (intersecting.isEmpty()) {
                                return false;
                            }
                            double minX = Double.POSITIVE_INFINITY;
                            double minY = Double.POSITIVE_INFINITY;
                            double maxX = Double.NEGATIVE_INFINITY;
                            double maxY = Double.NEGATIVE_INFINITY;
                            for (Envelope envelope : intersecting) {
                                if (envelope.getMinX() < minX) {
                                    minX = envelope.getMinX();
                                }
                                if (envelope.getMinY() < minY) {
                                    minY = envelope.getMinY();
                                }
                                if (envelope.getMaxX() > maxX) {
                                    maxX = envelope.getMaxX();
                                }
                                if (envelope.getMaxY() > maxY) {
                                    maxY = envelope.getMaxY();
                                }
                            }
                            Envelope boundary = new Envelope(minX, maxX, minY, maxY);
                            if (!matcher.relate(eA, boundary, ICobaltMatcher.WITHIN)) {
                                return false;
                            }
                        }
                    }
                }
                return true;

            case ICobaltMatcher.OVERLAPS:
                //Check if both of them have at least one tile which does not intersect the other
                boolean anyAOutsideB = checkAnyPartOfAOutsideAllPartsOfB(splitA, splitB);
                if (!anyAOutsideB) {
                    return false;
                }
                boolean anyBOutsideA = checkAnyPartOfAOutsideAllPartsOfB(splitB, splitA);
                if (!anyBOutsideA) {
                    return false;
                }
                //Check if they have any common point on the inside (intersects except touch)
                for (int i1 = 0; i1 < splitA.length; i1++) {
                    for (int j1 = 0; j1 < splitA[i1].length; j1++) {
                        for (int i2 = 0; i2 < splitB.length; i2++) {
                            for (int j2 = 0; j2 < splitB[i2].length; j2++) {
                                Envelope eA = splitA[i1][j1];
                                Envelope eB = splitB[i2][j2];
                                if (!eA.isNull() && !eB.isNull() &&
                                        matcher.relate(eA, eB, ICobaltMatcher.CONTAINS) //Contains also captures equals
                                        || matcher.relate(eA, eB, ICobaltMatcher.WITHIN)
                                        || matcher.relate(eA, eB, ICobaltMatcher.OVERLAPS)
                                ) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            default:
                return false;
        }
    }

    private boolean checkAnyPartOfAOutsideAllPartsOfB(Envelope[][] splitA, Envelope[][] splitB) {
        for (int i1 = 0; i1 < splitA.length; i1++) {
            for (int j1 = 0; j1 < splitA[i1].length; j1++) {
                Envelope eA = splitA[i1][j1];
                if (!eA.isNull()) {
                    ArrayList<Envelope> intersecting = new ArrayList<>();
                    for (int i2 = 0; i2 < splitB.length; i2++) {
                        for (int j2 = 0; j2 < splitB[i2].length; j2++) {
                            Envelope eB = splitB[i2][j2];
                            if (matcher.relate(eA, eB, ICobaltMatcher.INTERSECTS)) {
                                intersecting.add(eB);
                            }
                        }
                    }
                    if (intersecting.isEmpty()) {
                        return true;
                    }
                    double minX = Double.POSITIVE_INFINITY;
                    double minY = Double.POSITIVE_INFINITY;
                    double maxX = Double.NEGATIVE_INFINITY;
                    double maxY = Double.NEGATIVE_INFINITY;
                    for (Envelope envelope : intersecting) {
                        if (envelope.getMinX() < minX) {
                            minX = envelope.getMinX();
                        }
                        if (envelope.getMinY() < minY) {
                            minY = envelope.getMinY();
                        }
                        if (envelope.getMaxX() > maxX) {
                            maxX = envelope.getMaxX();
                        }
                        if (envelope.getMaxY() > maxY) {
                            maxY = envelope.getMaxY();
                        }
                    }
                    Envelope boundary = new Envelope(minX, maxX, minY, maxY);
                    if (!matcher.relate(eA, boundary, ICobaltMatcher.WITHIN)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void reset() {
        splitA = new ConcurrentHashMap<>();
        splitB = new ConcurrentHashMap<>();
    }
}
