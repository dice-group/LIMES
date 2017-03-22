
package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.util.ParenthesisMatcher;
import org.apache.log4j.Logger;

import weka.classifiers.trees.J48;

/**
 * class to parse a {@link J48} tree to a {@link LinkSpecification}
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class TreeParser {

    static Logger logger = Logger.getLogger(TreeParser.class);

    public static final String delimiter = "§";
    public static final String classPositive = "positive";
    public static final String classNegative = "negative";
    private static final String irrelevant = "irrelevant";

    /**
     * The measures which are used in this LinkSpecification. The Double value
     * is without delta
     */
    public HashMap<String, Double> measuresUsed = new HashMap<String, Double>();

    private DecisionTreeLearning dtl;

    /**
     * Constructor
     */
    public TreeParser() {

    }

    /**
     * Constructor
     * 
     * @param dtl
     *            corresponding DecisionTreeLearning
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
	    ls = parseAtomicTree(tree, false);
	} else {
	    int endOfRightChild = ParenthesisMatcher.findMatchingParenthesis(tree, tree.indexOf("["));
	    String leftChild = tree.substring(tree.indexOf("["), endOfRightChild + 1);
	    String rightChild = tree.substring(endOfRightChild + 1);
	    LinkSpecification leftSpec = processSubtree(leftChild, rightChild, tree, true);
	    LinkSpecification rightSpec = processSubtree(leftChild, rightChild, tree, false);

	    // Since we have to add the LS of the root node to the left with the
	    // <= threshold and to the right with the > threshold we replace the
	    // child strings with these classes to get the LS with the right
	    // threshold
	    LinkSpecification rootForLeft = parseAtomicTree(
		    tree.replaceFirst(Pattern.quote(leftChild), "[" + classPositive + "]").replace(rightChild, "[" + classNegative + "]"), true);
	    LinkSpecification rootForRight = parseAtomicTree(
		    tree.replace(rightChild, "[" + classPositive + "]").replaceFirst(Pattern.quote(leftChild), "[" + classNegative + "]"), true);
	    if (leftSpec != null && rightSpec != null) {
		leftSpec = addRootLSToChild(rootForLeft, leftSpec);
		rightSpec = addRootLSToChild(rootForRight, rightSpec);
		ls = new LinkSpecification("OR(" + leftSpec.getFullExpression() + "|" + leftSpec.getThreshold() + "," + rightSpec.getFullExpression() + "|"
			+ rightSpec.getThreshold() + ")", 0.0);
	    } else if (leftSpec != null) {
		ls = addRootLSToChild(rootForLeft, leftSpec);
	    } else if (rightSpec != null) {
		ls = addRootLSToChild(rootForRight, rightSpec);
	    } else {
//		logger.error("This should not happen! (Sub-)Tree is not recognized as atomic");
		return null;
	    }
	}

	return ls;
    }

    /**
     * connect the root and child via AND operator
     * 
     * @param root
     *            root LS
     * @param child
     *            child LS
     * @return connected LinkSpecification
     */
    private LinkSpecification addRootLSToChild(LinkSpecification root, LinkSpecification child) {
	// Does not make sense to connect the same LS with AND
	// This happens if a node has a positive leaf and a subtree
	if (root.equals(child)) {
	    return root;
	}

	return new LinkSpecification("AND(" + root.getFullExpression() + "|" + root.getThreshold() + "," + child.getFullExpression() + "|"
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
    private LinkSpecification processSubtree(String leftChild, String rightChild, String tree, boolean left) {
	boolean irrelevant = false;
//	    //Test if there is a threshold <= 0  in which case this is an irrelevant subtree/leaf
	    String test = tree.substring(tree.indexOf("<"), tree.indexOf("[")).replace("<", "").replace("=", "").replace(">","").replace("\n", "").replace(" ", "").trim();
	    if(test.equals("0,0")){
		irrelevant = true;
	    }
	if (left) {
	    if(!irrelevant){
	    if (leftChild.startsWith("[" + classPositive)) {
		return parseAtomicTree(tree.replace(rightChild, "[" + classPositive + "]").replace(leftChild, "[" + classNegative + "]"), true);
	    } else if (!(leftChild.startsWith("[" + classPositive) || leftChild.startsWith("[" + classNegative))) { // if
														    // it
														    // is
														    // a
														    // tree
														    // and
														    // not
														    // a
														    // leaf
		return parseTreePrefix(leftChild.substring(1, leftChild.length() - 1));
	    }
	    }
	} else {
	    if (rightChild.startsWith("[" + classPositive)) {
		return parseAtomicTree(tree.replace(rightChild, "[" + classPositive + "]").replace(leftChild, "[" + classNegative + "]"), true);
	    } else if (!(rightChild.startsWith("[" + classPositive) || rightChild.startsWith("[" + classNegative))) { // if
														      // it
														      // is
														      // a
														      // tree
														      // and
														      // not
														      // a
														      // leaf
		return parseTreePrefix(rightChild.substring(1, rightChild.length() - 1));
	    }
	}
	return null;
    }

    /**
     * parses an atomic tree to a LinkSpcification
     * 
     * @param tree
     *            to be parsed
     * @return parsed LinkSpecification
     */
    private LinkSpecification parseAtomicTree(String tree, boolean root) {
	if (tree.startsWith("positive")) {
	    return (dtl.getMlresult() != null) ? dtl.getMlresult().getLinkSpecification() : dtl.getDefaultLS();
	} else if (tree.startsWith("negative")) {
	    LinkSpecification resLS = (dtl.getMlresult() != null) ? dtl.getMlresult().getLinkSpecification() : dtl.getDefaultLS();
	    return createLessThanLinkSpec(resLS.getFullExpression(), String.valueOf(">= " + resLS.getThreshold()));
	} else if (!root && getPositiveThreshold(tree.substring(tree.indexOf("<"), tree.length())).equals(irrelevant)) {
	    return null;
	}
	LinkSpecification ls = null;
	if (tree.length() - tree.replace("<", "").length() > 1) {
	    logger.info("atomic tree contains illegal symbol '<' in attribute name. Returning null)");
	    return null;
	}
	String attributeName = tree.substring(0, tree.indexOf("<") - 2); // attributeName
									 // begins
									 // 2
									 // characters
									 // before
									 // <
	String[] measureAndProperties = getMeasureAndProperties(attributeName);
	String measureName = measureAndProperties[0];
	String propertyA = measureAndProperties[1];
	String propertyB = measureAndProperties[2];
	String metricExpression = measureName + "(x." + propertyA + ", y." + propertyB + ")";
	String threshold = getPositiveThreshold(tree.substring(tree.indexOf("<"), tree.length()));

	// add to measures hash map
	double thresholdDouble = Double.valueOf(threshold.replace(">", "").replace("<=", ""));
	if (measuresUsed.get(metricExpression) != null) {
	    if (measuresUsed.get(metricExpression) > thresholdDouble) {
		measuresUsed.put(metricExpression, thresholdDouble);
	    }
	} else {
	    measuresUsed.put(metricExpression, thresholdDouble);
	}

	if (threshold.startsWith(">")) {
	    ls = new LinkSpecification(metricExpression, Math.max(0.1, Double.parseDouble(threshold.substring(2))));
	} else {
	    ls = createLessThanLinkSpec(metricExpression, threshold);
	}
	return ls;
    }

    /**
     * Handles the negation of a measure by using the MINUS operator
     * 
     * @param metricExpression
     *            metric
     * @param threshold
     *            threshold
     * @return resulting LinkSpecification
     */
    private LinkSpecification createLessThanLinkSpec(String metricExpression, String threshold) {
	Double threshClean = Double.parseDouble(threshold.substring(3));
	if (threshClean.equals(0.0))
	    return null;

	return new LinkSpecification("MINUS(" + metricExpression + "|0.01," + metricExpression + "|" + threshClean + ")", 0.0);

    }

    /**
     * gives back the threshold of an attribute leading to a positive leaf
     * 
     * @param prefixPart
     * @return
     */
    private String getPositiveThreshold(String prefixPart) {
	String lessThanThreshold = prefixPart.substring(prefixPart.indexOf("<"), prefixPart.indexOf(","));
	String moreThanThreshold = prefixPart.substring(prefixPart.indexOf(">"), prefixPart.indexOf("["));
	double lessDouble = Double.valueOf(lessThanThreshold.replace(">", "").replace("<", "").replace("=", ""));
	double moreDouble = Double.valueOf(moreThanThreshold.replace(">", "").replace("<", "").replace("=", ""));
	if (prefixPart.substring(prefixPart.indexOf("[")).startsWith("[positive (")) {
	    if (lessDouble == 0.0 && moreDouble == 0.0) {
		return irrelevant;
	    }
	}
	if (prefixPart.indexOf(classPositive) < prefixPart.indexOf(classNegative)) {
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
	String[] properties = attrArr[1].split("\\|"); // escape because
						       // otherwise it is
						       // interpreted as OR
	res[1] = properties[0];
	res[2] = properties[1];
	return res;
    }

    /**
     * if the tree contains more than one delimiter it is not atomic
     * 
     * @param tree
     *            to be checked
     * @return true if atomic false else
     */
    private boolean treeIsAtomic(String tree) {
	if (tree.length() - tree.replace(delimiter, "").length() > 1) {
	    return false;
	}
	return true;
    }
    
    public static void main(String[] args){
	TreeParser tp = new TreeParser();
//	tp.parseTreePrefix("[qgrams§http://www.okkam.org/ontology_person1.owl#has_address|http://www.okkam.org/ontology_person2.owl#has_address: <= 0.826087,\n> 0.826087[cosine§http://www.okkam.org/ontology_person1.owl#age|http://www.okkam.org/ontology_person2.owl#age: <= 0,\n> 0[negative (15.0)][negative (5.0/2.0)]][positive (11.0)]]");
	System.out.println(tp.parseTreePrefix("qgrams§http://www.okkam.org/ontology_person1.owl#date_of_birth|http://www.okkam.org/ontology_person2.owl#date_of_birth: <= 0.2, > 0.2[negative (44.0)][jaro§http://www.okkam.org/ontology_person1.owl#age|http://www.okkam.org/ontology_person2.owl#age: <= 0, > 0[cosine§http://www.okkam.org/ontology_person1.owl#date_of_birth|http://www.okkam.org/ontology_person2.owl#date_of_birth: <= 0, > 0[qgrams§http://www.okkam.org/ontology_person1.owl#has_address|http://www.okkam.org/ontology_person2.owl#has_address: <= 0.826087, > 0.826087[negative (7.0/1.0)][positive (4.0)]][negative (29.0/8.0)]][qgrams§http://www.okkam.org/ontology_person1.owl#phone_numer|http://www.okkam.org/ontology_person2.owl#phone_numer: <= 0.5, > 0.5[qgrams§http://www.okkam.org/ontology_person1.owl#has_address|http://www.okkam.org/ontology_person2.owl#has_address: <= 0.826087, > 0.826087[negative (6.0/1.0)][positive (3.0/1.0)]][cosine§http://www.okkam.org/ontology_person1.owl#given_name|http://www.okkam.org/ontology_person2.owl#given_name: <= 0, > 0[qgrams§http://www.okkam.org/ontology_person1.owl#has_address|http://www.okkam.org/ontology_person2.owl#has_address: <= 0.826087, > 0.826087[positive (6.0/2.0)][jaro§http://www.okkam.org/ontology_person1.owl#has_address|http://www.okkam.org/ontology_person2.owl#has_address: <= 0.954882, > 0.954882[jaro§http://www.okkam.org/ontology_person1.owl#given_name|http://www.okkam.org/ontology_person2.owl#given_name: <= 0.916667, > 0.916667[negative (8.0/2.0)][positive (2.0)]][negative (3.0/1.0)]]][positive (116.0/51.0)]]]]")); 
	}


}
