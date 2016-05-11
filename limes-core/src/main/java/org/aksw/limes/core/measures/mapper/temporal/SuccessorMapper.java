package org.aksw.limes.core.measures.mapper.temporal;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;

public class SuccessorMapper extends EventMapper {
    
    /**
     * Maps a set of source instances to their successor target instances. The
     * mapping contains n-to-m relations. Each source instance takes as
     * successors the set of target instances with the lowest begin date
     * that is higher than the begin date of the source instance.
     * 
     * @author kleanthi
     */
    @Override
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {

	Mapping m = new MemoryMapping();

	TreeMap<String, Set<Instance>> sources = this.orderByBeginDate(source, expression);
	TreeMap<String, Set<Instance>> targets = this.orderByBeginDate(target, expression);

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

    @Override
    public String getName() {
	return "successor";
    }
    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }
    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }

}
