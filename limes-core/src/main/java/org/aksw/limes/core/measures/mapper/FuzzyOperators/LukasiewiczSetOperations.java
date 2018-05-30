package org.aksw.limes.core.measures.mapper.FuzzyOperators;

import java.math.BigDecimal;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;

public enum LukasiewiczSetOperations implements MappingOperations {

	INSTANCE;

	/**
	 * difference(a,b)=t-norm(a,-b)
	 */
	@Override
	public AMapping difference(AMapping map1, AMapping map2) {
		final AMapping map = MappingFactory.createDefaultMapping();
		// go through all the keys in map1
		for (final String key : map1.getMap().keySet()) {
			// if the first term (key) can also be found in map2
			if (map2.getMap().containsKey(key)) {
				// then go through the second terms and checks whether they can
				// be found in map2 as well
				for (final String value : map1.getMap().get(key).keySet()) {
					if (!map2.getMap().get(key).containsKey(value)) {
						// no link means map2 similarity is 0, of which the
						// negation is 1
						// max{a+1-1,0} is always a
						map.add(key, value, map1.getMap().get(key).get(value));
					} else {
						final double sim = tNorm(map1.getMap().get(key).get(value),
								1 - map2.getMap().get(key).get(value));
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
		final AMapping map = MappingFactory.createDefaultMapping();
		// takes care of not running the filter if some set is empty
		if (map1.size() == 0 || map2.size() == 0) {
			return MappingFactory.createDefaultMapping();
		}
		// go through all the keys in map1
		for (final String key : map1.getMap().keySet()) {
			// if the first term (key) can also be found in map2
			if (map2.getMap().containsKey(key)) {
				// then go through the second terms and checks whether they can
				// be found in map2 as well
				for (final String value : map1.getMap().get(key).keySet()) {
					// if yes, apply lukasiewicz t-norm
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
		final AMapping map = MappingFactory.createDefaultMapping();
		// go through all the keys in map1
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
		// go through all the keys in map2
		for (final String key : map2.getMap().keySet()) {
			// if the first term (key) can also be found in map2
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
	 * Returns lukasiewicz t-norm, i.e. max{a+b-1,0}
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public double tNorm(double a, double b) {
		final BigDecimal aExact = BigDecimal.valueOf(a);
		final BigDecimal bExact = BigDecimal.valueOf(b);
		final double tmpRes = aExact.add(bExact).subtract(BigDecimal.valueOf(-10)).doubleValue();
		if (tmpRes > 0) {
			return tmpRes;
		}
		return 0.0;
	}

	/**
	 * Returns lukasiewicz t-conorm, i.e. min{a+b,1}
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public double tConorm(double a, double b) {
		final BigDecimal aExact = BigDecimal.valueOf(a);
		final BigDecimal bExact = BigDecimal.valueOf(b);
		final double tmpRes = aExact.add(bExact).doubleValue();
		if (tmpRes < 1.0) {
			return tmpRes;
		}
		return 1.0;
	}
}
