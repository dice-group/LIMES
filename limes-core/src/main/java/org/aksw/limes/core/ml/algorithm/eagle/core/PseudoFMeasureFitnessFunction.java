package org.aksw.limes.core.ml.algorithm.eagle.core;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.ProgramChromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fitness function to evolve metric expression using a PseudoMeasue
 *
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class PseudoFMeasureFitnessFunction extends IGPFitnessFunction {

    /**
     *	
     */
    private static final long serialVersionUID = -7114137172832439294L;
    static Logger logger = LoggerFactory.getLogger("LIMES");
    private static PseudoFMeasureFitnessFunction instance = null;
    public ExecutionEngine engine;
    ACache sourceCache, targetCache;
    LinkSpecGeneticLearnerConfig config;
    double beta = 1.0d;

    PseudoFMeasure pfm;

    private PseudoFMeasureFitnessFunction(LinkSpecGeneticLearnerConfig a_config, PseudoFMeasure pfm, ACache c1,
            ACache c2) {
        config = a_config;
        sourceCache = c1;
        targetCache = c2;
        this.pfm = pfm;
        engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, c1, c2, a_config.source.getVar(),
                a_config.target.getVar(), 0, 1.0);
    }

    /**
     * Singleton pattern
     * 
     * @param a_config
     *            LinkSpecGeneticLearnerConfig
     * @param pfm
     *            PseudoFMeasure
     * @param c1
     *            Cache
     * @param c2
     *            Cache
     * @return instance of PseudoFMeasure Fitness Function
     */
    public static PseudoFMeasureFitnessFunction getInstance(LinkSpecGeneticLearnerConfig a_config, PseudoFMeasure pfm,
            ACache c1, ACache c2) {
        if (instance == null) {
            return instance = new PseudoFMeasureFitnessFunction(a_config, pfm, c1, c2);
        } else {
            return instance;
        }
    }

    @Override
    protected double evaluate(IGPProgram program) {
        return calculateRawFitness(program);
    }

    /**
     * Determine fitness of the individual p;
     *
     * @param p
     *            GP programs
     * @return 1-PseudoFMeasure. Or if something wents wrong either 5d, iff p
     *         isn't fulfilling all constraints. 8d if executing p results in
     *         memory error.
     */
    public double calculateRawFitness(IGPProgram p) {
        double pseudoFMeasure = calculatePseudoMeasure(p);
        if (!(pseudoFMeasure >= 0d && pseudoFMeasure <= 1d)) {
            Object[] args = {};
            ProgramChromosome pc = null;
            pc = p.getChromosome(0);
            LinkSpecification spec = (LinkSpecification) pc.getNode(0).execute_object(pc, 0, args);
            logger.info("LS: " + spec);
            logger.error("Pseudo Measure was not in [0,1]");
            System.out.println("Pseudo Measure for (" + spec + ") was not in [0,1]");
            System.err.println("Pseudo Measure for (" + spec + ") was not in [0,1]");
        }
        if (pseudoFMeasure >= 0)
            return Math.abs(1.0d - pseudoFMeasure);
        else {
            return Math.abs(pseudoFMeasure) + 1;
        }
    }

    public AMapping calculateMapping(IGPProgram p) {
        // execute individual
        Object[] args = {};
        ProgramChromosome pc = null;
        pc = p.getChromosome(0);
        AMapping actualMapping = MappingFactory.createDefaultMapping();
        LinkSpecification spec = (LinkSpecification) pc.getNode(0).execute_object(pc, 0, args);
        // get Mapping
        try {
            actualMapping = getMapping(sourceCache, targetCache, spec);
        } catch (java.lang.OutOfMemoryError e) {
            e.printStackTrace(); // should not happen

            return MappingFactory.createDefaultMapping();
        }
        return actualMapping;
    }

    /**
     * @param p
     *            GP programs
     * @return PseudoMeasure
     */
    public Double calculatePseudoMeasure(IGPProgram p) {
        // mapping
        AMapping mapping = calculateMapping(p);
        // gold standard is not needed by pseudoFM
        GoldStandard gold = new GoldStandard(mapping, sourceCache, targetCache);
        return pfm.calculate(mapping, gold, beta);
    }

    /**
     * Get or create a mapping from a link specification (Metric String +
     * Acceptance threshold: 0&lt;=threshold&lt;=1).
     * 
     * @param spec
     *            the link specification
     * @return Mapping m={sURI, tURI} of all pairs who satisfy the metric.
     */
    public AMapping getMapping(ACache sC, ACache tC, LinkSpecification spec) {
        try {
            IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sC, tC);
            return engine.execute(spec, planner);
        } catch (Exception e) {
            e.printStackTrace();
            String out = "Error getMapping() in PFM (" + config.source.getId() + " - " + config.target.getId()
                    + ") with metric: " + spec + " \n" + e.getMessage();
            System.err.println(out);
            logger.error(out);
            return MappingFactory.createDefaultMapping();
        }

    }

    /**
     * Needed between several runs
     */
    public void destroy() {
        instance = null;
    }

    public PseudoFMeasure getMeasure() {
        return pfm;
    }

    public void setMeasure(PseudoFMeasure pfm) {
        this.pfm = pfm;
    }

    public double getBeta() {
        return this.beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double calculateRawMeasure(IGPProgram p) {
        return calculatePseudoMeasure(p);
    }

    @Override
    public void addToReference(AMapping m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fillCachesIncrementally(AMapping matches) {
        throw new UnsupportedOperationException();
    }

    // public void addPropertyChangeListener(PropertyChangeListener l) {
    // changes.addPropertyChangeListener(l);
    // }
    //
    // public void removePropertyChangeListener(PropertyChangeListener l) {
    // changes.removePropertyChangeListener(l);
    // }
}
