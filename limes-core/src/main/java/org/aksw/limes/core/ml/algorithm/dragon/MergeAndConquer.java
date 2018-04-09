package org.aksw.limes.core.ml.algorithm.dragon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.execution.engine.filter.LinearFilter;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.apache.log4j.Logger;

public class MergeAndConquer {
	public static int comparisons = 0;
	static Logger logger = Logger.getLogger(MergeAndConquer.class);
	private HashMap<String, AMapping> calculatedMappings = new HashMap<String, AMapping>();
	public static List<LogicOperator> allowedOperators = Arrays.asList(LogicOperator.AND, LogicOperator.OR,
			LogicOperator.MINUS);
	private Dragon dtl;
	private double minPropertyCoverage;
	private double propertyLearningRate;
	private int maxLinkSpecHeight;
	private ACache testSourceCache;
	private ACache testTargetCache;
	private AMapping refMapping;

	public MergeAndConquer(Dragon dtl, double minPropertyCoverage, double propertyLearningRate,
			int maxLinkSpecHeight, ACache testSourceCache, ACache testTargetCache, AMapping refMapping) {
		this.dtl = dtl;
		this.minPropertyCoverage = minPropertyCoverage;
		this.propertyLearningRate = propertyLearningRate;
		this.maxLinkSpecHeight = maxLinkSpecHeight;
		this.testSourceCache = testSourceCache;
		this.testTargetCache = testTargetCache;
		this.refMapping = removeNegativeExamplesFromMapping(refMapping);
	}
	
	private AMapping removeNegativeExamplesFromMapping(AMapping m){
		LinearFilter lf = new LinearFilter();
		return lf.filter(m, 1.0);
	}

	private LinkSpecification getBestAtomicLinkSpecification() {
		List<LinkSpecification> initialClassifiers = new ArrayList<>();
		logger.info("PropPairs: " + dtl.getPropertyMapping().stringPropPairs.size());
		for (PairSimilar<String> propPair : dtl.getPropertyMapping().stringPropPairs) {
			for (String measure : Dragon.defaultMeasures) {
				LinkSpecification ls = findClassifier(propPair.a, propPair.b, measure);
				if (ls != null)
					initialClassifiers.add(ls);
			}
		}

		// logger.info("Done computing all classifiers.");
		// Sort LinkSpec list by quality in ascending order
		Collections.sort(initialClassifiers, this::linkSpecQualityCompareTo);
		if (initialClassifiers.size() == 0 || initialClassifiers.get(0).getQuality() == 0.0) {
			return null;
		}
		return initialClassifiers.get(0);
	}

	private int linkSpecQualityCompareTo(LinkSpecification ls1, LinkSpecification ls2) {
		double comparison = ls2.getQuality() - ls1.getQuality();
		if (comparison == 0.0) {
			return 0;
		} else if (comparison > 0) {
			return 1;
		}
		return -1;
	}

	private LinkSpecification findClassifier(String sourceProperty, String targetProperty, String measure) {
		String measureExpression = measure + "(x." + sourceProperty + ",y." + targetProperty + ")";
		double maxFM = 0.0;
		double theta = 1.0;

		for (double threshold = 1d; threshold > minPropertyCoverage; threshold = threshold * propertyLearningRate) {
			LinkSpecification ls = new LinkSpecification(measureExpression, threshold);
			AMapping mapping = dtl.predict(testSourceCache, testTargetCache, new MLResults(ls, null, 0.0, null));
			calculatedMappings.put(ls.getFullExpression() + "|" + ls.getThreshold(), mapping);
			// double pfm = prfm.calculate(mapping, gs, 0.1);
			double pfm = calculateFMeasure(mapping, refMapping);
			if (maxFM < pfm) { // only interested in largest threshold with
								// highest F-Measure
				theta = threshold;
				maxFM = pfm;
			}
		}
		LinkSpecification bestLS = new LinkSpecification(measureExpression, theta);
		bestLS.setQuality(maxFM);
		return bestLS;
	}
	
	private double calculateFMeasure(AMapping mapping, AMapping refMap) {
		comparisons++;
		double res = 0.0;
		GoldStandard gs = new GoldStandard(refMap, testSourceCache.getAllUris(), testTargetCache.getAllUris());
		FMeasure fm = new FMeasure();
		res = fm.calculate(mapping, gs);
		return res;
	}

	private LinkSpecification classifierToLinkSpec(ExtendedClassifier ec) {
		String measureExpression = ec.getMeasure() + "(x." + ec.getSourceProperty() + ",y." + ec.getTargetProperty()
				+ ")";
		return new LinkSpecification(measureExpression, ec.getThreshold());
	}

	private AMapping getMappingFromLinkSpec(LinkSpecification ls) {
		if (ls.isAtomic()) {
			AMapping m = calculatedMappings.get(ls.getFullExpression() + "|" + ls.getThreshold());
			return m;
		}
		List<LinkSpecification> children = ls.getChildren();
		LogicOperator op = ls.getOperator();
		AMapping leftMapping = getMappingFromLinkSpec(children.get(0));
		AMapping rightMapping = getMappingFromLinkSpec(children.get(1));
		switch (op) {
		case AND:
			return MappingOperations.intersection(leftMapping, rightMapping);
		case MINUS:
			return MappingOperations.difference(leftMapping, rightMapping);
		case OR:
			return MappingOperations.union(leftMapping, rightMapping);
		default:
			logger.error("The operator " + op + "should not be inside the LS!");
			return null;
		}
	}

	public LinkSpecification learn(LinkSpecification ls) {
		if (ls == null) {
			ls = getBestAtomicLinkSpecification();
			comparisons = 0;
			return learn(ls);
		}
		double maxFM = ls.getQuality();
		LinkSpecification resultLS = null;
		if (ls.isAtomic()) {
			for (String key : calculatedMappings.keySet()) {
				LinkSpecification tmpLS = null;
				if (!key.equals(ls.getFullExpression() + "|" + ls.getThreshold())
						&& !ls.getFullExpression().contains(getPropertiesFromMappingKey(key))) {
					double tmpFM = 0.0;
					tmpLS = new LinkSpecification(
							"OR(" + ls.getFullExpression() + "|" + ls.getThreshold() + "," + key + ")", 0.0);
					AMapping tmpMapping = getMappingFromLinkSpec(tmpLS);
					tmpFM = calculateFMeasure(tmpMapping, refMapping);
					if (tmpFM > maxFM) {
						maxFM = tmpFM;
						resultLS = tmpLS;
					}
					resultLS = tmpLS;
					tmpLS = new LinkSpecification(
							"AND(" + ls.getFullExpression() + "|" + ls.getThreshold() + "," + key + ")", 0.0);
					tmpMapping = getMappingFromLinkSpec(tmpLS);
					tmpFM = calculateFMeasure(tmpMapping, refMapping);
					if (tmpFM > maxFM) {
						maxFM = tmpFM;
						resultLS = tmpLS;
					}
					tmpLS = new LinkSpecification(
							"MINUS(" + ls.getFullExpression() + "|" + ls.getThreshold() + "," + key + ")", 0.0);
					tmpMapping = getMappingFromLinkSpec(tmpLS);
					tmpFM = calculateFMeasure(tmpMapping, refMapping);
					if (tmpFM > maxFM) {
						maxFM = tmpFM;
						resultLS = tmpLS;
					}
					tmpLS = new LinkSpecification(
							"MINUS(" + key + "," + ls.getFullExpression() + "|" + ls.getThreshold() + ")", 0.0);
					tmpMapping = getMappingFromLinkSpec(tmpLS);
					tmpFM = calculateFMeasure(tmpMapping, refMapping);
					if (tmpFM > maxFM) {
						maxFM = tmpFM;
						resultLS = tmpLS;
					}
				}
			}
		} else {
			List<LinkSpecification> leaves = ls.getAllLeaves();
			for (int i = 0; i < leaves.size(); i++) {
//				System.out.println("i " + i);
//				System.out.println("Leaves depth: " + leaves.get(i).getDepth());
				if (leaves.get(i).getDepth() < maxLinkSpecHeight) {
					for (String key : calculatedMappings.keySet()) {
						LinkSpecification newLeaf = null;
						LinkSpecification tmpLS = null;
						if (!ls.getFullExpression().contains(getPropertiesFromMappingKey(key))) {
							double tmpFM = 0.0;
							newLeaf = new LinkSpecification("OR(" + leaves.get(i).getFullExpression() + "|"
									+ leaves.get(i).getThreshold() + "," + key + ")", 0.0);
							tmpLS = ls.setLeaf(newLeaf, i);
//							if(getMaxDepthFromLS(tmpLS) > maxLinkSpecHeight)
//								System.out.println(tmpLS.toStringPretty());
							AMapping tmpMapping = getMappingFromLinkSpec(tmpLS);
							tmpFM = calculateFMeasure(tmpMapping, refMapping);
							if (tmpFM > maxFM) {
								maxFM = tmpFM;
								resultLS = tmpLS;
							}
							newLeaf = new LinkSpecification("AND(" + leaves.get(i).getFullExpression() + "|"
									+ leaves.get(i).getThreshold() + "," + key + ")", 0.0);
							tmpLS = ls.setLeaf(newLeaf, i);
//							if(getMaxDepthFromLS(tmpLS) > maxLinkSpecHeight)
//								System.out.println(tmpLS.toStringPretty());
							tmpMapping = getMappingFromLinkSpec(tmpLS);
							tmpFM = calculateFMeasure(tmpMapping, refMapping);
							if (tmpFM > maxFM) {
								maxFM = tmpFM;
								resultLS = tmpLS;
							}
							newLeaf = new LinkSpecification("MINUS(" + leaves.get(i).getFullExpression() + "|"
									+ leaves.get(i).getThreshold() + "," + key + ")", 0.0);
							tmpLS = ls.setLeaf(newLeaf, i);
//							if(getMaxDepthFromLS(tmpLS) > maxLinkSpecHeight)
//								System.out.println(tmpLS.toStringPretty());
							tmpMapping = getMappingFromLinkSpec(tmpLS);
							tmpFM = calculateFMeasure(tmpMapping, refMapping);
							if (tmpFM > maxFM) {
								maxFM = tmpFM;
								resultLS = tmpLS;
							}
							newLeaf = new LinkSpecification("MINUS(" + key + "," + leaves.get(i).getFullExpression()
									+ "|" + leaves.get(i).getThreshold() + ")", 0.0);
							tmpLS = ls.setLeaf(newLeaf, i);
//							if(getMaxDepthFromLS(tmpLS) > maxLinkSpecHeight)
//								System.out.println(tmpLS.toStringPretty());
							tmpMapping = getMappingFromLinkSpec(tmpLS);
							tmpFM = calculateFMeasure(tmpMapping, refMapping);
							if (tmpFM > maxFM) {
								maxFM = tmpFM;
								resultLS = tmpLS;
							}
						}
					}
				}
			}
		}
		if (resultLS == null) {
			return ls;
		}
		resultLS.setQuality(maxFM);
		if(maxFM == 1.0){
			return resultLS;
		}
		return learn(resultLS);
	}

	private int getMaxDepthFromLS(LinkSpecification ls) {
		if (ls.isAtomic()) {
			return 0;
		}
		int max = 0;
		List<LinkSpecification> children = ls.getAllLeaves();
		for (LinkSpecification c : children) {
			int tmp = c.getDepth();
			if (tmp > max) {
				max = tmp;
			}
		}
		return max;
	}

	private String getPropertiesFromMappingKey(String key) {
		for (String measure : Dragon.defaultMeasures) {
			key = key.replace(measure, "");
		}
		key = key.split("\\|")[0];
		return key;
	}

}
