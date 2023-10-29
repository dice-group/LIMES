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
package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implements <a href="https://en.wikipedia.org/wiki/McNemar's_test">McNemar's
 * Test</a>
 *
 * @author Daniel Obraczka
 */
public class McNemarsTest {

    public static double calculate(AMapping a, AMapping b, AMapping reference) {
        int successes = getSuccesses(a, b, reference);
        int failures = getSuccesses(b, a, reference);
        return calculate(successes, failures);
    }

    public static int getSuccesses(AMapping a, AMapping b, AMapping reference) {
        AMapping aRight = MappingOperations.intersection(a, reference);
        AMapping bWrong = MappingOperations.difference(reference, b);
        return MappingOperations.intersection(aRight, bWrong).getSize();
    }

    public static double calculate(int[] successesAndFailures) {
        return calculate(successesAndFailures[0], successesAndFailures[1]);
    }

    public static double calculate(int successes, int failures) {
        if (successes == 0 && failures == 0) {
            return Double.NaN;
        }
        BigDecimal s = BigDecimal.valueOf(successes);
        BigDecimal f = BigDecimal.valueOf(failures);
        double chisquared = s.subtract(f).abs().subtract(BigDecimal.ONE).pow(2)
                .divide(s.add(f), 9, RoundingMode.HALF_UP)
                .doubleValue();
        return 1 - new ChiSquaredDistribution(1).cumulativeProbability(chisquared);
    }

}
