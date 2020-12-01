package org.aksw.limes.core.ml.algorithm.eagle.core;

import java.util.HashMap;
import java.util.Map.Entry;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.IQualitativeMeasure;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.eagle.util.CacheTrimmer;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.ProgramChromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of our custom FitnessFunction. As we're using the
 * <code>DeltaFitnessEvaluator</code> higher fitness values mean the individual
 * is less fit! ReImplementation on <code>ExcutionEngine</code>
 * <p>
 * FIXME fix QualityMeasures to work on Mappings!
 *
 * @author Klaus Lyko
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 */
public class ExpressionFitnessFunction extends IGPFitnessFunction {
    public static final String fScore = "fScore";
    public static final String recall = "recall";
    public static final String precision = "precision";
    private static final long serialVersionUID = 1L;
    /** Complete optimal Mapping. Note that it should only hold matches! */
    // protected Mapping optimalMapping;
    static Logger logger = LoggerFactory.getLogger(ExpressionFitnessFunction.class.getName());
    private static ExpressionFitnessFunction instance = null;
    // public ExecutionEngine engine;
    // public ExecutionEngine fullEngine;
    protected LinkSpecGeneticLearnerConfig m_config;
    /**
     * Fragment of optimal Mapping used during evolution. Note that it should
     * only hold matches!
     */
    protected AMapping reference;
    // protected int numberOfExamples = 0;
    protected ACache sC;
    protected ACache tC;
    protected ACache trimmedSourceCache;
    protected ACache trimmedTargetCache;
    protected IQualitativeMeasure measure;
    protected double crossProduct;
    /**
     * Holding training data, that is a mapping maybe also holding non-matches.
     */
    @SuppressWarnings("unused")
    private AMapping trainingData;

    private boolean useFullCaches = false;

    /**
     * Needed for subclasses.
     */
    protected ExpressionFitnessFunction(LinkSpecGeneticLearnerConfig a_config) {
        m_config = a_config;
        if (a_config.sC != null)
            sC = a_config.sC;
        else {
            sC = HybridCache.getData(a_config.source);
        }
        if (a_config.tC != null)
            tC = a_config.tC;
        else
            tC = HybridCache.getData(a_config.target);

    }

    private ExpressionFitnessFunction(LinkSpecGeneticLearnerConfig a_config, IQualitativeMeasure measure,
            AMapping reference) {
        this(a_config);
        m_config = a_config;
        // optimalMapping = reference;
        this.reference = reference;

        // get Engines
        trimKnowledgeBases(reference);

        // fullEngine =
        // ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sC, tC,
        // a_config.source.getVar(), a_config.target.getVar());

        this.measure = measure;
        crossProduct = trimmedSourceCache.size() * trimmedTargetCache.size();
        // System.gc();
    }

    public static ExpressionFitnessFunction getInstance(LinkSpecGeneticLearnerConfig a_config,
            IQualitativeMeasure measure, AMapping reference) {
        if (instance == null) {
            instance = new ExpressionFitnessFunction(a_config, measure, reference);
        }
        return instance;
    }

    @Override
    protected double evaluate(IGPProgram a_subject) {
        return calculateRawFitness(a_subject);
    }

    /**
     * Calculates the fitness of the given GPProgram p. This is done as follows:
     * 1.1 get the Metric Expression by executing (as object) the first
     * chromosome. 1.2 get the mapping links with these settings 2. Calculate
     * either recall, precision or f-score and return 1 minus it. The evolution
     * could produce non wanted individuals especially those who compare
     * properties which are not part of the PropertyMapping (set in the
     * ExpressionConfiguration). In such cases setp 1.1 throws an
     * IllegalStateException. As a result the fitness value would be set to
     * rather bad one.
     *
     * @param p
     *            GPProgram fitness is calculated for.
     * @return Double value. The closer to 0 the better.
     */
    public double calculateRawFitness(IGPProgram p) {
        p.getGPConfiguration().clearStack();
        p.getGPConfiguration().clearMemory();
        // get actual Mapping
        Object[] args = {};
        ProgramChromosome pc = p.getChromosome(0);
        AMapping actualMapping = MappingFactory.createDefaultMapping();
        LinkSpecification spec = (LinkSpecification) pc.getNode(0).execute_object(pc, 0, args);
        String expr = spec.getFilterExpression();

        if (expr == null)
            return 5d; // manually return bad fitness

        if (expr.indexOf("falseProp") > -1) {
            return 8d;
        }
        try {
            if (!useFullCaches)
                actualMapping = getMapping(trimmedSourceCache, trimmedTargetCache, spec);
            else
                actualMapping = getMapping(sC, tC, spec);
        } catch (java.lang.OutOfMemoryError e) {
            e.printStackTrace();
            return 8d;
        }

        // compare actualMap to optimalMap
        double res = getMeasure(actualMapping, reference, crossProduct);
        if (res > 1d) {
            logger.info("Error Measure > 1: " + res + ". May want to normalize it?");
        }
        // get rid of Mapping
        actualMapping.getMap().clear();
        actualMapping = null;
        // this could happen
        if (Double.isNaN(res)) {// so we manually return a bad fitness value
            return 5d;
        }
        if (res >= 0)
            return Math.abs(1.0d - res);
        else {
            return Math.abs(res) + 1;
        }
    }

    public double calculateRawMeasure(IGPProgram p) {
        ProgramChromosome pc = p.getChromosome(0);
        AMapping actualMapping = MappingFactory.createDefaultMapping();
        Object[] args = {};
        LinkSpecification spec = (LinkSpecification) pc.getNode(0).execute_object(pc, 0, args);
        String expr = spec.getFilterExpression();
        if (expr.indexOf("falseProp") > -1) {
            return 0d;
        }
        try {
            if (!useFullCaches)
                actualMapping = getMapping(trimmedSourceCache, trimmedTargetCache, spec);
            else
                actualMapping = getMapping(sC, tC, spec);
        } catch (java.lang.OutOfMemoryError e) {
            e.printStackTrace();
            return 0d;
        }

        // compare actualMap to optimalMap
        return getMeasure(actualMapping, reference, crossProduct);
    }

    /**
     * Return either recall, precision of (default) f-score of the given
     * mappings.
     *
     * @param a_mapping
     *            Mapping to be analyzed.
     * @param reference
     *            Reference mapping.
     * @return
     */
    private double getMeasure(AMapping a_mapping, AMapping reference, double crossProduct) {
        // These two statements are added by Mofeed to suite the change in
        // QMeasure's new structure
        GoldStandard goldStandard = new GoldStandard(reference);
        double quality = measure.calculate(a_mapping, goldStandard);
        // TODO check
        return quality;
    }

    /**
     * Get full caches used by this learner.
     *
     * @return HybridCache of the source endpoint.
     */
    public ACache getSourceCache() {
        return sC;
    }

    /**
     * Get full caches used by this learner.
     *
     * @return HybridCache of the target endpoint.
     */
    public ACache getTargetCache() {
        return tC;
    }

    public void destroy() {
        instance = null;
    }

    @Override
    public AMapping getMapping(ACache sourceCache, ACache targetCache, LinkSpecification spec) {
        try {

            ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache,
                    targetCache, this.m_config.source.getVar(), this.m_config.target.getVar(), 0, 1.0);
            IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sC, tC);
            return engine.execute(spec, planner);
        } catch (Exception e) {
            logger.error("Exception execution expression " + spec + " on Caches " + sourceCache.size() + ", "
                    + targetCache.size());
            return MappingFactory.createDefaultMapping();
        } catch (java.lang.OutOfMemoryError e) {
            logger.warn("Out of memory trying to get Map for expression\"" + spec + "\".");
            return MappingFactory.createDefaultMapping();
        }
    }

    /**
     * Method to scale down caches according to given training data.
     *
     * @param trainingData
     *            Mapping holding data instances a user has evaluated. That may
     *            include non-matches.
     */
    public void trimKnowledgeBases(AMapping trainingData) {
        trimmedSourceCache = sC;
        trimmedTargetCache = tC;
        if (trainingData.size() <= 0) {
            logger.info("Trying to scale down caches to " + trainingData.size()
                    + " reference mapping. Using full caches instead");
            trimmedSourceCache = sC;
            trimmedTargetCache = tC;
        }
        this.trainingData = trainingData;
        ACache[] trimmed = CacheTrimmer.processData(sC, tC, trainingData);
        if (trimmed[0].size() > 0)
            trimmedSourceCache = trimmed[0];
        else
            logger.info(
                    "Scaling down source cache returned empty cache. Wrong training data was set. Using full Cache instead");
        if (trimmed[1].size() > 0)
            trimmedTargetCache = trimmed[1];
        else
            logger.info(
                    "Scaling down target cache returned empty cache. Wrong training data was set. Using full Cache instead");
        logger.info("Trimming to " + trimmed[0].size() + " and " + trimmed[1].size() + " caches.");
        crossProduct = trimmedSourceCache.size() * trimmedTargetCache.size();
    }

    /**
     * If we want to use Full Caches instead.
     *
     * @param value
     */
    public void setUseFullCaches(boolean value) {
        this.useFullCaches = value;
    }

    public LinkSpecification getMetric(IGPProgram p) {
        Object[] args = {};
        ProgramChromosome pc = p.getChromosome(0);
        return (LinkSpecification) pc.getNode(0).execute_object(pc, 0, args);
    }

    public AMapping getReferenceMapping() {
        return reference;
    }

    /**
     * As we assume referenceData only holding matches. Learner may have to set
     * it separately.
     *
     * @param referenceData
     *            A Mapping holding all matches.
     */
    public void setReferenceMapping(AMapping referenceData) {
        reference = referenceData;
    }

    /**
     * Method to add instances to reference?.
     *
     * @param m
     *            Mapping of matches, designated as such by an oracle.
     */
    public void addToReference(AMapping m) {
        logger.info("Filling reference of size " + reference.size() + " with " + m.size() + " additional matches.");
        for (Entry<String, HashMap<String, Double>> e1 : m.getMap().entrySet()) {
            for (Entry<String, Double> e2 : e1.getValue().entrySet()) {
                reference.add(e1.getKey(), e2.getKey(), 1d);
            }
        }
        logger.info("Reference has now " + reference.size() + " Matches.");
    }

    public void fillCachesIncrementally(AMapping matches) {
        for (String sUri : matches.getMap().keySet())
            for (String tUri : matches.getMap().get(sUri).keySet()) {
                if (!trimmedSourceCache.containsUri(sUri)) {
                    logger.info("Adding instance " + sUri + " to sC");
                    if (sC.containsUri(sUri))
                        trimmedSourceCache.addInstance(sC.getInstance(sUri));
                }
                if (!trimmedTargetCache.containsUri(tUri)) {
                    logger.info("Adding instance " + tUri + " to tC");
                    if (tC.containsUri(tUri))
                        trimmedTargetCache.addInstance(tC.getInstance(tUri));
                }
            }
        // engine =
        // ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT,
        // trimmedSourceCache, trimmedTargetCache,
        // this.m_config.source.getVar(), this.m_config.target.getVar());
        crossProduct = trimmedSourceCache.size() * trimmedTargetCache.size();
    }

    public void setCaches(ACache sC, ACache tC) {
        this.sC = sC;
        this.tC = tC;
    }
}
