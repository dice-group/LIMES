package org.aksw.limes.core.ml.algorithm.fptld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.ml.algorithm.fptld.nodes.ANode;
import org.aksw.limes.core.ml.algorithm.fptld.nodes.InternalNode;
import org.aksw.limes.core.ml.algorithm.fptld.nodes.LeafNode;
import org.junit.Before;
import org.junit.Test;

public class TestNode {

	LinkSpecification simple;
	LinkSpecification complex;
	public static final String jac = "jaccard(x.h,y.h)";
	public static final String qgr = "qgrams(x.h,y.h)";
	public static final String trig = "trigrams(x.h,y.h)";
	InternalNode complexTree;
	LeafNode middle;
	LeafNode left;
	LeafNode right;

	@Before
	public void prepareData() {
		simple = new LinkSpecification(jac, 0.3);
		complex = new LinkSpecification("EINT(" + jac + "|0.7,EINTCO(" + qgr + "|0.2," + jac + "|0.3)|0.0)", 0.0);

		middle = new LeafNode(qgr, 0.2);
		left = new LeafNode(jac, 0.7);
		right = new LeafNode(jac, 0.3);
		InternalNode eint = new InternalNode(LogicOperator.EINSTEINT);
		InternalNode eintco = new InternalNode(LogicOperator.EINSTEINTCO);
		eint.setLeftChild(left);
		eint.setRightChild(eintco);
		eintco.setLeftChild(middle);
		eintco.setRightChild(right);
		complexTree = eint;
	}

	@Test
	public void testSimple() {
		LeafNode ln = new LeafNode(jac, 0.3);
		assertEquals(simple, ln.toLS());
	}

	@Test
	public void testComplex() {
		assertEquals(complex, complexTree.toLS());
	}

	@Test
	public void testGetLeaves() {
		List<LeafNode> leaves = new ArrayList<>();
		leaves.add(left);
		leaves.add(middle);
		leaves.add(right);
		assertEquals(leaves, complexTree.getLeaves());
		List<LeafNode> leaf = new ArrayList<>();
		leaf.add(left);
		assertEquals(leaf, left.getLeaves());
	}

	@Test
	public void testSetLeftChild() {
		InternalNode test = (InternalNode) left.getParent().clone();
		test.setLeftChild(new LeafNode(trig, 0.8));

		LeafNode middle = new LeafNode(qgr, 0.2);
		LeafNode left = new LeafNode(trig, 0.8);
		LeafNode right = new LeafNode(jac, 0.3);
		InternalNode eint = new InternalNode(LogicOperator.EINSTEINT);
		InternalNode eintco = new InternalNode(LogicOperator.EINSTEINTCO);
		eint.setLeftChild(left);
		eint.setRightChild(eintco);
		eintco.setLeftChild(middle);
		eintco.setRightChild(right);
		assertEquals(eint, test.getRoot());
		assertEquals(left, test.getLeftChild());
		assertEquals(eint, test);
	}

	@Test
	public void testReplaceLeaf() {
		LeafNode replacee = new LeafNode(trig, 0.3);
		ANode newNode = complexTree.clone().replaceLeaf(middle, LogicOperator.ALGEBRAICT, replacee, Double.NaN);
		assertNotSame(newNode, complexTree);
		assertFalse(newNode.equals(complexTree));
		InternalNode middle = new InternalNode(LogicOperator.ALGEBRAICT);
		LeafNode middleLeft = new LeafNode(qgr, 0.2);
		LeafNode middleRight = new LeafNode("trigrams(x.h,y.h)", 0.3);
		LeafNode left = new LeafNode(jac, 0.7);
		LeafNode right = new LeafNode(jac, 0.3);
		InternalNode eint = new InternalNode(LogicOperator.EINSTEINT);
		InternalNode eintco = new InternalNode(LogicOperator.EINSTEINTCO);
		eint.setLeftChild(left);
		eint.setRightChild(eintco);
		eintco.setLeftChild(middle);
		eintco.setRightChild(right);
		middle.setLeftChild(middleLeft);
		middle.setRightChild(middleRight);
		assertEquals(eint, newNode);
	}

	@Test
	public void testReplaceLeafSimple() {
		LeafNode toBeReplaced = new LeafNode("qgrams(x.h,y.h)", 0.8);
		LeafNode replacee = new LeafNode("trigrams(x.h,y.h)", 0.3);
		ANode newNode = toBeReplaced.replaceLeaf(toBeReplaced, LogicOperator.ALGEBRAICT, replacee, Double.NaN);

		LeafNode left = new LeafNode("qgrams(x.h,y.h)", 0.8);
		LeafNode right = new LeafNode("trigrams(x.h,y.h)", 0.3);
		InternalNode middle = new InternalNode(LogicOperator.ALGEBRAICT);
		middle.setLeftChild(left);
		middle.setRightChild(right);
		assertEquals(middle, newNode);
	}

	@Test
	public void testClone() {
		InternalNode clone = (InternalNode) complexTree.clone();
		assertEquals(clone, complexTree);
		assertNotSame(clone, complexTree);
		List<LeafNode> leaves = complexTree.getLeaves();
		List<LeafNode> cloneLeaves = clone.getLeaves();
		for (int i = 0; i < leaves.size(); i++) {
			assertEquals(cloneLeaves.get(i), leaves.get(i));
			assertEquals(cloneLeaves.get(i).getParent(), leaves.get(i).getParent());
			assertNotSame(cloneLeaves.get(i), leaves.get(i));
			assertNotSame(cloneLeaves.get(i).getParent(), leaves.get(i).getParent());
		}
	}

	@Test
	public void testInnerClone() {
		InternalNode clone = (InternalNode) left.getParent().clone();
		assertEquals(clone, left.getParent());
		assertNotSame(clone, left.getParent());
		List<LeafNode> leaves = left.getParent().getLeaves();
		List<LeafNode> cloneLeaves = clone.getLeaves();
		for (int i = 0; i < leaves.size(); i++) {
			assertEquals(cloneLeaves.get(i), leaves.get(i));
			assertEquals(cloneLeaves.get(i).getParent(), leaves.get(i).getParent());
			assertNotSame(cloneLeaves.get(i), leaves.get(i));
			assertNotSame(cloneLeaves.get(i).getParent(), leaves.get(i).getParent());
		}
		assertEquals(clone.getRoot(), complexTree);
		assertNotSame(clone.getRoot(), complexTree);
	}

	@Test
	public void testDownClone() throws NoSuchMethodException, SecurityException, IllegalAccessException,
	IllegalArgumentException, InvocationTargetException {
		Method method = ANode.class.getDeclaredMethod("downwardClone", ANode.class);
		method.setAccessible(true);
		InternalNode clone = (InternalNode) method.invoke(complexTree, new Object[] { null });
		assertEquals(clone, complexTree);
		assertNotSame(clone, complexTree);
		List<LeafNode> leaves = complexTree.getLeaves();
		List<LeafNode> cloneLeaves = clone.getLeaves();
		for (int i = 0; i < leaves.size(); i++) {
			assertEquals(cloneLeaves.get(i), leaves.get(i));
			assertEquals(cloneLeaves.get(i).getParent(), leaves.get(i).getParent());
			assertNotSame(cloneLeaves.get(i), leaves.get(i));
			assertNotSame(cloneLeaves.get(i).getParent(), leaves.get(i).getParent());
		}
	}
}
