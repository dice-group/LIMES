package org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoRefFMeasure;
import org.aksw.limes.core.execution.engine.filter.LinearFilter;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTree;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTreeLearning;

public class GlobalFMeasure extends FitnessFunctionDTL{

	private ExtendedClassifier findClassifier(String sourceProperty, String targetProperty, String measure, DecisionTree currentNode) {
		String measureExpression = measure + "(x." + sourceProperty + ",y." + targetProperty + ")";
		String properties = "(x." + sourceProperty + ",y." + targetProperty + ")";
		ExtendedClassifier cp = new ExtendedClassifier(measure, 0.0, sourceProperty, targetProperty);
		if (currentNode.getParent() != null) {
			if (currentNode.getParent().getPathString().contains(measureExpression)) {
				return null;
			}
		}
		double maxFM = 0.0;
		double theta = 1.0;
		AMapping bestMapping = MappingFactory.createDefaultMapping();
		// PseudoRefFMeasure prfm = new PseudoRefFMeasure();
		// GoldStandard gs = new GoldStandard(null, sourceCache.getAllUris(),
		// targetCache.getAllUris());
		
		for (double threshold = 1d; threshold > dt.getMinPropertyCoverage(); threshold = threshold * dt.getPropertyLearningRate()) {
			cp = new ExtendedClassifier(measure, threshold, sourceProperty, targetProperty);
			AMapping mapping = currentNode.getMeasureMapping(measureExpression, cp);
			// double pfm = prfm.calculate(mapping, gs, 0.1);
			double pfm = calculateFMeasure(mapping, currentNode.getRefMapping(), currentNode);
			// System.out.println(measureExpression + "|" +threshold+ " " +
			// pfm);
			if (maxFM < pfm) { // only interested in largest threshold with
								// highest F-Measure
				bestMapping = mapping;
				theta = threshold;
				maxFM = pfm;
			}
		}
	/*	
	// ==== BINARY SEARCH ====
		double leftThreshold = dt.getMinPropertyCoverage();
		double rightThreshold = 1.0;
			cp = new ExtendedClassifier(measure, leftThreshold, sourceProperty, targetProperty);
//			AMapping basemapping = currentNode.getMeasureMapping(measureExpression, cp);
			// double pfm = prfm.calculate(mapping, gs, 0.1);
//			LinearFilter filter = new LinearFilter();
			double leftfm = 0.0;
			double rightfm  = 0.0;
//		System.out.println("==== " + measureExpression + "====");
		double tau = 2.0;
		while((rightThreshold - leftThreshold > 0.01) || leftfm == 1.0 || rightfm == 1.0){
//			System.out.println("Left Threshold: " + leftThreshold);
//			System.out.println("Right Threshold: " + rightThreshold);
//			AMapping leftMapping = filter.filter(basemapping, leftThreshold);
			cp.setThreshold(leftThreshold);
			AMapping leftMapping = currentNode.getMeasureMapping(measureExpression, cp);
			leftfm = calculateFMeasure(leftMapping, currentNode.getRefMapping(), currentNode);
//			System.out.println("Left fM: " + leftfm);
			cp.setThreshold(rightThreshold);
			AMapping rightMapping = currentNode.getMeasureMapping(measureExpression, cp);
//			AMapping rightMapping = filter.filter(basemapping, rightThreshold);
			rightfm = calculateFMeasure(rightMapping, currentNode.getRefMapping(), currentNode);
//			System.out.println("Right fM: " + rightfm);

			double middleThreshold = leftThreshold + ((rightThreshold - leftThreshold) / tau);
			cp.setThreshold(middleThreshold);
			AMapping middleMapping = currentNode.getMeasureMapping(measureExpression, cp);
			double middlefm = calculateFMeasure(middleMapping, currentNode.getRefMapping(), currentNode);
//			System.out.println("Middle fM: " + middlefm);
//			System.out.println("Middle Threshold: " + middleThreshold);
			if(middlefm >= leftfm){
				leftThreshold = middleThreshold;
			}else{
				rightThreshold = middleThreshold;
			}
//			System.out.println("Left Threshold: " + leftThreshold);
//			System.out.println("Right Threshold: " + rightThreshold);
//			System.out.println(" ------ ");
		}
		if(rightfm > leftfm){
			theta = rightThreshold;
			maxFM = rightfm;
		}else{
			theta = leftThreshold;
			maxFM = leftfm;
		}
		
		
		*/
		
		cp = new ExtendedClassifier(measure, theta, sourceProperty, targetProperty);
		cp.setfMeasure(maxFM);
		cp.setMapping(currentNode.executeAtomicMeasure(measureExpression, theta));
		return cp;
	}

	private AMapping removeNegativeExamplesFromMapping(AMapping m){
		LinearFilter lf = new LinearFilter();
		return lf.filter(m, 1.0);
	}
	

	private double calculateFMeasure(AMapping mapping, AMapping refMap, DecisionTree currentNode) {
		AMapping updatedRefMapping = removeNegativeExamplesFromMapping(refMap);
		double res = 0.0;
		if (DecisionTree.isSupervised) {
			GoldStandard gs = new GoldStandard(updatedRefMapping, currentNode.getTestSourceCache().getAllUris(), currentNode.getTestTargetCache().getAllUris());
			FMeasure fm = new FMeasure();
			res = fm.calculate(mapping, gs);
		} else {
			GoldStandard gs = new GoldStandard(null, currentNode.getSourceCache().getAllUris(), currentNode.getTargetCache().getAllUris());
			PseudoRefFMeasure prfm = new PseudoRefFMeasure();
			res = prfm.calculate(mapping, gs);
		}
		return res;
	}
	@Override
	public ExtendedClassifier getBestClassifier(DecisionTree currentNode) {
		// logger.info("Getting all classifiers ...");
		List<ExtendedClassifier> initialClassifiers = new ArrayList<>();
		for (PairSimilar<String> propPair : dt.getDtl().getPropertyMapping().stringPropPairs) {
			for (String measure : DecisionTreeLearning.defaultMeasures) {
				ExtendedClassifier cp = findClassifier(propPair.a, propPair.b, measure, currentNode);
				if (cp != null)
					initialClassifiers.add(cp);
			}
		}

		// logger.info("Done computing all classifiers.");
		Collections.sort(initialClassifiers, Collections.reverseOrder());
		if(initialClassifiers.size() == 0 || initialClassifiers.get(0).getfMeasure() == 0.0){
			return null;
		}
		return initialClassifiers.get(0);
	}

	@Override
	public boolean stopCondition(DecisionTree currentNode) {
		if(currentNode.getClassifier() != null){
			if(currentNode.getClassifier().getfMeasure() == 1.0){
				return true;
			}
		}
		return false;
	}

}
