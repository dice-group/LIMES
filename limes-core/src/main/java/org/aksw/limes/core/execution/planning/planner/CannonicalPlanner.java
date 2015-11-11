package org.aksw.limes.core.execution.planning.planner;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.measures.mapper.SetOperations.Operator;


/**
 *
 * Impelements Cannonical Planner class.
 * 
 * @author ngonga
 * @author kleanthi
 */
public class CannonicalPlanner extends ExecutionPlanner {

    public CannonicalPlanner() {

    }

    /**
     * Generates a nested instructionList for a link spec
     *
     * @param spec
     *            Input spec
     * @return Nested instructionList
     */
    public ExecutionPlan plan(LinkSpecification spec) {
	ExecutionPlan plan = new ExecutionPlan();
	plan.instructionList = new ArrayList<Instruction>();
	// atomic specs are simply ran
	if (spec.isAtomic()) {

	    // nested plan have a null instruction list as default
	    plan.instructionList = new ArrayList<Instruction>();
	    plan.addInstruction(new Instruction(Instruction.Command.RUN, spec.getFilterExpression(),
		    spec.getThreshold() + "", -1, -1, 0));
	} else {
	    List<ExecutionPlan> children = new ArrayList<ExecutionPlan>();
	    // set childrean
	    for (LinkSpecification child : spec.children) {
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
	    plan.filteringInstruction = new Instruction(Command.FILTER, spec.getFilterExpression(), spec.getThreshold() + "",
		    -1, -1, 0);
	}

	return plan;

    }

    
}
