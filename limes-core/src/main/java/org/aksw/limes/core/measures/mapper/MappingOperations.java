package org.aksw.limes.core.measures.mapper;

import java.math.BigDecimal;

import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.AlgebraicSetOperations;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.EinsteinSetOperations;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.LukasiewiczSetOperations;

// Classes should be enum to ensure they are singletons
public interface MappingOperations {


	/**
	 * difference(a,b)=t-norm(a,-b)
	 */
	default AMapping difference(AMapping map1, AMapping map2) {
		final AMapping map = MappingFactory.createDefaultMapping();
		for (final String key : map1.getMap().keySet()) {
			if (map2.getMap().containsKey(key)) {
				for (final String value : map1.getMap().get(key).keySet()) {
					if (!map2.getMap().get(key).containsKey(value)) {
						// no link means map2 similarity is 0, of which the
						// negation is 1
						// t-norm(a,1) is always a
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

	default AMapping intersection(AMapping map1, AMapping map2) {
		if (map1 == null || map1.size() == 0 || map2 == null || map2.size() == 0) {
			return MappingFactory.createDefaultMapping();
		}
		final AMapping map = MappingFactory.createDefaultMapping();
		for (final String key : map1.getMap().keySet()) {
			if (map2.getMap().containsKey(key)) {
				for (final String value : map1.getMap().get(key).keySet()) {
					if (map2.getMap().get(key).containsKey(value)) {
						final double sim = tNorm(
								BigDecimal.valueOf(
										map1.getMap().get(key).get(value)),
								BigDecimal.valueOf(
										map2.getMap().get(key).get(value)));
						if (sim > 0) {
							map.add(key, value, sim);
						}
					}
				}
			}
		}
		return map;
	}

	default AMapping union(AMapping map1, AMapping map2) {
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
					final double sim = tConorm(
							BigDecimal
							.valueOf(map1.getMap().get(key).get(value)),
							BigDecimal.valueOf(
									map2.getMap().get(key).get(value)));
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
					final double sim = tConorm(
							BigDecimal
							.valueOf(map1.getMap().get(key).get(value)),
							BigDecimal.valueOf(
									map2.getMap().get(key).get(value)));
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

	AMapping difference(AMapping map1, AMapping map2,
			double[] parameters);

	AMapping intersection(AMapping map1, AMapping map2,
			double[] parameters);

	AMapping union(AMapping map1, AMapping map2,
			double[] parameters);

	double tNorm(BigDecimal a, BigDecimal b);

	double tConorm(BigDecimal a, BigDecimal b);

	static MappingOperations getInstanceByEnum(Command c) {
		if (Command.lukasiewicz.contains(c)) {
			return LukasiewiczSetOperations.INSTANCE;
		} else if (Command.algebraic.contains(c)) {
			return AlgebraicSetOperations.INSTANCE;
		} else if (Command.einstein.contains(c)) {
			return EinsteinSetOperations.INSTANCE;
		}
		return CrispSetOperations.INSTANCE;
	}
}
