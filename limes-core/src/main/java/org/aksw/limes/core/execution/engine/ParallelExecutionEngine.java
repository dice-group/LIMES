package org.aksw.limes.core.execution.engine;

import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Implements the default execution engine class. The idea is that the engine
 * gets as input a link specification and a planner type, executes the
 * independent parts of the plan returned from the planner in parallel and
 * returns a MemoryMemoryMapping.
 *
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class ParallelExecutionEngine extends ExecutionEngine {
    /**
     * Constructor for a parallel execution engine.
     *
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceVar
     *            Source variable
     * @param targetVar
     *            Target variable
     */
    public ParallelExecutionEngine(ACache source, ACache target, String sourceVar, String targetVar, long maxOpt,
            double k) {
        super(source, target, sourceVar, targetVar);
    }

    /**
     * Implementation of the execution of an execution plan. Independent parts
     * of the plan are executed in parallel.
     *
     * @param spec
     *            The input link specification
     * @param planner
     *            The chosen planner
     * @return The mapping from running the plan
     */
    @Override
    public AMapping execute(LinkSpecification spec, IPlanner planner) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
