package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.complex;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.IMapper.Language;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.atomic.BeginBegin;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.atomic.BeginEnd;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.atomic.EndEnd;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;



public class IsMetBy extends AllenAlgebraMapper {
    public IsMetBy() {
	// BE0
	this.getRequiredAtomicRelations().add(2);
    }

    
    @Override
    public String getName() {
	return "IsMetBy: BE0";
    }

    @Override
    public Mapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
	Mapping m = new MemoryMapping();
	TreeMap<String, Set<String>> mapBE0 = maps.get(0);
	for (Map.Entry<String, Set<String>> entryBE0 : mapBE0.entrySet()) {
	    String instancBE0 = entryBE0.getKey();
	    Set<String> setBE0 = entryBE0.getValue();
	    
	    for (String targetInstanceUri : setBE0) {
		    m.add(instancBE0, targetInstanceUri,1);
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
