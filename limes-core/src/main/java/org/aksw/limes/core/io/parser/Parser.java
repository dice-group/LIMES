package org.aksw.limes.core.io.parser;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.exceptions.UnsupportedOperator;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse Link Specifications
 * 
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Aug 23, 2016
 */
public class Parser implements IParser {
    public static final Logger logger = LoggerFactory.getLogger(Parser.class.getName());

    public static final String MULT = "MULT";
    public static final String ADD = "ADD";
    public static final String MAX = "MAX";
    public static final String MIN = "MIN";
    protected double leftCoefficient = -1.0;
    protected double rightCoefficient = -1.0;
    protected String leftTerm;
    protected String rightTerm;
    protected String operator;
    protected String expression;
    private double threshold;
    private double threshold1;
    private double threshold2;

    public Parser(String input, double theta) {
        expression = input.replaceAll(" ", "");
        // expression = expression.toLowerCase();
        setThreshold(theta);
        getTerms();
    }

    /**
     * Tests whether an expression is atomic or not
     *
     * @return True if atomic, else false
     */
    @Override
    public boolean isAtomic() {
        // tests for operation labels. If they can be found, then our expression
        // is not atomic
        String copy = expression.toLowerCase();
        try {
            if (MeasureFactory.getMeasureType(copy) != null)
                return true;
        } catch (InvalidMeasureException e) {
            if (copy.startsWith("max(") || copy.startsWith("min(") || copy.startsWith("and(") || copy.startsWith("or(")
                    || copy.startsWith("add(") || copy.startsWith("xor(") || copy.startsWith("minus(")
                    || copy.startsWith("mult(") || copy.startsWith("diff(")) {
                return false;
            } else {
                int index = copy.indexOf("(");
                String wrongOperator = copy.substring(0, index);
                if (StringUtils.countMatches(copy, ",") == 1) {
                    throw new InvalidMeasureException(wrongOperator);
                } else
                    throw new UnsupportedOperator(wrongOperator);
            }
        }
        return false;
    }

    public String getLeftTerm() {
        return leftTerm;
    }

    public String getOperator() {
        return operator;
    }

    public String getRightTerm() {
        return rightTerm;
    }

    public double getLeftCoefficient() {
    	if(leftCoefficient != -1.0){
    		return leftCoefficient;
    	}
        leftCoefficient = 1.0;
        logger.debug("Parsing " + getRightTerm());
        if (leftTerm.contains("*")) {
            String split[] = leftTerm.split("\\*");
            try {
                leftCoefficient = Double.parseDouble(split[0]);
                leftTerm = Arrays.asList(split).stream().skip(1).collect(Collectors.joining("*"));
            } catch (Exception e) {
                logger.warn("Error parsing " + leftTerm + " for coefficient <" + leftCoefficient + ">");
                leftCoefficient = 1;
                // e.printStackTrace();
                // logger.warn(e.getStackTrace()[0].toString());
            }
        }
        return leftCoefficient;
    }

    /**
     * @return right coefficient
     */
    public double getRightCoefficient() {
    	if(rightCoefficient != -1.0){
    		return rightCoefficient;
    	}
        rightCoefficient = 1;
        logger.debug("Parsing " + getRightTerm());
        if (rightTerm.contains("*")) {
            String split[] = rightTerm.split("\\*");
            try {
                rightCoefficient = Double.parseDouble(split[0]);
                rightTerm = Arrays.asList(split).stream().skip(1).collect(Collectors.joining("*"));
            } catch (Exception e) {
                rightCoefficient = 1.0;
                logger.warn("Error parsing " + rightTerm + " for coefficient");
            }
        }
        return rightCoefficient;
    }

    /**
     * Splits the expression into two terms
     *
     */
    public void getTerms() {
        if (!isAtomic()) {
            int counter = 1;
            boolean found = false;
            operator = expression.substring(0, expression.indexOf("("));
            String noOpExpression = expression.substring(expression.indexOf("(") + 1, expression.lastIndexOf(")"));
            // get terms
            logger.debug("Expression stripped from operator = " + noOpExpression);
            for (int i = 0; i < noOpExpression.length(); i++) {
                if (noOpExpression.charAt(i) == '(') {
                    counter++;
                    found = true;
                } else if (noOpExpression.charAt(i) == ')') {
                    counter--;
                    found = true;
                } else if (counter == 1 && found && noOpExpression.charAt(i) == ',') {
                    leftTerm = noOpExpression.substring(0, i);
                    rightTerm = noOpExpression.substring(i + 1);
                }
            }

            leftCoefficient = -1.0;
            rightCoefficient = -1.0;
            getLeftCoefficient();
            getRightCoefficient();
            // now compute thresholds based on operations
            // first numeric operations
            if (operator.equalsIgnoreCase(MIN) || operator.equalsIgnoreCase(MAX)) {
                setThreshold1(getThreshold());
                setThreshold2(getThreshold());
            } else if (operator.equalsIgnoreCase(ADD)) {
                operator = ADD;
                setThreshold1(Math.abs(getThreshold() - rightCoefficient) / leftCoefficient);
                setThreshold2(Math.abs(getThreshold() - leftCoefficient) / rightCoefficient);
            } else if (operator.equalsIgnoreCase(MULT)) {
                operator = MULT;
                setThreshold1(getThreshold() / (rightCoefficient * leftCoefficient));
                setThreshold2(getThreshold1());
            } // now set constraints. separator for sets and thresholds is |
              // thus one can write
              // AND(sim(a,b)|0.5,sim(b,d)|0.7)
              // and then set global threshold to the minimal value wanted
            else {
                int index = leftTerm.lastIndexOf("|");
                String t;
                String set1 = leftTerm.substring(0, index);
                logger.debug("LeftTerm filtered = " + set1);
                t = leftTerm.substring(index + 1, leftTerm.length());
                logger.debug("Term = " + set1 + ", filter = " + t);
                setThreshold1(Double.parseDouble(t));
                leftTerm = set1;
                index = rightTerm.lastIndexOf("|");
                String set2 = rightTerm.substring(0, index);
                logger.debug("RightTerm filtered = " + set2);
                t = rightTerm.substring(index + 1, rightTerm.length());
                logger.debug("Term = " + set2 + ", filter = " + t);
                setThreshold2(Double.parseDouble(t));
                rightTerm = set2;
            }
        } // atomic
        else {
            operator = expression.substring(0, expression.indexOf("("));
            String noOpExpression = expression.substring(expression.indexOf("(") + 1, expression.lastIndexOf(")"));
            String split[] = noOpExpression.split(",");
            leftTerm = split[0];
            rightTerm = split[1];
        }
    }

    /**
     * @return threshold2
     */
    public double getThreshold2() {
        return threshold2;
    }

    /**
     * @param threshold2
     */
    public void setThreshold2(double threshold2) {
        this.threshold2 = threshold2;
    }

    public double getThreshold1() {
        return threshold1;
    }

    public void setThreshold1(double threshold1) {
        this.threshold1 = threshold1;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

}
