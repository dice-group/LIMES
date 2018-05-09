package org.aksw.limes.core.ml.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.dragon.DecisionTree;
import org.aksw.limes.core.ml.algorithm.dragon.Dragon;
import org.junit.Before;
import org.junit.Test;

public class DecisionTreeTest {

	public DecisionTree goldTreeMapping;
	public DecisionTree goldTree2;
	public AMLAlgorithm dtl;
	public AMLAlgorithm dtl2;
	public ACache sourceCache;
	public ACache targetCache;
	public ACache sourceCache2;
	public ACache targetCache2;
	public PseudoFMeasure pfm;
	public LinkSpecification goldLS;
	public EvaluationData c;
	public EvaluationData c2;
	
	@Before
	public void prepareData(){
		
		c = DataSetChooser.getData(DataSetChooser.DataSets.RESTAURANTS_FIXED);
		c2 = DataSetChooser.getData(DataSetChooser.DataSets.PERSON2);
		try {
			dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
					MLImplementationType.SUPERVISED_BATCH);
			dtl2 = MLAlgorithmFactory.createMLAlgorithm(Dragon.class,
					MLImplementationType.UNSUPERVISED);
			sourceCache = c.getSourceCache();
			targetCache = c.getTargetCache();
			sourceCache2 = c2.getSourceCache();
			targetCache2 = c2.getTargetCache();
			dtl.init(null, sourceCache, targetCache);
			dtl2.init(null, sourceCache2, targetCache2);
			dtl.getMl().setConfiguration(c.getConfigReader().read());
			((Dragon) dtl.getMl()).setParameter(Dragon.PARAMETER_PROPERTY_MAPPING, c.getPropertyMapping());
			dtl2.getMl().setConfiguration(c2.getConfigReader().read());
			((Dragon) dtl.getMl()).setParameter(Dragon.PARAMETER_PROPERTY_MAPPING, c2.getPropertyMapping());

			dtl2.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
//			dtl2.asUnsupervised().learn(new PseudoFMeasure());
		} catch (UnsupportedMLImplementationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		DecisionTreeLearning decisionTreeLearning = (DecisionTreeLearning)dtl.getMl();
		Dragon decisionTreeLearning2 = (Dragon)dtl2.getMl();
		try {
//			goldTree = setGoldTree(decisionTreeLearning);
			goldTree2 = setGoldTree2(decisionTreeLearning2);
			goldTreeMapping = setGoldTreeMapping(decisionTreeLearning2);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		goldLS = new LinkSpecification("OR(AND(MINUS(cosine(x.name,y.name)|0.01,cosine(x.name,y.name)|0.9)|0.0,AND(jaccard(x.name,y.name)|0.5,qgrams(x.name,y.name)|0.3)|0.0)|0.0,AND(cosine(x.name,y.name)|0.9,trigrams(x.name,y.name)|0.1)|0.0)",0.0);
	}
	
	@Test
	public void testLearn() throws UnsupportedMLImplementationException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
		MLResults res = dtl.asSupervised().learn(getTrainingData(c.getReferenceMapping()));
		Field sourceCacheField = DecisionTree.class.getDeclaredField("sourceCache");
		sourceCacheField.setAccessible(true);
		assertEquals(sourceCache, sourceCacheField.get(((Dragon)dtl.getMl()).root));
		System.out.println(res.getLinkSpecification());
		System.out.println("FMeasure: " + new FMeasure().calculate(dtl.predict(sourceCache, targetCache, res), new GoldStandard(c.getReferenceMapping())));
	}

	public static AMapping getTrainingData(AMapping full) {
		int sliceSizeWanted = full.size() - (int) Math.ceil(((double) full.getSize() / 10.0));
		AMapping slice = MappingFactory.createDefaultMapping();
		Object[] keyArr = full.getMap().keySet().toArray();
		int c = 5;
		int i = 2;
		while (slice.size() <= sliceSizeWanted) {
			// String key = (String)keyArr[(int)(Math.random() *
			// keyArr.length)];
			String key = (String) keyArr[c];
			c = c + i;
			if(c >= keyArr.length){
				c = 0;
				i++;
			}
			if (!slice.getMap().keySet().contains(key)) {
				slice.add(key, full.getMap().get(key));
			}
		}
		System.out.println("got: " + MappingOperations.intersection(slice, full).size() + " wanted: " + sliceSizeWanted + " full: " + full.size());
		return slice;
	}
	
	@Test
	public void testClone() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		//Test root
		DecisionTree test = goldTree2.clone();
		assertEquals(goldTree2.toString(), test.toString());
		//Test node
		DecisionTree goldLeft = (DecisionTree) leftChildField.get(goldTree2);
		DecisionTree testLeft = goldLeft.clone();
		assertEquals(goldLeft.toString(), testLeft.toString());
		//Test leaf
		DecisionTree goldcosSS = (DecisionTree) leftChildField.get(leftChildField.get(goldLeft));
		DecisionTree testcosSS = goldcosSS.clone();
		assertEquals(goldcosSS.toString(), testcosSS.toString());
	}
	
//	@Test
//	public void testParsing(){
//		//FIX THE GOLDLS
//		System.out.println(goldTree2.toStringPretty());
//		LinkSpecification testLS = ((DecisionTreeLearning)dtl2.getMl()).tp.parseTreePrefix(goldTree2.toString());
//		assertEquals(goldLS.toString(), testLS.toString());
//	}
	
	@Test
	public void testGetRootNode() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException{
		Method getRootNode = DecisionTree.class.getDeclaredMethod("getRootNode", new Class[0]);
		getRootNode.setAccessible(true);
		Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		DecisionTree root = (DecisionTree) getRootNode.invoke(goldTree2, new Object[0]);
		assertEquals(goldTree2.toString(),root.toString());
		DecisionTree leftChild = (DecisionTree) leftChildField.get(goldTree2);
		DecisionTree root2 = (DecisionTree) getRootNode.invoke(leftChild, new Object[0]);
		assertEquals(goldTree2.toString(),root2.toString());
		DecisionTree detachedNode = createNode((Dragon)dtl2.getMl(),"jaccard", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0, null, false);
		DecisionTree root3 = (DecisionTree) getRootNode.invoke(detachedNode, new Object[0]);
		assertNull(root3);
	}
	
	@Test
	public void testGetTotalMapping(){

		AMapping goldMap = MappingFactory.createDefaultMapping();
		goldMap.add("C", "C", 1.0);
		goldMap.add("D", "D", 1.0);
		goldMap.add("E", "E", 1.0);
		goldMap.add("F", "F", 1.0);

		AMapping map = goldTreeMapping.getTotalMapping();
		assertEquals(goldMap, map);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetPathMappings() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Method calculatePathMappings = DecisionTree.class.getDeclaredMethod("calculatePathMappings", new Class[]{DecisionTree.class});
		calculatePathMappings.setAccessible(true);
//		List<String> pathstringsLeft = (List<String>) calculatePathMappings.invoke(goldTreeMapping.getLeftChild(), goldTreeMapping.getLeftChild());
//		assertEquals(1, pathstringsLeft.size());
//		List<String> pathstringsRight = (List<String>) calculatePathMappings.invoke(goldTreeMapping.getRightChild(), goldTreeMapping.getRightChild());
//		assertEquals(1, pathstringsRight.size());
		List<String> pathstrings = (List<String>) calculatePathMappings.invoke(goldTreeMapping, goldTreeMapping);
		assertEquals(2, pathstrings.size());
	}

	private DecisionTree setGoldTreeMapping(Dragon decisionTreeLearning) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{
		
		Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		Field rightChildField = DecisionTree.class.getDeclaredField("rightChild");
		rightChildField.setAccessible(true);

		//root
		DecisionTree root = createRoot(decisionTreeLearning,"jaccard", "att1", "att1", 1.0);
		DecisionTree left = createNode(decisionTreeLearning,"cosine", "att2", "att2", 1.0, root, true);
		DecisionTree right = createNode(decisionTreeLearning,"trigrams", "att3", "att3", 1.0, root, false);

		leftChildField.set(root, left);
		rightChildField.set(root, right);
		
		AMapping refMap = MappingFactory.createDefaultMapping();
		refMap.add("A", "A", 1.0);
		refMap.add("B", "B", 1.0);
		refMap.add("C", "C", 1.0);
		refMap.add("D", "D", 1.0);
		refMap.add("E", "E", 1.0);
		refMap.add("F", "F", 1.0);
		refMap.add("G", "G", 1.0);
		refMap.add("H", "H", 1.0);

		AMapping rootMap = MappingFactory.createDefaultMapping();
		rootMap.add("A", "A", 1.0);
		rootMap.add("B", "B", 1.0);
		rootMap.add("C", "C", 1.0);
		
		AMapping rightMap = MappingFactory.createDefaultMapping();
		rightMap.add("C", "C", 1.0);
		rightMap.add("H", "H", 1.0);

		AMapping leftMap = MappingFactory.createDefaultMapping();
		leftMap.add("B", "B", 1.0);
		leftMap.add("D", "D", 1.0);
		leftMap.add("E", "E", 1.0);
		leftMap.add("F", "F", 1.0);

		root.setRefMapping(refMap);
		right.setRefMapping(refMap);
		left.setRefMapping(refMap);

		root.getClassifier().setMapping(rootMap);
		left.getClassifier().setMapping(leftMap);
		right.getClassifier().setMapping(rightMap);

		return root;
	}

	private DecisionTree setGoldTree2(Dragon decisionTreeLearning) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{
		
		Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		Field rightChildField = DecisionTree.class.getDeclaredField("rightChild");
		rightChildField.setAccessible(true);

		//root
		DecisionTree root = createRoot(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#surname", "http://www.okkam.org/ontology_person2.owl#surname", 1.0);
		DecisionTree jaccDD = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0, root, true);
		DecisionTree trigDD = createNode(decisionTreeLearning,"trigrams", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0, jaccDD, true);
		DecisionTree cosSS = createNode(decisionTreeLearning,"cosine", "http://www.okkam.org/ontology_person1.owl#surname", "http://www.okkam.org/ontology_person2.owl#surname", 1.0, trigDD, true );
		DecisionTree jacAA = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#age", "http://www.okkam.org/ontology_person2.owl#age", 1.0, trigDD, false);
		DecisionTree jacSoSo = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#soc_sec_id", "http://www.okkam.org/ontology_person2.owl#soc_sec_id", 1.0, jaccDD, false);
		DecisionTree trigDD2 = createNode(decisionTreeLearning,"trigrams", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0, jacSoSo, true);
		DecisionTree jacAA2 = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#age", "http://www.okkam.org/ontology_person2.owl#age", 1.0, jacSoSo, false);
		DecisionTree qgrDD = createNode(decisionTreeLearning,"qgrams", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 0.47829690000000014, root, false);
		DecisionTree trigSS = createNode(decisionTreeLearning,"trigrams", "http://www.okkam.org/ontology_person1.owl#surname", "http://www.okkam.org/ontology_person2.owl#surname", 1.0, qgrDD, true);
		DecisionTree cosSS2 = createNode(decisionTreeLearning,"cosine", "http://www.okkam.org/ontology_person1.owl#surname", "http://www.okkam.org/ontology_person2.owl#surname", 1.0, trigSS, true);
		DecisionTree jacGivGiv = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#given_name", "http://www.okkam.org/ontology_person2.owl#given_name", 1.0, trigSS, false);
		DecisionTree qgrAA = createNode(decisionTreeLearning,"qgrams", "http://www.okkam.org/ontology_person1.owl#age", "http://www.okkam.org/ontology_person2.owl#age", 1.0, qgrDD, false);
		DecisionTree trigSS2 = createNode(decisionTreeLearning,"trigrams", "http://www.okkam.org/ontology_person1.owl#surname", "http://www.okkam.org/ontology_person2.owl#surname", 1.0, qgrAA, true);
		DecisionTree jaccDD2 = createNode(decisionTreeLearning,"jaccard", "http://www.okkam.org/ontology_person1.owl#date_of_birth", "http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0, qgrAA, false);

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


	private DecisionTree createRoot(Dragon decisionTreeLearning, String measure, String sourceProperty, String targetProperty, double threshold) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		DecisionTree tree = new DecisionTree(decisionTreeLearning, sourceCache, targetCache, pfm, (double)decisionTreeLearning.getParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE), (double)decisionTreeLearning.getParameter(Dragon.PARAMETER_PROPERTY_LEARNING_RATE), threshold, MappingFactory.createDefaultMapping(), c.getPropertyMapping());
		
		ExtendedClassifier ec = new ExtendedClassifier(measure, threshold, sourceProperty, targetProperty);
		
		Field classifierField;
		classifierField = DecisionTree.class.getDeclaredField("classifier");
		classifierField.setAccessible(true);
		classifierField.set(tree, ec);
		
		return tree;
		
	}

	private DecisionTree createNode(Dragon decisionTreeLearning, String measure, String sourceProperty, String targetProperty, double threshold, DecisionTree parent, boolean isLeftNode) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{
		Constructor <DecisionTree> constructor;
		Class<?>[] parameterTypes = {Dragon.class, ACache.class, ACache.class, PseudoFMeasure.class, double.class, double.class, double.class, DecisionTree.class, boolean.class, AMapping.class};
		constructor = DecisionTree.class.getDeclaredConstructor(parameterTypes);
		constructor.setAccessible(true);
		DecisionTree tree = constructor.newInstance(decisionTreeLearning, sourceCache, targetCache, pfm, (double)decisionTreeLearning.getParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE),(double)decisionTreeLearning.getParameter(Dragon.PARAMETER_PRUNING_CONFIDENCE), (double)decisionTreeLearning.getParameter(Dragon.PARAMETER_PROPERTY_LEARNING_RATE), parent, isLeftNode, MappingFactory.createDefaultMapping());
		ExtendedClassifier ec = new ExtendedClassifier(measure, threshold, sourceProperty, targetProperty);
		
		Field classifierField;
		classifierField = DecisionTree.class.getDeclaredField("classifier");
		classifierField.setAccessible(true);
		classifierField.set(tree, ec);
		
		return tree;
		
	}

}
