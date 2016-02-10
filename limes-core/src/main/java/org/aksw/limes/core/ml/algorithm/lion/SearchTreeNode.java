package org.aksw.limes.core.ml.algorithm.lion;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.ml.algorithm.lion.graphic.NodeHistory;


/**
 * 
 * A node in the search tree spanned by the refinement operator.
 * 
 * @author Jens Lehmann
 *
 */
public class SearchTreeNode implements Comparable{
	/*for an optimize threshold approach*/
	public boolean optimizeThreshold = true;
	public double lowThreshold = 0d;
	public double highThreshold = 1d;
	
	// Mapping it produces to avoid double computation
//	private Mapping mapping;
	
	// link spec at this node
	private LinkSpecification spec;
	
	// link to parent node
	private SearchTreeNode parent;
	
	// children
	private List<SearchTreeNode> children = new LinkedList<SearchTreeNode>();
	
	// the expansion of the node (see PDF)
	private int expansion;
	public boolean expand = true;
	
	
	// the score of the node (e.g. using some pseudo F-measure)
	private double score;
	
	// the score of the node (e.g. using Max achievable pseudo F-measure)
//	private double bestScore=0d;
	private boolean weakNode = false;
	
	// penaltiy multiplikator expansion
	private double expansionPenalty = 0.95d;
	
	public double maxAcchievablePFM = 0.0d;
	
	private double rewardForBetterThenParent = 1.0;
	
	public String specificName="";
	////////////////////added by Mofee and accessed directlyd//////////////////
	public int nodeId = -1; //works as node's id
	public int creationTime =0; //specifies the the time where the node is created
	public List<NodeHistory> history = new ArrayList<NodeHistory>(); //list for storing changes history
	/////////////////////////////////////////////////////////////////////////////
	
	public SearchTreeNode(SearchTreeNode parent, LinkSpecification spec, double score, double expansionPenalty) {
		super();
		this.spec = spec;
		this.parent = parent;
		this.score = score;
		this.expansionPenalty = expansionPenalty;
		// eypansion is parents exp.
		expansion = parent.expansion-1;
//		expansion = spec.size();
	}
	
	/**
	 * Consructor for root node
	 * @param spec
	 * @param score
	 */
	public SearchTreeNode(LinkSpecification spec, double score, double expansionPenalty) {
		super();
		this.spec = spec;
//		this.parent = parent;
		this.score = score;
		this.expansionPenalty = expansionPenalty;
		expansion = spec.size();
		//timestamp=System.currentTimeMillis()/1000;
	}
	// method that creates history node in this search tree node's history list
	public void createHistoryevent(int time)
	{
		NodeHistory nodehistory = new NodeHistory(this);
		nodehistory.historyTiming= time;
		history.add(nodehistory);
	}
	
	public void incExpansion() {
		expansion++;
	}
	
	public void addChild(SearchTreeNode node) {
		children.add(node);
	}
	
	public boolean isRoot() {
		return (parent == null);
	}	
	
	public LinkSpecification getSpec() {
		return spec;
	}

	public SearchTreeNode getParent() {
		return parent;
	}

	public List<SearchTreeNode> getChildren() {
		return children;
	}

	public int getExpansion() {
		return expansion;
	}

	public double getScore() {
		return score;
	}
	/**
	 * Apply penalty to score.
	 * @return
	 */
	public double getPenaltyScore() {
		double pen = Math.pow(expansionPenalty, Math.max(0, getExpansion()-1));
		return score*pen*rewardForBetterThenParent;
	}

	@Override
	public int compareTo(Object o) {
		SearchTreeNode other = (SearchTreeNode) o;
		if(this.expansion != other.expansion)
			return this.expansion-other.expansion;
		else {
//			if(this.score > other.score) 
//				return 1;
//			if(this.score < other.score)
//				return -1;			
			if(this.getChildren().size() == other.getChildren().size())
				return this.spec.compareTo(other.spec);
			else
				return getChildren().size()-other.getChildren().size();
		}
	}
	
	public String toString() {
		if(specificName.length()>0) 
			return specificName+" expansion="+expansion+", children.size="+this.children.size()+", score="+score;
		return "Exp="+expansion+" children.size="+this.children.size()+" score="+score+" LS:["+spec+"]";
	}
	
	/**
	 * Method to produce debug output of SearchTree
	 * @return
	 */
	public String outputSearchTree() {
		DecimalFormat df =  (DecimalFormat)DecimalFormat.getInstance(Locale.ENGLISH);

		df.applyPattern( "#,###,######0.00000" );
	
		String str =specificName;
		str+=" {score="+df.format(score)+",exp="+expansion+" }";
		str+=" ["+spec+"]";
		for(SearchTreeNode child : children) {
			str +="\n  "+child.outputSearchTree();
		}
		return str;
	}
	
	public String toTreeString() {
		return toTreeString(0).toString();
	}
	
	private StringBuilder toTreeString(int depth) {
		StringBuilder treeString = new StringBuilder();
		for(int i=0; i<depth-1; i++)
			treeString.append("  ");
		if(depth!=0)
			treeString.append("|--> ");
		
		DecimalFormat df =  (DecimalFormat)DecimalFormat.getInstance(Locale.ENGLISH);

		df.applyPattern( "#,###,######0.00000" );
	
		String str ="|-"+specificName;
		str+=" {score="+df.format(score)+",exp="+expansion+" }";
		str+=" ["+spec.toStringOneLine()+"]";
		
		treeString.append(str+"\n");
		for(SearchTreeNode child : children) {
			treeString.append(child.toTreeString(depth+1));
		}
		return treeString;
	}
	
	

	public void addSpecificName(String name) {this.specificName = name;}
	public String getSpecificName(){return this.specificName;}
	public double getBestScore() {
		return score;
	}

	public void setParent(SearchTreeNode parent){
		this.parent = parent;
	}
//	public void setBestScore(double bestScore) {
//		this.bestScore = bestScore;
//	}

	public boolean isWeakNode() {
		return weakNode;
	}

	public void setWeakNode(boolean weakNode) {
		this.weakNode = weakNode;
	}

	public void setReward(double reward) {
		this.rewardForBetterThenParent = reward;
	}
	
	
	public boolean equals(Object other) {
		SearchTreeNode o =(SearchTreeNode) other;
		return this.getSpec().equals(o.getSpec());
	}

	public void setExpansion(int i) {
		this.expansion = i;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
//	/**
//	 * @return the mapping
//	 */
//	public Mapping getMapping() {
//		return mapping;
//	}
//
//	/**
//	 * @param mapping the mapping to set
//	 */
//	public void setMapping(Mapping mapping) {
//		this.mapping = mapping;
//	}
}
