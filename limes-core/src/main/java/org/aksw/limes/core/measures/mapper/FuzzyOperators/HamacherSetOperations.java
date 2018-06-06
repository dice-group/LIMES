package org.aksw.limes.core.measures.mapper.FuzzyOperators;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.aksw.limes.core.measures.mapper.MappingOperations;

public enum HamacherSetOperations implements MappingOperations {

	INSTANCE;

	/**
	 * Returns hamacher t-norm, i.e. (a*b)/(p+(1-p)*(a+b-a*b))
	 *
	 * @param a
	 * @param b
	 * @param parameter
	 * @return
	 */
	@Override
	public double tNorm(BigDecimal a, BigDecimal b, double parameter) {
		sanityCheck(a, b, parameter, 0.0, Double.MAX_VALUE);
		BigDecimal p = BigDecimal.valueOf(parameter);
		BigDecimal numerator = a.multiply(b);
		BigDecimal denominator = p.add(BigDecimal.valueOf(1).subtract(p).multiply(a.add(b.subtract(a.multiply(b)))));
		return numerator.divide(denominator, SCALE, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Returns hamacher t-conorm, i.e. (a+b+(p-1)*a*b)/(1+p*a*b)
	 *
	 * @param a
	 * @param b
	 * @param parameter
	 * @return
	 */
	@Override
	public double tConorm(BigDecimal a, BigDecimal b, double parameter) {
		sanityCheck(a, b, parameter, -1.0, Double.MAX_VALUE);
		BigDecimal p = BigDecimal.valueOf(parameter);
		BigDecimal numerator = a.add(b).add(p.subtract(BigDecimal.valueOf(1)).multiply(a).multiply(b));
		BigDecimal denominator = BigDecimal.valueOf(1).add(p.multiply(a).multiply(b));
		return numerator.divide(denominator, SCALE, RoundingMode.HALF_UP).doubleValue();
	}
}
