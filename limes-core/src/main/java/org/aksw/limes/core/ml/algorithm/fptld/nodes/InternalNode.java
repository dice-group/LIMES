package org.aksw.limes.core.ml.algorithm.fptld.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.io.ls.LinkSpecification;

import com.google.common.collect.ImmutableMap;

public class InternalNode extends ANode {
	public static final Map<LogicOperator, String> opToExpMapping = ImmutableMap.<LogicOperator, String>builder()
			.put(LogicOperator.AND, "AND").put(LogicOperator.ALGEBRAICDIFF, "ALGDIFF")
			.put(LogicOperator.ALGEBRAICT, "ALGT").put(LogicOperator.ALGEBRAICTCO, "ALGTCO")
			.put(LogicOperator.EINSTEINDIFF, "EINDIFF").put(LogicOperator.EINSTEINT, "EINT")
			.put(LogicOperator.EINSTEINTCO, "EINTCO").put(LogicOperator.HAMACHERDIFF, "HAMDIFF")
			.put(LogicOperator.HAMACHERT, "HAMT").put(LogicOperator.HAMACHERTCO, "HAMTCO")
			.put(LogicOperator.LUKASIEWICZT, "LUKT").put(LogicOperator.LUKASIEWICZTCO, "LUKTCO")
			.put(LogicOperator.LUKASIEWICZDIFF, "LUKDIFF").put(LogicOperator.MINUS, "MINUS").put(LogicOperator.OR, "OR")
			.put(LogicOperator.XOR, "XOR").put(LogicOperator.YAGERT, "YAGERT").put(LogicOperator.YAGERTCO, "YAGERTCO")
			.put(LogicOperator.YAGERDIFF, "YAGERDIFF")
			.build();

	public InternalNode(LogicOperator op) {
		super();
		this.op = op;
	}

	public InternalNode(LogicOperator op, double parameter) {
		super();
		this.op = op;
		this.parameter = parameter;
	}

	@Override
	public LinkSpecification toLS() {
		LinkSpecification ls = new LinkSpecification();
		ls.setOperator(op);
		ls.setThreshold(0);
		List<LinkSpecification> children = new ArrayList<>();
		children.add(leftChild.toLS());
		children.add(rightChild.toLS());
		ls.setChildren(children);
		String parameterVal = "";
		if (!Double.isNaN(parameter)) {
			parameterVal = "_" + parameter;
			ls.setOperatorParameter(parameter);
		}
		ls.setFullExpression(opToExpMapping.get(op) + parameterVal + "(" + children.get(0).getFullExpression() + "|"
				+ children.get(0).getThreshold() + "," + children.get(1).getFullExpression() + "|"
				+ children.get(1).getThreshold() + ")");
		return ls;
	}

	@Override
	public LogicOperator getOperator() {
		return op;
	}

	@Override
	public void setOperator(LogicOperator op) {
		this.op = op;
	}

	public void setLeftChild(ANode leftChild) {
		leftChild.setParent(this);
		leftChild.isLeftChild = true;
		leftChild.depth = depth + 1;
		this.leftChild = leftChild;
	}

	public void setRightChild(ANode rightChild) {
		rightChild.setParent(this);
		rightChild.isLeftChild = false;
		rightChild.depth = depth + 1;
		this.rightChild = rightChild;
	}

	@Override
	public ANode replaceLeaf(LeafNode leafToReplace, LogicOperator op, LeafNode newNode, Double p) {
		List<LeafNode> leaves = clone().getLeaves();
		LeafNode replace = null;
		for (LeafNode l : leaves) {
			if (l.equals(leafToReplace)) {
				replace = l;
				break;
			}
		}
		newNode = (LeafNode) newNode.clone();
		InternalNode parent = replace.getParent();
		InternalNode operator = new InternalNode(op);
		if (replace.isLeftChild()) {
			parent.setLeftChild(operator);
		} else {
			parent.setRightChild(operator);
		}
		operator.setLeftChild(replace);
		operator.setRightChild(newNode);
		if (!Double.isNaN(p))
			operator.parameter = p;
		return operator.getRoot();
	}

	@Override
	public List<LeafNode> getLeaves() {
		List<LeafNode> leaves = new ArrayList<>();
		if (leftChild != null) {
			leaves.addAll(leftChild.getLeaves());
		}
		if (rightChild != null) {
			leaves.addAll(rightChild.getLeaves());
		}
		return leaves;
	}

}
