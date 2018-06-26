package org.aksw.limes.core.ml.algorithm.fptld.fitness;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;

/**
 *
 * calculates Σ(min(a,b))/Σ(max(a,b))
 *
 * @author Daniel Obraczka
 *
 */
public enum SimFuzzyJaccard implements FuzzySimilarity {

	INSTANCE;

	@Override
	public double getSimilarity(AMapping a, AMapping b) {
		BigDecimal numerator = BigDecimal.valueOf(0);
		BigDecimal denominator = BigDecimal.valueOf(0);
		for (final String key : a.getMap().keySet()) {
			if (b.getMap().containsKey(key)) {
				for (final String value : a.getMap().get(key).keySet()) {
					if (b.getMap().get(key).containsKey(value)) {
						numerator = numerator.add(BigDecimal
								.valueOf(Math.min(a.getMap().get(key).get(value), b.getMap().get(key).get(value))));
						denominator = denominator.add(BigDecimal
								.valueOf(Math.max(a.getMap().get(key).get(value), b.getMap().get(key).get(value))));
					} else {
						denominator = denominator.add(BigDecimal.valueOf(a.getMap().get(key).get(value)));
					}
				}
			} else {
				for (final String value : a.getMap().get(key).keySet()) {
					denominator = denominator.add(BigDecimal.valueOf(a.getMap().get(key).get(value)));
				}
			}
		}
		for (final String key : b.getMap().keySet()) {
			for (final String value : b.getMap().get(key).keySet()) {
				if (!a.getMap().keySet().contains(key)) {
					denominator = denominator.add(BigDecimal.valueOf(b.getMap().get(key).get(value)));
				}
			}
		}
		return numerator.divide(denominator, MappingOperations.SCALE, RoundingMode.HALF_UP).doubleValue();
	}

}
