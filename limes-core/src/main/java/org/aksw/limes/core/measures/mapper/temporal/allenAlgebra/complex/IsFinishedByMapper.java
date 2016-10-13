package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.BeginBegin;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.EndEnd;

/**
 * Class for Allen's temporal relation "IsFinishedBy". Given two events X and Y,
 * it implements X fi Y.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class IsFinishedByMapper extends AllenAlgebraMapper {
    /**
     * Constructor of IsFinishedByMapper class.
     */
    public IsFinishedByMapper() {
        // BB1 & EE0
        this.getRequiredAtomicRelations().add(1);
        this.getRequiredAtomicRelations().add(6);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "IsFinishedBy";
    }

    /**
     * Maps each source instance to a set of target instances that began after
     * the aforementioned source instance but finished at the same time, using
     * the BeginBegin and EndEnd atomic Allen relations. The mapping contains
     * 1-to-m relations. A source event is linked to a target event if the begin
     * date of the source event is lower than the begin date of the target event
     * and if the end date of the source event is the same as the end date of
     * the target event.
     * 
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        AMapping m = MappingFactory.createDefaultMapping();
        TreeMap<String, Set<String>> mapBB1 = maps.get(0);
        TreeMap<String, Set<String>> mapEE0 = maps.get(1);

        for (Map.Entry<String, Set<String>> entryBB1 : mapBB1.entrySet()) {

            String instanceBB1 = entryBB1.getKey();
            Set<String> setBB1 = entryBB1.getValue();

            Set<String> setEE0 = mapEE0.get(instanceBB1);
            if (setEE0 == null)
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

    /**
     * Maps each source instance to a set of target instances that began after
     * the aforementioned source instance but finished at the same time, using
     * the BeginBegin and EndEnd atomic Allen relations.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
        EndEnd ee = new EndEnd();
        BeginBegin bb = new BeginBegin();
        // BB1 & EE0
        maps.add(bb.getPredecessorEvents(source, target, expression));
        maps.add(ee.getConcurrentEvents(source, target, expression));

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
