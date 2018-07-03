package org.aksw.limes.core.measures.mapper;

import java.math.BigDecimal;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.exceptions.ParameterOutOfRangeException;
import org.aksw.limes.core.exceptions.UnsupportedOperator;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.AlgebraicSetOperations;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.EinsteinSetOperations;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.HamacherSetOperations;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.LukasiewiczSetOperations;

// Classes should be enum to ensure they are singletons
public interface MappingOperations {

	int SCALE = 9;

	/**
	 * difference(a,b)=t-norm(a,-b)
	 */
	default AMapping difference(AMapping map1, AMapping map2, double parameter) {
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
						final double sim = tNorm(BigDecimal.valueOf(map1.getMap().get(key).get(value)),
								BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(map2.getMap().get(key).get(value))),
								parameter);
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

	default AMapping intersection(AMapping map1, AMapping map2, double parameter) {
		if (map1 == null || map1.size() == 0 || map2 == null || map2.size() == 0) {
			return MappingFactory.createDefaultMapping();
		}
		final AMapping map = MappingFactory.createDefaultMapping();
		for (final String key : map1.getMap().keySet()) {
			if (map2.getMap().containsKey(key)) {
				for (final String value : map1.getMap().get(key).keySet()) {
					if (map2.getMap().get(key).containsKey(value)) {
						final double sim = tNorm(BigDecimal.valueOf(map1.getMap().get(key).get(value)),
								BigDecimal.valueOf(map2.getMap().get(key).get(value)), parameter);
						if (sim > 0) {
							map.add(key, value, sim);
						}
					}
				}
			}
		}
		return map;
	}

	default AMapping union(AMapping map1, AMapping map2, double parameter) {
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
				if (map2.getMap().containsKey(key) && map2.getMap().get(key).get(value) != null) {
					final double sim = tConorm(BigDecimal.valueOf(map1.getMap().get(key).get(value)),
							BigDecimal.valueOf(map2.getMap().get(key).get(value)), parameter);
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
				if (map1.getMap().containsKey(key) && map1.getMap().get(key).get(value) != null) {
					final double sim = tConorm(BigDecimal.valueOf(map1.getMap().get(key).get(value)),
							BigDecimal.valueOf(map2.getMap().get(key).get(value)), parameter);
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

	double tNorm(BigDecimal a, BigDecimal b, double parameter);

	double tConorm(BigDecimal a, BigDecimal b, double parameter);

	default void sanityCheck(BigDecimal a, BigDecimal b) {
		sanityCheck(a, b, 0.0, 0.0, 0.0);
	}

	default void sanityCheck(BigDecimal a, BigDecimal b, double parameter, double parameterMinimum,
			double parameterMaximum) {
		if (a.signum() == -1 || b.signum() == -1 || a.doubleValue() > 1 || b.doubleValue() > 1) {
			throw new ParameterOutOfRangeException(
					"a and b have to be between 0 and 1!\n You provided:\n a: " + a + "\nb: " + b);
		}
		if (parameter < parameterMinimum) {
			throw new ParameterOutOfRangeException("Parameter value " + parameter
					+ " not allowed!\n Parameters must be at least: " + parameterMinimum);
		}
		if (parameter > parameterMaximum) {
			throw new ParameterOutOfRangeException("Parameter value " + parameter
					+ " not allowed!\n Parameters must be at most: " + parameterMaximum);
		}
	}

	static MappingOperations getInstanceByEnum(Command c) {
		if (Command.lukasiewicz.contains(c)) {
			return LukasiewiczSetOperations.INSTANCE;
		} else if (Command.algebraic.contains(c)) {
			return AlgebraicSetOperations.INSTANCE;
		} else if (Command.einstein.contains(c)) {
			return EinsteinSetOperations.INSTANCE;
		} else if (Command.hamacher.contains(c)) {
			return HamacherSetOperations.INSTANCE;
		} else if (Command.crisp.contains(c)) {
			return CrispSetOperations.INSTANCE;
		} else {
			throw new UnsupportedOperator(c.toString());
		}
	}

	static AMapping performOperation(AMapping a, AMapping b, LogicOperator op) {
		return performOperation(a, b, op, Double.NaN);
	}

	static AMapping performOperation(AMapping a, AMapping b, LogicOperator op, double p) {
		AMapping res = null;
		switch (op) {
		case AND:
			res = CrispSetOperations.INSTANCE.intersection(a, b);
			break;
		case OR:
			res = CrispSetOperations.INSTANCE.union(a, b);
			break;
		case DIFF:
			res = CrispSetOperations.INSTANCE.difference(a, b);
			break;
		case MINUS:
			res = CrispSetOperations.INSTANCE.difference(a, b);
			break;
		case LUKASIEWICZT:
			res = LukasiewiczSetOperations.INSTANCE.intersection(a, b, p);
			break;
		case LUKASIEWICZTCO:
			res = LukasiewiczSetOperations.INSTANCE.union(a, b, p);
			break;
		case LUKASIEWICZDIFF:
			res = LukasiewiczSetOperations.INSTANCE.difference(a, b, p);
			break;
		case ALGEBRAICT:
			res = AlgebraicSetOperations.INSTANCE.intersection(a, b, p);
			break;
		case ALGEBRAICTCO:
			res = AlgebraicSetOperations.INSTANCE.union(a, b, p);
			break;
		case ALGEBRAICDIFF:
			res = AlgebraicSetOperations.INSTANCE.difference(a, b, p);
			break;
		case EINSTEINT:
			res = EinsteinSetOperations.INSTANCE.intersection(a, b, p);
			break;
		case EINSTEINTCO:
			res = EinsteinSetOperations.INSTANCE.union(a, b, p);
			break;
		case EINSTEINDIFF:
			res = EinsteinSetOperations.INSTANCE.difference(a, b, p);
			break;
		case HAMACHERT:
			res = HamacherSetOperations.INSTANCE.intersection(a, b, p);
			break;
		case HAMACHERTCO:
			res = HamacherSetOperations.INSTANCE.union(a, b, p);
			break;
		case HAMACHERDIFF:
			res = HamacherSetOperations.INSTANCE.difference(a, b, p);
			break;
		default:
			throw new UnsupportedOperator(op + " can not be used in here");
		}
		return res;
	}
}
