package org.aksw.limes.core.measures.mapper.temporal.simpleTemporal;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

/**
 * Implements the successor mapper class.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class SuccessorMapper extends SimpleTemporalMapper {

    /**
     * Maps a set of source instances to their successor target instances. The
     * mapping contains 1-to-m relations. Each source instance takes as
     * successors the set of target instances with the lowest begin date that is
     * higher than the begin date of the source instance.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        AMapping m = MappingFactory.createDefaultMapping();

        TreeMap<String, Set<Instance>> sources = this.orderByBeginDate(source, expression, "source");
        TreeMap<String, Set<Instance>> targets = this.orderByBeginDate(target, expression, "target");

        for (Map.Entry<String, Set<Instance>> sourceEntry : sources.entrySet()) {
            String epochSource = sourceEntry.getKey();

            String higherEpoch = targets.higherKey(epochSource);
            if (higherEpoch != null) {
                Set<Instance> sourceInstances = sourceEntry.getValue();
                Set<Instance> targetInstances = targets.get(higherEpoch);
                for (Instance i : sourceInstances) {
                    for (Instance j : targetInstances) {
                        m.add(i.getUri(), j.getUri(), 1);
                    }
                }
            }
        }

        return m;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Successor";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }


}
