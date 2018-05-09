package org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.dragon.DecisionTree;
import org.aksw.limes.core.ml.algorithm.dragon.Dragon;
import org.aksw.limes.core.ml.algorithm.dragon.TrainingInstance;
import org.aksw.limes.core.ml.algorithm.dragon.Utils.InstanceCalculator;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GiniIndex extends FitnessFunctionDTL {
	protected static Logger logger = LoggerFactory.getLogger(GiniIndex.class);
	private static List<TrainingInstance> fullInstances;
	private static List<TrainingInstance> currentInstances;
	private static HashSet<Metric> metricExpressions;
	private AMapping currentMapping;
	public static boolean middlePoints = false;

	public GiniIndex(){
		fullInstances = null;
		currentInstances = null;
		metricExpressions = null;
	}

	private void populateInstances() {
		metricExpressions = new HashSet<Metric>();
		fullInstances = new ArrayList<TrainingInstance>();
		for (String s : dt.getRefMapping().getMap().keySet()) {
			for (String t : dt.getRefMapping().getMap().get(s).keySet()) {
				TrainingInstance currentInstance = new TrainingInstance(s, t,
						dt.getRefMapping().getMap().get(s).get(t));
				for (PairSimilar<String> propPair : propertyMapping.stringPropPairs) {
					for (String measure : Dragon.defaultMeasures) {
						String metricExpression = measure + "(x." + propPair.a + ",y." + propPair.b + ")";
						metricExpressions.add(new Metric(propPair.a, propPair.b, metricExpression, measure));
						if (dt.getTargetCache().getInstance(t) != null || dt.getSourceCache().getInstance(s) != null) {
							currentInstance.getMeasureValues().put(metricExpression,
									MeasureProcessor.getSimilarity(dt.getSourceCache().getInstance(s),
											dt.getTargetCache().getInstance(t), metricExpression,
											dt.getMinPropertyCoverage(), "?x", "?y"));
						} else {
							System.err.println("Could not find");
						}
					}
				}
				fullInstances.add(currentInstance);
			}
		}
		currentInstances = new ArrayList<TrainingInstance>();
		currentInstances.addAll(fullInstances);
	}

	private void updateInstances(DecisionTree currentNode) {
		currentMapping = null;
		if(currentNode.isLeftNode()){
			AMapping base = null;
			base = currentNode.getRefMapping();
			currentMapping = MappingOperations.difference(base, currentNode.getParent().getPathMapping());
		}else{
			currentMapping = currentNode.getParent().getPathMapping();
		}
		currentInstances = mappingToTrainingInstance(currentMapping);
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
			return metricExpression;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass() != getClass()) {
				return false;
			}
			Metric rhs = (Metric) obj;
			return new EqualsBuilder().append(sourceProperty, rhs.sourceProperty)
					.append(targetProperty, rhs.targetProperty).append(measure, rhs.measure)
					.append(metricExpression, rhs.metricExpression).isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 35).append(sourceProperty).append(targetProperty).append(measure)
					.append(metricExpression).toHashCode();
		}
	}

	public class MetricValueComparator implements Comparator<TrainingInstance> {

		public String mE;

		public MetricValueComparator(String metricExpression) {
			this.mE = metricExpression;
		}

		@Override
		public int compare(TrainingInstance o1, TrainingInstance o2) {
			return o1.getMeasureValues().get(mE).compareTo(o2.getMeasureValues().get(mE));
		}

	}
	
	/**
	 * 1 - sum of pi²
	 * @param pn 
	 * @return
	 */
	private double gini(double ... pn){
		double res = 0.0;
		for (double pi : pn) {
			// if all instances belong to one class return 0
			if (pi == 0.0 && pn.length == 2)
				return 0;
			res += Math.pow(pi, 2.0);
		}
		return 1 - res;
	}
	
	private double avgGini(DecisionTree currentNode, List<TrainingInstance> left, List<TrainingInstance> right) {
		if (currentNode.getParent() == null) {
			currentMapping = currentNode.getRefMapping();
		}
		double[] leftFraction = InstanceCalculator.getNumberOfPositiveNegativeInstances(left);
		double[] rightFraction = InstanceCalculator.getNumberOfPositiveNegativeInstances(right);
		double leftAll = leftFraction[0] + leftFraction[1];
		double rightAll = rightFraction[0] + rightFraction[1];
		double leftWeight = (leftAll) / currentMapping.size();
		double rightWeight = (rightAll) / currentMapping.size();
		double avgGini = leftWeight * gini(leftFraction[0]/leftAll, leftFraction[1]/leftAll) + rightWeight * gini(rightFraction[0]/rightAll,rightFraction[1]/rightAll);
		return avgGini;
	}


	@Override
	public ExtendedClassifier getBestClassifier(DecisionTree currentNode) {
		if (fullInstances == null) {
			populateInstances();
		} else {
			if (currentNode.getParent() == null) {
				logger.error("Node has no parent. This should not happen! Returning null");
				return null;
			}
			updateInstances(currentNode);
			if(currentInstances.size() == 0)
				return null;
			double[] posNeg = InstanceCalculator.getNumberOfPositiveNegativeInstances(currentInstances);
			if(posNeg[0] == 0.0 || posNeg[1] == 0.0){
				return null;
			}
		}
		// get Metric with highest info gain
		Metric bestMetric = null;
		double bestGain = 1.0;
		double bestSplitpoint = 0.0;
		for (Metric mE : metricExpressions) {
			if (currentNode.getParent() == null
					|| !currentNode.getParent().getPathString().contains(mE.metricExpression)) {
				Collections.sort(currentInstances, new MetricValueComparator(mE.metricExpression));
				ArrayList<TrainingInstance> lessThanI = new ArrayList<TrainingInstance>();
				ArrayList<TrainingInstance> moreThanEqualsI = new ArrayList<TrainingInstance>();
				moreThanEqualsI.addAll(currentInstances);
				double oldSplitpoint = 0.0;
				for (int i = 0; i < currentInstances.size() - 1; i++) {
					moreThanEqualsI.remove(currentInstances.get(i));
					lessThanI.add(currentInstances.get(i));
					// splitpoint is between the two values
					double splitpoint = 0.0;
					if(middlePoints){
						splitpoint = (currentInstances.get(i).getMeasureValues().get(mE.metricExpression)
							+ currentInstances.get(i + 1).getMeasureValues().get(mE.metricExpression)) / 2.0;
					}else{
						splitpoint = currentInstances.get(i + 1).getMeasureValues().get(mE.metricExpression);
					}
					if (splitpoint != oldSplitpoint) {
						oldSplitpoint = splitpoint;
						double gain = avgGini(currentNode, lessThanI, moreThanEqualsI);
						logger.debug("Gain: " + gain + " for " + mE.metricExpression + "|" + splitpoint);
						if (gain < bestGain) {
							bestMetric = mE;
							bestGain = gain;
							bestSplitpoint = splitpoint;
						}
					}
					if (splitpoint == 1.0)
						break;
				}
			} else {
			}
		}
		if (bestMetric == null)
			return null;
		ExtendedClassifier ec = new ExtendedClassifier(bestMetric.measure, bestSplitpoint, bestMetric.sourceProperty,
				bestMetric.targetProperty);
		ec.setfMeasure(bestGain);
		String measureExpression = bestMetric.measure + "(x." + bestMetric.sourceProperty + ",y." + bestMetric.targetProperty + ")";
		ec.setMapping(currentNode.executeAtomicMeasure(measureExpression, bestSplitpoint));
		return ec;
	}


	private List<TrainingInstance> mappingToTrainingInstance(AMapping mapping){
		List<TrainingInstance> instances = new ArrayList<TrainingInstance>();
		for(TrainingInstance t: fullInstances){
			if(mapping.getMap().containsKey(t.getSourceUri())){
				if(mapping.getMap().get(t.getSourceUri()).containsKey(t.getTargetUri())){
					instances.add(t);
				}
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
