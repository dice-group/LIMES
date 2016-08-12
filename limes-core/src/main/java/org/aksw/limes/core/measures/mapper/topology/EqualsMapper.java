/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.topology;

import java.util.Set;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

/**
 * Mapper that checks for the topological relation equals.
 *
 * @author psmeros
 */
public class EqualsMapper extends Mapper implements ITopologicRelationMapper {

    /**
     * @param sourceData
     *            Set of Polygons
     * @param targetData
     *            Set of Polygons
     * @return Mapping
     */
    @Override
    public AMapping getMapping(Set<Polygon> sourceData, Set<Polygon> targetData) {
        return RADON.getMapping(sourceData, targetData, RADON.EQUALS);
    }

    @Override
    public AMapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression, double threshold) {
        return RADON.getMapping(source, target, sourceVar, targetVar, expression, threshold, RADON.EQUALS);
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public String getName() {
        return "top_equals";
    }
}
