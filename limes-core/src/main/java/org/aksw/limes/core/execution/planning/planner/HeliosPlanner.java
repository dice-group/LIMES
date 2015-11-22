package org.aksw.limes.core.execution.planning.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.IMapper.Language;
import org.aksw.limes.core.measures.mapper.SetOperations.Operator;
import org.aksw.limes.core.measures.mapper.atomic.EDJoin;
import org.aksw.limes.core.measures.mapper.atomic.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.atomic.TotalOrderBlockingMapper;
import org.aksw.limes.core.measures.mapper.atomic.fastngram.FastNGram;
import org.aksw.limes.core.measures.measure.IMeasure;
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
public class HeliosPlanner extends ExecutionPlanner {

    static Logger logger = Logger.getLogger("LIMES");
    public Cache source;
    public Cache target;
    Map<String, Double> averageSourcePropertyLength;
    Map<String, Double> stdDevSourceProperty;
    Map<String, Double> averageTargetPropertyLength;
    Map<String, Double> stdDevTargetProperty;
    public Language lang;

    /**
     * Constructor. Caches are needed for stats.
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
     * Computes atomic costs for a measure
     *
     * @param measure
     * @param threshold
     * @return
     */
    public double getAtomicRuntimeCosts(String measure, double threshold) {
	IMeasure m = MeasureFactory.getMeasure(measure);
	double runtime;
	if (m.getName().equalsIgnoreCase("levenshtein")) {
	    runtime = (new EDJoin()).getRuntimeApproximation(source.size(), target.size(), threshold, lang);
	} else if (m.getName().equalsIgnoreCase("euclidean")) {
	    runtime = (new TotalOrderBlockingMapper()).getRuntimeApproximation(source.size(), target.size(), threshold,
		    lang);
	} else if (m.getName().equalsIgnoreCase("qgrams")) {
	    runtime = (new FastNGram()).getRuntimeApproximation(source.size(), target.size(), threshold, lang);
	} else {
	    runtime = (new PPJoinPlusPlus()).getRuntimeApproximation(source.size(), target.size(), threshold, lang);
	}
	logger.info("Runtime approximation for " + measure + " is " + runtime);
	return runtime;
    }

    /**
     * Computes atomic mapping sizes for a measure
     *
     * @param measure
     * @param threshold
     * @return
     */
    public double getAtomicMappingSizes(String measure, double threshold) {
	IMeasure m = MeasureFactory.getMeasure(measure);
	double size;
	if (m.getName().equalsIgnoreCase("levenshtein")) {
	    size = (new EDJoin()).getMappingSizeApproximation(source.size(), target.size(), threshold, lang);
	}
	if (m.getName().equalsIgnoreCase("euclidean")) {
	    size = (new TotalOrderBlockingMapper()).getMappingSizeApproximation(source.size(), target.size(), threshold,
		    lang);
	}
	if (m.getName().equalsIgnoreCase("qgrams")) {
	    size = (new FastNGram()).getMappingSizeApproximation(source.size(), target.size(), threshold, lang);
	} else {
	    size = (new PPJoinPlusPlus()).getMappingSizeApproximation(source.size(), target.size(), threshold, lang);
	}
	return size;
    }

    /**
     * Computes costs for a filtering
     *
     * @param filterExpression
     *            Expression used to filter
     * @param mappingSize
     *            Size of mapping
     * @return Costs for filtering
     */
    public double getFilterCosts(List<String> measures, int mappingSize) {
	// need costs for single operations on measures
	// = MeasureProcessor.getMeasures(filterExpression);
	double cost = 0;
	for (String measure : measures) {
	    cost = cost + MeasureFactory.getMeasure(measure).getRuntimeApproximation(mappingSize);
	}
	logger.info("Runtime approximation for filter expression " + measures + " is " + cost);
	return cost;
    }

    public ExecutionPlan plan(LinkSpecification spec) {

	return plan(spec, source, target, new MemoryMapping(), new MemoryMapping());

    }

    /**
     * Generates a instructionList based on the optimality assumption used in
     * databases
     *
     * @param spec
     *            Specification for which a instructionList is needed
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceMapping
     *            Size of source mapping
     * @param targetMapping
     *            Size of target mapping
     * @return Nested instructionList for the given spec
     */
    public ExecutionPlan plan(LinkSpecification spec, Cache source, Cache target, MemoryMapping sourceMapping, MemoryMapping targetMapping) {
	ExecutionPlan plan = new ExecutionPlan();
	// atomic specs are simply ran
	if (spec.isAtomic()) {
	    // here we should actually choose between different implementations
	    // of the operators based on their runtimeCost
	    Parser p = new Parser(spec.getFilterExpression(), spec.getThreshold());
	    plan.instructionList = new ArrayList<Instruction>();
	    plan.addInstruction(new Instruction(Instruction.Command.RUN, spec.getFilterExpression(),
		    spec.getThreshold() + ""));
	    plan.runtimeCost = getAtomicRuntimeCosts(p.getOperator(), spec.getThreshold());
	    plan.mappingSize = getAtomicMappingSizes(p.getOperator(), spec.getThreshold());
	    // there is a function in EDJoin that does that
	    plan.selectivity = plan.mappingSize / (double) (source.size() * target.size());
	    // System.out.println("Plan for " + spec.filterExpression + ":\n" +
	    // plan);
	} else {
	    // no optimization for non AND operators really
	    if (!spec.getOperator().equals(Operator.AND)) {
		List<ExecutionPlan> children = new ArrayList<ExecutionPlan>();
		// set children and update costs
		plan.runtimeCost = 0;
		for (LinkSpecification child : spec.getChildren()) {
		    ExecutionPlan childPlan = plan(child, source, target, sourceMapping, targetMapping);
		    children.add(childPlan);
		    plan.runtimeCost = plan.runtimeCost + childPlan.runtimeCost;
		}
		// add costs of union, which are 1
		plan.runtimeCost = plan.runtimeCost + (spec.getChildren().size() - 1);
		plan.subPlans = children;
		// set operator
		double selectivity;
		if (spec.getOperator().equals(Operator.OR)) {
		    plan.operator = Instruction.Command.UNION;
		    selectivity = 1 - children.get(0).selectivity;
		    plan.runtimeCost = children.get(0).runtimeCost;
		    for (int i = 1; i < children.size(); i++) {
			selectivity = selectivity * (1 - children.get(i).selectivity);
			// add filtering costs based on approximation of mapping
			// size
			if (plan.filteringInstruction != null) {

			    plan.runtimeCost = plan.runtimeCost
				    + MeasureProcessor.getCosts(plan.filteringInstruction.getMeasureExpression(),
					    source.size() * target.size() * (1 - selectivity));
			}
		    }
		    plan.selectivity = 1 - selectivity;
		} else if (spec.getOperator().equals(Operator.MINUS)) {
		    plan.operator = Instruction.Command.DIFF;
		    // p(A \ B \ C \ ... ) = p(A) \ p(B U C U ...)
		    selectivity = children.get(0).selectivity;
		    for (int i = 1; i < children.size(); i++) {
			selectivity = selectivity * (1 - children.get(i).selectivity);
			// add filtering costs based on approximation of mapping
			// size
			if (plan.filteringInstruction != null) {
			    plan.runtimeCost = plan.runtimeCost
				    + MeasureProcessor.getCosts(plan.filteringInstruction.getMeasureExpression(),
					    source.size() * target.size() * (1 - selectivity));
			}
		    }
		    plan.selectivity = selectivity;
		} else if (spec.getOperator().equals(Operator.XOR)) {
		    plan.operator = Instruction.Command.XOR;
		    // A XOR B = (A U B) \ (A & B)
		    selectivity = children.get(0).selectivity;
		    for (int i = 1; i < children.size(); i++) {
			selectivity = (1 - (1 - selectivity) * (1 - children.get(i).selectivity))
				* (1 - selectivity * children.get(i).selectivity);
			// add filtering costs based on approximation of mapping
			// size
			if (plan.filteringInstruction != null) {
			    plan.runtimeCost = plan.runtimeCost
				    + MeasureProcessor.getCosts(plan.filteringInstruction.getMeasureExpression(),
					    source.size() * target.size() * selectivity);
			}
		    }
		    plan.selectivity = selectivity;
		}
		plan.filteringInstruction = new Instruction(Instruction.Command.FILTER, spec.getFilterExpression(),
			spec.getThreshold() + "");
	    } // here we can optimize.
	    else if (spec.getOperator().equals(Operator.AND)) {
		List<ExecutionPlan> children = new ArrayList<ExecutionPlan>();
		plan.runtimeCost = 0;
		double selectivity = 1d;
		for (LinkSpecification child : spec.getChildren()) {
		    ExecutionPlan childPlan = plan(child);
		    children.add(childPlan);
		    plan.runtimeCost = plan.runtimeCost + childPlan.runtimeCost;
		    selectivity = selectivity * childPlan.selectivity;
		}
		plan = getBestConjunctivePlan(children, selectivity);
	    }
	}
	return plan;
    }

    /**
     * Compute the left-order best instructionList for a list of plans. Only
     * needed when more AND has more than 2 children. Simply splits the task in
     * computing the best instructionList for (leftmost, all others)
     *
     * @param plans
     *            List of plans
     * @param selectivity
     *            Selectivity of the instructionList (known beforehand)
     * @return ExecutionPlan
     */
    public ExecutionPlan getBestConjunctivePlan(List<ExecutionPlan> plans, double selectivity) {
	if (plans == null) {
	    return null;
	}
	if (plans.isEmpty()) {
	    return new ExecutionPlan();
	}
	if (plans.size() == 1) {
	    return plans.get(0);
	}
	if (plans.size() == 2) {
	    return getBestConjunctivePlan(plans.get(0), plans.get(1), selectivity);
	} else {
	    ExecutionPlan left = plans.get(0);
	    plans.remove(plans.get(0));
	    return getBestConjunctivePlan(left, plans, selectivity);
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
     * @return ExecutionPlan
     */
    public ExecutionPlan getBestConjunctivePlan(ExecutionPlan left, List<ExecutionPlan> plans, double selectivity) {
	if (plans == null) {
	    return left;
	}
	if (plans.isEmpty()) {
	    return left;
	}
	if (plans.size() == 1) {
	    return getBestConjunctivePlan(left, plans.get(0), selectivity);
	} else {
	    ExecutionPlan right = getBestConjunctivePlan(plans, selectivity);
	    return getBestConjunctivePlan(left, right, selectivity);
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
     * @return
     */
    public ExecutionPlan getBestConjunctivePlan(ExecutionPlan left, ExecutionPlan right, double selectivity) {
	double runtime1 = 0, runtime2, runtime3;
	ExecutionPlan result = new ExecutionPlan();
	double mappingSize = source.size() * target.size() * right.selectivity;

	// first instructionList: run both children and then merge
	runtime1 = left.runtimeCost + right.runtimeCost;
	// second instructionList: run left child and use right child as filter
	runtime2 = left.runtimeCost;
	runtime2 = runtime2 + getFilterCosts(right.getAllMeasures(), (int) Math.ceil(mappingSize));

	// third instructionList: run right child and use left child as filter
	runtime3 = right.runtimeCost;
	mappingSize = source.size() * target.size() * left.selectivity;
	runtime3 = runtime3 + getFilterCosts(left.getAllMeasures(), (int) Math.ceil(mappingSize));
	// logger.info("RUN - RUN approximation for left-right is "+runtime1);
	// logger.info("RUN - FILTER approximation for left-right is
	// "+runtime2+" with right selectivity = "+right.selectivity);
	// logger.info("FILTER - RUN approximation for left-right is
	// "+runtime3+" with left selectivity = "+left.selectivity);
	double min = Math.min(Math.min(runtime3, runtime2), runtime1);
	// //just for tests
	// min = -10d;
	// runtime2 = -10d;

	if (min == runtime1) {
	    result.operator = Instruction.Command.INTERSECTION;
	    result.filteringInstruction = null;
	    List<ExecutionPlan> plans = new ArrayList<ExecutionPlan>();
	    plans.add(left);
	    plans.add(right);
	    result.subPlans = plans;

	} else if (min == runtime2) {
	    // System.out.println("Right = "+right.getEquivalentMeasure()+" --->
	    // \n"+right.toString());
	    if (right.isAtomic()) {
		result.filteringInstruction = new Instruction(Instruction.Command.FILTER, right.getEquivalentMeasure(),
			right.getInstructionList().get(0).getThreshold() + "");
	    } else {
		result.filteringInstruction = new Instruction(Instruction.Command.FILTER, right.getEquivalentMeasure(),
			right.filteringInstruction.getThreshold() + "");
	    }
	    result.operator = null;
	    List<ExecutionPlan> plans = new ArrayList<ExecutionPlan>();
	    plans.add(left);
	    result.subPlans = plans;
	} else // min == runtime3
	{
	    // System.out.println("Left = "+left.getEquivalentMeasure()+" --->
	    // \n"+left.toString());
	    if (left.isAtomic()) {
		result.filteringInstruction = new Instruction(Instruction.Command.FILTER, left.getEquivalentMeasure(),
			left.getInstructionList().get(0).getThreshold() + "");
	    } else {
		result.filteringInstruction = new Instruction(Instruction.Command.FILTER, left.getEquivalentMeasure(),
			left.filteringInstruction.getThreshold() + "");
	    }
	    result.operator = null;
	    List<ExecutionPlan> plans = new ArrayList<ExecutionPlan>();
	    plans.add(right);
	    result.subPlans = plans;
	}
	result.runtimeCost = min;
	result.selectivity = selectivity;
	result.mappingSize = source.size() * target.size() * selectivity;

	return result;
    }

    
}
