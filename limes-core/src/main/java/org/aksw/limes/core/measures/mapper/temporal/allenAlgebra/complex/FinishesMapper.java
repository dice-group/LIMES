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
 * Class for Allen's temporal relation "Finishes". Given two events X and Y, it
 * implements X f Y.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class FinishesMapper extends AllenAlgebraMapper {
    /**
     * Constructor of FinishesMapper class.
     */
    public FinishesMapper() {
        // EE0 \\ (BB0 U BB1)
        this.getRequiredAtomicRelations().add(6);
        this.getRequiredAtomicRelations().add(0);
        this.getRequiredAtomicRelations().add(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Finishes";
    }

    /**
     * Maps each source instance to a set of target instances that began before
     * the aforementioned source instance but finished at the same time, using
     * the BeginBegin and EndEnd atomic Allen relations. The mapping contains
     * 1-to-m relations. A source event is linked to a target event if the begin
     * date of the source event is higher than the begin date of the target
     * event and if the end date of the source event is the same as the end date
     * of the target event.
     * 
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        AMapping m = MappingFactory.createDefaultMapping();

        TreeMap<String, Set<String>> mapEE0 = maps.get(0);
        TreeMap<String, Set<String>> mapBB0 = maps.get(1);
        TreeMap<String, Set<String>> mapBB1 = maps.get(2);

        for (Map.Entry<String, Set<String>> entryEE0 : mapEE0.entrySet()) {

            String instanceEE0 = entryEE0.getKey();
            Set<String> setEE0 = entryEE0.getValue();

            Set<String> setBB0 = mapBB0.get(instanceEE0);
            Set<String> setBB1 = mapBB1.get(instanceEE0);
            if (setBB0 == null)
                setBB0 = new TreeSet<String>();
            if (setBB1 == null)
                setBB1 = new TreeSet<String>();

            Set<String> union = AllenAlgebraMapper.union(setBB0, setBB1);
            Set<String> difference = AllenAlgebraMapper.difference(setEE0, union);

            if (!difference.isEmpty()) {
                for (String targetInstanceUri : difference) {
                    m.add(instanceEE0, targetInstanceUri, 1);
                }
            }

        }
        return m;

    }

    /**
     * Maps each source instance to a set of target instances that began before
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
        // EE0 \\ (BB0 U BB1)
        maps.add(ee.getConcurrentEvents(source, target, expression));
        maps.add(bb.getConcurrentEvents(source, target, expression));
        maps.add(bb.getPredecessorEvents(source, target, expression));

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
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

}
