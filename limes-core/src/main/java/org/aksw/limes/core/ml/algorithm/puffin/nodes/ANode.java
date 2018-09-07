package org.aksw.limes.core.ml.algorithm.puffin.nodes;

import java.util.List;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;

public abstract class ANode {

	protected LogicOperator op;
	protected String fuzzyTerm;
	protected ANode leftChild;
	protected ANode rightChild;
	protected AMapping set;
	protected InternalNode parent;
	protected double threshold;
	protected boolean isLeftChild;
	protected int depth;
	protected double quality;
	protected double parameter = Double.NaN;

	public abstract LinkSpecification toLS();

	public AMapping getSet() {
		return set;
	}

	public void setSet(AMapping set) {
		this.set = set;
	}

	public InternalNode getParent() {
		return parent;
	}

	public void setParent(InternalNode parent) {
		this.parent = parent;
	}

	public double getQuality() {
		return quality;
	}

	public void setQuality(double quality) {
		this.quality = quality;
	}

	public LogicOperator getOperator() {
		return op;
	}

	public void setOperator(LogicOperator op) {
		this.op = op;
	}

	public int getDepth() {
		return depth;
	}

	public LogicOperator getOp() {
		return op;
	}

	public void setOp(LogicOperator op) {
		this.op = op;
	}

	public String getFuzzyTerm() {
		return fuzzyTerm;
	}

	public void setFuzzyTerm(String fuzzyTerm) {
		this.fuzzyTerm = fuzzyTerm;
	}

	public ANode getLeftChild() {
		return leftChild;
	}

	public ANode getRightChild() {
		return rightChild;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public boolean isLeftChild() {
		return isLeftChild;
	}

	public double getParameter() {
		return parameter;
	}

	public void setParameter(double parameter) {
		this.parameter = parameter;
	}

	public void setDepth(int depth) {
		this.depth = depth;
		leftChild.setDepth(depth + 1);
		rightChild.setDepth(depth + 1);
	}

	public abstract List<LeafNode> getLeaves();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\n");
		sb.append(new String(new char[depth]).replace("\0", "\t"));
		if (op != null) {
			sb.append(op + "_" + parameter);
		}
		if (fuzzyTerm != null && !fuzzyTerm.equals("")) {
			sb.append(fuzzyTerm + "|" + threshold);
		}
		if (leftChild != null) {
			sb.append(leftChild.toString());
		}
		if (rightChild != null) {
			sb.append(rightChild.toString());
		}
		return sb.toString();
	}

	public abstract ANode replaceLeaf(LeafNode leafToReplace, LogicOperator op, LeafNode newNode, Double p);

	public ANode getRoot() {
		if (parent == null) {
			return this;
		}
		return parent.getRoot();
	}

	@Override
	public ANode clone() {
		ANode rootClone = getRoot().downwardClone(null);
		if (parent == null) {
			return rootClone;
		}
		List<LeafNode> thisLeaves = getLeaves();
		for (LeafNode l : rootClone.getLeaves()) {
			if (this instanceof LeafNode) {
				if (l.equals(this)) {
					return l;
				}
			} else {
				if (thisLeaves.contains(l)) {
					return traverseUpwardsUntilNode((InternalNode) this);
				}
			}
		}
		return null;
	}

	private ANode traverseUpwardsUntilNode(InternalNode findMe) {
		if (equals(findMe)) {
			return this;
		}
		return traverseUpwardsUntilNode(parent);
	}

	private ANode downwardClone(ANode parent) {
		ANode clone = null;
		if (this instanceof LeafNode) {
			clone = new LeafNode(fuzzyTerm, threshold);
		}
		if (this instanceof InternalNode) {
			clone = new InternalNode(op);
			clone.leftChild = leftChild.downwardClone(clone);
			clone.rightChild = rightChild.downwardClone(clone);
		}
		clone.parent = (InternalNode) parent;
		clone.isLeftChild = isLeftChild;
		clone.set = set;
		clone.depth = depth;
		clone.quality = quality;
		clone.parameter = parameter;
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + depth;
		result = prime * result + (fuzzyTerm == null ? 0 : fuzzyTerm.hashCode());
		result = prime * result + (isLeftChild ? 1231 : 1237);
		result = prime * result + (leftChild == null ? 0 : leftChild.hashCode());
		result = prime * result + (op == null ? 0 : op.hashCode());
		result = prime * result + (rightChild == null ? 0 : rightChild.hashCode());
		result = prime * result + (set == null ? 0 : set.hashCode());
		long temp;
		temp = Double.doubleToLongBits(threshold);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(quality);
		result = prime * result + (int) (temp ^ temp >>> 32);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ANode other = (ANode) obj;
		if (depth != other.depth) {
			return false;
		}
		if (fuzzyTerm == null) {
			if (other.fuzzyTerm != null) {
				return false;
			}
		} else if (!fuzzyTerm.equals(other.fuzzyTerm)) {
			return false;
		}
		if (isLeftChild != other.isLeftChild) {
			return false;
		}
		if (leftChild == null) {
			if (other.leftChild != null) {
				return false;
			}
		} else if (!leftChild.equals(other.leftChild)) {
			return false;
		}
		if (op != other.op) {
			return false;
		}
		if (rightChild == null) {
			if (other.rightChild != null) {
				return false;
			}
		} else if (!rightChild.equals(other.rightChild)) {
			return false;
		}
		if (set == null) {
			if (other.set != null) {
				return false;
			}
		} else if (!set.equals(other.set)) {
			return false;
		}
		if (Double.doubleToLongBits(threshold) != Double.doubleToLongBits(other.threshold)) {
			return false;
		}
		if (Double.doubleToLongBits(quality) != Double.doubleToLongBits(other.quality)) {
			return false;
		}
		return true;
	}

	public AMapping calculateSet() {
		if (this instanceof LeafNode) {
			return set;
		}
		return MappingOperations.performOperation(leftChild.calculateSet(), rightChild.calculateSet(), op, parameter);
	}

}
