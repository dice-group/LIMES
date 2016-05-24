package org.aksw.limes.core.controller;

import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.Planner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Execution pipeline for generating mappings out of link specifications.
 * Provides overloaded convenience methods.
 *
 * @author Kevin Dreßler
 */
public class LSPipeline {
    /**
     * Execute a LS given a string metric expression and a double threshold, generating a mapping.
     */
    public static Mapping execute(Cache sourceCache, Cache targetCache, String metricExpression,
                                  double threshold, String sourceVar, String targetVar,
                                  RewriterFactory.RewriterFactoryType rewriterFactoryType,
                                  ExecutionPlannerFactory.ExecutionPlannerType executionPlannerType,
                                  ExecutionEngineFactory.ExecutionEngineType executionEngineType) {
        LinkSpecification ls = new LinkSpecification(metricExpression, threshold);
        return execute(sourceCache, targetCache, ls, sourceVar, targetVar,
                rewriterFactoryType, executionPlannerType, executionEngineType);
    }

    /**
     * Execute a LS object, generating a mapping.
     */
    public static Mapping execute(Cache sourceCache, Cache targetCache, LinkSpecification ls,
                                  String sourceVar, String targetVar,
                                  RewriterFactory.RewriterFactoryType rewriterFactoryType,
                                  ExecutionPlannerFactory.ExecutionPlannerType executionPlannerType,
                                  ExecutionEngineFactory.ExecutionEngineType executionEngineType) {
        // Optimize LS by rewriting
        Rewriter rw = RewriterFactory.getRewriter(rewriterFactoryType);
        assert rw != null;
        LinkSpecification rwLs = rw.rewrite(ls);
        // Planning execution of the LS
        Planner planner = ExecutionPlannerFactory.getPlanner(executionPlannerType, sourceCache, targetCache);
        assert planner != null;
        // Execute the ExecutionPlan obtained from the LS
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(executionEngineType, sourceCache, targetCache,
                sourceVar, targetVar);
        assert engine != null;
        return engine.execute(rwLs, planner);
    }

    /**
     * Execute a given LS with default rewriter, planner and execution engine, generating a mapping.
     */
    public static Mapping execute(Cache sourceCache, Cache targetCache, LinkSpecification ls) {
        return execute(sourceCache, targetCache, ls, "?x", "?y",
                RewriterFactory.RewriterFactoryType.DEFAULT,
                ExecutionPlannerFactory.ExecutionPlannerType.DEFAULT,
                ExecutionEngineFactory.ExecutionEngineType.DEFAULT);
    }

}
