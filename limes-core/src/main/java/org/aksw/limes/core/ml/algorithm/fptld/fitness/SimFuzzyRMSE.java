package org.aksw.limes.core.ml.algorithm.fptld.fitness;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;

public class SimFuzzyRMSE implements FuzzySimilarity {

	@Override
	public double getSimilarity(AMapping a, AMapping b) {
		BigDecimal numerator = BigDecimal.valueOf(0);
		for (final String key : a.getMap().keySet()) {
			if (b.getMap().containsKey(key)) {
				for (final String value : a.getMap().get(key).keySet()) {
					if (b.getMap().get(key).containsKey(value)) {
						BigDecimal deviation = BigDecimal.valueOf(a.getMap().get(key).get(value))
								.subtract(BigDecimal.valueOf(b.getMap().get(key).get(value)));
						numerator = numerator.add(deviation.multiply(deviation));
					} else {
						BigDecimal deviation = BigDecimal.valueOf(a.getMap().get(key).get(value));
						numerator = numerator.add(deviation.multiply(deviation));
					}
				}
			} else {
				for (final String value : a.getMap().get(key).keySet()) {
					BigDecimal deviation = BigDecimal.valueOf(a.getMap().get(key).get(value));
					numerator = numerator.add(deviation.multiply(deviation));
				}
			}
		}
		for (final String key : b.getMap().keySet()) {
			for (final String value : b.getMap().get(key).keySet()) {
				if (!a.getMap().keySet().contains(key)) {
					BigDecimal deviation = BigDecimal.valueOf(b.getMap().get(key).get(value));
					numerator = numerator.add(deviation.multiply(deviation));
				}
			}
		}
		return 1 - Math
				.sqrt(numerator.divide(BigDecimal.valueOf(b.getSize()), MappingOperations.SCALE, RoundingMode.HALF_UP)
						.doubleValue());
	}
}
