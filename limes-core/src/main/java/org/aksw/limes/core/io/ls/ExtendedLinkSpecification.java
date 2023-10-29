/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.io.ls;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.io.parser.Parser;

import java.util.ArrayList;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
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
     * Reads a link specification expression into its canonical form Don't
     * forget to optimize the filters by checking (if threshold_left and
     * threshold_right grater than or equals to theta, then theta equals 0)
     *
     * @param spec
     *            Spec expression to read
     * @param theta
     *            Global threshold
     */
    public void readSpec(String spec, double theta) {
        spec = spec.trim();
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
                fullExpression = "MINUS(" + leftSpec.fullExpression + "|" + theta + "," + rightSpec.fullExpression + "|"
                        + theta + ")";
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
