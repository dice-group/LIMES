package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.BeginEnd;

public class IsMetByMapper extends AllenAlgebraMapper {
    public IsMetByMapper() {
	// BE0
	this.getRequiredAtomicRelations().add(2);
    }

    @Override
    public String getName() {
	return "IsMetBy";
    }

    @Override
    public Mapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
	Mapping m = MappingFactory.createDefaultMapping();
	TreeMap<String, Set<String>> mapBE0 = maps.get(0);
	for (Map.Entry<String, Set<String>> entryBE0 : mapBE0.entrySet()) {
	    String instancBE0 = entryBE0.getKey();
	    Set<String> setBE0 = entryBE0.getValue();

	    for (String targetInstanceUri : setBE0) {
		m.add(instancBE0, targetInstanceUri, 1);
	    }
	}
	return m;
    }

    @Override
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {
	ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
	BeginEnd be = new BeginEnd();
	// BE0
	maps.add(be.getConcurrentEvents(source, target, expression));
	Mapping m = getMapping(maps);
	return m;
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }
}
