package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.EndBegin;

/**
 * Class for Allen's temporal relation "Before". 
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class BeforeMapper extends AllenAlgebraMapper {
    /**
     * Constructor of BeforeMapper class.
     */
    public BeforeMapper() {
        // EB1
        this.getRequiredAtomicRelations().add(5);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Before";
    }

    /**
     * Maps each source instance to a set of target instances that occurred
     * before the aforementioned source instance, using the EndBegin atomic
     * Allen relation. The mapping contains 1-to-m relations. A source event is
     * linked to a target event if the end date of the source event is lower
     * than the begin date of the target event.
     * 
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        AMapping m = MappingFactory.createDefaultMapping();

        TreeMap<String, Set<String>> mapEB1 = maps.get(0);

        for (Map.Entry<String, Set<String>> entryEB1 : mapEB1.entrySet()) {
            String instancEB1 = entryEB1.getKey();
            Set<String> setEB1 = entryEB1.getValue();

            for (String targetInstanceUri : setEB1) {
                m.add(instancEB1, targetInstanceUri, 1);
            }

        }
        return m;

    }

    /**
     * Maps each source instance to a set of target instances that occurred
     * before the aforementioned source instance, using the EndBegin atomic
     * Allen relation.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
        
        EndBegin eb = new EndBegin();
        // EB1
        maps.add(eb.getPredecessorEvents(source, target, expression));

        AMapping m = getMapping(maps);
        return m;
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
