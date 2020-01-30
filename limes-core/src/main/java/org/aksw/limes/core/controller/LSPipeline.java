package org.aksw.limes.core.controller;

import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.Planner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Execution pipeline for generating mappings out of link specifications.
 * Provides overloaded convenience methods.
 *
 * @author Kevin Dreßler
 */
public class LSPipeline {
    /**
     * Execute a LS given a string metric expression and a double threshold,
     * generating a mapping.
     *
     * @param sourceCache
     *            Resources from source KB
     * @param targetCache
     *            Resources from target KB
     * @param metricExpression
     *            Specifies which measures are used and how they are combined to
     *            assert the similarity between two resources
     * @param threshold
     *            Minimal similarity value for resource pairs to be included in
     *            the generated mapping
     * @param sourceVar
     *            Name of SPARQL variable for resources from source KB
     * @param targetVar
     *            Name of SPARQL variable for resources from target KB
     * @param rewriterType
     *            Specifies rewriter module to use
     * @param executionPlannerType
     *            Specifies executionPlanner module to use
     * @param executionEngineType
     *            Specifies executionEngine module to use
     * @return Mapping of resources in sourceCache to resources in targetCache
     *         with similarity &gt; threshold
     */
    public static AMapping execute(ACache sourceCache, ACache targetCache, String metricExpression, double threshold,
            String sourceVar, String targetVar, RewriterFactory.RewriterType rewriterType,
            ExecutionPlannerFactory.ExecutionPlannerType executionPlannerType,
            ExecutionEngineFactory.ExecutionEngineType executionEngineType, long maxOpt, double k) {
        LinkSpecification ls = new LinkSpecification(metricExpression, threshold);
        return execute(sourceCache, targetCache, ls, sourceVar, targetVar, rewriterType, executionPlannerType,
                executionEngineType, maxOpt, k);
    }

    /**
     * Execute a given LS, generating a mapping.
     *
     * @param sourceCache
     *            Resources from source KB
     * @param targetCache
     *            Resources from target KB
     * @param ls
     *            LIMES Link Specification
     * @param sourceVar
     *            Name of SPARQL variable for resources from source KB
     * @param targetVar
     *            Name of SPARQL variable for resources from target KB
     * @param rewriterType
     *            Specifies rewriter module to use
     * @param executionPlannerType
     *            Specifies executionPlanner module to use
     * @param executionEngineType
     *            Specifies executionEngine module to use
     * @return Mapping of resources in sourceCache to resources in targetCache
     *         with similarity &gt; threshold
     */
    public static AMapping execute(ACache sourceCache, ACache targetCache, LinkSpecification ls, String sourceVar,
            String targetVar, RewriterFactory.RewriterType rewriterType,
            ExecutionPlannerFactory.ExecutionPlannerType executionPlannerType,
            ExecutionEngineFactory.ExecutionEngineType executionEngineType, long maxOpt, double k) {
        // Optimize LS by rewriting
        Rewriter rw = RewriterFactory.getRewriter(rewriterType);
        assert rw != null;
        LinkSpecification rwLs = rw.rewrite(ls);
        // Planning execution of the LS
        Planner planner = ExecutionPlannerFactory.getPlanner(executionPlannerType, sourceCache, targetCache);
        assert planner != null;
        // Execute the ExecutionPlan obtained from the LS
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(executionEngineType, sourceCache, targetCache,
                sourceVar, targetVar, maxOpt, k);
        assert engine != null;
        return engine.execute(rwLs, planner);
    }

    /**
     * Execute a given LS with default rewriter, planner and execution engine,
     * generating a mapping.
     *
     * @param sourceCache
     *            Resources from source KB
     * @param targetCache
     *            Resources from target KB
     * @param ls
     *            LIMES Link Specification
     * @return Mapping of resources in sourceCache to resources in targetCache
     *         with similarity &gt; threshold
     */
    public static AMapping execute(ACache sourceCache, ACache targetCache, LinkSpecification ls) {
        return execute(sourceCache, targetCache, ls, "?x", "?y", RewriterFactory.RewriterType.DEFAULT,
                ExecutionPlannerFactory.ExecutionPlannerType.DEFAULT,
                ExecutionEngineFactory.ExecutionEngineType.DEFAULT, 0, 1.0);
    }

}
