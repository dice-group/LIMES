package org.aksw.limes.core.measures.mapper.FuzzyOperators;

import java.math.BigDecimal;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;

public enum LukasiewiczSetOperations implements MappingOperations {

	INSTANCE;

	/**
	 * Returns lukasiewicz t-norm, i.e. max{a+b-1,0}
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	@Override
	public double tNorm(BigDecimal a, BigDecimal b) {
		final double tmpRes = a.add(b.subtract(BigDecimal.valueOf(1)))
				.doubleValue();
		return Math.max(tmpRes, 0.0);
	}

	/**
	 * Returns lukasiewicz t-conorm, i.e. min{a+b,1}
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	@Override
	public double tConorm(BigDecimal a, BigDecimal b) {
		final double tmpRes = a.add(b).doubleValue();
		return Math.min(tmpRes, 1.0);
	}

	@Override
	public AMapping difference(AMapping map1, AMapping map2, double[] parameters) {
		return difference(map1, map2);
	}

	@Override
	public AMapping intersection(AMapping map1, AMapping map2, double[] parameters) {
		return intersection(map1, map2);
	}

	@Override
	public AMapping union(AMapping map1, AMapping map2, double[] parameters) {
		return union(map1, map2);
	}
}
