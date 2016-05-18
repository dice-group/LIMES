package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.BeginBegin;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.EndEnd;

import java.util.Map;
import java.util.Set;

public class DuringReverseMapper extends AllenAlgebraMapper {

    public DuringReverseMapper() {
	// BB1 \\ (EE0 U EE1)
	this.getRequiredAtomicRelations().add(1);
	this.getRequiredAtomicRelations().add(6);
	this.getRequiredAtomicRelations().add(7);
    }

    @Override
    public String getName() {
	return "DuringReverse";

    }

    @Override
    public Mapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
	Mapping m = new MemoryMapping();
	TreeMap<String, Set<String>> mapBB1 = maps.get(0);
	TreeMap<String, Set<String>> mapEE0 = maps.get(1);
	TreeMap<String, Set<String>> mapEE1 = maps.get(2);

	for (Map.Entry<String, Set<String>> entryBB1 : mapBB1.entrySet()) {

	    String instanceBB1 = entryBB1.getKey();
	    Set<String> setBB1 = entryBB1.getValue();

	    Set<String> setEE0 = mapEE0.get(instanceBB1);
	    Set<String> setEE1 = mapEE1.get(instanceBB1);
	    if (setEE0 == null)
		setEE0 = new TreeSet<String>();
	    if (setEE1 == null)
		setEE1 = new TreeSet<String>();

	    Set<String> union = AllenAlgebraMapper.union(setEE0, setEE1);
	    Set<String> difference = AllenAlgebraMapper.difference(setBB1, union);

	    if (!difference.isEmpty()) {
		for (String targetInstanceUri : difference) {
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
	// BB1 \\ (EE0 U EE1)
	maps.add(bb.getPredecessorEvents(source, target, expression));
	maps.add(ee.getConcurrentEvents(source, target, expression));
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
