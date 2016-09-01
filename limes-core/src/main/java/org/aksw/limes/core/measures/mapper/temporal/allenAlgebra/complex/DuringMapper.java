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
 * Class for Allen's temporal relation "During". Given two events X and Y, it
 * implements X d Y.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class DuringMapper extends AllenAlgebraMapper {
    /**
     * Constructor of DuringMapper class.
     */
    public DuringMapper() {
        // EE1 \ (BB0 U BB1)
        this.getRequiredAtomicRelations().add(7);
        this.getRequiredAtomicRelations().add(0);
        this.getRequiredAtomicRelations().add(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "During";
    }

    /**
     * Maps each source instance to a set of target instances that occurred
     * during the aforementioned source instance, using the BeginBegin and
     * EndEnd atomic Allen relations. The mapping contains 1-to-m relations. A
     * source event is linked to a target event if the end date of the source
     * event is lower than the end date of the target event and if the begin
     * date of the source event is higher than the begin date of the target
     * event.
     * 
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        AMapping m = MappingFactory.createDefaultMapping();
        TreeMap<String, Set<String>> mapEE1 = maps.get(0);
        TreeMap<String, Set<String>> mapBB0 = maps.get(1);
        TreeMap<String, Set<String>> mapBB1 = maps.get(2);

        for (Map.Entry<String, Set<String>> entryEE1 : mapEE1.entrySet()) {

            String instanceEE1 = entryEE1.getKey();
            Set<String> setEE1 = entryEE1.getValue();

            Set<String> setBB0 = mapBB0.get(instanceEE1);
            Set<String> setBB1 = mapBB1.get(instanceEE1);
            if (setBB0 == null)
                setBB0 = new TreeSet<String>();
            if (setBB1 == null)
                setBB1 = new TreeSet<String>();

            Set<String> union = AllenAlgebraMapper.union(setBB0, setBB1);
            Set<String> difference = AllenAlgebraMapper.difference(setEE1, union);

            if (!difference.isEmpty()) {
                for (String targetInstanceUri : difference) {
                    m.add(instanceEE1, targetInstanceUri, 1);
                }

            }

        }
        return m;

    }

    /**
     * Maps each source instance to a set of target instances that occurred
     * during the aforementioned source instance, using the BeginBegin and
     * EndEnd atomic Allen relations.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
        EndEnd ee = new EndEnd();
        BeginBegin bb = new BeginBegin();
        // EE1 \ (BB0 U BB1)
        maps.add(ee.getPredecessorEvents(source, target, expression));

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
    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }
}
