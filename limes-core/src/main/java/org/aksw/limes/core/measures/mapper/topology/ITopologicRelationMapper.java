/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.topology;


import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

import java.util.Set;

/**
 * Interface for mappers that check for RCC-8 topological relations
 *
 * @author ngonga
 */
public interface ITopologicRelationMapper {
    public AMapping getMapping(Set<Polygon> source, Set<Polygon> target);
}
