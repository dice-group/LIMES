package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.util.ParenthesisMatcher;
import org.apache.log4j.Logger;

import weka.classifiers.trees.J48;

/**
 * class to parse a {@link J48} tree to a {@link LinkSpecification}
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class TreeParser {

	static Logger logger = Logger.getLogger(TreeParser.class);

	public static final String delimiter = "§";
	public static final String classPositive = "positive";
	public static final String classNegative = "negative";
	
	/**
	 * Since the most informative links are the ones near the boundary, where the instance pairs are being classified as links or not, we need to shift the
	 * threshold of the measure so we can also get the instance pairs that are almost classified as links
	 */
	public static final double delta = 0.1;
	/**
	 * The measures which are used in this LinkSpecification. The Double value is without delta
	 */
	public static HashMap<String, Double> measuresUsed = new HashMap<String, Double>();

	private DecisionTreeLearning dtl;
	
	/**
	 * Constructor
	 */
	public TreeParser(){
	    
	}

	/**
	 * Constructor
	 * @param dtl corresponding DecisionTreeLearning
	 */
	public TreeParser(DecisionTreeLearning dtl) {
	    this.dtl = dtl;
	}

	/**
	 * Takes a string output of J48 tree.prefix() and parses it into a
	 * LinkSpecification
	 * 
	 * @param tree
	 * @return
	 */
	public LinkSpecification parseTreePrefix(String tree) {
		LinkSpecification ls = null;
		if (treeIsAtomic(tree)) {
			ls = parseAtomicTree(tree);
		} else {
			int endOfRightChild = ParenthesisMatcher.findMatchingParenthesis(
					tree, tree.indexOf("["));
			String leftChild = tree.substring(tree.indexOf("["),
					endOfRightChild + 1);
			String rightChild = tree.substring(endOfRightChild + 1);
			LinkSpecification leftSpec = processSubtree(leftChild, rightChild,
					tree, true);
			LinkSpecification rightSpec = processSubtree(leftChild, rightChild,
					tree, false);

			// Since we have to add the LS of the root node to the left with the
			// <= threshold and to the right with the > threshold we replace the
			// child strings with these classes to get the LS with the right
			// threshold
			LinkSpecification rootForLeft = parseAtomicTree(tree.replaceFirst(
					Pattern.quote(leftChild), "[" + classPositive + "]").replace(rightChild,
					"[" + classNegative + "]"));
			LinkSpecification rootForRight = parseAtomicTree(tree.replace(
					rightChild, "[" + classPositive + "]").replaceFirst(Pattern.quote(leftChild),
					"[" + classNegative + "]"));
			if (leftSpec != null && rightSpec != null) {
				leftSpec = addRootLSToChild(rootForLeft, leftSpec);
				rightSpec = addRootLSToChild(rootForRight, rightSpec);
				ls = new LinkSpecification("OR(" + leftSpec.getFullExpression()
						+ "|" + leftSpec.getThreshold() + ","
						+ rightSpec.getFullExpression() + "|"
						+ rightSpec.getThreshold() + ")", 0.0);
			} else if (leftSpec != null) {
				ls = addRootLSToChild(rootForLeft, leftSpec);
			} else if (rightSpec != null) {
				ls = addRootLSToChild(rootForRight, rightSpec);
			} else {
				logger.error("This should not happen! (Sub-)Tree is not recognized as atomic");
				return null;
			}
		}

		return ls;
	}

	/**
	 * connect the root and child via AND operator
	 * @param root root LS
	 * @param child child LS
	 * @return connected LinkSpecification
	 */
	private LinkSpecification addRootLSToChild(LinkSpecification root,
			LinkSpecification child) {
	    //Does not make sense to connect the same LS with AND
	    //This happens if a node has a positive leaf and a subtree
		if(root.equals(child)){
		    return root;
		}
	    
		return new LinkSpecification("AND(" + root.getFullExpression() + "|"
				+ root.getThreshold() + "," + child.getFullExpression() + "|"
				+ child.getThreshold() + ")", 0.0);
	}

	/**
	 * Returns the LinkSpec for the subtree by calling parseTreePrefix if it is
	 * a [class (\d*\.\d*)] it returns null
	 * 
	 * @param child
	 *            , whole tree
	 * @return LinkSpecification or null
	 */
	private LinkSpecification processSubtree(String leftChild,
			String rightChild, String tree, boolean left) {
		if (left) {
			if (leftChild.startsWith("[" + classPositive)) {
				return parseAtomicTree(tree.replace(rightChild,
						"[" + classPositive + "]").replace(leftChild,
						"[" + classNegative + "]"));
			} else if (!(leftChild.startsWith("[" + classPositive) || leftChild
					.startsWith("[" + classNegative))) { // if it is a tree and not a leaf
				return parseTreePrefix(leftChild.substring(1,
						leftChild.length() - 1));
			}
		} else {
			if (rightChild.startsWith("[" + classPositive)) {
				return parseAtomicTree(tree.replace(rightChild,
						"[" + classPositive + "]").replace(leftChild,
						"[" + classNegative + "]"));
			} else if (!(rightChild.startsWith("[" + classPositive) || rightChild
					.startsWith("[" + classNegative))) { // if it is a tree and not a leaf
				return parseTreePrefix(rightChild.substring(1,
						rightChild.length() - 1));
			}
		}
		return null;
	}

	/**
	 * parses an atomic tree to a LinkSpcification
	 * @param tree to be parsed
	 * @return parsed LinkSpecification
	 */
	private LinkSpecification parseAtomicTree(String tree) {
	    	//TODO figure out if this is the correct solution
	    	if(tree.startsWith("positive ")){ 
	    	    return dtl.getDefaultLS();
	    	}
		LinkSpecification ls = null;
		if (tree.length() - tree.replace("<", "").length() > 1) {
			logger.info("atomic tree contains illegal symbol '<' in attribute name. Returning null)");
			return null;
		}
		String attributeName = tree.substring(0, tree.indexOf("<") - 2); // attributeName begins 2 characters before <
		String[] measureAndProperties = getMeasureAndProperties(attributeName);
		String measureName = measureAndProperties[0];
		String propertyA = measureAndProperties[1];
		String propertyB = measureAndProperties[2];
		String metricExpression = measureName + "(x." + propertyA + ", y."
				+ propertyB + ")";
		String threshold = getPositiveThreshold(tree.substring(
				tree.indexOf("<"), tree.length()));
		
		//add to measures hash map
		double thresholdDouble = Double.valueOf(threshold.replace(">", "").replace("<=", ""));
		if(measuresUsed.get(metricExpression)!= null){
		    if(measuresUsed.get(metricExpression) > thresholdDouble){
			measuresUsed.put(metricExpression, thresholdDouble);
		    }
		}else{
		    measuresUsed.put(metricExpression, thresholdDouble);
		}
		
		
		if (threshold.startsWith(">")) {
			ls = new LinkSpecification(metricExpression,
					Math.max(0, Double.parseDouble(threshold.substring(2)) - delta));
		} else {
			ls = createLessThanLinkSpec(metricExpression, threshold);
		}
		return ls;
	}

	/**
	 * Handles the negation of a measure by using the MINUS operator
	 * @param metricExpression metric
	 * @param threshold threshold
	 * @return resulting LinkSpecification
	 */
	private LinkSpecification createLessThanLinkSpec(String metricExpression,
			String threshold) {
		Double threshClean = Math.min(1, Double.parseDouble(threshold.substring(3)) + delta);
		if (threshClean.equals("0.0"))
			return new LinkSpecification(metricExpression,
					threshClean);

		return new LinkSpecification("MINUS(" + metricExpression + "|0.0,"
				+ metricExpression + "|" + threshClean + ")", 0.0);

	}

	/**
	 * gives back the threshold of an attribute leading to a positive leaf
	 * 
	 * @param prefixPart
	 * @return
	 */
	private String getPositiveThreshold(String prefixPart) {
		String lessThanThreshold = prefixPart.substring(
				prefixPart.indexOf("<"), prefixPart.indexOf(","));
		String moreThanThreshold = prefixPart.substring(
				prefixPart.indexOf(">"), prefixPart.indexOf("["));
		if (prefixPart.indexOf(classPositive) < prefixPart
				.indexOf(classNegative)) {
			return lessThanThreshold;
		}
		return moreThanThreshold;
	}

	/**
	 * takes the attributeName of an weka Instance and parses the name of the
	 * measure and the properties out of it, since it has the form
	 * "measureName#propertyA|propertyB"
	 * 
	 * @param attributeName
	 * @return res[] = {measureName, propertyA, propertyB}
	 */
	public String[] getMeasureAndProperties(String attributeName) {
		String[] res = new String[3];
		String[] attrArr = attributeName.split(delimiter);
		res[0] = attrArr[0];
		String[] properties = attrArr[1].split("\\|"); // escape because otherwise it is interpreted as OR
		res[1] = properties[0];
		res[2] = properties[1];
		return res;
	}

	/**
	 * if the tree contains more than one delimiter it is not atomic
	 * @param tree to be checked
	 * @return true if atomic false else
	 */
	private boolean treeIsAtomic(String tree) {
		if (tree.length() - tree.replace(delimiter, "").length() > 1) {
			return false;
		}
		return true;
	}

}
