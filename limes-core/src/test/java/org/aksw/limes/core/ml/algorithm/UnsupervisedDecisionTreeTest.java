package org.aksw.limes.core.ml.algorithm;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTreeLearning;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.TreeParser;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.UnsupervisedDecisionTree;
import org.junit.Before;
import org.junit.Test;

public class UnsupervisedDecisionTreeTest {
	public UnsupervisedDecisionTree goldTree;
	public UnsupervisedDecisionTree goldTree2;
	public AMLAlgorithm dtl;
	public AMLAlgorithm dtl2;
	public ACache sourceCache;
	public ACache targetCache;
	public ACache sourceCache2;
	public ACache targetCache2;
	public PseudoFMeasure pfm;
	public LinkSpecification goldLS;
	
	@Before
	public void prepareData(){
		
//		EvaluationData c = DataSetChooser.getData(DataSetChooser.DataSets.PERSON1);
		EvaluationData c2 = DataSetChooser.getData(DataSetChooser.DataSets.PERSON2);
		try {
//			dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
//					MLImplementationType.UNSUPERVISED);
			dtl2 = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
					MLImplementationType.UNSUPERVISED);
//			sourceCache = c.getSourceCache();
//			targetCache = c.getTargetCache();
			sourceCache2 = c2.getSourceCache();
			targetCache2 = c2.getTargetCache();
//			dtl.init(null, sourceCache, targetCache);
			dtl2.init(null, sourceCache2, targetCache2);
//			dtl.getMl().setConfiguration(c.getConfigReader().read());
//			((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
			dtl2.getMl().setConfiguration(c2.getConfigReader().read());
			((DecisionTreeLearning) dtl2.getMl()).setPropertyMapping(c2.getPropertyMapping());
//			MLResults res = dtl.asUnsupervised().learn(new PseudoFMeasure());
//			System.out.println(res.getLinkSpecification());
//			System.out.println("FMeasure: " + new FMeasure().calculate(res.getMapping(), new GoldStandard(c.getReferenceMapping(),c.getSourceCache(), c.getTargetCache())));

			dtl2.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
			dtl2.asUnsupervised().learn(new PseudoFMeasure());
		} catch (UnsupportedMLImplementationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		DecisionTreeLearning decisionTreeLearning = (DecisionTreeLearning)dtl.getMl();
//		DecisionTreeLearning decisionTreeLearning2 = (DecisionTreeLearning)dtl2.getMl();
//		try {
//			goldTree = setGoldTree(decisionTreeLearning);
//			goldTree2 = setGoldTree2(decisionTreeLearning2);
//		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		goldLS = new LinkSpecification("OR(AND(MINUS(cosine(x.name,y.name)|0.01,cosine(x.name,y.name)|0.9)|0.0,AND(jaccard(x.name,y.name)|0.5,qgrams(x.name,y.name)|0.3)|0.0)|0.0,AND(cosine(x.name,y.name)|0.9,trigrams(x.name,y.name)|0.1)|0.0)",0.0);
	}
	
//	@Test
//	public void parseTreeToLS(){
//		TreeParser tp = new TreeParser((DecisionTreeLearning)dtl.getMl());
//		LinkSpecification ls = tp.parseTreePrefix(goldTree.toString());
//		assertEquals(goldLS,ls);
//	}
	
	@Test
	public void pathMapping(){
		TreeParser tp = new TreeParser((DecisionTreeLearning)dtl2.getMl());
		System.out.println("TREE: " + ((DecisionTreeLearning)dtl2.getMl()).root.toString());
//		LinkSpecification ls = tp.parseTreePrefix("jaccard§http://www.okkam.org/ontology_person1.owl#surname|http://www.okkam.org/ontology_person2.owl#surname: <= 1.0, > 1.0[jaccard§http://www.okkam.org/ontology_person1.owl#date_of_birth|http://www.okkam.org/ontology_person2.owl#date_of_birth: <= 1.0, > 1.0[trigrams§http://www.okkam.org/ontology_person1.owl#date_of_birth|http://www.okkam.org/ontology_person2.owl#date_of_birth: <= 1.0, > 1.0[cosine§http://www.okkam.org/ontology_person1.owl#surname|http://www.okkam.org/ontology_person2.owl#surname: <= 1.0, > 1.0[negative (0)][positive (0)]][jaccard§http://www.okkam.org/ontology_person1.owl#age|http://www.okkam.org/ontology_person2.owl#age: <= 1.0, > 1.0[negative (0)][positive (0)]]][jaccard§http://www.okkam.org/ontology_person1.owl#soc_sec_id|http://www.okkam.org/ontology_person2.owl#soc_sec_id: <= 1.0, > 1.0[trigrams§http://www.okkam.org/ontology_person1.owl#date_of_birth|http://www.okkam.org/ontology_person2.owl#date_of_birth: <= 1.0, > 1.0[negative (0)][positive (0)]][jaccard§http://www.okkam.org/ontology_person1.owl#age|http://www.okkam.org/ontology_person2.owl#age: <= 1.0, > 1.0[negative (0)][positive (0)]]]][qgrams§http://www.okkam.org/ontology_person1.owl#date_of_birth|http://www.okkam.org/ontology_person2.owl#date_of_birth: <= 0.47829690000000014, > 0.47829690000000014[trigrams§http://www.okkam.org/ontology_person1.owl#surname|http://www.okkam.org/ontology_person2.owl#surname: <= 1.0, > 1.0[cosine§http://www.okkam.org/ontology_person1.owl#surname|http://www.okkam.org/ontology_person2.owl#surname: <= 1.0, > 1.0[negative (0)][positive (0)]][jaccard§http://www.okkam.org/ontology_person1.owl#given_name|http://www.okkam.org/ontology_person2.owl#given_name: <= 1.0, > 1.0[negative (0)][positive (0)]]][qgrams§http://www.okkam.org/ontology_person1.owl#age|http://www.okkam.org/ontology_person2.owl#age: <= 1.0, > 1.0[trigrams§http://www.okkam.org/ontology_person1.owl#surname|http://www.okkam.org/ontology_person2.owl#surname: <= 1.0, > 1.0[negative (0)][positive (0)]][jaccard§http://www.okkam.org/ontology_person1.owl#date_of_birth|http://www.okkam.org/ontology_person2.owl#date_of_birth: <= 1.0, > 1.0[negative (0)][positive (0)]]]]");
		LinkSpecification ls = tp.parseTreePrefix("jaccard§http://www.okkam.org/ontology_person1.owl#surname|http://www.okkam.org/ontology_person2.owl#surname: <= 1.0, > 1.0[jaccard§http://www.okkam.org/ontology_person1.owl#date_of_birth|http://www.okkam.org/ontology_person2.owl#date_of_birth: <= 1.0, > 1.0[negative (0)][positive (0)]][qgrams§http://www.okkam.org/ontology_person1.owl#date_of_birth|http://www.okkam.org/ontology_person2.owl#date_of_birth: <= 0.47829690000000014, > 0.47829690000000014[jaccard§http://www.okkam.org/ontology_person1.owl#surname|http://www.okkam.org/ontology_person2.owl#surname: <= 1.0, > 1.0[negative (0)][positive (0)]][positive (0)]]");
//		LinkSpecification lsAND = new LinkSpecification("AND(AND(AND(jaccard(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|1.00,MINUS(jaccard(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,y.http://www.okkam.org/ontology_person2.owl#date_of_birth)|0.01,jaccard(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,y.http://www.okkam.org/ontology_person2.owl#date_of_birth)|1.00)|0.0)|0.0,jaccard(x.http://www.okkam.org/ontology_person1.owl#soc_sec_id,y.http://www.okkam.org/ontology_person2.owl#soc_sec_id)|1.00)|0.0,jaccard(x.http://www.okkam.org/ontology_person1.owl#age,y.http://www.okkam.org/ontology_person2.owl#age)|1.00)", 0.0);
		LinkSpecification lsPath3 = new LinkSpecification("MINUS(jaccard(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,y.http://www.okkam.org/ontology_person2.owl#date_of_birth)|1.00,jaccard(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|1.0)",0.0);
		LinkSpecification lsPath2 = new LinkSpecification("AND(jaccard(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|1.00,qgrams(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,y.http://www.okkam.org/ontology_person2.owl#date_of_birth)|0.48)", 0.0);
		LinkSpecification lsPath1 = new LinkSpecification("AND(jaccard(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|1.00,MINUS(jaccard(x.http://www.okkam.org/ontology_person1.owl#surname,y.http://www.okkam.org/ontology_person2.owl#surname)|1.00,qgrams(x.http://www.okkam.org/ontology_person1.owl#date_of_birth,y.http://www.okkam.org/ontology_person2.owl#date_of_birth)|0.48)|0.0)", 0.0);
		DynamicPlanner dp = new DynamicPlanner(sourceCache2, targetCache2);
		SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache2, targetCache2,
				dtl2.getMl().getConfiguration().getSourceInfo().getVar(), dtl2.getMl().getConfiguration().getTargetInfo().getVar());
//		AMapping parsedMapping = ee.execute(ls, dp);
		AMapping pathMapping = UnsupervisedDecisionTree.getTotalMapping(((DecisionTreeLearning)dtl2.getMl()).root);
//		AMapping path2Mapping = ee.execute(lsPath2, dp);
//		AMapping getpath2Mapping = UnsupervisedDecisionTree.getpath2MAPPING();
//		AMapping path1Mapping = ee.execute(lsPath1, dp);
//		AMapping getpath1Mapping = UnsupervisedDecisionTree.getpath1MAPPING();
//		AMapping path3Mapping = ee.execute(lsPath3, dp);
//		AMapping getpath3Mapping = UnsupervisedDecisionTree.getpath3MAPPING();
//		assertEquals(path1Mapping,getpath1Mapping);
//		assertEquals(path2Mapping,getpath2Mapping);
//		assertEquals(path3Mapping,getpath3Mapping);
//		AMapping totalM = MappingOperations.union(getpath1Mapping, MappingOperations.union(getpath2Mapping, getpath3Mapping));
//		assertEquals(ls, tp.parseTreePrefix(((DecisionTreeLearning)dtl2.getMl()).root.toString()));
		AMapping lsMapping = ee.execute(tp.parseTreePrefix(((DecisionTreeLearning)dtl2.getMl()).root.toString()),dp);
		AMapping intersection = MappingOperations.intersection(lsMapping, pathMapping);
		assertEquals(intersection.size(),pathMapping.size());
		assertEquals(intersection.size(),lsMapping.size());
//		assertEquals(totalM,pathMapping);
	}
	
	
	private UnsupervisedDecisionTree setGoldTree2(DecisionTreeLearning decisionTreeLearning) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		Field leftChildField = UnsupervisedDecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		Field rightChildField = UnsupervisedDecisionTree.class.getDeclaredField("rightChild");
		rightChildField.setAccessible(true);

		//root
		UnsupervisedDecisionTree root = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#surname", "http://www.okkam.org/ontology_person2.owl#surname", 1.0);
		UnsupervisedDecisionTree jaccDD = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0);
		UnsupervisedDecisionTree trigDD = createNode(decisionTreeLearning,"trigrams", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0);
		UnsupervisedDecisionTree cosSS = createNode(decisionTreeLearning,"cosine", "http://www.okkam.org/ontology_person1.owl#surname", "http://www.okkam.org/ontology_person2.owl#surname", 1.0);
		UnsupervisedDecisionTree jacAA = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#age", "http://www.okkam.org/ontology_person2.owl#age", 1.0);
		UnsupervisedDecisionTree jacSoSo = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#soc_sec_id", "http://www.okkam.org/ontology_person2.owl#soc_sec_id", 1.0);
		UnsupervisedDecisionTree trigDD2 = createNode(decisionTreeLearning,"trigrams", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0);
		UnsupervisedDecisionTree jacAA2 = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#age", "http://www.okkam.org/ontology_person2.owl#age", 1.0);
		UnsupervisedDecisionTree qgrDD = createNode(decisionTreeLearning,"qgrams", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 0.47829690000000014);
		UnsupervisedDecisionTree trigSS = createNode(decisionTreeLearning,"trigrams", "http://www.okkam.org/ontology_person1.owl#surname", "http://www.okkam.org/ontology_person2.owl#surname", 1.0);
		UnsupervisedDecisionTree cosSS2 = createNode(decisionTreeLearning,"cosine", "http://www.okkam.org/ontology_person1.owl#surname", "http://www.okkam.org/ontology_person2.owl#surname", 1.0);
		UnsupervisedDecisionTree jacGivGiv = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#given_name", "http://www.okkam.org/ontology_person2.owl#given_name", 1.0);
		UnsupervisedDecisionTree qgrAA = createNode(decisionTreeLearning,"qgrams", "http://www.okkam.org/ontology_person1.owl#age", "http://www.okkam.org/ontology_person2.owl#age", 1.0);
		UnsupervisedDecisionTree trigSS2 = createNode(decisionTreeLearning,"trigrams", "http://www.okkam.org/ontology_person1.owl#surname", "http://www.okkam.org/ontology_person2.owl#surname", 1.0);
		UnsupervisedDecisionTree jaccDD2 = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0);

		leftChildField.set(root, jaccDD);
		rightChildField.set(root, qgrDD);

		leftChildField.set(jaccDD, trigDD);
		rightChildField.set(jaccDD, jacSoSo);

		leftChildField.set(qgrDD, trigSS);
		rightChildField.set(qgrDD, qgrAA);

		leftChildField.set(trigDD, cosSS);
		rightChildField.set(trigDD, jacAA);

		leftChildField.set(jacSoSo, trigDD2);
		rightChildField.set(jacSoSo, jacAA2);

		leftChildField.set(trigSS, cosSS2);
		rightChildField.set(trigSS, jacGivGiv);
		
		leftChildField.set(qgrAA, trigSS2);
		rightChildField.set(qgrAA, jaccDD2);
		return root;
	}
	
	
//	private UnsupervisedDecisionTree setGoldTree(DecisionTreeLearning decisionTreeLearning) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
//		
//		Field leftChildField = UnsupervisedDecisionTree.class.getDeclaredField("leftChild");
//		leftChildField.setAccessible(true);
//		Field rightChildField = UnsupervisedDecisionTree.class.getDeclaredField("rightChild");
//		rightChildField.setAccessible(true);
//
//		//root
//		UnsupervisedDecisionTree tree = createNode(decisionTreeLearning,"cosine", "name", "name", 0.9);
//
//		//leftChild with 1 subnode
//		UnsupervisedDecisionTree leftChild = createNode(decisionTreeLearning,"jaccard","name", "name", 0.5);
//		UnsupervisedDecisionTree rightChild2 = createNode(decisionTreeLearning,"qgrams","name", "name", 0.3);
//		rightChildField.set(leftChild, rightChild2);
//		leftChildField.set(tree, leftChild);
//		
//		//rightChild
//		UnsupervisedDecisionTree rightChild = createNode(decisionTreeLearning,"trigrams","name", "name", 0.1);
//		rightChildField.set(tree, rightChild);
//		return tree;
//	}

	private UnsupervisedDecisionTree createNode(DecisionTreeLearning decisionTreeLearning, String measure, String sourceProperty, String targetProperty, double threshold) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		UnsupervisedDecisionTree tree = new UnsupervisedDecisionTree(decisionTreeLearning, sourceCache, targetCache, pfm, (double)decisionTreeLearning.getParameter(DecisionTreeLearning.PARAMETER_MIN_PROPERTY_COVERAGE), (double)decisionTreeLearning.getParameter(DecisionTreeLearning.PARAMETER_PROPERTY_LEARNING_RATE));
		
		ExtendedClassifier ec = new ExtendedClassifier(measure, threshold, sourceProperty, targetProperty);
		
		Field classifierField;
		classifierField = UnsupervisedDecisionTree.class.getDeclaredField("classifier");
		classifierField.setAccessible(true);
		classifierField.set(tree, ec);
		
		return tree;
		
	}
}
