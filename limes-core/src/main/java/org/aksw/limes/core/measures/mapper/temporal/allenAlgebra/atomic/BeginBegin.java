package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic;

import org.aksw.limes.core.io.cache.Cache;

import java.util.Set;
import java.util.TreeMap;

/**
 * Atomic class for Allen's temporal relations. It orders both source
 * and target events by their begin property.
 */
public class BeginBegin extends AtomicAllenAlgebraMapper {
    /**
     * Constructor of BeginBegin class.
     */
    public BeginBegin() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "BeginBegin";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeMap<String, Set<String>> getConcurrentEvents(Cache source, Cache target, String expression) {
        TreeMap<Long, Set<String>> sources = AtomicAllenAlgebraMapper.orderByBeginDate(source, expression);
        TreeMap<Long, Set<String>> targets = AtomicAllenAlgebraMapper.orderByBeginDate(target, expression);
        TreeMap<String, Set<String>> events = AtomicAllenAlgebraMapper.mapConcurrent(sources, targets);
        return events;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeMap<String, Set<String>> getPredecessorEvents(Cache source, Cache target, String expression) {
        TreeMap<Long, Set<String>> sources = AtomicAllenAlgebraMapper.orderByBeginDate(source, expression);
        TreeMap<Long, Set<String>> targets = AtomicAllenAlgebraMapper.orderByBeginDate(target, expression);
        TreeMap<String, Set<String>> events = AtomicAllenAlgebraMapper.mapPredecessor(sources, targets);
        return events;
    }

}
