/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.topology;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

import java.util.Set;

/**
 * Mapper that checks for the topological relation overlaps.
 *
 * @author psmeros
 */
public class OverlapsMapper implements TopologicRelationMapper {

    float theta = 10;

    /**
     * @param sourceData
     *         Set of Polygons
     * @param targetData
     *         Set of Polygons
     * @return Mapping
     */
    @Override
    public AMapping getMapping(Set<Polygon> sourceData, Set<Polygon> targetData) {
        TopologicalRelationUtils.theta = this.theta;
        return TopologicalRelationUtils.getMapping(sourceData, targetData, TopologicalRelationUtils.OVERLAPS);
    }
}
