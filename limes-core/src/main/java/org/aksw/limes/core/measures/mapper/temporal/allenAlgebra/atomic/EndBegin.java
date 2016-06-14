package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic;

import java.util.Set;
import java.util.TreeMap;

import org.aksw.limes.core.io.cache.Cache;

/**
 * Atomic class for Allen's temporal relations. It orders source events
 * by their end date property and the target events by their begin date
 * property.
 */
public class EndBegin extends AtomicAllenAlgebraMapper {
    public EndBegin() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "EndBegin";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeMap<String, Set<String>> getConcurrentEvents(Cache source, Cache target, String expression) {
        TreeMap<Long, Set<String>> sources = AtomicAllenAlgebraMapper.orderByEndDate(source, expression);
        TreeMap<Long, Set<String>> targets = AtomicAllenAlgebraMapper.orderByBeginDate(target, expression);
        TreeMap<String, Set<String>> events = AtomicAllenAlgebraMapper.mapConcurrent(sources, targets);
        return events;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeMap<String, Set<String>> getPredecessorEvents(Cache source, Cache target, String expression) {
        TreeMap<Long, Set<String>> sources = AtomicAllenAlgebraMapper.orderByEndDate(source, expression);
        TreeMap<Long, Set<String>> targets = AtomicAllenAlgebraMapper.orderByBeginDate(target, expression);
        TreeMap<String, Set<String>> events = AtomicAllenAlgebraMapper.mapPredecessor(sources, targets);
        return events;
    }

}
