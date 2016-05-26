package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic;

import java.util.TreeMap;

import org.aksw.limes.core.io.cache.Cache;

import java.util.Set;

/**
 * Atomic class for Allen's Algebra Temporal relations. It orders both source
 * and target events by their end property.
 * 
 */
public class EndEnd extends AtomicAllenAlgebraMapper {
    public EndEnd() {

    }

    @Override
    public String getName() {
        return "EndEnd";
    }

    @Override
    public TreeMap<String, Set<String>> getConcurrentEvents(Cache source, Cache target, String expression) {
        TreeMap<Long, Set<String>> sources = AtomicAllenAlgebraMapper.orderByEndDate(source, expression);
        TreeMap<Long, Set<String>> targets = AtomicAllenAlgebraMapper.orderByEndDate(target, expression);
        TreeMap<String, Set<String>> events = AtomicAllenAlgebraMapper.mapConcurrent(sources, targets);
        return events;
    }

    @Override
    public TreeMap<String, Set<String>> getPredecessorEvents(Cache source, Cache target, String expression) {
        TreeMap<Long, Set<String>> sources = AtomicAllenAlgebraMapper.orderByEndDate(source, expression);
        TreeMap<Long, Set<String>> targets = AtomicAllenAlgebraMapper.orderByEndDate(target, expression);
        TreeMap<String, Set<String>> events = AtomicAllenAlgebraMapper.mapPredecessor(sources, targets);
        return events;
    }
}
