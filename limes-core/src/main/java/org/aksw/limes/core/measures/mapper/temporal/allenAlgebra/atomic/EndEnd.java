package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic;

import java.util.Set;
import java.util.TreeMap;

import org.aksw.limes.core.io.cache.ACache;

/**
 * Atomic class for Allen's temporal relations. It orders both source and target
 * events by their end property.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class EndEnd extends AAtomicAllenAlgebraMapper {
    public EndEnd() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "EndEnd";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeMap<String, Set<String>> getConcurrentEvents(ACache source, ACache target, String expression) {
        TreeMap<Long, Set<String>> sources = AAtomicAllenAlgebraMapper.orderByEndDate(source, expression, "source");
        TreeMap<Long, Set<String>> targets = AAtomicAllenAlgebraMapper.orderByEndDate(target, expression, "target");
        TreeMap<String, Set<String>> events = AAtomicAllenAlgebraMapper.mapConcurrent(sources, targets);
        return events;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeMap<String, Set<String>> getPredecessorEvents(ACache source, ACache target, String expression) {
        TreeMap<Long, Set<String>> sources = AAtomicAllenAlgebraMapper.orderByEndDate(source, expression, "source");
        TreeMap<Long, Set<String>> targets = AAtomicAllenAlgebraMapper.orderByEndDate(target, expression, "target");
        TreeMap<String, Set<String>> events = AAtomicAllenAlgebraMapper.mapPredecessor(sources, targets);
        return events;
    }
}
