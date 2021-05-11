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
package org.aksw.limes.core.execution.engine.filter;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Implements the filter interface.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */

public interface IFilter {
    /**
     * Naive filter function for mapping using a threshold as filtering
     * criterion.
     *
     * @param map
     *            Map bearing the results of Link Specification
     * @param threshold
     *            Value of threshold
     * @return a filtered mapping that satisfies sim {@literal >}= threshold
     */
    public AMapping filter(AMapping map, double threshold);

    /**
     * Filter function for mapping using a condition and a threshold as
     * filtering criterion.
     *
     * @param map
     *            Map bearing the results of Link Specification
     * @param condition
     *            The condition for filtering
     * @param threshold
     *            Value of threshold
     * @param source
     *            Source knowledge base
     * @param target
     *            Target knowledge base
     * @param sourceVar
     *            Source property
     * @param targetVar
     *            Target property
     * @return a filtered mapping that satisfies both the condition and the
     *         threshold
     */
    public AMapping filter(AMapping map, String condition, double threshold, ACache source, ACache target,
                           String sourceVar, String targetVar);

    /**
     * Filter function for mapping using a condition and two thresholds as
     * filtering criterion.
     *
     * @param map
     *            map bearing the results of Link Specification
     * @param condition
     *            The condition for filtering
     * @param threshold
     *            Value of the first threshold
     * @param mainThreshold
     *            Value of second threshold
     * @param source
     *            Source knowledge base
     * @param target
     *            Target knowledge base
     * @param sourceVar
     *            Source property
     * @param targetVar
     *            Target property
     * @return a filtered mapping that satisfies both the condition and the
     *         thresholds
     */
    public AMapping filter(AMapping map, String condition, double threshold, double mainThreshold, ACache source,
                           ACache target, String sourceVar, String targetVar);

    /**
     * Reverse filter function for mapping using a condition and two thresholds
     * as filtering criterion.
     *
     * @param map
     *            Map bearing the results of Link Specification
     * @param condition
     *            The condition for filtering
     * @param threshold
     *            Value of the first threshold
     * @param mainThreshold
     *            Value of second threshold
     * @param source
     *            Source knowledge base
     * @param target
     *            Target knowledge base
     * @param sourceVar
     *            Source property
     * @param targetVar
     *            Target property
     * @return a filtered mapping that satisfies both the condition and the
     *         thresholds
     */
    public AMapping reversefilter(AMapping map, String condition, double threshold, double mainThreshold, ACache source,
                                  ACache target, String sourceVar, String targetVar);

    /**
     * Filter for linear combinations when operation is set to "add", given the
     * expression a*sim1 + b*sim2 {@literal >}= t or multiplication given the
     * expression (a*sim1)*(b*sim2) {@literal >}= t, which is not likely to be
     * used.
     *
     * @param map1
     *            Map bearing the results of sim1 {@literal >}= (t-b)/a for add,
     *            sim1 {@literal >}= t/(a*b) for mult
     * @param map2
     *            Map bearing the results of sim2 {@literal >}= (t-a)/b for add,
     *            sim2 {@literal >}= t/(a*b) for mult
     * @param coef1
     *            Value of first coefficient
     * @param coef2
     *            Value of second coefficient
     * @param threshold
     *            Value of threshold
     * @param operation
     *            Mathematical operation
     * @return a filtered mapping that satisfies a*sim1 + b*sim2 {@literal >}= t
     *         for add, (a*sim1)*(b*sim2) {@literal >}= t for mult
     */
    public AMapping filter(AMapping map1, AMapping map2, double coef1, double coef2, double threshold,
                           String operation);
}
