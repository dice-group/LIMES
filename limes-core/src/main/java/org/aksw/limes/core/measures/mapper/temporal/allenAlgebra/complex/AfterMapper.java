package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.BeginEnd;

/**
 * Class for Allen's temporal relation "After".
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class AfterMapper extends AllenAlgebraMapper {
    /**
     * Source cache.
     */
    ACache source;
    /**
     * Target cache.
     */
    ACache target;

    /**
     * Constructor of AfterMapper class.
     */
    public AfterMapper() {
        // SxT \ (BE0 U BE1)
        this.getRequiredAtomicRelations().add(2);
        this.getRequiredAtomicRelations().add(3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "After";
    }

    /**
     * Maps each source instance to a set of target instances that occurred
     * after the aforementioned source instance, using the BeginEnd atomic Allen
     * relation. The mapping contains 1-to-m relations. A source event is linked
     * to a target event if the begin date of the source event is higher than
     * the end date of the target event.
     * 
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        AMapping m = MappingFactory.createDefaultMapping();
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

    /**
     * Maps each source instance to a set of target instances that occurred
     * after the aforementioned source instance, using the BeginEnd atomic Allen
     * relation.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        
        this.source = source;
        this.target = target;
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();

        BeginEnd be = new BeginEnd();
        // SxT \ (BE0 U BE1)
        maps.add(be.getConcurrentEvents(source, target, expression));
        maps.add(be.getPredecessorEvents(source, target, expression));

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
