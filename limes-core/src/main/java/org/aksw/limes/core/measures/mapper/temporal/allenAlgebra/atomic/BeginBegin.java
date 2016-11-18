package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic;

import java.util.Set;
import java.util.TreeMap;

import org.aksw.limes.core.io.cache.ACache;

/**
 * Atomic class for Allen's temporal relations. It orders both source and target
 * events by their begin property.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class BeginBegin extends AAtomicAllenAlgebraMapper {
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
    public TreeMap<String, Set<String>> getConcurrentEvents(ACache source, ACache target, String expression) {
        TreeMap<Long, Set<String>> sources = AAtomicAllenAlgebraMapper.orderByBeginDate(source, expression, "source");
        TreeMap<Long, Set<String>> targets = AAtomicAllenAlgebraMapper.orderByBeginDate(target, expression, "target");
        TreeMap<String, Set<String>> events = AAtomicAllenAlgebraMapper.mapConcurrent(sources, targets);
        return events;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeMap<String, Set<String>> getPredecessorEvents(ACache source, ACache target, String expression) {
        TreeMap<Long, Set<String>> sources = AAtomicAllenAlgebraMapper.orderByBeginDate(source, expression, "source");
        TreeMap<Long, Set<String>> targets = AAtomicAllenAlgebraMapper.orderByBeginDate(target, expression, "target");
        TreeMap<String, Set<String>> events = AAtomicAllenAlgebraMapper.mapPredecessor(sources, targets);
        return events;
    }

}
