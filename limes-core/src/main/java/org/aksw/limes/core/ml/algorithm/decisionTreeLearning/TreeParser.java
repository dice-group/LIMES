package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.util.regex.Pattern;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.util.ParenthesisMatcher;
import org.apache.log4j.Logger;

public class TreeParser {

	static Logger logger = Logger.getLogger(TreeParser.class);

	public static final String delimiter = "§";
	public static final String classPositive = "positive";
	public static final String classNegative = "negative";

	private DecisionTreeLearning dtl;
	
	public TreeParser(){
	    
	}

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
		if (threshold.startsWith(">")) {
			ls = new LinkSpecification(metricExpression,
					Double.parseDouble(threshold.substring(2)));
		} else {
			ls = createLessThanLinkSpec(metricExpression, threshold);
		}
		return ls;
	}

	private LinkSpecification createLessThanLinkSpec(String metricExpression,
			String threshold) {
		String threshClean = threshold.substring(3);
		if (threshClean.equals("0.0"))
			return new LinkSpecification(metricExpression,
					Double.parseDouble(threshClean));

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

	private boolean treeIsAtomic(String tree) {
		// if the tree contains more than one delimiter it is not atomic
		if (tree.length() - tree.replace(delimiter, "").length() > 1) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		TreeParser tp = new TreeParser();
		LinkSpecification ls;
		// ls =
		// tp.parseAtomicTree("jaccard#title|name: <= 0.857143,\n > 0.857143[positive (1528.0/8.0)][negative (108.0)]");
		// ls =
		// tp.parseTreePrefix("trigrams#title|name: <= 0.888889,\n > 0.888889[cosine#manufacturer|manufacturer: <= 0.5, \n > 0.5[positive (3.0/1.0)][negative (3.0)]][jaccard#title|name: <= 0.606977, \n > 0.606977[negative (3.0/1.0)][positive (2.0)]]");
		// System.out.println(tp.getPositiveThreshold("<= 0.888889,\n > 0.888889[negative (1468.0/8.0)][positive (168.0)]"));
		// System.out.println(tp.parseTreePrefix("[jaccard#title|name: <= 0.857143,\n > 0.857143[positive (1528.0/8.0)][negative (108.0)]]").toString());
		ls = tp.parseTreePrefix("trigrams#title|name: <= 0.888889, \n > 0.888889[jaccard#description|description: <= 0.487179, \n > 0.487179[exactmatch#manufacturer|manufacturer: <= 0, \n > 0[jaccard#title|name: <= 0.230769, \n > 0.230769[jaro#manufacturer|manufacturer: <= 0, \n > 0[negative (26.0)][cosine#manufacturer|manufacturer: <= 0.5, \n > 0.5[positive (3.0/1.0)][negative (3.0)]]][negative (1369.0/1.0)]][negative (42.0/1.0)]][cosine#manufacturer|manufacturer: <= 0.288675, \n > 0.288675[negative (20.0/1.0)][cosine#title|name: <= 0.606977, \n > 0.606977[negative (3.0/1.0)][positive (2.0)]]]][positive (168.0)]");
		// System.out.println("trigrams#title|name: <= 0.888889, \n > 0.888889[jaccard#description|description: <= 0.487179, \n > 0.487179[exactmatch#manufacturer|manufacturer: <= 0, \n > 0[jaccard#title|name: <= 0.230769, \n > 0.230769[jaro#manufacturer|manufacturer: <= 0, \n > 0[negative (26.0)][cosine#manufacturer|manufacturer: <= 0.5, \n > 0.5[positive (3.0/1.0)][negative (3.0)]]][negative (1369.0/1.0)]][negative (42.0/1.0)]][cosine#manufacturer|manufacturer: <= 0.288675, \n > 0.288675[negative (20.0/1.0)][cosine#title|name: <= 0.606977, \n > 0.606977[negative (3.0/1.0)][positive (2.0)]]]][positive (168.0)]".substring(46,
		// 560));
		// LinkSpecification ls2 = new
		// LinkSpecification("AND(trigrams(x.name, y.name)|0.5,jaccard(x.title,y.title)|0.4)",
		// 0.7);
		// LinkSpecification ls1 = new
		// LinkSpecification("cosine(x.name, y.name)", 0.7);
		// LinkSpecification ls3 = new LinkSpecification("AND(" +
		// ls1.getFullExpression() + "|" + ls1.getThreshold() + "," +
		// ls2.getFullExpression() + "|" + ls2.getThreshold() + ")", 0.0);
		// System.out.println(ls3.toStringOneLine());
		System.out.println(ls.toStringOneLine());
	}

}
