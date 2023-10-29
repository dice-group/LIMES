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
package org.aksw.limes.core.measures.measure.resourcesets;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.AMeasure;

import java.util.Set;

/**
 * @author Kevin Dreßler
 * @since 1.0
 */
public class SetJaccardMeasure extends AMeasure {
    @Override
    public double getSimilarity(Object object1, Object object2) {
        if (object1 instanceof Set && object2 instanceof Set) {
            @SuppressWarnings("unchecked")
            Set<String> s = (Set<String>) object1;
            @SuppressWarnings("unchecked")
            Set<String> t = (Set<String>) object2;
            if (s.isEmpty() && t.isEmpty())
                return 1d;
            int matches = 0;
            for (String x : s)
                if (t.contains(x))
                    matches++;
            return matches / ((double) s.size() + (double) t.size() - (double) matches);
        } else {
            return 0d;
        }
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        Set<String> object1 = instance1.getProperty(property1);
        Set<String> object2 = instance2.getProperty(property2);
        return getSimilarity(object1, object2);
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

    @Override
    public String getName() {
        return "set_jaccard";
    }

    @Override
    public String getType() {
        return "resource_sets";
    }
}
