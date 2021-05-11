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
package org.aksw.limes.core.measures.mapper.temporal.simpleTemporal;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Implements the predecessor mapper class.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class PredecessorMapper extends SimpleTemporalMapper {

    /**
     * Maps a set of source instances to their predecessor target instances. The
     * mapping contains 1-to-m relations. Each source instance takes as
     * predecessors the set of target instances with the highest begin date that
     * is lower than the begin date of the source instance.
     *
     * @return a mapping, the resulting mapping
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
                               double threshold) {
        AMapping m = MappingFactory.createDefaultMapping();

        TreeMap<String, Set<Instance>> sources = this.orderByBeginDate(source, expression, "source");
        TreeMap<String, Set<Instance>> targets = this.orderByBeginDate(target, expression, "target");

        for (Map.Entry<String, Set<Instance>> sourceEntry : sources.entrySet()) {
            String epochSource = sourceEntry.getKey();

            String lowerEpoch = targets.lowerKey(epochSource);
            if (lowerEpoch != null) {
                Set<Instance> sourceInstances = sourceEntry.getValue();
                Set<Instance> targetInstances = targets.get(lowerEpoch);
                for (Instance i : sourceInstances) {
                    for (Instance j : targetInstances) {
                        m.add(i.getUri(), j.getUri(), 1);
                    }
                }
            }
        }

        return m;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Predecessor";
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
