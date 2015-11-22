package org.aksw.limes.core.execution.engine;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.engine.filter.LinearFilter;
import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.measures.mapper.SetOperations;
import org.aksw.limes.core.measures.mapper.atomic.EDJoin;
import org.aksw.limes.core.measures.mapper.atomic.JaroMapper;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.atomic.TotalOrderBlockingMapper;
import org.aksw.limes.core.measures.mapper.atomic.fastngram.FastNGram;

/**
 * Implements the default execution engine class. The idea is that the engine
 * gets a series of instructions in the form of an execution plan and runs these
 * instructions sequentially and returns a MemoryMemoryMapping.
 *
 * @author ngonga
 * @author kleanthi
 */
public class DefaultExecutionEngine extends ExecutionEngine {

    public DefaultExecutionEngine(Cache source, Cache target, String sourceVar, String targetVar) {
	super(source, target, sourceVar, targetVar);
    }

    
    /**
     * Implements running the run operator. Assume atomic measures
     *
     * @param inst
     *            Instruction
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @return MemoryMapping
     */
    public Mapping executeRun(ExecutionPlan plan) {
	// get threshold
	Instruction inst = plan.getInstructionList().get(0);
	double threshold = Double.parseDouble(inst.getThreshold());
	// generate correct mapper
	IMapper mapper;
	if (inst.getMeasureExpression().startsWith("leven")) {
	    mapper = new EDJoin();
	} else if (inst.getMeasureExpression().startsWith("euclid")) {
	    mapper = new TotalOrderBlockingMapper();
	} else if (inst.getMeasureExpression().startsWith("jaro")) {
	    mapper = new JaroMapper();
	} else if (inst.getMeasureExpression().startsWith("qgrams")) {
	    mapper = new FastNGram();
	} else if (inst.getMeasureExpression().startsWith("hausdorff")
		|| inst.getMeasureExpression().startsWith("geomean")) {
	    mapper = new OrchidMapper();
	} else {
	    mapper = new PPJoinPlusPlus();
	}
	// run mapper
	return mapper.getMapping(source, target, sourceVariable, targetVariable, inst.getMeasureExpression(),
		threshold);
    }
    

    /**
     * Runs the filtering operator
     *
     * @param inst
     *            Instruction
     * @param input
     *            MemoryMapping that is to be filtered
     * @return Filtered MemoryMapping
     */
    public Mapping executeFilter(Instruction inst, Mapping input) {
	LinearFilter filter = new LinearFilter();
	return filter.filter(input, inst.getMeasureExpression(), Double.parseDouble(inst.getThreshold()), source,
		target, sourceVariable, targetVariable);
    }
    /**
     * Implements the difference of MemoryMappings
     *
     * @param inst
     *            Instruction
     * @param m1
     *            First MemoryMapping
     * @param m2
     *            Second MemoryMapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeDifference( Mapping m1, Mapping m2) {
	return SetOperations.difference(m1, m2);
    }/**
     * Implements the exclusive or of MemoryMappings
    *
    * @param inst
    *            Instruction
    * @param m1
    *            First MemoryMapping
    * @param m2
    *            Second MemoryMapping
    * @return Intersection of m1 and m2
    */
   public Mapping executeExclusiveOr( Mapping m1, Mapping m2) {
	return SetOperations.xor(m1, m2);
   }
    
    /**
     * Implements the intersection of MemoryMappings
     *
     * @param inst
     *            Instruction
     * @param m1
     *            First MemoryMapping
     * @param m2
     *            Second MemoryMapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeIntersection(Mapping m1, Mapping m2) {
	return SetOperations.intersection(m1, m2);
    }
   
    /**
     * Implements the union of MemoryMappings
     *
     * @param inst
     *            Instruction
     * @param m1
     *            First MemoryMapping
     * @param m2
     *            Second MemoryMapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeUnion(Mapping m1, Mapping m2) {
	return SetOperations.union(m1, m2);
    }
    /**
     * Runs an execution plan
     *
     * @param ExecutionPlan
     *            ExecutionPlan
     * @return MemoryMapping
     */
    public Mapping execute(ExecutionPlan ExecutionPlan) {
	// empty nested plan contains nothing
	long begin = System.currentTimeMillis();

	Mapping m = new MemoryMapping();
	if (ExecutionPlan.isEmpty()) {
	} // atomic nested plan just contain simple list of instructions
	else if (ExecutionPlan.isAtomic()) {
	    //Instruction inst = ExecutionPlan.getInstructionList().get(0);
	    //m = executeRun(inst);
	    //m = executeAtomic(ExecutionPlan);
	    m = executeRun(ExecutionPlan);
	} // nested plans contain subplans, an operator for merging the results
	  // of the
	  // subplans and a filter for filtering the results of the subplan
	else {
	    // run all the subplans
	    m = execute(ExecutionPlan.subPlans.get(0));
	    Mapping m2, result = m;
	    for (int i = 1; i < ExecutionPlan.subPlans.size(); i++) {
		m2 = execute(ExecutionPlan.subPlans.get(i));
		if (ExecutionPlan.operator.equals(Command.INTERSECTION)) {
		    //result = SetOperations.intersection(m, m2);
		    result = executeIntersection(m, m2);
		} // union
		else if (ExecutionPlan.operator.equals(Command.UNION)) {
		    //result = SetOperations.union(m, m2);
		    result = executeUnion(m, m2);
		} // diff
		else if (ExecutionPlan.operator.equals(Command.DIFF)) {
		    //result = SetOperations.difference(m, m2);
		    result = executeDifference(m, m2);
		} else if (ExecutionPlan.operator.equals(Command.XOR)) {
		    result = executeExclusiveOr(m,m2);
		    //result = SetOperations.xor(m, m2);
		}

		m = result;
	    }
	    // only run filtering if there is a filter indeed, else simply
	    // return MemoryMapping
	    if (ExecutionPlan.filteringInstruction != null) {
		if (Double.parseDouble(ExecutionPlan.filteringInstruction.getThreshold()) > 0) {
		    m = executeFilter(ExecutionPlan.filteringInstruction, m);
		}
	    }
	}
	long end = System.currentTimeMillis();
	return m;
    }

}
