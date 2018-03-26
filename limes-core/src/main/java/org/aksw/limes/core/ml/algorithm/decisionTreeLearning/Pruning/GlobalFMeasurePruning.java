package org.aksw.limes.core.ml.algorithm.decisionTreeLearning.Pruning;

import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTree;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTreeLearning;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions.GiniIndex;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.evaluation.DTLEvaluation;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.evaluation.FoldData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalFMeasurePruning extends PruningFunctionDTL{
	protected static Logger logger = LoggerFactory.getLogger(GlobalFMeasurePruning.class);
	static LinkSpecification one;
	static LinkSpecification two;
	static AMapping twomap;

	private DecisionTree returnRoot(DecisionTree node){
		if(node.isRoot()){
			return node;
		}else{
			return returnRoot(node.getParent());
		}
	}
		@Override
		public DecisionTree pruneChildNodesIfNecessary(DecisionTree node) {
            DecisionTree tmpRightChild = node.getRightChild();
            DecisionTree tmpLeftChild = node.getLeftChild();
//            System.out.println("=============================");
//            System.out.println("right: " + tmpRightChild);
//            System.out.println("left: " + tmpLeftChild);
            boolean deleteLeft = false;
            boolean deleteRight = false;
            
            
            boolean leftalreadynull = false;
            boolean rightalreadynull = false;
            if(tmpLeftChild == null){
            	leftalreadynull = true;
            }
            if(tmpRightChild == null){
            	rightalreadynull = true;
            }
            
            
            
            
            node.setRightChild(null);
            double tmp = 0.0;
            AMapping withoutRight = node.getTotalMapping();
            tmp = node.calculateFMeasure(withoutRight, node.getRefMapping());
//            System.out.println(returnRoot(node));
//            System.out.println("right null: " + tmp);
            if (tmp >= DecisionTree.totalFMeasure) {
                deleteRight = true;
            }
            node.setRightChild(tmpRightChild);
            node.setLeftChild(null);
            AMapping withoutLeft = node.getTotalMapping();
            tmp = node.calculateFMeasure(withoutLeft, node.getRefMapping());
//            System.out.println(returnRoot(node));
//            System.out.println("left null: " + tmp );
            if (tmp >= DecisionTree.totalFMeasure) {
                DecisionTree.totalFMeasure = tmp;
                deleteLeft = true;
                deleteRight = false;
            }
            node.setRightChild(null);
            node.setLeftChild(null);
            AMapping withoutBoth = node.getTotalMapping();
            tmp = node.calculateFMeasure(withoutBoth, node.getRefMapping());
//            System.out.println(returnRoot(node));
//            System.out.println("both null: " + tmp + "\n");
            if (tmp >= DecisionTree.totalFMeasure) {
                DecisionTree.totalFMeasure = tmp;
                deleteLeft = true;
                deleteRight = true;
            }
            
            
            
            
            if(leftalreadynull){
            	assert withoutRight.equals(withoutBoth);
            }
            if(rightalreadynull){
            	assert withoutLeft.equals(withoutBoth);
            }
            
            
//            if(node.isRoot()){
//            	if(tmpLeftChild != null){
//            	tmpLeftChild.setLeftChild(null);
//            	tmpLeftChild.setRightChild(null);
//            	}
//            	if(tmpRightChild != null){
//            	tmpRightChild.setLeftChild(null);
//            	tmpRightChild.setRightChild(null);
//            	}
//            	node.setLeftChild(tmpLeftChild);
//            	node.setRightChild(null);
//            	System.out.println(node);
//
//            	tmp = node.calculateFMeasure(node.getTotalMapping(), node.getRefMapping());
//            	one = node.getTotalLS();
//
//            	System.out.println(tmp);
//            	node.setRightChild(tmpRightChild);
//            	node.setLeftChild(null);
//
//            	two = node.getTotalLS();
//            	System.out.println(node);
//            	tmp = node.calculateFMeasure(node.getTotalMapping(), node.getRefMapping());
//            	twomap = node.getTotalMapping();
//            	System.out.println(tmp);
//            	node.setRightChild(null);
//            	node.setLeftChild(null);
//
//            	System.out.println(node);
//            	tmp = node.calculateFMeasure(node.getTotalMapping(), node.getRefMapping());
//            	System.out.println(tmp);
//            	
//            }
            if (!deleteLeft) {
                node.setLeftChild(tmpLeftChild);
            }
            if (!deleteRight) {
                node.setRightChild(tmpRightChild);
            }
//            System.out.println("Result: " + node);
			return node;
		}
		
		public static void main(String[] args) throws UnsupportedMLImplementationException{
			EvaluationData c = DataSetChooser.getData("amazongoogleproducts");
			int FOLDS_COUNT = 10;
				List<FoldData> folds = DTLEvaluation.generateFolds(c);

				FoldData trainData = new FoldData();
				FoldData testData = folds.get(FOLDS_COUNT - 1);
				// perform union on test folds
				for (int i = 0; i < FOLDS_COUNT; i++) {
					if (i != 9) {
						trainData.map = MappingOperations.union(trainData.map, folds.get(i).map);
						trainData.sourceCache = DTLEvaluation.cacheUnion(trainData.sourceCache, folds.get(i).sourceCache);
						trainData.targetCache = DTLEvaluation.cacheUnion(trainData.targetCache, folds.get(i).targetCache);
					}
				}
				// fix caches if necessary
				for (String s : trainData.map.getMap().keySet()) {
					for (String t : trainData.map.getMap().get(s).keySet()) {
						if (!trainData.targetCache.containsUri(t)) {
							// logger.info("target: " + t);
							trainData.targetCache.addInstance(c.getTargetCache().getInstance(t));
						}
					}
					if (!trainData.sourceCache.containsUri(s)) {
						// logger.info("source: " + s);
						trainData.sourceCache.addInstance(c.getSourceCache().getInstance(s));
					}
				}

				AMapping trainingData = trainData.map;
				ACache trainSourceCache = trainData.sourceCache;
				ACache trainTargetCache = trainData.targetCache;
				ACache testSourceCache = testData.sourceCache;
				ACache testTargetCache = testData.targetCache;
				AMLAlgorithm	dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
							MLImplementationType.SUPERVISED_BATCH);
					logger.info("source size: " + testSourceCache.size());
					logger.info("target size: " + testTargetCache.size());
					dtl.init(null, trainSourceCache, trainTargetCache);
					Configuration config = c.getConfigReader().read();
					dtl.getMl().setConfiguration(config);
					((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
					long start = System.currentTimeMillis();
					dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
					dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
					dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new GlobalFMeasurePruning());
					dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MIN_PROPERTY_COVERAGE, 0.4);
					MLResults res = dtl.asSupervised().learn(trainingData);
					long end = System.currentTimeMillis();
					logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
					
					
//					MLResults resone = new MLResults(one, null, 0.0, null);
//					MLResults restwo = new MLResults(two, null, 0.0, null);
//					AMapping mapping1in = dtl.predict(trainSourceCache, trainTargetCache, resone);
//					AMapping mapping2in = dtl.predict(trainSourceCache, trainTargetCache, restwo);
//					System.out.println(one);
//					System.out.println(two);
//					System.out.println(twomap.size());
//					System.out.println(mapping2in.size());
//					System.out.println("onein: " + new FMeasure().calculate(mapping1in,
//							new GoldStandard(trainData.map, trainSourceCache.getAllUris(), trainTargetCache.getAllUris())));
//					System.out.println("twoin: " + new FMeasure().calculate(mapping2in,
//							new GoldStandard(trainData.map, trainSourceCache.getAllUris(), trainTargetCache.getAllUris())));
//					AMapping mapping1 = dtl.predict(testSourceCache, testTargetCache, resone);
//					AMapping mapping2 = dtl.predict(testSourceCache, testTargetCache, restwo);
//					System.out.println("one: " + new FMeasure().calculate(mapping1,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris())));
//					System.out.println("two: " + new FMeasure().calculate(mapping2,
//							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris())));

					AMapping mapping = dtl.predict(testSourceCache, testTargetCache, res);
					double giGFM = new FMeasure().calculate(mapping,
							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
					double giGPrecision = new Precision().calculate(mapping,
							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
					double giGRecall = new Recall().calculate(mapping,
							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
					int giGSize = res.getLinkSpecification().size();
					logger.info("FMeasure: " + giGFM);
					logger.info("Precision: " + giGPrecision);
					logger.info("Recall: " + giGRecall);
					long giGTime = (end - start);
					logger.info("Time: " + giGTime);
					logger.info("Size: " + giGSize);

		}
}
