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
public class CanonicalPlanner extends Planner {

    public CanonicalPlanner() {
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
	    if (spec.getOperator().equals(Operator.AND)) {
		plan.setOperator(Command.INTERSECTION);
	    } else if (spec.getOperator().equals(Operator.OR)) {
		plan.setOperator(Command.UNION);
	    } else if (spec.getOperator().equals(Operator.XOR)) {
		plan.setOperator(Command.XOR);
	    } else if (spec.getOperator().equals(Operator.MINUS)) {
		plan.setOperator(Command.DIFF);
	    }else{
		System.out.println("Wrong operator: "+spec.getOperator()+". at LS: "+spec);
		return null;
	    }
	    plan.setFilteringInstruction(new Instruction(Command.FILTER, spec.getFilterExpression(),
		    spec.getThreshold() + "", -1, -1, 0));
	}
	return plan;
    }

}
