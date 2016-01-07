package org.aksw.limes.core.execution.engine;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.engine.filter.LinearFilter;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.measures.mapper.atomic.EDJoin;
import org.aksw.limes.core.measures.mapper.atomic.ExactMatchMapper;
import org.aksw.limes.core.measures.mapper.atomic.JaroMapper;
import org.aksw.limes.core.measures.mapper.atomic.JaroWinklerMapper;
import org.aksw.limes.core.measures.mapper.atomic.MongeElkanMapper;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.atomic.RatcliffObershelpMapper;
import org.aksw.limes.core.measures.mapper.atomic.SoundexMapper;
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
    public DefaultExecutionEngine(Cache source, Cache target, String sourceVar, String targetVar) {
	super(source, target, sourceVar, targetVar);
    }

    /**
     * Implementation of the execution of a plan. Be aware that this doesn't
     * executes complex Link Specifications.
     *
     * @param plan
     *            An execution plan
     * @return The MemoryMapping that results from running the plan
     */
    private Mapping run(Plan plan) {
	buffer = new ArrayList<MemoryMapping>();
	if (plan.isEmpty()) {
	    logger.info("Plan is empty. Done.");
	    return new MemoryMapping();
	}
	List<Instruction> instructions = plan.getInstructionList();
	Mapping m = new MemoryMapping();
	for (int i = 0; i < instructions.size(); i++) {
	    Instruction inst = instructions.get(i);
	    // get the index for writing the results
	    int index = inst.getResultIndex();
	    // first process the RUN operator
	    if (inst.getCommand().equals(Command.RUN)) {
		m = executeRun(inst);
	    } // runs the filter operator
	    else if (inst.getCommand().equals(Command.FILTER)) {
		m = executeFilter(inst, buffer.get(inst.getSourceMapping()));
	    } // runs set operations such as intersection,
	    else if (inst.getCommand().equals(Command.INTERSECTION)) {
		m = executeIntersection(buffer.get(inst.getSourceMapping()), buffer.get(inst.getTargetMapping()));
	    } // union
	    else if (inst.getCommand().equals(Command.UNION)) {
		m = executeUnion(buffer.get(inst.getSourceMapping()), buffer.get(inst.getTargetMapping()));
	    } // diff
	    else if (inst.getCommand().equals(Command.DIFF)) {
		m = executeDifference(buffer.get(inst.getSourceMapping()), buffer.get(inst.getTargetMapping()));
	    } // xor
	    else if (inst.getCommand().equals(Command.XOR)) {
		m = executeExclusiveOr(buffer.get(inst.getSourceMapping()), buffer.get(inst.getTargetMapping()));
	    } // end of processing. Return the indicated mapping
	    else if (inst.getCommand().equals(Command.RETURN)) {
		logger.info("Reached return command. Returning results.");
		if (buffer.isEmpty()) {
		    return m;
		}
		if (index < 0) {
		    return buffer.get(buffer.size() - 1);
		} else {
		    return buffer.get(index);
		}
	    }
	    if (index < 0) {
		buffer.add((MemoryMapping) m);
	    } else {
		// add placeholders to ensure that the mapping can be placed
		// where the user wanted to have it
		while ((index + 1) > buffer.size()) {
		    buffer.add(new MemoryMapping());
		}
		buffer.set(index, (MemoryMapping) m);
	    }
	}

	// just in case the return operator was forgotten.
	// Then we return the last mapping computed
	if (buffer.isEmpty()) {
	    return new MemoryMapping();
	} else {
	    return buffer.get(buffer.size() - 1);
	}
    }

    /**
     * Implements running the run operator. Assume atomic measures.
     *
     * @param inst
     *            Instruction
     * @return Mapping
     */
    public Mapping executeRun(Instruction inst) {
	// get threshold

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
	} else if (inst.getMeasureExpression().startsWith("monge")) {
	    mapper = new MongeElkanMapper();
	} else if (inst.getMeasureExpression().startsWith("exactmatch")) {
	    mapper = new ExactMatchMapper();
	} else if (inst.getMeasureExpression().startsWith("soundex")) {
	    mapper = new SoundexMapper();
	}else if (inst.getMeasureExpression().startsWith("overlap")
		|| inst.getMeasureExpression().startsWith("trigrams")||
		inst.getMeasureExpression().startsWith("cosine") ||
		inst.getMeasureExpression().startsWith("jaccard")) {
	    mapper = new PPJoinPlusPlus();
	} else 
	    return new MemoryMapping();
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
    public Mapping executeFilter(Instruction inst, Mapping m) {
	LinearFilter filter = new LinearFilter();
	return filter.filter(m, inst.getMeasureExpression(), Double.parseDouble(inst.getThreshold()), source, target,
		sourceVariable, targetVariable);
    }

    /**
     * Implements the difference of MemoryMappings
     *
     * @param m1
     *            First MemoryMapping
     * @param m2
     *            Second MemoryMapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeDifference(Mapping m1, Mapping m2) {
	return MappingOperations.difference(m1, m2);
    }

    /**
     * Implements the exclusive or of MemoryMappings
     *
     * @param m1
     *            First MemoryMapping
     * @param m2
     *            Second MemoryMapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeExclusiveOr(Mapping m1, Mapping m2) {
	return MappingOperations.xor(m1, m2);
    }

    /**
     * Implements the intersection of MemoryMappings
     *
     * @param m1
     *            First MemoryMapping
     * @param m2
     *            Second MemoryMapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeIntersection(Mapping m1, Mapping m2) {
	return MappingOperations.intersection(m1, m2);
    }

    /**
     * Implements the union of MemoryMappings
     *
     * @param m1
     *            First MemoryMapping
     * @param m2
     *            Second MemoryMapping
     * @return Intersection of m1 and m2
     */
    public Mapping executeUnion(Mapping m1, Mapping m2) {
	return MappingOperations.union(m1, m2);
    }

    /**
     * Runs an execution plan
     *
     * @param plan
     *            ExecutionPlan
     * @return MemoryMapping
     */
    public Mapping execute(Plan plan) {
	// empty nested plan contains nothing
	Mapping m = new MemoryMapping();
	if (plan.isEmpty()) {
	} // atomic nested plan just contain simple list of instructions
	else if (plan.isFlat()) {
	    m = run(plan);
	} // nested plans contain subplans, an operator for merging the results
	  // of the subplans and a filter for filtering the results of the
	  // subplan
	else {
	    // run all the subplans
	    m = execute(plan.getSubPlans().get(0));
	    Mapping m2, result = m;
	    for (int i = 1; i < plan.getSubPlans().size(); i++) {
		m2 = execute(plan.getSubPlans().get(i));
		if (plan.getOperator().equals(Command.INTERSECTION)) {
		    result = executeIntersection(m, m2);
		} // union
		else if (plan.getOperator().equals(Command.UNION)) {
		    result = executeUnion(m, m2);
		} // diff
		else if (plan.getOperator().equals(Command.DIFF)) {
		    result = executeDifference(m, m2);
		    // exclusive or
		} else if (plan.getOperator().equals(Command.XOR)) {
		    result = executeExclusiveOr(m, m2);
		}
		m = result;
	    }
	    // only run filtering if there is a filter indeed, else simply
	    // return MemoryMapping
	    if (plan.getFilteringInstruction() != null) {
		if (Double.parseDouble(plan.getFilteringInstruction().getThreshold()) > 0) {
		    m = executeFilter(plan.getFilteringInstruction(), m);
		}
	    }
	}
	return m;
    }

}
