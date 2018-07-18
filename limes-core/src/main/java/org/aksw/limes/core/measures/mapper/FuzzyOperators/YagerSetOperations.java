package org.aksw.limes.core.measures.mapper.FuzzyOperators;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.aksw.limes.core.measures.mapper.MappingOperations;

public enum YagerSetOperations implements MappingOperations {

	INSTANCE;
	public static final int maxTries = 10;
	public static final double epsilon = 0.0001;

	/**
	 * Returns yager t-norm, i.e. <br>
	 * Drastic t-Norm if p = 0 <br>
	 * max(0,1-((1-a)^p+(1-b)^p)^(1/p)) if 0 &lt; p &lt; +Infinity <br>
	 * min(a,b) if p = +Infinity
	 *
	 * @param a
	 * @param b
	 * @param parameter
	 * @return
	 */
	@Override
	public double tNorm(BigDecimal a, BigDecimal b, double parameter) {
		sanityCheck(a, b, parameter, 0.0, Double.POSITIVE_INFINITY);
		if (parameter == 0) {
			if (a.doubleValue() == 1.0) {
				return b.doubleValue();
			}
			if (b.doubleValue() == 1.0) {
				return a.doubleValue();
			}
			return 0;
		}
		if (Double.isInfinite(parameter)) {
			return Math.min(a.doubleValue(), b.doubleValue());
		}
		double oneDivp = BigDecimal.ONE.divide(BigDecimal.valueOf(parameter), SCALE, RoundingMode.HALF_UP)
				.doubleValue();
		BigDecimal left = BigDecimal.valueOf(Math.pow(BigDecimal.ONE.subtract(a).doubleValue(), parameter));
		BigDecimal right = BigDecimal.valueOf(Math.pow(BigDecimal.ONE.subtract(b).doubleValue(), parameter));
		BigDecimal powed = BigDecimal.valueOf(Math.pow(left.add(right).doubleValue(), oneDivp));
		double subtraction = BigDecimal.ONE.subtract(powed).doubleValue();
		return Math.max(0, subtraction);
	}

	/**
	 * Returns yager t-norm, i.e. <br>
	 * Drastic t-CoNorm if p = 0 <br>
	 * min(1,(a^p+b^p)^(1/p)) if 0 &lt; p &lt; +Infinity <br>
	 * max(a,b) if p = +Infinity
	 *
	 * @param a
	 * @param b
	 * @param parameter
	 * @return
	 */
	@Override
	public double tConorm(BigDecimal a, BigDecimal b, double parameter) {
		sanityCheck(a, b, parameter, 0, Double.POSITIVE_INFINITY);
		if (parameter == 0) {
			if (a.doubleValue() == 0.0) {
				return b.doubleValue();
			}
			if (b.doubleValue() == 0.0) {
				return a.doubleValue();
			}
			return 1;
		}
		if (Double.isInfinite(parameter)) {
			return Math.max(a.doubleValue(), b.doubleValue());
		}
		double oneDivp = BigDecimal.ONE.divide(BigDecimal.valueOf(parameter), SCALE, RoundingMode.HALF_UP)
				.doubleValue();
		BigDecimal left = BigDecimal.valueOf(Math.pow(a.doubleValue(), parameter));
		BigDecimal right = BigDecimal.valueOf(Math.pow(b.doubleValue(), parameter));
		double added = left.add(right).doubleValue();
		return Math.min(1, Math.pow(added, oneDivp));
	}

	// TODO find a way to optimize p value
	//
	// /**
	// * Optimizes p value using a technique similar to backprop (specificly
	// * quickprop) and returns the mapping result using the best p-value
	// *
	// * @param a
	// * first Mapping for intersection
	// * @param b
	// * second Mapping for intersection
	// * @param ref
	// * trainingData or reference Mapping
	// * @return pair mapping,best p value
	// */
	// public Pair<AMapping, Double> intersection(AMapping a, AMapping b, AMapping
	// ref) {
	// if (a == null || a.size() == 0 || b == null || b.size() == 0) {
	// return new Pair<AMapping, Double>(MappingFactory.createDefaultMapping(),
	// 1.0);
	// }
	// double bestP = 1.0;
	// double bestRMSE = 0;
	// AMapping bestMapping = MappingFactory.createDefaultMapping();
	// double p = bestP;
	// double errorOld = 0;
	// double error = errorOld - 1;
	// int tries = 0;
	// while (Math.abs(errorOld - error) > epsilon && tries < maxTries) {
	// errorOld = error;
	// System.out.println(p + " : " + errorOld);
	// AMapping output = intersection(a, b, p);
	// double simrmse = SimFuzzyRMSE.INSTANCE.getSimilarity(output, ref);
	// if (simrmse > bestRMSE) {
	// bestRMSE = simrmse;
	// bestP = p;
	// bestMapping = output;
	// }
	// error = errorTNorm(a, b, output, ref, p);
	// p = Math.abs(error / (errorOld - error) * p);
	// tries++;
	// }
	// return new Pair<AMapping, Double>(bestMapping, bestP);
	// }
	//
	// /**
	// * Optimizes p value using a technique similar to backprop (specificly
	// * quickprop) and returns the mapping result using the best p-value
	// *
	// * @param a
	// * first Mapping for union
	// * @param b
	// * second Mapping for union
	// * @param ref
	// * trainingData or reference Mapping
	// * @return pair mapping,best p value
	// */
	// public Pair<AMapping, Double> union(AMapping a, AMapping b, AMapping ref) {
	// if ((a == null || a.size() == 0) && (b == null || b.size() == 0)) {
	// return new Pair<AMapping, Double>(MappingFactory.createDefaultMapping(),
	// 1.0);
	// } else if (a == null || a.size() == 0) {
	// return new Pair<AMapping, Double>(b, 1.0);
	// } else if (b == null || b.size() == 0) {
	// return new Pair<AMapping, Double>(a, 1.0);
	// }
	// double bestP = 1.0;
	// double bestRMSE = 0;
	// AMapping bestMapping = MappingFactory.createDefaultMapping();
	// double p = bestP;
	// double errorOld = 0;
	// double error = errorOld - 1;
	// int tries = 0;
	// while (Math.abs(errorOld - error) > epsilon && tries < maxTries) {
	// errorOld = error;
	// AMapping output = union(a, b, p);
	// double simrmse = SimFuzzyRMSE.INSTANCE.getSimilarity(output, ref);
	// if (simrmse > bestRMSE) {
	// bestRMSE = simrmse;
	// bestP = p;
	// bestMapping = output;
	// }
	// error = errorTConorm(a, b, output, ref, p);
	// p = Math.abs(error / (errorOld - error) * p);
	// if (p == 0) {
	// p += epsilon;
	// }
	// tries++;
	// }
	// return new Pair<AMapping, Double>(bestMapping, bestP);
	// }
	//
	// public Pair<AMapping, Double> difference(AMapping a, AMapping b, AMapping
	// ref) {
	// AMapping bNeg = MappingFactory.createDefaultMapping();
	// b.getMap().forEach((key, inner) -> {
	// inner.forEach((key2, value) -> {
	// bNeg.add(key, key2, 1 - value);
	// });
	// });
	// return intersection(a, bNeg, ref);
	// }
	//
	// /**
	// * Calculates partial derivative of p from the euclidean distance between link
	// * from ref Mapping and result of tNorm(a,b,p)
	// */
	// private double errorTNorm(AMapping aMap, AMapping bMap, AMapping
	// intersection, AMapping ref, double p) {
	// double error = 0.0;
	// for (String s : intersection.getMap().keySet()) {
	// for (String t : intersection.getMap().get(s).keySet()) {
	// double refValue = ref.getValue(s, t);
	// double a = aMap.getValue(s, t);
	// double b = bMap.getValue(s, t);
	// // 1/p
	// double oneDivp = BigDecimal.ONE.divide(BigDecimal.valueOf(p), SCALE,
	// RoundingMode.HALF_UP)
	// .doubleValue();
	// // (1-a)^p
	// BigDecimal aSubbed = BigDecimal
	// .valueOf(Math.pow(BigDecimal.ONE.subtract(BigDecimal.valueOf(a)).doubleValue(),
	// p));
	// // (1-b)^p
	// BigDecimal bSubbed = BigDecimal
	// .valueOf(Math.pow(BigDecimal.ONE.subtract(BigDecimal.valueOf(b)).doubleValue(),
	// p));
	// // (1-a)^p + (1-b)^p
	// double addedAB = aSubbed.add(bSubbed).doubleValue();
	// // ((1-a)^p + (1-b)^p)^(1/p)
	// BigDecimal powed = BigDecimal.valueOf(Math.pow(addedAB, oneDivp));
	// if (powed.doubleValue() < 1) {
	// // -(ref-Max(0,1-((1-a)^p+(1-b)^p)^(1/p)))
	// BigDecimal leftMultplicand = BigDecimal.valueOf(refValue)
	// .subtract(BigDecimal.valueOf(Math.max(0,
	// BigDecimal.ONE.subtract(powed).doubleValue())))
	// .negate();
	//
	// BigDecimal rightLeft = powed.negate();
	// //
	// -(powed)*(-(Log(addedAb)/p^2)+((aSubbed*Log(1-a)+bSubbed*Log(1-b))/(addedAb*p)))
	// BigDecimal rightRight = BigDecimal.valueOf(Math.log(addedAB))
	// .divide(BigDecimal.valueOf(Math.pow(p, 2)), SCALE,
	// RoundingMode.HALF_UP).negate()
	// .add(aSubbed.multiply(BigDecimal.valueOf(Math.log(1 - a)))
	// .add(bSubbed.multiply(BigDecimal.valueOf(Math.log(1 - b))))
	// .divide(BigDecimal.valueOf(addedAB).multiply(BigDecimal.valueOf(p)), SCALE,
	// RoundingMode.HALF_UP));
	// BigDecimal right = rightLeft.multiply(rightRight);
	// error += leftMultplicand.multiply(right).doubleValue();
	// } else {
	// error += 0;
	// }
	// }
	// }
	// return error;
	// }
	//
	// /**
	// * Calculates partial derivative of p from the euclidean distance between link
	// * from ref Mapping and result of tConorm(a,b,p)
	// */
	// private double errorTConorm(AMapping aMap, AMapping bMap, AMapping
	// intersection, AMapping ref, double p) {
	// double error = 0.0;
	// for (String s : intersection.getMap().keySet()) {
	// for (String t : intersection.getMap().get(s).keySet()) {
	// double refValue = ref.getValue(s, t) + epsilon;
	// double a = aMap.getValue(s, t) + epsilon;
	// double b = bMap.getValue(s, t) + epsilon;
	// // 1/p
	// double oneDivp = BigDecimal.ONE.divide(BigDecimal.valueOf(p), SCALE,
	// RoundingMode.HALF_UP)
	// .doubleValue();
	// // a^p
	// BigDecimal aPow = BigDecimal.valueOf(Math.pow(a, p));
	// // b^p
	// BigDecimal bPow = BigDecimal.valueOf(Math.pow(b, p));
	// // (1-a)^p + (1-b)^p
	// double addedAB = aPow.add(bPow).doubleValue();
	// // (a^p + b^p)^(1/p)
	// BigDecimal powed = BigDecimal.valueOf(Math.pow(addedAB, oneDivp));
	// if (powed.doubleValue() >= 1) {
	// error += 0;
	// } else {
	// // -(ref-Min(1,(a^p+b^p)^(1/p)))
	// BigDecimal leftMultplicand = BigDecimal.valueOf(refValue)
	// .subtract(BigDecimal.valueOf(Math.min(1, powed.doubleValue()))).negate();
	//
	// // powed*((aPow*Log(a)+bPow*Log(b))/((aPow+bPow)*p) - (Log(aPow+bPow)/(p^2))
	// BigDecimal rightRight = powed
	// .multiply(aPow.multiply(BigDecimal.valueOf(Math.log(a)))
	// .add(bPow.multiply(BigDecimal.valueOf(Math.log(b)))))
	// .divide(aPow.add(bPow).multiply(BigDecimal.valueOf(p)), SCALE,
	// RoundingMode.HALF_UP)
	// .subtract(BigDecimal.valueOf(Math.log(aPow.add(bPow).doubleValue()))
	// .divide(BigDecimal.valueOf(Math.pow(p, 2)), SCALE, RoundingMode.HALF_UP));
	// BigDecimal right = powed.multiply(rightRight);
	// error += leftMultplicand.multiply(right).doubleValue();
	// }
	// }
	// }
	// return error;
	// }

}
