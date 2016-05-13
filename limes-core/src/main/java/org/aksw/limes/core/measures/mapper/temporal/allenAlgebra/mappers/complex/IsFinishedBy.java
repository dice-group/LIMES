package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.complex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.IMapper.Language;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.atomic.BeginBegin;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.atomic.EndEnd;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class IsFinishedBy extends AllenAlgebraMapper {
    public IsFinishedBy() {
	// BB1 & EE0
	this.getRequiredAtomicRelations().add(1);
	this.getRequiredAtomicRelations().add(6);
    }

  
    @Override
    public String getName() {
	return "IsFinishedBy";
    }

    @Override
    public Mapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
	Mapping m = new MemoryMapping();
	TreeMap<String, Set<String>> mapBB1 = maps.get(0);
	TreeMap<String, Set<String>> mapEE0 = maps.get(1);
	
	for (Map.Entry<String, Set<String>> entryBB1 : mapBB1.entrySet()) {

	    String instanceBB1 = entryBB1.getKey();
	    Set<String> setBB1 = entryBB1.getValue();
	   
	    
	    Set<String> setEE0 = mapEE0.get(instanceBB1);
	    if(setEE0 == null)
		setEE0 = new TreeSet<String>();
	    Set<String> intersection = AllenAlgebraMapper.intersection(setBB1, setEE0);
	    if (!intersection.isEmpty()) {
		for (String targetInstanceUri : intersection) {
			m.add(instanceBB1, targetInstanceUri, 1);
		}
	    }

	}
	return m;
	
    }


    @Override
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {
	ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
	EndEnd ee = new EndEnd();
	BeginBegin bb = new BeginBegin();
	// BB1 & EE0
	maps.add(bb.getPredecessorEvents(source, target, expression));
	maps.add(ee.getConcurrentEvents(source, target, expression));
	
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
