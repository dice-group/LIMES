package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.complex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.IMapper.Language;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.atomic.BeginBegin;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.atomic.EndBegin;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.atomic.EndEnd;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;



public class Before extends AllenAlgebraMapper {

    public Before() {
	// EB1
	this.getRequiredAtomicRelations().add(5);
    }

   
    @Override
    public String getName() {
	return "Before";
    }

    @Override
    public Mapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
	Mapping m = new MemoryMapping();
	
	TreeMap<String, Set<String>> mapEB1 = maps.get(0);
	
	for (Map.Entry<String, Set<String>> entryEB1 : mapEB1.entrySet()) {
	    String instancEB1 = entryEB1.getKey();
	    Set<String> setEB1 = entryEB1.getValue();
	    
	    for (String targetInstanceUri : setEB1) {
		    m.add(instancEB1, targetInstanceUri,1);
	    }

	}
	return m;

    }
    @Override
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {
	ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
	
	EndBegin eb = new EndBegin();
	// EB1
	maps.add(eb.getConcurrentEvents(source, target, expression));
	
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
