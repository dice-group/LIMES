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
 * Class for Allen's temporal relation "Meets". Given two events X and Y, it
 * implements X m Y.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class MeetsMapper extends AllenAlgebraMapper {
    /**
     * Constructor of MeetsMapper class.
     */
    public MeetsMapper() {
        // EB0
        this.getRequiredAtomicRelations().add(4);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Meets";
    }

    /**
     * Maps each source instance to a set of target instances that began
     * strictly after the aforementioned source instance, using the EndBegin
     * Allen relation. The mapping contains 1-to-m relations. A source event is
     * linked to a target event if the end date of the source event is the same
     * as the end date of the target event.
     * 
     * @return a mapping, the resulting mapping
     */
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

    /**
     * Maps each source instance to a set of target instances that began
     * strictly after the aforementioned source instance, using the EndBegin
     * Allen relation. The mapping contains 1-to-m relations.
     * 
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
        EndBegin eb = new EndBegin();
        // EB0
        maps.add(eb.getConcurrentEvents(source, target, expression));
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
