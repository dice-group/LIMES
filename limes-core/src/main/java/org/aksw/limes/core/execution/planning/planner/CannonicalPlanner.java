package org.aksw.limes.core.execution.planning.planner;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.measures.mapper.MappingOperations.Operator;

/**
 *
 * Impelements Cannonical Planner class.
 * 
 * @author ngonga
 * @author kleanthi
 */
public class CannonicalPlanner extends Planner {

    public CannonicalPlanner() {
    }

    /**
     * Generates a NestedPlan for a link specification
     *
     * @param spec
     *            Input link specification
     * @return NestedPlan of the input link specification
     */
    public NestedPlan plan(LinkSpecification spec) {
	NestedPlan plan = new NestedPlan();
	plan.instructionList = new ArrayList<Instruction>();
	// atomic specs are simply ran
	if (spec.isAtomic()) {
	    // nested plan have a null instruction list as default
	    plan.instructionList = new ArrayList<Instruction>();
	    plan.addInstruction(new Instruction(Instruction.Command.RUN, spec.getFilterExpression(),
		    spec.getThreshold() + "", -1, -1, 0));
	} else {
	    List<NestedPlan> children = new ArrayList<NestedPlan>();
	    // set children
	    for (LinkSpecification child : spec.getChildren()) {
		children.add(plan(child));
	    }
	    plan.subPlans = children;
	    // set operator
	    if (spec.getOperator().equals(Operator.AND)) {
		plan.operator = Command.INTERSECTION;
	    } else if (spec.getOperator().equals(Operator.OR)) {
		plan.operator = Command.UNION;
	    } else if (spec.getOperator().equals(Operator.XOR)) {
		plan.operator = Command.XOR;
	    } else if (spec.getOperator().equals(Operator.MINUS)) {
		plan.operator = Command.DIFF;
	    }
	    plan.filteringInstruction = new Instruction(Command.FILTER, spec.getFilterExpression(),
		    spec.getThreshold() + "", -1, -1, 0);
	}
	return plan;
    }

}
