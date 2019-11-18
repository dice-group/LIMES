package org.aksw.limes.core.evaluation.qualititativeMeasures;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

/**
 * Implements <a href="https://en.wikipedia.org/wiki/McNemar's_test">McNemar's
 * Test</a>
 *
 * @author Daniel Obraczka
 *
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
