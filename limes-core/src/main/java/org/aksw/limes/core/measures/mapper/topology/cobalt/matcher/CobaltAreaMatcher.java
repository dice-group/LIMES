package org.aksw.limes.core.measures.mapper.topology.cobalt.matcher;

import org.aksw.limes.core.measures.mapper.topology.cobalt.CobaltMeasures;
import org.locationtech.jts.geom.Envelope;

public class CobaltAreaMatcher implements ICobaltMatcher {
    @Override
    public boolean relate(Envelope mbrA, Envelope mbrB, String relation) {
        double X = CobaltMeasures.fA(mbrA, mbrB);
        double Y = CobaltMeasures.fA(mbrB, mbrA);
        double Z = X + Y;

        return relate(X, Y, Z, relation);
    }
    public boolean relate(double X, double Y, double Z, String relation) {
        switch (relation) {
            case EQUALS:
                if (X == 1 && Y == 1 && Z == 2) {
                    return true;
                } else {
                    return false;
                }
            case DISJOINT:
                if (0 < X && X < 1 && 0 < Y && Y < 1 && 0 < Z && Z < 1) {
                    return true;
                } else {
                    return false;
                }
            case INTERSECTS:
                if (relate(X, Y, Z, EQUALS) || relate(X, Y, Z, TOUCHES) || relate(X, Y, Z, CONTAINS)
                        || relate(X, Y, Z, COVERS) || relate(X, Y, Z, COVEREDBY) || relate(X, Y, Z, WITHIN)
                        || relate(X, Y, Z, OVERLAPS)
                ) {
                    return true;
                } else {
                    return false;
                }
            case TOUCHES: //meet
                if (0 < X && X < 1 && 0 < Y && Y < 1 && 0 < Z && Z <= 1) {
                    return true;
                } else {
                    return false;
                }
            case WITHIN://inside
            case COVEREDBY:
                if (0 < X && X < 1 && Y == 1 && 1 < Z && Z < 2) {
                    return true;
                } else {
                    return relate(X, Y, Z, EQUALS);
                }
            case CONTAINS:
            case COVERS:
                if (X == 1 && 0 < Y && Y < 1 && 1 < Z && Z < 2) {
                    return true;
                } else {
                    return relate(X, Y, Z, EQUALS);
                }
            case OVERLAPS:
                if (0 < X && X < 1 && 0 < Y && Y < 1 && 0 < Z && Z < 2) {
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

}
