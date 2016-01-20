package org.aksw.limes.core.io.parser;

import org.apache.log4j.Logger;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version DEC 2, 2015
 */
public class Parser implements IParser {
	public static final Logger logger = Logger.getLogger(Parser.class.getName());
	
	public static final String MULT = "MULT";
	public static final String ADD = "ADD";
	public static final String MAX = "MAX";
	public static final String MIN = "MIN";

	private double threshold;
	private double threshold1;
    private double threshold2;
    protected double coef1;
    protected double coef2;
    protected String term1;
    protected String term2;
    protected String operator;
    protected String expression;
	

    public Parser(String input, double theta) {
        expression = input.replaceAll(" ", "");
        //expression = expression.toLowerCase();
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
        //tests for operation labels. If they can be found, then our expression
        //is not atomic
        String copy = expression.toLowerCase();
        if (!copy.contains("max(") && !copy.contains("min(")
                && !copy.contains("and(") && !copy.contains("or(")
                && !copy.contains("add(") && !copy.contains("xor(")
                && !copy.contains("minus(") && !copy.contains("mult(")
                && !copy.contains("diff(")) {
            return true;
        } else {
            return false;
        }
    }


    public String getTerm1() {
        return term1;
    }

    public String getOperator() {
        return operator;
    }

    public String getTerm2() {
        return term2;
    }

    public double getCoef1() {
        coef1 = 1.0;
        if (term1.contains("*")) {
            String split[] = term1.split("\\*");
            try {
                coef1 = Double.parseDouble(split[0]);
                term1 = split[1];
            } catch (Exception e) {
                logger.warn("Error parsing " + term1 + " for coefficient <" + coef1 + ">");
                coef1 = 1;
                //e.printStackTrace();
                //logger.warn(e.getStackTrace()[0].toString());
            }
        }
        return coef1;
    }

    public double getCoef2() {
        coef2 = 1;
        //  System.out.println("Parsing "+term2);
        if (term2.contains("*")) {
            String split[] = term2.split("\\*");
            try {
                coef2 = Double.parseDouble(split[0]);
                term2 = split[1];
            } catch (Exception e) {
                coef2 = 1.0;
                logger.warn("Error parsing " + term2 + " for coefficient");
            }
        }
        return coef2;
    }

    /**
     * Splits the expression into two terms
     *
     * @return
     */
    public void getTerms() {
        if (!isAtomic()) {
            int counter = 1;
            boolean found = false;
            operator = expression.substring(0, expression.indexOf("("));
            String noOpExpression = expression.substring(expression.indexOf("(") + 1, expression.lastIndexOf(")"));
            //get terms
            //       System.out.println("Expression stripped from operator = "+noOpExpression);
            for (int i = 0; i < noOpExpression.length(); i++) {

                if (noOpExpression.charAt(i) == '(') {
                    counter++;
                    found = true;
                } else if (noOpExpression.charAt(i) == ')') {
                    counter--;
                    found = true;

                } else if (counter == 1 && found && noOpExpression.charAt(i) == ',') {
                    term1 = noOpExpression.substring(0, i);
                    term2 = noOpExpression.substring(i + 1);
                }
            }

            logger.debug("Term 1 = "+term1);
            logger.debug("Term 2 = "+term2);
            getCoef1();
            getCoef2();
            //now compute thresholds based on operations
            //first numeric operations
            if (operator.equalsIgnoreCase(MIN) || operator.equalsIgnoreCase(MAX)) {
                setThreshold1(getThreshold());
                setThreshold2(getThreshold());
            } else if (operator.equalsIgnoreCase(ADD)) {
                operator = ADD;
                System.out.println("Coef1 = " + coef1 + ", Coef2 = " + coef2);
                setThreshold1((getThreshold() - coef2) / coef1);
                setThreshold2((getThreshold() - coef1) / coef2);
            } else if (operator.equalsIgnoreCase(MULT)) {
                operator = MULT;
                setThreshold1(getThreshold() / (coef2 * coef1));
                setThreshold2(getThreshold1());
            } //now set constraints. separator for sets and thresholds is |
            //thus one can write
            // AND(sim(a,b)|0.5,sim(b,d)|0.7)
            // and then set global threshold to the minimal value wanted
            else {
                int index = term1.lastIndexOf("|");
                String t;
                String set1 = term1.substring(0, index);
                //              System.out.println("Term1 filtered = "+set1);
                t = term1.substring(index + 1, term1.length());
                //              System.out.println("Term = "+set1+", filter = "+t);
                setThreshold1(Double.parseDouble(t));
                term1 = set1;

                index = term2.lastIndexOf("|");
                String set2 = term2.substring(0, index);
//                System.out.println("Term2 filtered = "+set2);
                t = term2.substring(index + 1, term2.length());
//                System.out.println("Term = "+set2+", filter = "+t);
                setThreshold2(Double.parseDouble(t));
                term2 = set2;
            }
        }//atomic
        else {
            operator = expression.substring(0, expression.indexOf("("));
            String noOpExpression = expression.substring(expression.indexOf("(") + 1, expression.lastIndexOf(")"));
            String split[] = noOpExpression.split(",");
            term1 = split[0];
            term2 = split[1];
        }
    }


	public double getThreshold2() {
		return threshold2;
	}

	
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
