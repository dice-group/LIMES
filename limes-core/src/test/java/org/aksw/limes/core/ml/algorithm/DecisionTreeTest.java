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
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
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
	public void prepareData() {

		this.c = DataSetChooser.getData(DataSetChooser.DataSets.RESTAURANTS_FIXED);
		this.c2 = DataSetChooser.getData(DataSetChooser.DataSets.PERSON2);
		try {
			this.dtl = MLAlgorithmFactory.createMLAlgorithm(Dragon.class, MLImplementationType.SUPERVISED_BATCH);
			this.dtl2 = MLAlgorithmFactory.createMLAlgorithm(Dragon.class, MLImplementationType.SUPERVISED_BATCH);
			this.sourceCache = this.c.getSourceCache();
			this.targetCache = this.c.getTargetCache();
			this.sourceCache2 = this.c2.getSourceCache();
			this.targetCache2 = this.c2.getTargetCache();
			this.dtl.init(null, this.sourceCache, this.targetCache);
			this.dtl2.init(null, this.sourceCache2, this.targetCache2);
			this.dtl.getMl().setConfiguration(this.c.getConfigReader().read());
			((Dragon) this.dtl.getMl()).setParameter(Dragon.PARAMETER_PROPERTY_MAPPING, this.c.getPropertyMapping());
			this.dtl2.getMl().setConfiguration(this.c2.getConfigReader().read());
			((Dragon) this.dtl2.getMl()).setParameter(Dragon.PARAMETER_PROPERTY_MAPPING, this.c2.getPropertyMapping());

			this.dtl2.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
			// dtl2.asUnsupervised().learn(new PseudoFMeasure());
		} catch (final UnsupportedMLImplementationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// DecisionTreeLearning decisionTreeLearning =
		// (DecisionTreeLearning)dtl.getMl();
		final Dragon decisionTreeLearning2 = (Dragon) this.dtl2.getMl();
		try {
			// goldTree = setGoldTree(decisionTreeLearning);
			this.goldTree2 = this.setGoldTree2(decisionTreeLearning2);
			this.goldTreeMapping = this.setGoldTreeMapping(decisionTreeLearning2);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.goldLS = new LinkSpecification(
				"OR(AND(MINUS(cosine(x.name,y.name)|0.01,cosine(x.name,y.name)|0.9)|0.0,AND(jaccard(x.name,y.name)|0.5,qgrams(x.name,y.name)|0.3)|0.0)|0.0,AND(cosine(x.name,y.name)|0.9,trigrams(x.name,y.name)|0.1)|0.0)",
				0.0);
	}

	@Test
	public void testLearn() throws UnsupportedMLImplementationException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		this.dtl.getMl().setParameter(Dragon.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
		final MLResults res = this.dtl.asSupervised().learn(getTrainingData(this.c.getReferenceMapping()));
		final Field sourceCacheField = DecisionTree.class.getDeclaredField("sourceCache");
		sourceCacheField.setAccessible(true);
		assertEquals("Source caches do not match", this.sourceCache,
				sourceCacheField.get(((Dragon) this.dtl.getMl()).root));
		System.out.println(res.getLinkSpecification());
		System.out.println(
				"FMeasure: " + new FMeasure().calculate(this.dtl.predict(this.sourceCache, this.targetCache, res),
						new GoldStandard(this.c.getReferenceMapping())));
	}

	public static AMapping getTrainingData(AMapping full) {
		final int sliceSizeWanted = full.size() - (int) Math.ceil(full.getSize() / 10.0);
		final AMapping slice = MappingFactory.createDefaultMapping();
		final Object[] keyArr = full.getMap().keySet().toArray();
		int c = 5;
		int i = 2;
		while (slice.size() <= sliceSizeWanted) {
			// String key = (String)keyArr[(int)(Math.random() *
			// keyArr.length)];
			final String key = (String) keyArr[c];
			c = c + i;
			if (c >= keyArr.length) {
				c = 0;
				i++;
			}
			if (!slice.getMap().keySet().contains(key)) {
				slice.add(key, full.getMap().get(key));
			}
		}
		System.out.println("got: " + MappingOperations.intersection(slice, full).size() + " wanted: " + sliceSizeWanted
				+ " full: " + full.size());
		return slice;
	}

	@Test
	public void testClone1()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		// Test root
		final DecisionTree test = this.goldTree2.clone();
		assertEquals("Tree is incorrect", this.goldTree2.toString(), test.toString());
	}

	@Test
	public void testClone2()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		// Test node
		final DecisionTree goldLeft = (DecisionTree) leftChildField.get(this.goldTree2);
		final DecisionTree testLeft = goldLeft.clone();
		assertEquals("Tree is incorrect", goldLeft.toString(), testLeft.toString());
	}

	@Test
	public void testClone3()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		// Test leaf
		final DecisionTree goldLeft = (DecisionTree) leftChildField.get(this.goldTree2);
		final DecisionTree goldcosSS = (DecisionTree) leftChildField.get(leftChildField.get(goldLeft));
		final DecisionTree testcosSS = goldcosSS.clone();
		assertEquals("Tree is incorrect", goldcosSS.toString(), testcosSS.toString());
	}

	@Test
	public void testGetRootNode() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		final Method getRootNode = DecisionTree.class.getDeclaredMethod("getRootNode", new Class[0]);
		getRootNode.setAccessible(true);
		final Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		final DecisionTree root = (DecisionTree) getRootNode.invoke(this.goldTree2, new Object[0]);
		assertEquals("Tree is incorrect", this.goldTree2.toString(), root.toString());
	}

	@Test
	public void testGetRootNodeForInnerNode() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		final Method getRootNode = DecisionTree.class.getDeclaredMethod("getRootNode", new Class[0]);
		getRootNode.setAccessible(true);
		final Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		final DecisionTree leftChild = (DecisionTree) leftChildField.get(this.goldTree2);
		final DecisionTree root2 = (DecisionTree) getRootNode.invoke(leftChild, new Object[0]);
		assertEquals("Tree is incorrect", this.goldTree2.toString(), root2.toString());
	}

	@Test
	public void testGetRootNodeDetachedNode() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		final Method getRootNode = DecisionTree.class.getDeclaredMethod("getRootNode", new Class[0]);
		getRootNode.setAccessible(true);
		final DecisionTree detachedNode = this.createNode((Dragon) this.dtl2.getMl(), "jaccard",
				"http://www.okkam.org/ontology_person1.owl#date_of_birth",
				"http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0, null, false);
		final DecisionTree root3 = (DecisionTree) getRootNode.invoke(detachedNode, new Object[0]);
		assertNull("No Tree should be returned for detached node", root3);
	}

	@Test
	public void testGetTotalMapping() {

		final AMapping goldMap = MappingFactory.createDefaultMapping();
		goldMap.add("C", "C", 1.0);
		goldMap.add("D", "D", 1.0);
		goldMap.add("E", "E", 1.0);
		goldMap.add("F", "F", 1.0);

		final AMapping map = this.goldTreeMapping.getTotalMapping();
		assertEquals("Mapping of tree is incorrect", goldMap, map);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetPathMappings() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final Method calculatePathMappings = DecisionTree.class.getDeclaredMethod("calculatePathMappings",
				new Class[] { DecisionTree.class });
		calculatePathMappings.setAccessible(true);
		final List<String> pathstrings = (List<String>) calculatePathMappings.invoke(this.goldTreeMapping,
				this.goldTreeMapping);
		assertEquals("Should contain 2 paths", 2, pathstrings.size());
	}

	private DecisionTree setGoldTreeMapping(Dragon decisionTreeLearning)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
			InstantiationException, InvocationTargetException, NoSuchMethodException {

		final Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		final Field rightChildField = DecisionTree.class.getDeclaredField("rightChild");
		rightChildField.setAccessible(true);

		// root
		final DecisionTree root = this.createRoot(decisionTreeLearning, "jaccard", "att1", "att1", 1.0);
		final DecisionTree left = this.createNode(decisionTreeLearning, "cosine", "att2", "att2", 1.0, root, true);
		final DecisionTree right = this.createNode(decisionTreeLearning, "trigrams", "att3", "att3", 1.0, root, false);

		leftChildField.set(root, left);
		rightChildField.set(root, right);

		final AMapping refMap = MappingFactory.createDefaultMapping();
		refMap.add("A", "A", 1.0);
		refMap.add("B", "B", 1.0);
		refMap.add("C", "C", 1.0);
		refMap.add("D", "D", 1.0);
		refMap.add("E", "E", 1.0);
		refMap.add("F", "F", 1.0);
		refMap.add("G", "G", 1.0);
		refMap.add("H", "H", 1.0);

		final AMapping rootMap = MappingFactory.createDefaultMapping();
		rootMap.add("A", "A", 1.0);
		rootMap.add("B", "B", 1.0);
		rootMap.add("C", "C", 1.0);

		final AMapping rightMap = MappingFactory.createDefaultMapping();
		rightMap.add("C", "C", 1.0);
		rightMap.add("H", "H", 1.0);

		final AMapping leftMap = MappingFactory.createDefaultMapping();
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

	private DecisionTree setGoldTree2(Dragon decisionTreeLearning)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
			InstantiationException, InvocationTargetException, NoSuchMethodException {

		final Field leftChildField = DecisionTree.class.getDeclaredField("leftChild");
		leftChildField.setAccessible(true);
		final Field rightChildField = DecisionTree.class.getDeclaredField("rightChild");
		rightChildField.setAccessible(true);

		// root
		final DecisionTree root = this.createRoot(decisionTreeLearning, "jaccard",
				"http://www.okkam.org/ontology_person1.owl#surname",
				"http://www.okkam.org/ontology_person2.owl#surname", 1.0);
		final DecisionTree jaccDD = this.createNode(decisionTreeLearning, "jaccard",
				"http://www.okkam.org/ontology_person1.owl#date_of_birth",
				"http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0, root, true);
		final DecisionTree trigDD = this.createNode(decisionTreeLearning, "trigrams",
				"http://www.okkam.org/ontology_person1.owl#date_of_birth",
				"http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0, jaccDD, true);
		final DecisionTree cosSS = this.createNode(decisionTreeLearning, "cosine",
				"http://www.okkam.org/ontology_person1.owl#surname",
				"http://www.okkam.org/ontology_person2.owl#surname", 1.0, trigDD, true);
		final DecisionTree jacAA = this.createNode(decisionTreeLearning, "jaccard",
				"http://www.okkam.org/ontology_person1.owl#age", "http://www.okkam.org/ontology_person2.owl#age", 1.0,
				trigDD, false);
		final DecisionTree jacSoSo = this.createNode(decisionTreeLearning, "jaccard",
				"http://www.okkam.org/ontology_person1.owl#soc_sec_id",
				"http://www.okkam.org/ontology_person2.owl#soc_sec_id", 1.0, jaccDD, false);
		final DecisionTree trigDD2 = this.createNode(decisionTreeLearning, "trigrams",
				"http://www.okkam.org/ontology_person1.owl#date_of_birth",
				"http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0, jacSoSo, true);
		final DecisionTree jacAA2 = this.createNode(decisionTreeLearning, "jaccard",
				"http://www.okkam.org/ontology_person1.owl#age", "http://www.okkam.org/ontology_person2.owl#age", 1.0,
				jacSoSo, false);
		final DecisionTree qgrDD = this.createNode(decisionTreeLearning, "qgrams",
				"http://www.okkam.org/ontology_person1.owl#date_of_birth",
				"http://www.okkam.org/ontology_person2.owl#date_of_birth", 0.47829690000000014, root, false);
		final DecisionTree trigSS = this.createNode(decisionTreeLearning, "trigrams",
				"http://www.okkam.org/ontology_person1.owl#surname",
				"http://www.okkam.org/ontology_person2.owl#surname", 1.0, qgrDD, true);
		final DecisionTree cosSS2 = this.createNode(decisionTreeLearning, "cosine",
				"http://www.okkam.org/ontology_person1.owl#surname",
				"http://www.okkam.org/ontology_person2.owl#surname", 1.0, trigSS, true);
		final DecisionTree jacGivGiv = this.createNode(decisionTreeLearning, "jaccard",
				"http://www.okkam.org/ontology_person1.owl#given_name",
				"http://www.okkam.org/ontology_person2.owl#given_name", 1.0, trigSS, false);
		final DecisionTree qgrAA = this.createNode(decisionTreeLearning, "qgrams",
				"http://www.okkam.org/ontology_person1.owl#age", "http://www.okkam.org/ontology_person2.owl#age", 1.0,
				qgrDD, false);
		final DecisionTree trigSS2 = this.createNode(decisionTreeLearning, "trigrams",
				"http://www.okkam.org/ontology_person1.owl#surname",
				"http://www.okkam.org/ontology_person2.owl#surname", 1.0, qgrAA, true);
		final DecisionTree jaccDD2 = this.createNode(decisionTreeLearning, "jaccard",
				"http://www.okkam.org/ontology_person1.owl#date_of_birth",
				"http://www.okkam.org/ontology_person2.owl#date_of_birth", 1.0, qgrAA, false);

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

	private DecisionTree createRoot(Dragon decisionTreeLearning, String measure, String sourceProperty,
			String targetProperty, double threshold)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final DecisionTree tree = new DecisionTree(decisionTreeLearning, this.sourceCache, this.targetCache, this.pfm,
				(double) decisionTreeLearning.getParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE),
				(double) decisionTreeLearning.getParameter(Dragon.PARAMETER_PROPERTY_LEARNING_RATE), threshold,
				MappingFactory.createDefaultMapping(), this.c.getPropertyMapping());

		final ExtendedClassifier ec = new ExtendedClassifier(measure, threshold, sourceProperty, targetProperty);

		Field classifierField;
		classifierField = DecisionTree.class.getDeclaredField("classifier");
		classifierField.setAccessible(true);
		classifierField.set(tree, ec);

		return tree;

	}

	private DecisionTree createNode(Dragon decisionTreeLearning, String measure, String sourceProperty,
			String targetProperty, double threshold, DecisionTree parent, boolean isLeftNode)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
			InstantiationException, InvocationTargetException, NoSuchMethodException {
		Constructor<DecisionTree> constructor;
		final Class<?>[] parameterTypes = { Dragon.class, ACache.class, ACache.class, PseudoFMeasure.class,
				double.class, double.class, double.class, DecisionTree.class, boolean.class, AMapping.class,
				PropertyMapping.class };
		constructor = DecisionTree.class.getDeclaredConstructor(parameterTypes);
		constructor.setAccessible(true);
		final DecisionTree tree = constructor.newInstance(decisionTreeLearning, this.sourceCache, this.targetCache,
				this.pfm, (double) decisionTreeLearning.getParameter(Dragon.PARAMETER_MIN_PROPERTY_COVERAGE),
				(double) decisionTreeLearning.getParameter(Dragon.PARAMETER_PRUNING_CONFIDENCE),
				(double) decisionTreeLearning.getParameter(Dragon.PARAMETER_PROPERTY_LEARNING_RATE), parent, isLeftNode,
				MappingFactory.createDefaultMapping(),
				decisionTreeLearning.getParameter(Dragon.PARAMETER_PROPERTY_MAPPING));
		final ExtendedClassifier ec = new ExtendedClassifier(measure, threshold, sourceProperty, targetProperty);

		Field classifierField;
		classifierField = DecisionTree.class.getDeclaredField("classifier");
		classifierField.setAccessible(true);
		classifierField.set(tree, ec);

		return tree;

	}

}
