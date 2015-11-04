package org.aksw.limes.core.execution.engine;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.cache.Cache;
import org.aksw.limes.core.data.Mapping;
import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.apache.log4j.Logger;


/**
 * Implements the execution engine abstract class. The idea is that the engine
 * gets a series of instructions in the form of an execution plan and runs these
 * instructions and returns a mapping.
 *
 * @author ngonga
 * @author kleanthi
 */
public abstract class ExecutionEngine implements Engine {
    static Logger logger = Logger.getLogger("LIMES");
    //contains the results     
    private List<Mapping> buffer;
    private String sourceVariable;
    private String targetVariable;
    private Cache source;
    private Cache target;
    /**
     * Constructor for an execution engine.
     *
     * @param source Source cache
     * @param target Target cache
     * @param sourceVar Source variable (usually "?x")
     * @param targetVar Target variable (usually "?y")
     */
    public ExecutionEngine(Cache source, Cache target, String sourceVar, String targetVar) {
        buffer = new ArrayList<Mapping>();
        this.source = source;
        this.target = target;
        sourceVariable = sourceVar;
        targetVariable = targetVar;
    }
}
