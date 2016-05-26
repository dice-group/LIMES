package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.EndBegin;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class MeetsMapper extends AllenAlgebraMapper {

    public MeetsMapper() {
        // EB0
        this.getRequiredAtomicRelations().add(4);
    }

    @Override
    public String getName() {
        return "Meets";
    }

    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {

        AMapping m = MappingFactory.createDefaultMapping();

        TreeMap<String, Set<String>> mapEB0 = maps.get(0);

        for (Map.Entry<String, Set<String>> entryEB0 : mapEB0.entrySet()) {

            String instancEB0 = entryEB0.getKey();
            Set<String> setEB0 = entryEB0.getValue();

            for (String targetInstanceUri : setEB0) {
                m.add(instancEB0, targetInstanceUri, 1);
            }
        }
        return m;
    }

    @Override
    public AMapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
                               double threshold) {
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
        EndBegin eb = new EndBegin();
        // EB0
        maps.add(eb.getConcurrentEvents(source, target, expression));
        AMapping m = getMapping(maps);
        return m;
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }
}
