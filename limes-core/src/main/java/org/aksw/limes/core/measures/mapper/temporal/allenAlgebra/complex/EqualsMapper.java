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
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.EndEnd;

import java.util.*;

/**
 * Class for Allen's temporal relation "Equals". Given two events X and Y, it
 * implements X = Y.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class EqualsMapper extends AllenAlgebraMapper {
    /**
     * Constructor of EqualsMapper class.
     */
    public EqualsMapper() {
        // BB0 & EE0
        this.getRequiredAtomicRelations().add(0);
        this.getRequiredAtomicRelations().add(6);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Equals";
    }

    /**
     * Maps each source instance to a set of target instances that occurred at
     * the same time as the aforementioned source instance, using the BeginBegin
     * and EndEnd atomic Allen relations. The mapping contains 1-to-m relations.
     * A source event is linked to a target event if the begin date of the
     * source event is the same as the begin date of the target event and if the
     * end date of the source event is the same as the end date of the target
     * event.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        AMapping m = MappingFactory.createDefaultMapping();

        TreeMap<String, Set<String>> mapBB0 = maps.get(0);
        TreeMap<String, Set<String>> mapEE1 = maps.get(1);

        for (Map.Entry<String, Set<String>> entryBB0 : mapBB0.entrySet()) {

            String instanceBB0 = entryBB0.getKey();
            Set<String> setBB0 = entryBB0.getValue();

            Set<String> setEE1 = mapEE1.get(instanceBB0);
            if (setEE1 == null)
                setEE1 = new TreeSet<String>();

            Set<String> intersection = AllenAlgebraMapper.intersection(setBB0, setEE1);

            if (!intersection.isEmpty()) {
                for (String targetInstanceUri : intersection) {
                    m.add(instanceBB0, targetInstanceUri, 1);
                }
            }

        }
        return m;

    }

    /**
     * Maps each source instance to a set of target instances that occurred at
     * the same time as the aforementioned source instance, using the BeginBegin
     * and EndEnd atomic Allen relations.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
                               double threshold) {
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
        EndEnd ee = new EndEnd();
        BeginBegin bb = new BeginBegin();
        // BB0 & EE0
        maps.add(bb.getConcurrentEvents(source, target, expression));
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
