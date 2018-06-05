package org.aksw.limes.core.measures.mapper.FuzzyOperators;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;

public enum EinsteinSetOperations implements MappingOperations {

	INSTANCE;

	private static final int SCALE = 9;
	/**
	 * Returns einstein t-norm, i.e. (a*b)/(2-(a+b-a*b)
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	@Override
	public double tNorm(BigDecimal a, BigDecimal b) {
		final BigDecimal numerator = a.multiply(b);
		final BigDecimal denominator = BigDecimal.valueOf(2)
				.subtract(a.add(b.subtract(a.multiply(b))));
		return numerator.divide(denominator, SCALE, RoundingMode.HALF_UP)
				.doubleValue();
	}

	/**
	 * Returns einsein t-conorm, i.e. (a+b)/(1+a*b)
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	@Override
	public double tConorm(BigDecimal a, BigDecimal b) {
		final BigDecimal numerator = a.add(b);
		final BigDecimal denominator = BigDecimal.valueOf(1).add(a.multiply(b));
		return numerator.divide(denominator, SCALE, RoundingMode.HALF_UP)
				.doubleValue();
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
