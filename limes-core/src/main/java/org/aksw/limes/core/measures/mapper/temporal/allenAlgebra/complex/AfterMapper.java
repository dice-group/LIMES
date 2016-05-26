package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.BeginEnd;

import java.util.HashSet;
import java.util.Set;

public class AfterMapper extends AllenAlgebraMapper {
    Cache source;
    Cache target;

    public AfterMapper() {
        // SxT \ (BE0 U BE1)
        this.getRequiredAtomicRelations().add(2);
        this.getRequiredAtomicRelations().add(3);
    }

    @Override
    public String getName() {
        return "After";
    }

    @Override
    public Mapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        Mapping m = new MemoryMapping();
        TreeMap<String, Set<String>> mapBE0 = maps.get(0);
        TreeMap<String, Set<String>> mapBE1 = maps.get(1);

        Set<String> sources = new HashSet<String>();
        sources.addAll(source.getAllUris());

        Set<String> targets = new HashSet<String>();
        targets.addAll(target.getAllUris());

        for (String sourceInstance : sources) {

            Set<String> setBE0 = mapBE0.get(sourceInstance);
            Set<String> setBE1 = mapBE1.get(sourceInstance);
            if (setBE0 == null)
                setBE0 = new TreeSet<String>();
            if (setBE1 == null)
                setBE1 = new TreeSet<String>();

            Set<String> union = AllenAlgebraMapper.union(setBE0, setBE1);
            Set<String> difference = AllenAlgebraMapper.difference(targets, union);

            if (!difference.isEmpty()) {
                for (String targetInstanceUri : difference) {
                    m.add(sourceInstance, targetInstanceUri, 1);
                }
            }
        }
        return m;
    }

    @Override
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        this.source = source;
        this.target = target;
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();

        BeginEnd be = new BeginEnd();
        // SxT \ (BE0 U BE1)
        maps.add(be.getConcurrentEvents(source, target, expression));
        maps.add(be.getPredecessorEvents(source, target, expression));

        Mapping m = getMapping(maps);
        return m;
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
