package org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.execution.engine.filter.LinearFilter;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.dragon.DecisionTree;
import org.aksw.limes.core.ml.algorithm.dragon.Dragon;

public class GlobalFMeasure extends FitnessFunctionDTL {

	private ExtendedClassifier findClassifier(String sourceProperty, String targetProperty, String measure,
			DecisionTree currentNode) {
		final String measureExpression = measure + "(x." + sourceProperty + ",y." + targetProperty + ")";
		ExtendedClassifier cp = new ExtendedClassifier(measure, 0.0, sourceProperty, targetProperty);
		if (currentNode.getParent() != null && currentNode.getParent().getPathString().contains(measureExpression)) {
			return null;
		}
		double maxFM = 0.0;
		double theta = 1.0;
		for (double threshold = 1d; threshold > this.dt.getMinPropertyCoverage(); threshold = threshold
				* this.dt.getPropertyLearningRate()) {
			cp = new ExtendedClassifier(measure, threshold, sourceProperty, targetProperty);
			final AMapping mapping = currentNode.getMeasureMapping(measureExpression, cp);
			// double pfm = prfm.calculate(mapping, gs, 0.1);
			final double pfm = this.calculateFMeasure(mapping, currentNode.getRefMapping(), currentNode);
			// System.out.println(measureExpression + "|" +threshold+ " " +
			// pfm);
			if (maxFM < pfm) { // only interested in largest threshold with
				theta = threshold;
				maxFM = pfm;
			}
		}

		cp = new ExtendedClassifier(measure, theta, sourceProperty, targetProperty);
		cp.setfMeasure(maxFM);
		cp.setMapping(currentNode.executeAtomicMeasure(measureExpression, theta));
		return cp;
	}

	private AMapping removeNegativeExamplesFromMapping(AMapping m) {
		final LinearFilter lf = new LinearFilter();
		return lf.filter(m, 1.0);
	}

	private double calculateFMeasure(AMapping mapping, AMapping refMap, DecisionTree currentNode) {
		final AMapping updatedRefMapping = this.removeNegativeExamplesFromMapping(refMap);
		double res = 0.0;
		final GoldStandard gs = new GoldStandard(updatedRefMapping, currentNode.getSourceCache().getAllUris(),
				currentNode.getTargetCache().getAllUris());
		final FMeasure fm = new FMeasure();
		res = fm.calculate(mapping, gs);
		return res;
	}

	@Override
	public ExtendedClassifier getBestClassifier(DecisionTree currentNode) {
		// logger.info("Getting all classifiers ...");
		final List<ExtendedClassifier> initialClassifiers = new ArrayList<>();
		for (final PairSimilar<String> propPair : this.propertyMapping.stringPropPairs) {
			for (final String measure : Dragon.defaultMeasures) {
				final ExtendedClassifier cp = this.findClassifier(propPair.a, propPair.b, measure, currentNode);
				if (cp != null) {
					initialClassifiers.add(cp);
				}
			}
		}

		// logger.info("Done computing all classifiers.");
		Collections.sort(initialClassifiers, Collections.reverseOrder());
		if (initialClassifiers.size() == 0 || initialClassifiers.get(0).getfMeasure() == 0.0) {
			return null;
		}
		return initialClassifiers.get(0);
	}

	@Override
	public boolean stopCondition(DecisionTree currentNode) {
		if (currentNode.getClassifier() != null && currentNode.getClassifier().getfMeasure() == 1.0) {
			return true;
		}
		return false;
	}

}
