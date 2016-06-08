package org.aksw.limes.core.io.ls;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.io.parser.Parser;

import java.util.ArrayList;

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

    // @Override

    /**
     * Reads a spec expression into its canonical form Don't forget to optimize
     * the filters by checking (if threshold_left and threshold_right >= theta,
     * then theta = 0)
     *
     * @param spec
     *         Spec expression to read
     * @param theta
     *         Global threshold
     */
    public void readSpec(String spec, double theta) {

        Parser p = new Parser(spec, theta);
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
                setOperator(LogicOperator.AND);
                leftSpec.readSpec(p.getLeftTerm(), p.getThreshold1());
                rightSpec.readSpec(p.getRightTerm(), p.getThreshold2());
                filterExpression = null;
                setThreshold(theta);
                fullExpression = "AND(" + leftSpec.fullExpression + "|" + p.getThreshold1() + ","
                        + rightSpec.fullExpression + "|" + p.getThreshold2() + ")";
            } else if (p.getOperator().equalsIgnoreCase(MIN)) {
                setOperator(LogicOperator.AND);
                leftSpec.readSpec(p.getLeftTerm(), theta);
                rightSpec.readSpec(p.getRightTerm(), theta);
                filterExpression = null;
                setThreshold(theta);
                fullExpression = "AND(" + leftSpec.fullExpression + "|" + p.getThreshold1() + ","
                        + rightSpec.fullExpression + "|" + p.getThreshold2() + ")";
            } else if (p.getOperator().equalsIgnoreCase(OR)) {
                setOperator(LogicOperator.OR);
                leftSpec.readSpec(p.getLeftTerm(), p.getThreshold1());
                rightSpec.readSpec(p.getRightTerm(), p.getThreshold2());
                filterExpression = null;
                setThreshold(theta);
                fullExpression = "OR(" + leftSpec.fullExpression + "|" + p.getThreshold1() + ","
                        + rightSpec.fullExpression + "|" + p.getThreshold2() + ")";
            } else if (p.getOperator().equalsIgnoreCase(MAX)) {
                setOperator(LogicOperator.OR);
                leftSpec.readSpec(p.getLeftTerm(), theta);
                rightSpec.readSpec(p.getRightTerm(), theta);
                filterExpression = null;
                setThreshold(theta);
                fullExpression = "OR(" + leftSpec.fullExpression + "|" + p.getThreshold1() + ","
                        + rightSpec.fullExpression + "|" + p.getThreshold2() + ")";
            } else if (p.getOperator().equalsIgnoreCase(XOR)) {
                setOperator(LogicOperator.MINUS);
                leftSpec.readSpec("OR(" + p.getLeftTerm() + "|" + p.getThreshold1() + "," + p.getRightTerm() + "|"
                        + p.getThreshold2() + ")", theta);
                rightSpec.readSpec("AND(" + p.getLeftTerm() + "|" + p.getThreshold1() + "," + p.getRightTerm() + "|"
                        + p.getThreshold2() + ")", theta);
                fullExpression = "MINUS(" + leftSpec.fullExpression + "|" + theta + ","
                        + rightSpec.fullExpression + "|" + theta + ")";
                filterExpression = null;
                setThreshold(theta);
                
            } else if (p.getOperator().equalsIgnoreCase(MINUS)) {
                setOperator(LogicOperator.MINUS);
                leftSpec.readSpec(p.getLeftTerm(), p.getThreshold1());
                rightSpec.readSpec(p.getRightTerm(), p.getThreshold2());
                filterExpression = null;
                setThreshold(theta);
                fullExpression = "MINUS(" + leftSpec.fullExpression + "|" + p.getThreshold1() + ","
                        + rightSpec.fullExpression + "|" + p.getThreshold2() + ")";

            } else if (p.getOperator().equalsIgnoreCase(ADD)) {
                setOperator(LogicOperator.AND);
                leftSpec.readSpec(p.getLeftTerm(), Math.abs(theta - p.getRightCoefficient()) / p.getLeftCoefficient());
                rightSpec.readSpec(p.getRightTerm(),
                        Math.abs(theta - p.getLeftCoefficient()) / p.getRightCoefficient());
                filterExpression = spec;
                setThreshold(theta);
                fullExpression = "AND(" + leftSpec.fullExpression + "|"
                        + (Math.abs(theta - p.getRightCoefficient()) / p.getLeftCoefficient()) + ","
                        + rightSpec.fullExpression + "|"
                        + (Math.abs(theta - p.getLeftCoefficient()) / p.getRightCoefficient()) + ")";

            }
        }
    }
}
