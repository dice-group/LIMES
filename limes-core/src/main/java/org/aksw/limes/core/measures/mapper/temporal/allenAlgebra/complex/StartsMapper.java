package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.BeginBegin;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.EndEnd;

public class StartsMapper extends AllenAlgebraMapper {
    public StartsMapper() {
	// BB0 & EE1
	this.getRequiredAtomicRelations().add(0);
	this.getRequiredAtomicRelations().add(7);
    }

    @Override
    public String getName() {
	return "Starts";
    }

    @Override
    public Mapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
	Mapping m = MappingFactory.createDefaultMapping();

	TreeMap<String, Set<String>> mapBB0 = maps.get(0);
	TreeMap<String, Set<String>> mapEE1 = maps.get(1);

	for (Map.Entry<String, Set<String>> entryBB0 : mapBB0.entrySet()) {

	    String instancBB0 = entryBB0.getKey();
	    Set<String> setBB0 = entryBB0.getValue();
	    Set<String> setEE1 = mapEE1.get(instancBB0);
	    if (setEE1 == null)
		setEE1 = new TreeSet<String>();

	    Set<String> intersection = AllenAlgebraMapper.intersection(setBB0, setEE1);

	    if (!intersection.isEmpty()) {
		for (String targetInstanceUri : intersection) {
		    m.add(instancBB0, targetInstanceUri, 1);
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
	// BB0 & EE1
	maps.add(bb.getConcurrentEvents(source, target, expression));
	maps.add(ee.getPredecessorEvents(source, target, expression));
	
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
