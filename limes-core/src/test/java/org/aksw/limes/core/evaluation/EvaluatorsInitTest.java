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
package org.aksw.limes.core.evaluation;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertTrue;

public class EvaluatorsInitTest {

    @Test
    public void test() {
        initializeEvaluators();
    }

    public Set<EvaluatorType> initializeEvaluators() {
        Set<EvaluatorType> evaluators = null;
        try {
            evaluators=new TreeSet<EvaluatorType>();
            evaluators.add(EvaluatorType.PRECISION);
            evaluators.add(EvaluatorType.RECALL);
            evaluators.add(EvaluatorType.F_MEASURE);
            evaluators.add(EvaluatorType.P_PRECISION);
            evaluators.add(EvaluatorType.P_RECALL);
            evaluators.add(EvaluatorType.PF_MEASURE);
            evaluators.add(EvaluatorType.ACCURACY);
            return evaluators;
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
        return evaluators;
    }

}
