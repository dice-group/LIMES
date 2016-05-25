package org.aksw.limes.core.execution.engine;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.apache.log4j.Logger;

/**
 * Implements the execution engine abstract class. The idea is that the engine
 * gets a series of instructions in the form of an execution plan and runs these
 * instructions and returns a mapping
 *
 * @author ngonga
 * @author kleanthi
 */
public abstract class ExecutionEngine implements IExecutionEngine {
    static Logger logger = Logger.getLogger(ExecutionEngine.class.getName());
    // contains the results
    protected List<MemoryMapping> buffer;
    protected String sourceVariable;
    protected String targetVariable;
    protected Cache source;
    protected Cache target;

    /**
     * Constructor for an execution engine.
     *
     * @param source,
     *            Source cache
     * @param target,
     *            Target cache
     * @param sourceVar,
     *            Source variable (usually "?x")
     * @param targetVar,
     *            Target variable (usually "?y")
     */
    public ExecutionEngine(Cache source, Cache target, String sourceVar, String targetVar) {
        this.buffer = new ArrayList<MemoryMapping>();
        this.source = source;
        this.target = target;
        this.sourceVariable = sourceVar;
        this.targetVariable = targetVar;
    }
}
