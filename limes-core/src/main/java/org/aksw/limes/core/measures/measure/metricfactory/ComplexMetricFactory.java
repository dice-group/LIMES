package org.aksw.limes.core.measures.measure.metricfactory;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

import org.aksw.limes.core.io.cache.Instance;
import org.apache.log4j.*;

/**
 * A more complex metric factory that allows for metrics to combine properties
 * to a similarity via operations such as linear combinations, MAX, AVG, ...
 * 
 * @author ngonga
 */
public class ComplexMetricFactory implements MetricFactory {

    String metricExpression;
    Logger logger;
    int counter;
    private Stacklist stacklist;
    private String output = "";
    private String var1 = "x";
    private String var2 = "y";

    public ComplexMetricFactory() {
	metricExpression = null;
	logger = Logger.getLogger("LIMES");
	counter = 0;
    }

    /**
     * Sets the variable used in expressions for the source
     *
     * @param s
     *            Name of the variable
     */
    public void setSourceVar(String s) {
	var1 = s;
    }

    /**
     * Sets the variable used in expressions for the target
     *
     * @param s
     *            Name of the variable
     */
    public void setTargerVar(String s) {
	var2 = s;
    }

    
    public void setExpression(String expression) {
	metricExpression = expression;
	expression = expression.replaceAll(".", "_");
	expression = expression.replaceAll(" ", "?");
	int stacklistSize = expression.length();
	stacklist = new Stacklist(stacklistSize);
	metricExpression = metricExpression.toLowerCase();
	logger.info("Transforming " + metricExpression);

	metricExpression = metricExpression.replaceAll("max", "xam");
	metricExpression = metricExpression.replaceAll("euclidean", "naedilcuE");
	metricExpression = metricExpression.replaceAll("blockdistance", "ecnatsidkcolb");
	metricExpression = metricExpression.replaceAll("avg", "gva");
	metricExpression = metricExpression.replaceAll("min", "nim");
	metricExpression = metricExpression.replaceAll("levenshtein", "niethsnevel");
	metricExpression = metricExpression.replaceAll("qgrams", "smargq");
	metricExpression = toPostfix(metricExpression);
	metricExpression = metricExpression.replaceAll("MAX", " MAX");

	String localExpression = metricExpression;
	String[] split = localExpression.split(" ");
	String reverse;
	for (int i = 0; i < split.length; i++) {
	    if (split[i].contains("_")) {
		reverse = new StringBuffer(split[i]).reverse().toString();
		logger.info(split[i] + " reversed to " + reverse);
		metricExpression = metricExpression.replaceAll(split[i], reverse);
	    }
	}

	logger.info("The reversed Input is " + metricExpression + '\n');
    }

    public float evaluateExpression(String s) {
	// System.out.println("Processing RPN |" + s + "|");
	StringTokenizer st = new StringTokenizer(s);
	@SuppressWarnings("rawtypes")
	Stack<Comparable> stack = new Stack<Comparable>();
	while (st.hasMoreTokens()) {
	    String token = st.nextToken();
	    try {
		stack.push(Float.parseFloat(token));
		stack.push(new Float(token));
		System.out.println("New Number Processed " + token);
	    } catch (NumberFormatException e) {
		double v1, v2;
		String s1, s2;
		float f1;
		String tmp = "";

		switch (token.charAt(0)) {

		case '*':
		    v2 = Double.valueOf(stack.pop().toString());
		    v1 = Double.valueOf(stack.pop().toString());
		    System.out.println("Multiplying " + v1 + " * " + v2);
		    System.out.println("Product Processed " + (v1 * v2));
		    stack.push(new Float(v1 * v2));
		    break;
		case '+':
		    v2 = Double.valueOf(stack.pop().toString());
		    v1 = Double.valueOf(stack.pop().toString());
		    System.out.println("Adding " + v1 + " + " + v2);
		    System.out.println("Sum Processed " + (v1 + v2));
		    stack.push(new Double(v1 + v2));
		    break;
		case '-':
		    v2 = Double.valueOf(stack.pop().toString());
		    v1 = Double.valueOf(stack.pop().toString());
		    System.out.println("Subtracting " + v1 + " - " + v2);
		    System.out.println("Difference Processed " + (v1 - v2));
		    stack.push(new Double(v1 - v2));
		    break;
		
		default:

		    tmp = token.toString();
		    if (tmp.toLowerCase().matches("euclidean")) {
			s1 = stack.pop().toString();
			s2 = stack.pop().toString();
			// v2 = ((Double)stack.pop()).intValue();
			// v1 = ((Double)stack.pop()).intValue();
			System.out.println("Euclidean of " + s1 + " and " + s2);
			v1 = new EuclideanDistance().getEuclidDistance(s1, s2);
			System.out.println("Euclidean Processed " + v1);
			stack.push(v1);
			tmp = "";
			break;
		    } else if (tmp.toLowerCase().matches("blockdistance")) {
			// v2 = ((Double)stack.pop()).doubleValue();
			// v1 = ((Double)stack.pop()).doubleValue();
			s2 = stack.pop().toString();
			s1 = stack.pop().toString();
			System.out.println("Blockdistance of " + s1 + " and " + s2);
			f1 = new BlockDistance().getSimilarity(s1, s2);
			System.out.println("Blockdistance Processed " + f1);
			stack.push(f1);
			tmp = "";
			break;
		    } else if (tmp.toLowerCase().matches("qgrams")) {
			// v2 = ((Double)stack.pop()).doubleValue();
			// v1 = ((Double)stack.pop()).doubleValue();
			s1 = stack.pop().toString();
			s2 = stack.pop().toString();
			System.out.println("Qgrams of " + s1 + " and " + s2);
			f1 = new QGramsDistance().getSimilarity(s1, s2);
			System.out.println("Qgrams Processed " + f1);
			stack.push(f1);
			tmp = "";
			break;
		    } else if (tmp.toLowerCase().matches("max")) {
			v2 = ((Double) stack.pop()).doubleValue();
			v1 = ((Double) stack.pop()).doubleValue();
			System.out.println("MAX of " + v1 + " and " + v2);
			stack.push((new Double(java.lang.Math.max(v1, v2))));
			tmp = "";
			break;
		    } else if (tmp.toLowerCase().matches("avg")) {
			v2 = ((Double) stack.pop()).doubleValue();
			v1 = ((Double) stack.pop()).doubleValue();
			// s1 = stack.pop().toString();
			// s2 = stack.pop().toString();
			System.out.println("AVG of " + v1 + " and " + v2);
			// f1 = new
			System.out.println("AVG Processed " + (v1 + v2));
			stack.push(new Double(v1 + v2) / 2);
			tmp = "";
			break;
		    } else if (tmp.toLowerCase().matches("min")) {
			v2 = ((Double) stack.pop()).doubleValue();
			v1 = ((Double) stack.pop()).doubleValue();
			System.out.println("MIN of " + v1 + " and " + v2);
			System.out.println("MIN Processed " + (v1 + v2));
			stack.push(new Double(java.lang.Math.min(v1, v2)));
			tmp = "";
			break;
		    } else if (tmp.toLowerCase().matches("levenshtein")) {
			// v2 = ((Double)stack.pop()).doubleValue();
			// v1 = ((Double)stack.pop()).doubleValue();
			s1 = stack.pop().toString();
			s2 = stack.pop().toString();
			System.out.println("Levenshtein of " + s1 + " and " + s2);
			f1 = new Levenshtein().getSimilarity(s1, s2);
			System.out.println("Levenshtein Processed " + f1);
			stack.push(f1);
			tmp = "";
			break;
		    } else {
			stack.push(tmp.toLowerCase().toString());
		    }
		    System.out.println("String " + tmp + " to be processed.");
		    break;

		}

	    }
	}
	return new Float((Double) stack.pop()).floatValue();
    }

    static List<Token> tokenize(String source, List<Rule> rules) {

	List<Token> tokens = new ArrayList<Token>();
	int pos = 0;
	final int end = source.length();
	Matcher m = Pattern.compile("dummy").matcher(source);
	m.useTransparentBounds(false).useAnchoringBounds(false);
	while (pos < end) {
	    m.region(pos, end);
	    for (Rule r : rules) {
		if (m.usePattern(r.pattern).lookingAt()) {
		    tokens.add(new Token(r.name, m.start(), m.end()));
		    pos = m.end();
		    break;
		}
	    }
	    pos++; // bump-along, in case no rule matched
	}
	return tokens;
    }

    public float getSimilarity(Instance a, Instance b) {
	String localExpression = metricExpression;
	String[] expression = localExpression.split(" ");
	String property;
	String value;
	float similarity = 0;
	try {
	    for (int i = 0; i < expression.length; i++) {
		if (expression[i].contains("_")) {
		    if (expression[i].startsWith(var1)) {
			property = expression[i].substring(expression[i].indexOf("_") + 1);
			value = a.getProperty(property).first();
			value = removeExtraCharacters(value);
			localExpression = localExpression.replaceAll(expression[i], value);
			logger.info("New local expression = " + localExpression);
		    } else if (expression[i].startsWith(var2)) {
			property = expression[i].substring(expression[i].indexOf("_") + 1);
			value = b.getProperty(property).first();
			value = removeExtraCharacters(value);
			localExpression = localExpression.replaceAll(expression[i], value);
			logger.info("New local expression = " + localExpression);
		    }
		}
	    }
	    logger.info("New expression is " + localExpression);
	    similarity = evaluateExpression(localExpression);
	} catch (Exception e) {
	    logger.warn(e.getMessage());
	}
	return similarity;
    }

    public String removeExtraCharacters(String value) {
	logger.info("Input = " + value);
	value = value.replaceAll(" ", "?");
	value = value.replaceAll("\\.", "_");
	value = value.replaceAll("\\*", "_");
	value = value.replaceAll("\\+", "_");
	value = value.replaceAll("-", "_");
	logger.info("Output = " + value);
	return value;
    }

    public String foldExpression(String expression, String var1, String var2) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String toPostfix(String input) {
	for (int j = 0; j < input.length(); j++) {
	    char ch = input.charAt(j);
	    switch (ch) {
	    case '+':
	    case '-':
		gotOperator(ch, 1);
		break; // (precedence 1)
	    case '*': // it's * or /
	    case '/':
		gotOperator(ch, 2); // go pop operators
		break; // (precedence 2)
	    case '(': // it's a left paren
		stacklist.push(ch); // push it
		break;
	    case ')': // it's a right paren
		gotParenthesis(ch); // go pop operators
		break;
	    case ',': // it's a function argument separator i.e a comma:
		gotSeparator(ch);
		break;
	    case '.': // it's a point e.g 4.6
		output = output + ch;
		break;
	    case ' ': //
		output = output + ch;
		break;
	    default: // must be an operand
		if (Character.isDigit(ch)) {
		    output = output + ch; // write it to output
		} else {
		    stacklist.push(ch);
		}
		break;
	    }
	}
	while (!stacklist.isEmpty()) {
	    output = output + stacklist.pop();
	}
	System.out.println(output);
	return output; // return postfix
    }

    public void gotOperator(char opThis, int prec1) {
	stacklist.push(' ');
	while (!stacklist.isEmpty()) {
	    char opTop = stacklist.pop();
	    if (opTop == '(') {
		stacklist.push(opTop);
		break;
	    } // it's an operator
	    else {// precedence of new op
		int prec2;
		if (opTop == '+' || opTop == '-') {

		    prec2 = 1;
		} else {
		    prec2 = 2;
		}
		if (prec2 < prec1) // if prec of new op less than prec of old
		{

		    stacklist.push(opTop); // save newly-popped op
		    // theStacklist.push(' ');
		    break;
		} else // prec of new not less
		{
		    output = output + opTop; // than prec of old
		}
	    }
	}
	stacklist.push(' ');
	stacklist.push(opThis);
	stacklist.push(' ');
    }

    public void gotParenthesis(char ch) {
	while (!stacklist.isEmpty()) {
	    char chx = stacklist.pop();
	    if (chx == '(') {
		break;
	    } else {
		output = output + chx;
	    }
	}
	output = output + " ";
    }

    public void gotSeparator(char ch) {
	output = output + " ";
	while (!stacklist.isEmpty()) {
	    char chx = stacklist.pop();
	    if (chx == '(') {
		stacklist.push(chx);
		break;
	    } else {
		output = output + chx;
	    }
	}
	output = output + " ";
    }

    private static class Rule {

	final String name = null;
	final Pattern pattern = null;
    }

    static class Token {

	final String name;
	final int startPos;
	final int endPos;

	Token(String name, int startPos, int endPos) {
	    this.name = name;
	    this.startPos = startPos;
	    this.endPos = endPos;
	}

	@Override
	public String toString() {
	    return String.format("Token [%2d, %2d, %s]", startPos, endPos, name);
	}
    }

    class Stacklist {

	private int maxSize;
	private char[] stacklistArray;
	private int top;

	public Stacklist(int max) {
	    maxSize = max;
	    stacklistArray = new char[maxSize];
	    top = -1;
	}

	public void push(char j) {
	    stacklistArray[++top] = j;
	}

	public char pop() {
	    return stacklistArray[top--];
	}

	public char peek() {
	    return stacklistArray[top];
	}

	public boolean isEmpty() {
	    return (top == -1);
	}
    }

}
