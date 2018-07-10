package org.aksw.limes.core.measures.mapper.FuzzyOperators;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.fptld.fitness.SimFuzzyRMSE;
import org.apache.commons.math3.util.Pair;

public enum HamacherSetOperations implements MappingOperations {

	INSTANCE;
	private double bestRMSE;
	private double bestP;
	private AMapping bestMapping;
	public static final int maxTries = 10;
	public static final double epsilon = 0.0001;

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

	/**
	 * Optimizes p value using a technique similar to backprop (specificly
	 * quickprop) and returns the mapping result using the best p-value
	 * 
	 * @param a
	 *            first Mapping for intersection
	 * @param b
	 *            second Mapping for intersection
	 * @param ref
	 *            trainingData or reference Mapping
	 * @return pair mapping,best p value
	 */
	public Pair<AMapping, Double> intersection(AMapping a, AMapping b, AMapping ref) {
		if (a == null || a.size() == 0 || b == null || b.size() == 0) {
			return new Pair<AMapping, Double>(MappingFactory.createDefaultMapping(), 1.0);
		}
		bestP = 1.0;
		bestRMSE = 0;
		bestMapping = MappingFactory.createDefaultMapping();
		double p = bestP;
		double errorOld = 0;
		double error = errorOld - 1;
		int tries = 0;
		while (Math.abs(errorOld - error) > epsilon && tries < maxTries) {
			errorOld = error;
			AMapping output = intersection(a, b, p);
			double simrmse = SimFuzzyRMSE.INSTANCE.getSimilarity(output, ref);
			if (simrmse > bestRMSE) {
				bestRMSE = simrmse;
				bestP = p;
				bestMapping = output;
			}
			error = errorTNorm(a, b, output, ref, p);
			p = Math.abs(error / (errorOld - error) * p);
			tries++;
		}
		return new Pair<AMapping, Double>(bestMapping, bestP);
	}

	/**
	 * Optimizes p value using a technique similar to backprop (specificly
	 * quickprop) and returns the mapping result using the best p-value
	 * 
	 * @param a
	 *            first Mapping for union
	 * @param b
	 *            second Mapping for union
	 * @param ref
	 *            trainingData or reference Mapping
	 * @return pair mapping,best p value
	 */
	public Pair<AMapping, Double> union(AMapping a, AMapping b, AMapping ref) {
		if ((a == null || a.size() == 0) && (b == null || b.size() == 0)) {
			return new Pair<AMapping, Double>(MappingFactory.createDefaultMapping(), 1.0);
		} else if (a == null || a.size() == 0) {
			return new Pair<AMapping, Double>(b, 1.0);
		} else if (b == null || b.size() == 0) {
			return new Pair<AMapping, Double>(a, 1.0);
		}
		bestP = 1.0;
		bestRMSE = 0;
		bestMapping = MappingFactory.createDefaultMapping();
		double p = bestP;
		double errorOld = 0;
		double error = errorOld - 1;
		int tries = 0;
		while (Math.abs(errorOld - error) > epsilon && tries < maxTries) {
			errorOld = error;
			AMapping output = union(a, b, p);
			double simrmse = SimFuzzyRMSE.INSTANCE.getSimilarity(output, ref);
			if (simrmse > bestRMSE) {
				bestRMSE = simrmse;
				bestP = p;
				bestMapping = output;
			}
			error = errorTConorm(a, b, output, ref, p);
			p = Math.abs(error / (errorOld - error) * p);
			tries++;
		}
		return new Pair<AMapping, Double>(bestMapping, bestP);
	}

	public Pair<AMapping, Double> difference(AMapping a, AMapping b, AMapping ref) {
		AMapping bNeg = MappingFactory.createDefaultMapping();
		b.getMap().forEach((key, inner) -> {
			inner.forEach((key2, value) -> {
				bNeg.add(key, key2, 1 - value);
			});
		});
		return intersection(a, bNeg, ref);
	}

	/**
	 * Calculates partial derivative of p from the euclidean distance between link
	 * from ref Mapping and result of tNorm(a,b,p)
	 */
	private double errorTNorm(AMapping aMap, AMapping bMap, AMapping intersection, AMapping ref, double p) {
		double error = 0.0;
		for (String s : intersection.getMap().keySet()) {
			for (String t : intersection.getMap().get(s).keySet()) {
				double refValue = ref.getValue(s, t);
				double a = aMap.getValue(s, t);
				double b = bMap.getValue(s, t);
				BigDecimal denominator = BigDecimal.valueOf(a * b * (1 - a - b + a * b)
						* (-tNorm(BigDecimal.valueOf(a), BigDecimal.valueOf(b), p) + refValue));
				BigDecimal numerator = BigDecimal.valueOf((a + b - a * b) * (1 - p) + p).pow(2);
				error += denominator.divide(numerator, SCALE, RoundingMode.HALF_UP).doubleValue();
			}
		}
		return error;
	}

	/**
	 * Calculates partial derivative of p from the euclidean distance between link
	 * from ref Mapping and result of tConorm(a,b,p)
	 */
	private double errorTConorm(AMapping aMap, AMapping bMap, AMapping intersection, AMapping ref, double p) {
		double error = 0.0;
		for (String s : intersection.getMap().keySet()) {
			for (String t : intersection.getMap().get(s).keySet()) {
				double refValue = ref.getValue(s, t);
				double a = aMap.getValue(s, t);
				double b = bMap.getValue(s, t);
				BigDecimal denominator = BigDecimal.valueOf(a * b * (1 - a - b + a * b)
						* (-tNorm(BigDecimal.valueOf(a), BigDecimal.valueOf(b), p) + refValue));
				BigDecimal numerator = BigDecimal.valueOf((a + b - a * b) * (1 - p) + p).pow(2);
				error += denominator.divide(numerator, SCALE, RoundingMode.HALF_UP).doubleValue();
				BigDecimal leftFirstDenominator = BigDecimal.valueOf(a * b * (a + b + a * b * (-1 + p)));
				BigDecimal smallTerm = BigDecimal.valueOf(1 + a * b * p);
				BigDecimal leftFirstNumerator = smallTerm.pow(2);
				BigDecimal leftSecond = BigDecimal.valueOf(a * b).divide(smallTerm, SCALE, RoundingMode.HALF_UP);
				BigDecimal right = BigDecimal.valueOf(a + b + a * b * (-1 + p)).negate()
						.divide(smallTerm, SCALE, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(refValue));
				BigDecimal left = leftFirstDenominator.divide(leftFirstNumerator, SCALE, RoundingMode.HALF_UP)
						.subtract(leftSecond);
				error += left.multiply(right).doubleValue();
			}
		}
		return error;
	}

}
