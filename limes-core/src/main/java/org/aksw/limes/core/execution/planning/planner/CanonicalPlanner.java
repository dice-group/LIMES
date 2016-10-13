package org.aksw.limes.core.execution.planning.planner;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.io.ls.LinkSpecification;

/**
 * Implements the Canonical planner class. It receives a link specification as
 * input and generates an immutable NestedPlan.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class CanonicalPlanner extends Planner {
    /**
     * Constructor of the CanonicalPlanner class.
     */
    public CanonicalPlanner() {
    }

    /**
     * Generates a NestedPlan for a link specification. If the link
     * specification is null or empty it returns an empty plan. If the link
     * specification is atomic, the planner generates a simple plan that
     * consists of one RUN instruction. If the link specification is complex,
     * firstly it generates a plan for each of the children specifications and
     * sets them as subPlans. Then, it assigns to the plan the corresponding
     * command based on the link specification operator and finally creates a
     * filtering instruction using the filtering expression and threshold of the
     * input link specification.
     *
     * @param spec
     *            Input link specification
     * @return a NestedPlan of the input link specification
     */
    public NestedPlan plan(LinkSpecification spec) {
        NestedPlan plan = new NestedPlan();
        // atomic specs are simply ran
        if (spec == null)
            return plan;
        if (spec.isEmpty())
            return plan;
        if (spec.isAtomic()) {
            // nested plan have a null instruction list as default
            plan.addInstruction(new Instruction(Instruction.Command.RUN, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
        } else {
            List<NestedPlan> children = new ArrayList<NestedPlan>();
            // set children
            for (LinkSpecification child : spec.getChildren()) {
                NestedPlan childPlan = plan(child);
                children.add(childPlan);
            }
            plan.setSubPlans(children);
            // set operator
            if (spec.getOperator().equals(LogicOperator.AND)) {
                plan.setOperator(Instruction.Command.INTERSECTION);
            } else if (spec.getOperator().equals(LogicOperator.OR)) {
                plan.setOperator(Instruction.Command.UNION);
            } else if (spec.getOperator().equals(LogicOperator.XOR)) {
                plan.setOperator(Instruction.Command.XOR);
            } else if (spec.getOperator().equals(LogicOperator.MINUS)) {
                plan.setOperator(Instruction.Command.DIFF);
            } else {
                System.out.println("Wrong operator: " + spec.getOperator() + ". at LS: " + spec);
                return null;
            }
            plan.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
                    spec.getThreshold() + "", -1, -1, 0));
        }
        return plan;
    }
    /**
     * Returns the status of the planner.
     *
     * @return true
     */
    @Override
    public boolean isStatic() {
        return true;
    }
    /**
     * Normalization of input link specification.
     *
     * @param spec
     *            The input link specification
     */
    @Override
    public LinkSpecification normalize(LinkSpecification spec) {
        return spec;
    }

}
