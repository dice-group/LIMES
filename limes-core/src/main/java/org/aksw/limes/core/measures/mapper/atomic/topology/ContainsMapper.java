/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic.topology;

<<<<<<< HEAD

=======
import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.io.cache.Cache;
>>>>>>> 04f229403216e5956dd16f2b2e0519c2b5ae47d3
import org.aksw.limes.core.io.mapping.Mapping;


import java.util.*;

import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;

/**
 * Mapper that checks for the topological relation contains.
 * @author psmeros
 */
public class ContainsMapper implements TopologicRelationMapper {

    float theta = 10;

    /**
     *
     * @param sourceData Set of Polygons
     * @param targetData Set of Polygons
     * @return Mapping
     */
    @Override
    public Mapping getMapping(Set<Polygon> sourceData, Set<Polygon> targetData) {
        TopologicalRelationUtils.theta = this.theta;
        return TopologicalRelationUtils.getMapping(sourceData, targetData, TopologicalRelationUtils.CONTAINS);
    }
}
