package org.aksw.limes.core.ml.algorithm.dragon.Pruning;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.dragon.DecisionTree;
import org.aksw.limes.core.ml.algorithm.dragon.Utils.InstanceCalculator;
import org.apache.commons.math3.distribution.NormalDistribution;

public class ErrorEstimatePruning extends PruningFunctionDTL{
	public static double defaultConfidence = 0.25;
	
	/**
	 * Calculates the pessimistic error rate e using the formula
	 * (f + (z^2)/2N + z * sqrt(f/N - f^2/N + z^2/(4N^2)))/1 + z^2/N
	 * @param f observed error rate E/N, with N number of instances where E are errors
	 * @param N number of instances
	 * @param z confidence limit
	 * @return error 
	 */
	public static double errorRate(double f, double N, double z){
		double zPot = Math.pow(z, 2.0);
		double root = Math.sqrt(f/N - Math.pow(f, 2)/N + zPot/(4.0 * Math.pow(N, 2)));
		double counter = f + zPot/(2.0*N) + z * root;
		double denominator = 1 + zPot/N;
		double error = counter/denominator;
		return error;
	}
	
	public static void main(String[] args){

		System.out.println();
		/*
		ErrorEstimatePruning eep = new ErrorEstimatePruning();
		double pC = 0.69;
		AMapping parentMapping = MappingFactory.createDefaultMapping();
		parentMapping.add("m1", "m1",1.0);
		parentMapping.add("m2", "m2",1.0);
		parentMapping.add("m3", "m3",0.0);
		parentMapping.add("m4", "m4",1.0);
		parentMapping.add("m5", "m5",0.0);
		parentMapping.add("m6", "m6",0.0);
		parentMapping.add("m7", "m7",0.0);
		parentMapping.add("m8", "m8",1.0);
		parentMapping.add("m9", "m9",0.0);
		parentMapping.add("m10", "m11",1.0);
		parentMapping.add("m11", "m11",0.0);
		parentMapping.add("m12", "m12",0.0);
		parentMapping.add("m13", "m13",0.0);
		parentMapping.add("m14", "m14",0.0);

		parentMapping.add("m15", "m15",1.0);
		parentMapping.add("m16", "m16",0.0);
		AMapping nodeMapping = MappingFactory.createDefaultMapping();
		nodeMapping.add("m1", "m1",1.0);
		nodeMapping.add("m2", "m2",1.0);
		nodeMapping.add("m3", "m3",0.0);
		nodeMapping.add("m4", "m4",1.0);
		nodeMapping.add("m5", "m5",0.0);
		nodeMapping.add("m6", "m6",0.0);
		nodeMapping.add("m7", "m7",0.0);
		nodeMapping.add("m8", "m8",1.0);
		nodeMapping.add("m9", "m9",0.0);
		nodeMapping.add("m10", "m11",1.0);
		nodeMapping.add("m11", "m11",0.0);
		nodeMapping.add("m12", "m12",0.0);
		nodeMapping.add("m13", "m13",0.0);
		nodeMapping.add("m14", "m14",0.0);
		ExtendedClassifier ec = new ExtendedClassifier("", 0.0);
		ec.setMapping(nodeMapping);
		DecisionTree t = new DecisionTree(null, null, null, null, 0, 0, pC, null);
		t.setClassifier(ec);
		eep.pruneChild(t,parentMapping);
		*/
	}

	@Override
	public DecisionTree pruneChildNodesIfNecessary(DecisionTree node) {
		AMapping leftMapping = null;
		if(node.isRoot()){
			leftMapping = MappingOperations.difference(node.getRefMapping(), node.getClassifier().getMapping());
		}else{
			leftMapping = MappingOperations.difference(node.getParent().getClassifier().getMapping(), node.getClassifier().getMapping());
		}
		if(node.getLeftChild() != null && pruneChild(node.getLeftChild(), leftMapping)){
			node.setLeftChild(null);
		}
		if(node.getRightChild() != null && pruneChild(node.getRightChild(), node.getClassifier().getMapping())){
			node.setLeftChild(null);
		}
		return node;
	}
	
	public boolean pruneChild(DecisionTree node, AMapping parent){
		AMapping leftMapping = MappingOperations.difference(parent, node.getClassifier().getMapping());
		double[] posNegLeft = InstanceCalculator.getNumberOfPositiveNegativeInstances(leftMapping);
		double[] posNegRight = InstanceCalculator.getNumberOfPositiveNegativeInstances(node.getClassifier().getMapping());
		double[] posNegParent = InstanceCalculator.getNumberOfPositiveNegativeInstances(parent);
		double leftWeight = posNegLeft[0] + posNegLeft[1];
		double rightWeight = posNegRight[0] + posNegRight[1];
		double rightErrorRate = getErrorRate(posNegRight, node.getPruningConfidence());
		double leftErrorRate = getErrorRate(posNegLeft, node.getPruningConfidence());
		double parentError = getErrorRate(posNegParent, node.getPruningConfidence());
		double combinedError = (leftWeight * leftErrorRate + rightWeight * rightErrorRate)/(leftWeight + rightWeight);
		if(parentError < combinedError){
			return true;
		}
		return false;
	}
	
	private double getErrorRate(double[] posNeg, double confidence){
		double z = 0.0;
		if(confidence == defaultConfidence){
			z = 0.69;
		}else{
			z = new NormalDistribution(0, 1).inverseCumulativeProbability(1 - confidence);
		}
		double f = -1.0;
		double N = posNeg[0] + posNeg[1];
		if(posNeg[0] > posNeg[1]){
			f = posNeg[1]/N;
		}else{
			f = posNeg[0]/N;
		}
		double nodeErrorRate = errorRate(f, N, z);
		return nodeErrorRate;
	}

}
