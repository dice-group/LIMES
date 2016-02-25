package org.aksw.limes.core.io.ls;

import java.util.ArrayList;

import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.MappingOperations.Operator;

public class ExtendedLinkSpecification extends LinkSpecification {
    
    public ExtendedLinkSpecification() {
	super();
    }

    public ExtendedLinkSpecification(String measure, double threshold) {
	setOperator(null);
	setChildren(null);
	parent = null;
	setDependencies(null);
	this.readSpec(measure, threshold);
    }

    //@Override
    /**
     * Reads a spec expression into its canonical form Don't forget to optimize
     * the filters by checking (if threshold_left and threshold_right >= theta,
     * then theta = 0)
     *
     * @param spec
     *            Spec expression to read
     * @param theta
     *            Global threshold
     */
    public void readSpec(String spec, double theta) {

	Parser p = new Parser(spec, getThreshold());
	if (p.isAtomic()) {
	    filterExpression = spec;
	    setThreshold(theta);
	    fullExpression = spec;

	} else {
	    ExtendedLinkSpecification leftSpec = new ExtendedLinkSpecification();
	    ExtendedLinkSpecification rightSpec = new ExtendedLinkSpecification();
	    leftSpec.parent = this;
	    rightSpec.parent = this;
	    setChildren(new ArrayList<LinkSpecification>());
	    getChildren().add(leftSpec);
	    getChildren().add(rightSpec);

	    if (p.getOperator().equalsIgnoreCase(AND)) {
		setOperator(Operator.AND);
		leftSpec.readSpec(p.getTerm1(), p.getThreshold1());
		rightSpec.readSpec(p.getTerm2(), p.getThreshold2());
		filterExpression = null;
		setThreshold(theta);
		fullExpression = "AND(" + leftSpec.fullExpression + "|" + p.getThreshold1() + ","
			+ rightSpec.fullExpression + "|" + p.getThreshold2() + ")";
	    } else if (p.getOperator().equalsIgnoreCase(MIN)) {
		setOperator(Operator.AND);
		leftSpec.readSpec(p.getTerm1(), theta);
		rightSpec.readSpec(p.getTerm2(), theta);
		filterExpression = null;
		setThreshold(theta);
		fullExpression = "MIN(" + leftSpec.fullExpression + "|" + p.getThreshold1() + ","
			+ rightSpec.fullExpression + "|" + p.getThreshold2() + ")";
	    } else if (p.getOperator().equalsIgnoreCase(OR)) {
		setOperator(Operator.OR);
		leftSpec.readSpec(p.getTerm1(), p.getThreshold1());
		rightSpec.readSpec(p.getTerm2(), p.getThreshold2());
		filterExpression = null;
		setThreshold(theta);
		fullExpression = "OR(" + leftSpec.fullExpression + "|" + p.getThreshold1() + ","
			+ rightSpec.fullExpression + "|" + p.getThreshold2() + ")";
	    } else if (p.getOperator().equalsIgnoreCase(MAX)) {
		setOperator(Operator.OR);
		leftSpec.readSpec(p.getTerm1(), theta);
		rightSpec.readSpec(p.getTerm2(), theta);
		filterExpression = null;
		setThreshold(theta);
		fullExpression = "MAX(" + leftSpec.fullExpression + "|" + p.getThreshold1() + ","
			+ rightSpec.fullExpression + "|" + p.getThreshold2() + ")";
	    } else if (p.getOperator().equalsIgnoreCase(XOR)) {
		setOperator(Operator.MINUS);
		leftSpec.readSpec("OR(" + p.getTerm1() + "|" + theta + "," + p.getTerm2() + "|"
			+ theta + ")", theta);
		rightSpec.readSpec("AND(" + p.getTerm1() + "|" + theta + "," + p.getTerm2() + "|"
			+ theta + ")", theta);
		filterExpression = null;
		setThreshold(theta);
		fullExpression = "MINUS(" + leftSpec.fullExpression + "|" + theta + ","
			+ rightSpec.fullExpression + "|" + theta + ")";
	    } else if (p.getOperator().equalsIgnoreCase(MINUS)) {
		setOperator(Operator.MINUS);
		leftSpec.readSpec(p.getTerm1(), p.getThreshold1());
		rightSpec.readSpec(p.getTerm2(), p.getThreshold2());
		filterExpression = null;
		setThreshold(theta);
		fullExpression = "MINUS(" + leftSpec.fullExpression + "|" + p.getThreshold1() + ","
			+ rightSpec.fullExpression + "|" + p.getThreshold2() + ")";

	    } else if (p.getOperator().equalsIgnoreCase(ADD)) {
		setOperator(Operator.AND);
		leftSpec.readSpec(p.getTerm1(), (theta - p.getCoef2()) / p.getCoef1());
		rightSpec.readSpec(p.getTerm2(), (theta - p.getCoef1()) / p.getCoef2());
		filterExpression = spec;
		setThreshold(theta);
		fullExpression = "ADD(" + leftSpec.fullExpression + "|" + ((theta - p.getCoef2()) / p.getCoef1()) + ","
			+ rightSpec.fullExpression + "|" + ((theta - p.getCoef1()) / p.getCoef2()) + ")";

	    }
	}
    }
}
