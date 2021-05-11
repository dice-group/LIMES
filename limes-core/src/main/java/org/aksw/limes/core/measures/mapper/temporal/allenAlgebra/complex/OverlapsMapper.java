/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.BeginBegin;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.EndBegin;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.EndEnd;

import java.util.*;

public class OverlapsMapper extends AllenAlgebraMapper {
    /**
     * Constructor of OverlapsMapper class.
     */
    public OverlapsMapper() {
        // (BB1 & EE1) \ (EB0 U EB1)
        this.getRequiredAtomicRelations().add(1);
        this.getRequiredAtomicRelations().add(7);
        this.getRequiredAtomicRelations().add(4);
        this.getRequiredAtomicRelations().add(5);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "OverlapBefore: (BB1 & EE1) \\ (EB0 U EB1)";
    }

    /**
     * Maps each source instance to a set of target instances that is overlapped
     * by the aforementioned source instance, using the EndBegin, BeginBegin and
     * EndEnd Allen relations. The mapping contains 1-to-m relations. A source
     * event is linked to a target event if the begin date of the source event
     * is the same as the begin date of the target event and the end date of the
     * source event is the same as the end date of the target event and the end
     * date of the source event is higher than the begin date of the target
     * event.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        AMapping m = MappingFactory.createDefaultMapping();
        TreeMap<String, Set<String>> mapBB1 = maps.get(0);
        TreeMap<String, Set<String>> mapEE1 = maps.get(1);

        TreeMap<String, Set<String>> mapEB0 = maps.get(2);
        TreeMap<String, Set<String>> mapEB1 = maps.get(3);

        for (Map.Entry<String, Set<String>> entryBB1 : mapBB1.entrySet()) {
            // get targets from EB1
            String instanceBB1 = entryBB1.getKey();
            Set<String> setBB1 = entryBB1.getValue();

            Set<String> setEE1 = mapEE1.get(instanceBB1);
            if (setEE1 == null)
                setEE1 = new TreeSet<String>();

            Set<String> intersection = AllenAlgebraMapper.intersection(setBB1, setEE1);

            Set<String> setEB0 = mapEB0.get(instanceBB1);
            Set<String> setEB1 = mapEB1.get(instanceBB1);
            if (setEB0 == null)
                setEB0 = new TreeSet<String>();
            if (setEB1 == null)
                setEB1 = new TreeSet<String>();
            Set<String> union = AllenAlgebraMapper.union(setEB0, setEB1);

            Set<String> difference = AllenAlgebraMapper.difference(intersection, union);

            if (!difference.isEmpty()) {
                for (String targetInstanceUri : difference) {
                    m.add(instanceBB1, targetInstanceUri, 1);
                }
            }
        }
        return m;
    }

    /**
     * Maps each source instance to a set of target instances that is overlapped
     * by the aforementioned source instance, using the EndBegin, BeginBegin and
     * EndEnd Allen relations.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
                               double threshold) {
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
        EndEnd ee = new EndEnd();
        BeginBegin bb = new BeginBegin();
        EndBegin eb = new EndBegin();
        // (BB1 & EE1) \ (EB0 U EB1)
        maps.add(bb.getPredecessorEvents(source, target, expression));
        maps.add(ee.getPredecessorEvents(source, target, expression));

        maps.add(eb.getConcurrentEvents(source, target, expression));
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
