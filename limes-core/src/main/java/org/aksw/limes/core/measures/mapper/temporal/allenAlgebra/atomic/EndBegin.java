package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic;

import org.aksw.limes.core.io.cache.Cache;

import java.util.Set;
import java.util.TreeMap;

/**
 * Atomic class for Allen's Algebra Temporal relations. It orders source events
 * by their end date property and the target events by their begin date
 * property.
 */
public class EndBegin extends AtomicAllenAlgebraMapper {
    public EndBegin() {

    }

    @Override
    public String getName() {
        return "EndBegin";
    }

    @Override
    public TreeMap<String, Set<String>> getConcurrentEvents(Cache source, Cache target, String expression) {
        TreeMap<Long, Set<String>> sources = AtomicAllenAlgebraMapper.orderByEndDate(source, expression);
        TreeMap<Long, Set<String>> targets = AtomicAllenAlgebraMapper.orderByBeginDate(target, expression);
        TreeMap<String, Set<String>> events = AtomicAllenAlgebraMapper.mapConcurrent(sources, targets);
        return events;
    }

    @Override
    public TreeMap<String, Set<String>> getPredecessorEvents(Cache source, Cache target, String expression) {
        TreeMap<Long, Set<String>> sources = AtomicAllenAlgebraMapper.orderByEndDate(source, expression);
        TreeMap<Long, Set<String>> targets = AtomicAllenAlgebraMapper.orderByBeginDate(target, expression);
        TreeMap<String, Set<String>> events = AtomicAllenAlgebraMapper.mapPredecessor(sources, targets);
        return events;
    }

}
