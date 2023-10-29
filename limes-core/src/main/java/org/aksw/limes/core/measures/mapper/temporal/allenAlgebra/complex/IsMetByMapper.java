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
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic.BeginEnd;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class for Allen's temporal relation "IsMetBy". Given two events X and Y, it
 * implements X mi Y.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class IsMetByMapper extends AllenAlgebraMapper {
    /**
     * Constructor of IsMetByMapper class.
     */
    public IsMetByMapper() {
        // BE0
        this.getRequiredAtomicRelations().add(2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "IsMetBy";
    }

    /**
     * Maps each source instance to a set of target instances that began
     * strictly before the aforementioned source instance, using the BeginEnd
     * Allen relation. The mapping contains 1-to-m relations. A source event is
     * linked to a target event if the begin date of the source event is the
     * same as the end date of the target event.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
        AMapping m = MappingFactory.createDefaultMapping();
        TreeMap<String, Set<String>> mapBE0 = maps.get(0);
        for (Map.Entry<String, Set<String>> entryBE0 : mapBE0.entrySet()) {
            String instancBE0 = entryBE0.getKey();
            Set<String> setBE0 = entryBE0.getValue();

            for (String targetInstanceUri : setBE0) {
                m.add(instancBE0, targetInstanceUri, 1);
            }
        }
        return m;
    }

    /**
     * Maps each source instance to a set of target instances that began
     * strictly before the aforementioned source instance, using the BeginEnd
     * Allen relation.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
                               double threshold) {
        ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
        BeginEnd be = new BeginEnd();
        // BE0
        maps.add(be.getConcurrentEvents(source, target, expression));
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
