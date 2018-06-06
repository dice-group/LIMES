package org.aksw.limes.core.measures.mapper.FuzzyOperators;

import java.math.BigDecimal;

import org.aksw.limes.core.measures.mapper.MappingOperations;

public enum AlgebraicSetOperations implements MappingOperations {

	INSTANCE;

	/**
	 * Returns algebraic t-norm, i.e. a*b
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	@Override
	public double tNorm(BigDecimal a, BigDecimal b, double parameter) {
		sanityCheck(a, b);
		return a.multiply(b).doubleValue();
	}

	/**
	 * Returns algebraic t-conorm, i.e. a+b-a*b
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	@Override
	public double tConorm(BigDecimal a, BigDecimal b, double parameter) {
		sanityCheck(a, b);
		return a.add(b).subtract(a.multiply(b))
				.doubleValue();
	}
}
