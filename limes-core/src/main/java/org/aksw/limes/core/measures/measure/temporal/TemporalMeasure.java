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
package org.aksw.limes.core.measures.measure.temporal;

import org.aksw.limes.core.measures.measure.AMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Implements the temporal measure abstract class.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public abstract class TemporalMeasure extends AMeasure implements ITemporalMeasure {
    private static final Logger logger = LoggerFactory.getLogger(TemporalMeasure.class.getName());

    /**
     * Extract first property (beginDate) from metric expression.
     *
     * @param expression,
     *         metric expression
     * @return first property of metric expression as string
     */
    public String getFirstProperty(String expression) throws IllegalArgumentException {
        int plusIndex = expression.indexOf("|");
        if (expression.indexOf("|") != -1) {
            String p1 = expression.substring(0, plusIndex);
            return p1;
        } else
            return expression;
    }

    /**
     * Extract second property (endDate or machineID) from metric expression.
     *
     * @param expression,
     *         the metric expression
     * @return second property of metric expression as string
     */
    public String getSecondProperty(String expression) throws IllegalArgumentException {
        int plusIndex = expression.indexOf("|");
        if (expression.indexOf("|") != -1) {
            String p1 = expression.substring(plusIndex + 1, expression.length());
            return p1;
        } else{
            logger.error("Second property is missing.");
            throw new IllegalArgumentException();
        }
    }
}
