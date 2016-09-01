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
 * Class for Allen's temporal relation "IsStartedBy". Given two events X and Y,
 * it implements X si Y.
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class IsStartedByMapper extends AllenAlgebraMapper {
    /**
     * Constructor of IsStartedByMapper class.
     */
    public IsStartedByMapper() {
        // BB0 \\ (EE0 U EE1)
        this.getRequiredAtomicRelations().add(0);
        this.getRequiredAtomicRelations().add(6);
        this.getRequiredAtomicRelations().add(7);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "IsStartedBy";
    }

    /**
     * Maps each source instance to a set of target instances that begin at the
     * same time of the aforementioned source instance but terminate before,
     * using the BeginBegin and EndEnd Allen relations. The mapping contains
     * 1-to-m relations. A source event is linked to a target event if the begin
     * date of the source event is the same as the begin date of the target
     * event and the end date of the source instance is higher than the end date
     * of the target event.
     * 
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        AMapping m = MappingFactory.createDefaultMapping();

        TreeMap<String, Set<String>> mapBB0 = maps.get(0);
        TreeMap<String, Set<String>> mapEE0 = maps.get(1);
        TreeMap<String, Set<String>> mapEE1 = maps.get(2);

        for (Map.Entry<String, Set<String>> entryBB0 : mapBB0.entrySet()) {

            String instanceBB0 = entryBB0.getKey();
            Set<String> setBB0 = entryBB0.getValue();

            Set<String> setEE0 = mapEE0.get(instanceBB0);
            Set<String> setEE1 = mapEE1.get(instanceBB0);

            if (setEE0 == null)
                setEE0 = new TreeSet<String>();
            if (setEE1 == null)
                setEE1 = new TreeSet<String>();

            Set<String> union = AllenAlgebraMapper.union(setEE0, setEE1);
            Set<String> difference = AllenAlgebraMapper.difference(setBB0, union);

            if (!difference.isEmpty()) {
                for (String targetInstanceUri : difference) {
                    m.add(instanceBB0, targetInstanceUri, 1);
                }
            }
        }
        return m;
    }

    /**
     * Maps each source instance to a set of target instances that begin at the
     * same time of the aforementioned source instance but terminate earlier,
     * using the BeginBegin and EndEnd Allen relations.
     * 
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
        EndEnd ee = new EndEnd();
        BeginBegin bb = new BeginBegin();
        // BB0 \\ (EE0 U EE1)
        maps.add(bb.getConcurrentEvents(source, target, expression));
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
