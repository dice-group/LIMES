/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.topology;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

import java.util.Set;

/**
 * Mapper that checks for the topological relation crosses.
 *
 * @author psmeros
 */
public class CrossesMapper implements ITopologicRelationMapper {

    /**
     * @param sourceData
     *            Set of Polygons
     * @param targetData
     *            Set of Polygons
     * @return Mapping
     */
    @Override
    public AMapping getMapping(Set<Polygon> sourceData, Set<Polygon> targetData) {
        return RADON.getMapping(sourceData, targetData, RADON.CROSSES);
    }
}
