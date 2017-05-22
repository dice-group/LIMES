package org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoRefFMeasure;
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
			if (currentNode.getParent().getPathString().contains(properties)) {
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
		cp = new ExtendedClassifier(measure, theta, sourceProperty, targetProperty);
		cp.setfMeasure(maxFM);
		cp.setMapping(currentNode.executeAtomicMeasure(measureExpression, theta));
		return cp;
	}
	

	private double calculateFMeasure(AMapping mapping, AMapping refMap, DecisionTree currentNode) {
		double res = 0.0;
		if (DecisionTree.isSupervised) {
			GoldStandard gs = new GoldStandard(refMap, currentNode.getTestSourceCache().getAllUris(), currentNode.getTestTargetCache().getAllUris());
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
