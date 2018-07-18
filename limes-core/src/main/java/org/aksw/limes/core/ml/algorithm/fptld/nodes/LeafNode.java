package org.aksw.limes.core.ml.algorithm.fptld.nodes;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;

public class LeafNode extends ANode {

	public LeafNode(String fuzzyTerm, double threshold) {
		this.fuzzyTerm = fuzzyTerm;
		this.threshold = threshold;
	}

	public LeafNode(String fuzzyTerm, double threshold, AMapping set, double quality) {
		this.fuzzyTerm = fuzzyTerm;
		this.threshold = threshold;
		this.set = set;
		this.quality = quality;
	}

	@Override
	public LinkSpecification toLS() {
		return new LinkSpecification(fuzzyTerm, threshold);
	}

	@Override
	public String getFuzzyTerm() {
		return fuzzyTerm;
	}

	@Override
	public void setFuzzyTerm(String fuzzyTerm) {
		this.fuzzyTerm = fuzzyTerm;
	}

	@Override
	public List<LeafNode> getLeaves() {
		List<LeafNode> leaves = new ArrayList<>();
		leaves.add(this);
		return leaves;
	}

	@Override
	public ANode replaceLeaf(LeafNode leafToReplace, LogicOperator op, LeafNode newNode, Double p) {
		InternalNode internal = new InternalNode(op);
		if (!Double.isNaN(p)) {
			internal.parameter = p;
		}
		internal.setLeftChild(leafToReplace.clone());
		internal.setRightChild(newNode.clone());
		return internal;
	}
}
