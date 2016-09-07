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
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.BeginEnd;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.EndEnd;

/**
 * Class for Allen's temporal relation "IsOverlappedBy". Given two events X and
 * Y, it implements X oi Y.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class IsOverlappedByMapper extends AllenAlgebraMapper {
    /**
     * Constructor of IsOverlappedByMapper class.
     */
    public IsOverlappedByMapper() {
        // { BE1 \ (BB0 U BB1) } \ (EE0 U EE1)
        this.getRequiredAtomicRelations().add(3);
        this.getRequiredAtomicRelations().add(0);
        this.getRequiredAtomicRelations().add(1);
        this.getRequiredAtomicRelations().add(6);
        this.getRequiredAtomicRelations().add(7);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "IsOverlappedBy";
    }

    /**
     * Maps each source instance to a set of target instances that overlap the
     * aforementioned source instance, using the BeginEnd, BeginBegin and EndEnd
     * Allen relations. The mapping contains 1-to-m relations. A source event is
     * linked to a target event if the begin date of the source event is lower
     * than the end date of the target event, the begin date of the source event
     * is higher than the begin date of the target event and the end date of the
     * source instance is higher than the end date of the target event.
     * 
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        AMapping m = MappingFactory.createDefaultMapping();
        TreeMap<String, Set<String>> mapBE1 = maps.get(0);

        TreeMap<String, Set<String>> mapBB0 = maps.get(1);
        TreeMap<String, Set<String>> mapBB1 = maps.get(2);

        TreeMap<String, Set<String>> mapEE0 = maps.get(3);
        TreeMap<String, Set<String>> mapEE1 = maps.get(4);

        for (Map.Entry<String, Set<String>> entryBE1 : mapBE1.entrySet()) {
            // get targets from EB1
            String instanceBE1 = entryBE1.getKey();
            Set<String> setBE1 = entryBE1.getValue();

            Set<String> setBB0 = mapBB0.get(instanceBE1);
            Set<String> setBB1 = mapBB1.get(instanceBE1);
            if (setBB0 == null)
                setBB0 = new TreeSet<String>();
            if (setBB1 == null)
                setBB1 = new TreeSet<String>();
            Set<String> unionBB = AllenAlgebraMapper.union(setBB0, setBB1);
            Set<String> difference1 = AllenAlgebraMapper.difference(setBE1, unionBB);

            Set<String> setEE0 = mapEE0.get(instanceBE1);
            Set<String> setEE1 = mapEE1.get(instanceBE1);
            if (setEE0 == null)
                setEE0 = new TreeSet<String>();
            if (setEE1 == null)
                setEE1 = new TreeSet<String>();
            Set<String> unionEE = AllenAlgebraMapper.union(setEE0, setEE1);

            Set<String> difference2 = AllenAlgebraMapper.difference(difference1, unionEE);

            if (!difference2.isEmpty()) {
                for (String targetInstanceUri : difference2) {
                    m.add(instanceBE1, targetInstanceUri, 1);
                }

            }

        }
        return m;
    }

    /**
     * Maps each source instance to a set of target instances that overlap the
     * aforementioned source instance, using the BeginEnd, BeginBegin and EndEnd
     * Allen relations.
     * 
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
        EndEnd ee = new EndEnd();
        BeginBegin bb = new BeginBegin();
        BeginEnd be = new BeginEnd();
        // { BE1 \ (BB0 U BB1) } \ (EE0 U EE1)
        maps.add(be.getPredecessorEvents(source, target, expression));

        maps.add(bb.getConcurrentEvents(source, target, expression));
        maps.add(bb.getPredecessorEvents(source, target, expression));

        maps.add(ee.getConcurrentEvents(source, target, expression));
        maps.add(ee.getPredecessorEvents(source, target, expression));

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
