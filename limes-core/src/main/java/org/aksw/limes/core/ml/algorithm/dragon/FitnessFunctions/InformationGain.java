package org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.dragon.DecisionTree;
import org.aksw.limes.core.ml.algorithm.dragon.Dragon;
import org.aksw.limes.core.ml.algorithm.dragon.TrainingInstance;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InformationGain extends FitnessFunctionDTL {
	protected static Logger logger = LoggerFactory.getLogger(InformationGain.class);
	private static ArrayList<TrainingInstance> fullInstances;
	private static ArrayList<TrainingInstance> currentInstances;
	private static HashSet<Metric> metricExpressions;
	private AMapping currentMapping;

	public InformationGain(){
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
				for (PairSimilar<String> propPair : dt.getDtl().getPropertyMapping().stringPropPairs) {
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
		ArrayList<TrainingInstance> updatedInstances = new ArrayList<TrainingInstance>();
		String splitExpression = currentNode.getParent().getClassifier().getMetricExpression().split("\\|")[0];
		double splitpoint = currentNode.getParent().getClassifier().getThreshold();
		AMapping parentMapping = null;
		if(currentNode.isLeftNode()){
			AMapping base = null;
			if(currentNode.getParent().isRoot()){
				base = currentNode.getRefMapping();
			}else{
				base = currentNode.getParent().getParent().getClassifier().getMapping();
			}
			parentMapping = MappingOperations.difference(base, currentNode.getParent().getClassifier().getMapping());
		}else{
			parentMapping = currentNode.getParent().getClassifier().getMapping();
		}
		for (TrainingInstance t : fullInstances) {
			if ((currentNode.isLeftNode() && t.getMeasureValues().get(splitExpression) <= splitpoint)
					|| (!currentNode.isLeftNode() && t.getMeasureValues().get(splitExpression) > splitpoint)) {
				if (parentMapping.getMap().get(t.getSourceUri()) != null) {
					if (parentMapping.getMap().get(t.getSourceUri()).get(t.getTargetUri()) != null) {
						updatedInstances.add(t);
					}
				}
			}
		}
		currentInstances = updatedInstances;
		AMapping newMapping = trainingInstancesToMapping(currentInstances);
		currentMapping = newMapping;
	}

//	private void updateInstances(DecisionTree currentNode) {
//		ArrayList<TrainingInstance> updatedInstances = new ArrayList<TrainingInstance>();
//		String splitExpression = currentNode.getParent().getClassifier().getMetricExpression().split("\\|")[0];
//		double splitpoint = currentNode.getParent().getClassifier().getThreshold();
//		for (TrainingInstance t : fullInstances) {
//			if ((currentNode.isLeftNode() && t.getMeasureValues().get(splitExpression) <= splitpoint)
//					|| (!currentNode.isLeftNode() && t.getMeasureValues().get(splitExpression) > splitpoint)) {
//				if (currentNode.getParent().getClassifier().getMapping().getMap().get(t.getSourceUri()) != null) {
//					if (currentNode.getParent().getClassifier().getMapping().getMap().get(t.getSourceUri())
//							.get(t.getTargetUri()) != null) {
//						updatedInstances.add(t);
//					}
//				}
//			}
//		}
//		currentInstances = updatedInstances;
//		AMapping newMapping = trainingInstancesToMapping(currentInstances);
//		currentMapping = newMapping;
//	}

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
	 * Sum of -p_i log p_i from 0 to n
	 * 
	 * @param pn
	 * @return
	 */
	private double entropy(double... pn) {
		double res = 0.0;
		for (double pi : pn) {
			// if all instances belong to one class return 0
			if (pi == 0.0 && pn.length == 2)
				return 0;
			// Change of base rule to get log2
			res += -pi * (Math.log10(pi) / Math.log10(2.0));
		}
		return res;
	}

	/**
	 * 
	 * @param posNeg
	 *            = {positive, negative}
	 * @return entropy(positive/all, negative/all)
	 */
	private double info(double[] posNeg) {
		double all = posNeg[0] + posNeg[1];
		double entropy = entropy(posNeg[0] / all, posNeg[1] / all);
		// System.out.println("entropy(" + posNeg[0] + "/" + all + "," +
		// posNeg[1] + "/" + all+")="+entropy);
		return entropy;
	}

	private double infox(double rootNumber, double[]... childNodes) {
		double res = 0.0;
		for (double[] ti : childNodes) {
			double allChild = ti[0] + ti[1];
			res += ((allChild) / rootNumber) * info(ti);
		}
		return res;
	}

	private double gain(DecisionTree currentNode, List<TrainingInstance> left, List<TrainingInstance> right) {
		if (currentNode.getParent() == null) {
			currentMapping = currentNode.getRefMapping();
		}
		double gain = info(getNumberOfPositiveNegativeInstances(currentMapping)) - infox(currentMapping.size(),
				getNumberOfPositiveNegativeInstances(left), getNumberOfPositiveNegativeInstances(right));
		return gain;
	}

	/*
	 * public static void main(String[] args) { // double[] posNeg = {9.0,5.0};
	 * // double info = gr.info(posNeg); //
	 * System.out.println("info[9,5]="+info); // double[] posNeg2 = {2.0,3.0};
	 * // double[] posNeg3 = {4.0,0.0}; // double[] posNeg4 = {3.0,2.0}; //
	 * double infox = gr.infox(14.0,posNeg2, posNeg3,posNeg4); //
	 * System.out.println("infox(..)="+infox); // System.out.println("gain="
	 * +(info -infox));
	 * 
	 * // AMapping parentMapping = MappingFactory.createDefaultMapping(); //
	 * parentMapping.add("ex1", "ex1",1.0); // parentMapping.add("ex2",
	 * "ex2",1.0); // parentMapping.add("ex3", "ex3",1.0); //
	 * parentMapping.add("ex4", "ex4",1.0); // parentMapping.add("ex5",
	 * "ex5",1.0); // parentMapping.add("ex6", "ex6",1.0); //
	 * parentMapping.add("ex7", "ex7",1.0); // parentMapping.add("ex8",
	 * "ex8",1.0); // parentMapping.add("ex9", "ex9",1.0); //
	 * parentMapping.add("ex10", "ex10",0.0); // parentMapping.add("ex11",
	 * "ex11",0.0); // parentMapping.add("ex12", "ex12",0.0); //
	 * parentMapping.add("ex13", "ex13",0.0); // parentMapping.add("ex14",
	 * "ex14",0.0); // DecisionTree node = new DecisionTree(null, null, null,
	 * null, 0.0, // 0.0, parentMapping); // InformationGain gr = new
	 * InformationGain(node); // List<TrainingInstance> left = new
	 * ArrayList<TrainingInstance>(); // List<TrainingInstance> right = new
	 * ArrayList<TrainingInstance>(); // left.add(new TrainingInstance("", "",
	 * 1.0)); // left.add(new TrainingInstance("", "", 1.0)); // left.add(new
	 * TrainingInstance("", "", 1.0)); // left.add(new TrainingInstance("", "",
	 * 0.0)); // left.add(new TrainingInstance("", "", 0.0)); // left.add(new
	 * TrainingInstance("", "", 0.0)); // left.add(new TrainingInstance("", "",
	 * 0.0)); // // right.add(new TrainingInstance("", "", 1.0)); //
	 * right.add(new TrainingInstance("", "", 1.0)); // right.add(new
	 * TrainingInstance("", "", 1.0)); // right.add(new TrainingInstance("", "",
	 * 1.0)); // right.add(new TrainingInstance("", "", 1.0)); // right.add(new
	 * TrainingInstance("", "", 1.0)); // right.add(new TrainingInstance("", "",
	 * 0.0)); // System.out.println(gr.gain(node,left,right)); }
	 */

	private double[] getNumberOfPositiveNegativeInstances(List<TrainingInstance> instanceList) {
		double[] posNegNumber = { 0.0, 0.0 };
		for (TrainingInstance t : instanceList) {
			if (t.getClassLabel() > 0.9) {
				posNegNumber[0]++;
			} else {
				posNegNumber[1]++;
			}
		}
		return posNegNumber;
	}

	/**
	 * 
	 * @param m
	 * @return [positive][negative]
	 */
	private double[] getNumberOfPositiveNegativeInstances(AMapping m) {
		double[] posNegNumber = { 0.0, 0.0 };
		for (String s : m.getMap().keySet()) {
			for (String t : m.getMap().get(s).keySet()) {
				double value = m.getMap().get(s).get(t);
				if (value > 0.9) {
					posNegNumber[0]++;
				} else {
					posNegNumber[1]++;
				}
			}
		}
		return posNegNumber;
	}

	// nach sortieren alle splitpunkte durchnehmen und gain ratio berechnen
	// dafür gain ratio implementieren
	// und methode wo teilmappings rausfindet wenn man split macht

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
			double[] posNeg = getNumberOfPositiveNegativeInstances(currentMapping);
			if(posNeg[0] == 0.0 || posNeg[1] == 0.0){
				return null;
			}
		}
		// get Metric with highest info gain
		Metric bestMetric = null;
		double bestGain = 0.0;
		double bestSplitpoint = 0.0;
		for (Metric mE : metricExpressions) {
			if (currentNode.getParent() == null
					|| !currentNode.getParent().getPathString().contains(mE.metricExpression)) {
				Collections.sort(currentInstances, new MetricValueComparator(mE.metricExpression));
				ArrayList<TrainingInstance> lessThanEqualsI = new ArrayList<TrainingInstance>();
				ArrayList<TrainingInstance> moreThanI = new ArrayList<TrainingInstance>();
				moreThanI.addAll(currentInstances);
				double oldSplitpoint = 0.0;
				for (int i = 0; i < currentInstances.size() - 1; i++) {
					moreThanI.remove(currentInstances.get(i));
					lessThanEqualsI.add(currentInstances.get(i));
					// splitpoint is between the two values
//					double splitpoint = (currentInstances.get(i).getMeasureValues().get(mE.metricExpression)
//							+ currentInstances.get(i + 1).getMeasureValues().get(mE.metricExpression)) / 2.0;
					double splitpoint = currentInstances.get(i).getMeasureValues().get(mE.metricExpression);
					if (splitpoint != oldSplitpoint) {
						oldSplitpoint = splitpoint;
						double gain = gain(currentNode, lessThanEqualsI, moreThanI);
						if (gain > bestGain) {
							bestMetric = mE;
							bestGain = gain;
							bestSplitpoint = splitpoint;
						}
					}
					if (splitpoint == 1.0)
						break;
				}
			} else {
				System.out.println("Ommitting: " + mE);
			}
		}
		if (bestMetric == null)
			return null;
		ExtendedClassifier ec = new ExtendedClassifier(bestMetric.measure, bestSplitpoint, bestMetric.sourceProperty,
				bestMetric.targetProperty);
		ec.setfMeasure(bestGain);
//		ec.setMapping(getNodeMapping(currentNode,ec));
		String measureExpression = bestMetric.measure + "(x." + bestMetric.sourceProperty + ",y." + bestMetric.targetProperty + ")";
		ec.setMapping(currentNode.getMeasureMapping(measureExpression, ec));
		return ec;
	}

	private AMapping getNodeMapping(DecisionTree currentNode, ExtendedClassifier ec) {
		ArrayList<TrainingInstance> newInstances = new ArrayList<TrainingInstance>();
		String splitExpression = ec.getMetricExpression().split("\\|")[0];
		double splitpoint = ec.getThreshold();
		for (TrainingInstance t : currentInstances) {
			if (t.getMeasureValues().get(splitExpression) > splitpoint) {
				if (currentMapping.getMap().get(t.getSourceUri()) != null) {
					if (currentMapping.getMap().get(t.getSourceUri()).get(t.getTargetUri()) != null) {
						newInstances.add(t);
					}
				}
			}
		}
		return trainingInstancesToMapping(newInstances);
	}

	private AMapping trainingInstancesToMapping(ArrayList<TrainingInstance> trainingInstances) {
		AMapping resMap = MappingFactory.createDefaultMapping();
		for (TrainingInstance t : trainingInstances) {
			resMap.add(t.getSourceUri(), t.getTargetUri(), t.getClassLabel());
		}
		return resMap;
	}

	@Override
	public boolean stopCondition(DecisionTree currentNode) {
		// TODO Auto-generated method stub
		return false;
	}

}
