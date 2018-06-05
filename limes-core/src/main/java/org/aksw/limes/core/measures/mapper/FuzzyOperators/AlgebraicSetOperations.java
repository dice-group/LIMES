package org.aksw.limes.core.measures.mapper.FuzzyOperators;

import java.math.BigDecimal;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;

public enum AlgebraicSetOperations implements MappingOperations {

	INSTANCE;

	/**
	 * difference(a,b)=t-norm(a,-b)
	 */
	@Override
	public AMapping difference(AMapping map1, AMapping map2) {
		final AMapping map = MappingFactory.createDefaultMapping();
		for (final String key : map1.getMap().keySet()) {
			if (map2.getMap().containsKey(key)) {
				for (final String value : map1.getMap().get(key).keySet()) {
					if (!map2.getMap().get(key).containsKey(value)) {
						// no link means map2 similarity is 0, of which the
						// negation is 1
						// a*1 is always a
						map.add(key, value, map1.getMap().get(key).get(value));
					} else {
						final double sim = tNorm(
								BigDecimal.valueOf(
										map1.getMap().get(key).get(value)),
								BigDecimal.valueOf(1).subtract(
										BigDecimal.valueOf(map2.getMap()
												.get(key).get(value))));
						if (sim > 0) {
							map.add(key, value, sim);
						}
					}
				}
			} else {
				map.add(key, map1.getMap().get(key));
			}
		}
		return map;
	}

	@Override
	public AMapping intersection(AMapping map1, AMapping map2) {
		if (map1 == null || map1.size() == 0 || map2 == null || map2.size() == 0) {
			return MappingFactory.createDefaultMapping();
		}
		final AMapping map = MappingFactory.createDefaultMapping();
		for (final String key : map1.getMap().keySet()) {
			if (map2.getMap().containsKey(key)) {
				for (final String value : map1.getMap().get(key).keySet()) {
					if (map2.getMap().get(key).containsKey(value)) {
						final double sim = tNorm(map1.getMap().get(key).get(value), map2.getMap().get(key).get(value));
						if (sim > 0) {
							map.add(key, value, sim);
						}
					}
				}
			}
		}
		return map;
	}

	@Override
	public AMapping union(AMapping map1, AMapping map2) {
		if ((map1 == null || map1.size() == 0) && (map2 == null || map2.size() == 0)) {
			return MappingFactory.createDefaultMapping();
		} else if (map1 == null || map1.size() == 0) {
			return map2;
		} else if (map2 == null || map2.size() == 0) {
			return map1;
		}
		final AMapping map = MappingFactory.createDefaultMapping();
		for (final String key : map1.getMap().keySet()) {
			for (final String value : map1.getMap().get(key).keySet()) {
				if (map2.getMap().containsKey(key)) {
					final double sim = tConorm(map1.getMap().get(key).get(value), map2.getMap().get(key).get(value));
					if (sim > 0) {
						map.add(key, value, sim);
					}
				} else {
					map.add(key, value, map1.getMap().get(key).get(value));
				}
			}
		}
		for (final String key : map2.getMap().keySet()) {
			for (final String value : map2.getMap().get(key).keySet()) {
				if (map1.getMap().containsKey(key)) {
					final double sim = tConorm(map1.getMap().get(key).get(value), map2.getMap().get(key).get(value));
					if (sim > 0) {
						map.add(key, value, sim);
					}
				} else {
					map.add(key, value, map2.getMap().get(key).get(value));
				}
			}
		}
		return map;
	}

	/**
	 * Returns algebraic t-norm, i.e. a*b
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public double tNorm(double a, double b) {
		final BigDecimal aExact = BigDecimal.valueOf(a);
		final BigDecimal bExact = BigDecimal.valueOf(b);
		return tNorm(aExact, bExact);
	}

	private double tNorm(BigDecimal a, BigDecimal b) {
		return a.multiply(b).doubleValue();
	}

	/**
	 * Returns algebraic t-conorm, i.e. a+b-a*b
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public double tConorm(double a, double b) {
		final BigDecimal aExact = BigDecimal.valueOf(a);
		final BigDecimal bExact = BigDecimal.valueOf(b);
		return aExact.add(bExact).subtract(aExact.multiply(bExact))
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
