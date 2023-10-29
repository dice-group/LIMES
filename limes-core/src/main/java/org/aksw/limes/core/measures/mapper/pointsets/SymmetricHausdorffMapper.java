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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.pointsets;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.MappingOperations;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class SymmetricHausdorffMapper extends AMapper {
    /**
     * Computes a mapping between a source and a target.
     *
     * @param source
     *         Source cache
     * @param target
     *         Target cache
     * @param sourceVar
     *         Variable for the source dataset
     * @param targetVar
     *         Variable for the target dataset
     * @param expression
     *         Expression to process.
     * @param threshold
     *         Similarity threshold
     * @return A mapping which contains links between the source instances and
     * the target instances
     */
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
                               double threshold) {

        OrchidMapper hm = new OrchidMapper();
        AMapping m1 = hm.getMapping(source, target, sourceVar, targetVar, expression, threshold);
        AMapping m2 = hm.getMapping(target, source, targetVar, sourceVar, expression, threshold);
        m2 = m2.reverseSourceTarget();
        m1 = MappingOperations.intersection(m1, m2);
        return m1;
    }

    public String getName() {
        return "Symmetric Hausdorff";
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return (new OrchidMapper()).getRuntimeApproximation(sourceSize, targetSize, theta, language);
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return (new OrchidMapper()).getMappingSizeApproximation(sourceSize, targetSize, theta, language);
    }


}
