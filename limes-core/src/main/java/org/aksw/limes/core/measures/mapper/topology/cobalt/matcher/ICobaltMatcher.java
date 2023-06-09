package org.aksw.limes.core.measures.mapper.topology.cobalt.matcher;

import org.locationtech.jts.geom.Envelope;

public interface ICobaltMatcher {
   String EQUALS = "equals";
   String DISJOINT = "disjoint";
   String INTERSECTS = "intersects";
   String TOUCHES = "touches";
   String WITHIN = "within";
   String CONTAINS = "contains";
   String OVERLAPS = "overlaps";
   String COVERS = "covers";
   String COVEREDBY = "coveredby";

    boolean relate(Envelope mbrA, Envelope mbrB, String relation);

}
