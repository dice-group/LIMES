package org.aksw.limes.core.measures.mapper.FuzzyOperators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.fptld.fitness.SimFuzzyRMSE;
import org.apache.commons.math3.util.Pair;

public enum YagerSetOperations implements MappingOperations {

	INSTANCE;
	public static final int MAX_ITERATIONS = 50;
	public static final double EPSILON = 0.0001;
	public static final double STD_DEV = 1;
	public static final long SEED = 7829;
	public static final double[] INITIAL_VALUES = new double[] { 0, 1, 2, 100 };

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

	/**
	 * Optimizes p-value using random search
	 *
	 * @param a
	 *            first Mapping for intersection
	 * @param b
	 *            second Mapping for intersection
	 * @param ref
	 *            trainingData or reference Mapping
	 * @return pair mapping,best p value
	 */
	public Pair<AMapping, Double> union(AMapping a, AMapping b, AMapping ref) {
		if (a == null || a.size() == 0 || b == null || b.size() == 0) {
			return new Pair<AMapping, Double>(MappingFactory.createDefaultMapping(), 1.0);
		}
		double bestP = 1.0;
		AMapping bestMapping = MappingFactory.createDefaultMapping();
		double bestSim = 0.0;
		Random rand = new Random(SEED);
		int iterations = 0;
		for (double p : INITIAL_VALUES) {
			AMapping m = union(a, b, p);
			double simrmse = SimFuzzyRMSE.INSTANCE.getSimilarity(m, ref);
			if (simrmse > bestSim) {
				bestSim = simrmse;
				bestMapping = m;
				bestP = p;
			}
			iterations++;
		}
		while (bestSim < 1 && iterations < MAX_ITERATIONS) {
			double next = rand.nextGaussian();
			double p = Math.abs(next * Math.pow(STD_DEV, 2) + bestP);
			AMapping m = union(a, b, p);
			double simrmse = SimFuzzyRMSE.INSTANCE.getSimilarity(m, ref);
			if (simrmse > bestSim) {
				bestSim = simrmse;
				bestMapping = m;
				bestP = p;
			}
			iterations++;
		}
		return new Pair<AMapping, Double>(bestMapping, bestP);
	}

	/**
	 * Optimizes p-value using random search
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
		double bestP = 1.0;
		AMapping bestMapping = MappingFactory.createDefaultMapping();
		FMeasure fm = new FMeasure();
		double bestFM = 0.0;
		GoldStandard gs = new GoldStandard(ref);
		Random rand = new Random(SEED);
		int iterations = 0;
		for (double p : INITIAL_VALUES) {
			AMapping m = intersection(a, b, p);
			double currentFM = fm.calculate(m, gs);
			if (currentFM > bestFM) {
				bestFM = currentFM;
				bestMapping = m;
				bestP = p;
			}
			iterations++;
		}
		while (bestFM < 1 && iterations < MAX_ITERATIONS) {
			double next = rand.nextGaussian();
			double p = Math.abs(next * STD_DEV + bestP);
			AMapping m = intersection(a, b, p);
			double currentFM = fm.calculate(m, gs);
			if (currentFM > bestFM) {
				bestFM = currentFM;
				bestMapping = m;
				bestP = p;
			}
			iterations++;
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
}
