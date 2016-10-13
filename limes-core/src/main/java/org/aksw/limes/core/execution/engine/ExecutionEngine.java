package org.aksw.limes.core.execution.engine;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the execution engine abstract class. The engine gets as input a
 * link specification and a planner type, executes the plan returned from the
 * planner and returns the set of links as a mapping.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public abstract class ExecutionEngine implements IExecutionEngine {
    static Logger logger = LoggerFactory.getLogger(ExecutionEngine.class);
    /**
     * List of intermediate mappings.
     */
    protected List<AMapping> buffer;
    /**
     * Source variable (usually "?x").
     */
    protected String sourceVariable;
    /**
     * Target variable (usually "?y").
     */
    protected String targetVariable;
    /**
     * Source cache.
     */
    protected ACache source;
    /**
     * Target cache.
     */
    protected ACache target;

    /**
     * Constructor for an execution engine.
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
    public ExecutionEngine(ACache source, ACache target, String sourceVar, String targetVar) {
        this.buffer = new ArrayList<>();
        this.source = source;
        this.target = target;
        this.sourceVariable = sourceVar;
        this.targetVariable = targetVar;
    }
}
