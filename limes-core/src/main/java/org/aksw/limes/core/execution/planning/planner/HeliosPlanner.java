package org.aksw.limes.core.execution.planning.planner;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.IMapper.Language;
import org.aksw.limes.core.measures.mapper.MappingOperations.Operator;

import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.apache.log4j.Logger;

/**
 *
 * Impelements Helios Planner class.
 * 
 * @author ngonga
 * @author kleanthi
 */
public class HeliosPlanner extends Planner {

    static Logger logger = Logger.getLogger("LIMES");
    public Cache source;
    public Cache target;
    public Language lang;

    /**
     * Constructor. Caches are needed for statistic computations.
     *
     * @param s
     *            Source cache
     * @param t
     *            Target get
     */
    public HeliosPlanner(Cache s, Cache t) {
	source = s;
	target = t;
	lang = Language.NULL;
    }

    /**
     * Computes atomic costs for a metric expression
     *
     * @param measure,
     *            measure of metric expression
     * @param threshold,
     *            threshold of metric expression
     * @return runtime, estimated runtime cost of the metric expression
     */
    public double getAtomicRuntimeCosts(String measure, double threshold) {
	Mapper am = MeasureFactory.getMapper(measure);
	return am.getRuntimeApproximation(source.size(), target.size(), threshold, lang);
    }

    /**
     * Computes atomic mapping sizes for a measure
     *
     * @param measure,
     *            measure of metric expression
     * @param threshold,
     *            threshold of metric expression
     * @return size, estimated size of returned mapping
     */
    public double getAtomicMappingSizes(String measure, double threshold) {
	Mapper am = MeasureFactory.getMapper(measure);
	return am.getMappingSizeApproximation(source.size(), target.size(), threshold, lang);
    }

    /**
     * Computes costs for a filtering
     *
     * @param filterExpression
     *            Expression used to filter
     * @param mappingSize
     *            Size of mapping
     * @return cost, estimated runtime cost of filteringInstruction(s)
     */
    public double getFilterCosts(List<String> measures, int mappingSize) {
	double cost = 0;
	if (measures != null) {
	    for (String measure : measures) {
		double tempCost = MeasureFactory.getMeasure(measure).getRuntimeApproximation(mappingSize);
		if (tempCost >= 0)
		    cost += tempCost;
	    }
	}
	return cost;
    }

    /**
     * Generates a NestedPlan for a link specification
     *
     * @param spec
     *            Input link specification
     * @return NestedPlan of the input link specification
     */
    public NestedPlan plan(LinkSpecification spec) {
	return plan(spec, source, target, new MemoryMapping(), new MemoryMapping());
    }

    /**
     * Generates a NestedPlan based on the optimality assumption used in
     * databases
     *
     * @param spec
     *            Input link specification
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceMapping
     *            Size of source mapping
     * @param targetMapping
     *            Size of target mapping
     * @return plan, a NestedPlan for the input link specification
     */
    public NestedPlan plan(LinkSpecification spec, Cache source, Cache target, MemoryMapping sourceMapping,
	    MemoryMapping targetMapping) {
	NestedPlan plan = new NestedPlan();
	// atomic specs are simply ran
	if (spec == null)
	    return plan;
	if (spec.isEmpty())
	    return plan;
	if (spec.isAtomic()) {
	    // here we should actually choose between different implementations
	    // of the operators based on their runtimeCost
	    Parser p = new Parser(spec.getFilterExpression(), spec.getThreshold());
	    plan.setInstructionList(new ArrayList<Instruction>());
	    plan.addInstruction(new Instruction(Instruction.Command.RUN, spec.getFilterExpression(),
		    spec.getThreshold() + "", -1, -1, 0));
	    plan.setRuntimeCost(getAtomicRuntimeCosts(p.getOperator(), spec.getThreshold()));
	    plan.setMappingSize(getAtomicMappingSizes(p.getOperator(), spec.getThreshold()));
	    plan.setSelectivity(plan.getMappingSize() / (double) (source.size() * target.size()));
	} else {
	    // no optimization for non AND operators really
	    if (!spec.getOperator().equals(Operator.AND)) {
		List<NestedPlan> children = new ArrayList<NestedPlan>();
		// set children and update costs
		plan.setRuntimeCost(0);
		for (LinkSpecification child : spec.getChildren()) {
		    NestedPlan childPlan = plan(child, source, target, sourceMapping, targetMapping);
		    children.add(childPlan);
		    plan.setRuntimeCost(plan.getRuntimeCost() + childPlan.getRuntimeCost());
		}
		// add costs of union, which are 1
		plan.setRuntimeCost(plan.getRuntimeCost() + (spec.getChildren().size() - 1));
		plan.setSubPlans(children);
		plan.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
			spec.getThreshold() + "", -1, -1, 0));
		// set operator
		double selectivity;
		if (spec.getOperator().equals(Operator.OR)) {
		    plan.setOperator(Instruction.Command.UNION);
		    selectivity = 1 - children.get(0).getSelectivity();
		    for (int i = 1; i < children.size(); i++) {
			selectivity = selectivity * (1 - children.get(i).getSelectivity());
			// add filtering costs based on approximation of mapping
			// size
			if (plan.getFilteringInstruction().getMeasureExpression() != null) {
			    plan.setRuntimeCost(plan.getRuntimeCost()
				    + MeasureProcessor.getCosts(plan.getFilteringInstruction().getMeasureExpression(),
					    source.size() * target.size() * (1 - selectivity)));
			}
		    }
		    plan.setSelectivity(1 - selectivity);
		} else if (spec.getOperator().equals(Operator.MINUS)) {
		    plan.setOperator(Instruction.Command.DIFF);
		    // p(A \ B \ C \ ... ) = p(A) \ p(B U C U ...)
		    selectivity = children.get(0).getSelectivity();
		    for (int i = 1; i < children.size(); i++) {
			selectivity = selectivity * (1 - children.get(i).getSelectivity());
			// add filtering costs based on approximation of mapping
			// size
			if (plan.getFilteringInstruction().getMeasureExpression() != null) {
			    plan.setRuntimeCost(plan.getRuntimeCost()
				    + MeasureProcessor.getCosts(plan.getFilteringInstruction().getMeasureExpression(),
					    source.size() * target.size() * (1 - selectivity)));
			}
		    }
		    plan.setSelectivity(selectivity);
		} else if (spec.getOperator().equals(Operator.XOR)) {
		    plan.setOperator(Instruction.Command.XOR);
		    // A XOR B = (A U B) \ (A & B)
		    selectivity = children.get(0).getSelectivity();
		    for (int i = 1; i < children.size(); i++) {
			selectivity = (1 - (1 - selectivity) * (1 - children.get(i).getSelectivity()))
				* (1 - selectivity * children.get(i).getSelectivity());
			// add filtering costs based on approximation of mapping
			// size
			if (plan.getFilteringInstruction().getMeasureExpression() != null) {
			    plan.setRuntimeCost(plan.getRuntimeCost()
				    + MeasureProcessor.getCosts(plan.getFilteringInstruction().getMeasureExpression(),
					    source.size() * target.size() * selectivity));
			}
		    }
		    plan.setSelectivity(selectivity);
		}

	    } // here we can optimize.
	    else if (spec.getOperator().equals(Operator.AND)) {
		List<NestedPlan> children = new ArrayList<NestedPlan>();
		plan.setRuntimeCost(0);
		double selectivity = 1d;
		for (LinkSpecification child : spec.getChildren()) {
		    NestedPlan childPlan = plan(child);
		    children.add(childPlan);
		    plan.setRuntimeCost(plan.getRuntimeCost() + childPlan.getRuntimeCost());
		    selectivity = selectivity * childPlan.getSelectivity();
		}
		plan = getBestConjunctivePlan(spec, children, selectivity);
	    }
	}
	return plan;
    }

    /**
     * Compute the left-order best instructionList for a list of plans. Only
     * needed when AND has more than 2 children. Simply splits the task in
     * computing the best instructionList for (leftmost, all others)
     *
     * @param plans
     *            List of plans
     * @param selectivity
     *            Selectivity of the instructionList (known beforehand)
     * @return NestedPlan
     */
    public NestedPlan getBestConjunctivePlan(LinkSpecification spec, List<NestedPlan> plans, double selectivity) {
	if (plans == null) {
	    return null;
	}
	if (plans.isEmpty()) {
	    return new NestedPlan();
	}
	if (plans.size() == 1) {
	    return plans.get(0);
	}
	if (plans.size() == 2) {
	    return getBestConjunctivePlan(spec, plans.get(0), plans.get(1), selectivity);
	} else {
	    NestedPlan left = plans.get(0);
	    plans.remove(plans.get(0));
	    return getBestConjunctivePlan(spec, left, plans, selectivity);
	}
    }

    /**
     * Computes the best conjunctive instructionList for a instructionList
     * against a list of plans by calling back the method
     *
     * @param left
     *            Left instructionList
     * @param plans
     *            List of other plans
     * @param selectivity
     *            Overall selectivity
     * @return NestedPlan
     */
    public NestedPlan getBestConjunctivePlan(LinkSpecification spec, NestedPlan left, List<NestedPlan> plans,
	    double selectivity) {
	if (plans == null) {
	    return left;
	}
	if (plans.isEmpty()) {
	    return left;
	}
	if (plans.size() == 1) {
	    return getBestConjunctivePlan(spec, left, plans.get(0), selectivity);
	} else {
	    NestedPlan right = getBestConjunctivePlan(spec, plans, selectivity);
	    return getBestConjunctivePlan(spec, left, right, selectivity);
	}
    }

    /**
     * Computes the best conjunctive instructionList for one pair of nested
     * plans
     *
     * @param left
     *            Left instructionList
     * @param right
     *            Right instructionList
     * @param selectivity
     * @return NestedPlan
     */
    public NestedPlan getBestConjunctivePlan(LinkSpecification spec, NestedPlan left, NestedPlan right,
	    double selectivity) {
	double runtime1 = 0, runtime2, runtime3;
	NestedPlan result = new NestedPlan();
	// first instructionList: run both children and then merge
	runtime1 = left.getRuntimeCost() + right.getRuntimeCost();
	result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
		spec.getThreshold() + "", -1, -1, 0));
	if (result.getFilteringInstruction().getMeasureExpression() != null) {
	    runtime1 = runtime1 + MeasureProcessor.getCosts(result.getFilteringInstruction().getMeasureExpression(),
		    (int) Math.ceil(source.size() * target.size() * selectivity));
	}
	// second instructionList: run left child and use right child as filter
	runtime2 = left.getRuntimeCost();
	runtime2 = runtime2 + getFilterCosts(right.getAllMeasures(),
		(int) Math.ceil(source.size() * target.size() * right.getSelectivity()));
	// third instructionList: run right child and use left child as filter
	runtime3 = right.getRuntimeCost();
	runtime3 = runtime3 + getFilterCosts(left.getAllMeasures(),
		(int) Math.ceil(source.size() * target.size() * left.getSelectivity()));

	double min = Math.min(Math.min(runtime3, runtime2), runtime1);
	// //just for tests
	// min = -10d;
	// runtime2 = -10d;
	if (min == runtime1) {
	    result.setOperator(Instruction.Command.INTERSECTION);
	    List<NestedPlan> subplans = new ArrayList<NestedPlan>();
	    subplans.add(left);
	    subplans.add(right);
	    result.setSubPlans(subplans);
	} else if (min == runtime2) {
	    String rightChild = spec.getChildren().get(1).getFullExpression();
	    result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, rightChild,
		    spec.getChildren().get(1).getThreshold() + "", -1, -1, 0));
	    result.getFilteringInstruction().setMainThreshold(spec.getThreshold() + "");
	    result.setOperator(null);
	    List<NestedPlan> subplans = new ArrayList<NestedPlan>();
	    subplans.add(left);
	    result.setSubPlans(subplans);
	} else // min == runtime3
	{
	    String leftChild = spec.getChildren().get(0).getFullExpression();
	    result.setFilteringInstruction(new Instruction(Instruction.Command.FILTER, leftChild,
		    spec.getChildren().get(0).getThreshold() + "", -1, -1, 0));
	    result.getFilteringInstruction().setMainThreshold(spec.getThreshold() + "");
	    result.setOperator(null);
	    List<NestedPlan> subplans = new ArrayList<NestedPlan>();
	    subplans.add(right);
	    result.setSubPlans(subplans);
	}
	result.setRuntimeCost(min);
	result.setSelectivity(selectivity);
	result.setMappingSize(source.size() * target.size() * selectivity);
	return result;
    }
}
