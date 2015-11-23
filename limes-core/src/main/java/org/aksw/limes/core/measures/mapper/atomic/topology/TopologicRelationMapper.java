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


import java.util.Set;

import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;

/**
 * Interface for mappers that check for RCC-8 topological relations
 * @author ngonga
 */
public interface TopologicRelationMapper {
    public Mapping getMapping(Set<Polygon> source, Set<Polygon> target);
}
