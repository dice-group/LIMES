package org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.dragon.DecisionTree;
import org.aksw.limes.core.ml.algorithm.dragon.Dragon;
import org.aksw.limes.core.ml.algorithm.dragon.TrainingInstance;
import org.aksw.limes.core.ml.algorithm.dragon.Utils.InstanceCalculator;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GiniIndex extends FitnessFunctionDTL {
	protected static Logger logger = LoggerFactory.getLogger(GiniIndex.class);
	private static List<TrainingInstance> fullInstances;
	private static List<TrainingInstance> currentInstances;
	private static Set<Metric> metricExpressions;
	private AMapping currentMapping;
	public static boolean middlePoints = false;

	public GiniIndex() {
		fullInstances = null;
		currentInstances = null;
		metricExpressions = null;
	}

	private void populateInstances() {
		metricExpressions = new HashSet<>();
		fullInstances = new ArrayList<>();
		for (final String s : this.dt.getRefMapping().getMap().keySet()) {
			for (final String t : this.dt.getRefMapping().getMap().get(s).keySet()) {
				final TrainingInstance currentInstance = new TrainingInstance(s, t,
						this.dt.getRefMapping().getMap().get(s).get(t));
				for (final PairSimilar<String> propPair : this.propertyMapping.stringPropPairs) {
					for ( String measure : Dragon.defaultMeasures) {
						final String metricExpression = measure + "(x." + propPair.a + ",y." + propPair.b + ")";
						metricExpressions.add(new Metric(propPair.a, propPair.b, metricExpression, measure));
						if (this.dt.getTargetCache().getInstance(t) != null
								|| this.dt.getSourceCache().getInstance(s) != null) {
							currentInstance.getMeasureValues().put(metricExpression,
									MeasureProcessor.getSimilarity(this.dt.getSourceCache().getInstance(s),
											this.dt.getTargetCache().getInstance(t), metricExpression,
											this.dt.getMinPropertyCoverage(), "?x", "?y"));
						} else {
							System.err.println("Could not find");
						}
					}
				}
				fullInstances.add(currentInstance);
			}
		}
		currentInstances = new ArrayList<>();
		currentInstances.addAll(fullInstances);
	}

	private void updateInstances(DecisionTree currentNode) {
		this.currentMapping = null;
		if (currentNode.isLeftNode()) {
			AMapping base = null;
			base = currentNode.getRefMapping();
			this.currentMapping = MappingOperations.difference(base, currentNode.getParent().getPathMapping());
		} else {
			this.currentMapping = currentNode.getParent().getPathMapping();
		}
		currentInstances = this.mappingToTrainingInstance(this.currentMapping);
	}

	private class Metric {
		String sourceProperty;
		String targetProperty;
		String metricExpression;
		String measure;

		public Metric(String sourceProperty, String targetProperty, String metricExpression, String measure) {
			this.sourceProperty = sourceProperty;
			this.targetProperty = targetProperty;
			this.metricExpression = metricExpression;
			this.measure = measure;
		}

		@Override
		public String toString() {
			return this.metricExpression;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass() != this.getClass()) {
				return false;
			}
			final Metric rhs = (Metric) obj;
			return new EqualsBuilder().append(this.sourceProperty, rhs.sourceProperty)
					.append(this.targetProperty, rhs.targetProperty).append(this.measure, rhs.measure)
					.append(this.metricExpression, rhs.metricExpression).isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 35).append(this.sourceProperty).append(this.targetProperty)
					.append(this.measure).append(this.metricExpression).toHashCode();
		}
	}

	public class MetricValueComparator implements Comparator<TrainingInstance> {

		public String mE;

		public MetricValueComparator(String metricExpression) {
			this.mE = metricExpression;
		}

		@Override
		public int compare(TrainingInstance o1, TrainingInstance o2) {
			return o1.getMeasureValues().get(this.mE).compareTo(o2.getMeasureValues().get(this.mE));
		}

	}

	/**
	 * 1 - sum of pi²
	 *
	 * @param pn
	 * @return
	 */
	private double gini(double... pn) {
		double res = 0.0;
		for (final double pi : pn) {
			// if all instances belong to one class return 0
			if (pi == 0.0 && pn.length == 2) {
				return 0;
			}
			res += Math.pow(pi, 2.0);
		}
		return 1 - res;
	}

	private double avgGini(DecisionTree currentNode, List<TrainingInstance> left, List<TrainingInstance> right) {
		if (currentNode.getParent() == null) {
			this.currentMapping = currentNode.getRefMapping();
		}
		final double[] leftFraction = InstanceCalculator.getNumberOfPositiveNegativeInstances(left);
		final double[] rightFraction = InstanceCalculator.getNumberOfPositiveNegativeInstances(right);
		final double leftAll = leftFraction[0] + leftFraction[1];
		final double rightAll = rightFraction[0] + rightFraction[1];
		final double leftWeight = leftAll / this.currentMapping.size();
		final double rightWeight = rightAll / this.currentMapping.size();
		final double avgGini = leftWeight * this.gini(leftFraction[0] / leftAll, leftFraction[1] / leftAll)
				+ rightWeight * this.gini(rightFraction[0] / rightAll, rightFraction[1] / rightAll);
		return avgGini;
	}

	@Override
	public ExtendedClassifier getBestClassifier(DecisionTree currentNode) {
		if (fullInstances == null) {
			this.populateInstances();
		} else {
			if (currentNode.getParent() == null) {
				logger.error("Node has no parent. This should not happen! Returning null");
				return null;
			}
			this.updateInstances(currentNode);
			if (currentInstances.size() == 0) {
				return null;
			}
			final double[] posNeg = InstanceCalculator.getNumberOfPositiveNegativeInstances(currentInstances);
			if (posNeg[0] == 0.0 || posNeg[1] == 0.0) {
				return null;
			}
		}
		// get Metric with highest info gain
		Metric bestMetric = null;
		double bestGain = 1.0;
		double bestSplitpoint = 0.0;
		for (final Metric mE : metricExpressions) {
			if (currentNode.getParent() == null
					|| !currentNode.getParent().getPathString().contains(mE.metricExpression)) {
				Collections.sort(currentInstances, new MetricValueComparator(mE.metricExpression));
				final ArrayList<TrainingInstance> lessThanI = new ArrayList<>();
				final ArrayList<TrainingInstance> moreThanEqualsI = new ArrayList<>();
				moreThanEqualsI.addAll(currentInstances);
				double oldSplitpoint = 0.0;
				for (int i = 0; i < currentInstances.size() - 1; i++) {
					moreThanEqualsI.remove(currentInstances.get(i));
					lessThanI.add(currentInstances.get(i));
					// splitpoint is between the two values
					double splitpoint = 0.0;
					if (middlePoints) {
						splitpoint = (currentInstances.get(i).getMeasureValues().get(mE.metricExpression)
								+ currentInstances.get(i + 1).getMeasureValues().get(mE.metricExpression)) / 2.0;
					} else {
						splitpoint = currentInstances.get(i + 1).getMeasureValues().get(mE.metricExpression);
					}
					if (splitpoint != oldSplitpoint) {
						oldSplitpoint = splitpoint;
						final double gain = this.avgGini(currentNode, lessThanI, moreThanEqualsI);
						logger.debug("Gain: " + gain + " for " + mE.metricExpression + "|" + splitpoint);
						if (gain < bestGain) {
							bestMetric = mE;
							bestGain = gain;
							bestSplitpoint = splitpoint;
						}
					}
					if (splitpoint == 1.0) {
						break;
					}
				}
			} else {
			}
		}
		if (bestMetric == null) {
			return null;
		}
		final ExtendedClassifier ec = new ExtendedClassifier(bestMetric.measure, bestSplitpoint,
				bestMetric.sourceProperty, bestMetric.targetProperty);
		ec.setfMeasure(bestGain);
		final String measureExpression = bestMetric.measure + "(x." + bestMetric.sourceProperty + ",y."
				+ bestMetric.targetProperty + ")";
		ec.setMapping(currentNode.executeAtomicMeasure(measureExpression, bestSplitpoint));
		return ec;
	}

	private List<TrainingInstance> mappingToTrainingInstance(AMapping mapping) {
		final List<TrainingInstance> instances = new ArrayList<>();
		for (final TrainingInstance t : fullInstances) {
			if (mapping.getMap().containsKey(t.getSourceUri())
					&& mapping.getMap().get(t.getSourceUri()).containsKey(t.getTargetUri())) {
				instances.add(t);
			}
		}
		return instances;
	}

	@Override
	public boolean stopCondition(DecisionTree currentNode) {
		// TODO Auto-generated method stub
		return false;
	}

}
