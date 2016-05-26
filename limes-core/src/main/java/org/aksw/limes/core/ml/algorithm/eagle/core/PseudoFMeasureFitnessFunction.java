package org.aksw.limes.core.ml.algorithm.eagle.core;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFM;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.apache.log4j.Logger;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.ProgramChromosome;


/**
 * Fitness function to evolve metric expression using a PseudoMeasue
 *
 * @author Lyko
 */
public class PseudoFMeasureFitnessFunction extends GPFitnessFunction implements IFitnessFunction {

    /**
     *
     */
    private static final long serialVersionUID = -7114137172832439294L;
    static Logger logger = Logger.getLogger("LIMES");
    private static PseudoFMeasureFitnessFunction instance = null;
    public ExecutionEngine engine;
    Cache sourceCache, targetCache;
    LinkSpecGeneticLearnerConfig config;
    double beta = 1.0d;
    PseudoFM pfm = new PseudoFM();


    private PseudoFMeasureFitnessFunction(LinkSpecGeneticLearnerConfig a_config, PseudoFM pfm, Cache c1, Cache c2) {
        config = a_config;
        sourceCache = c1;
        targetCache = c2;
        this.pfm = pfm;
        engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, c1, c2, a_config.source.getVar(), a_config.target.getVar());
    }

    /**
     * Singleton pattern
     */
    public static PseudoFMeasureFitnessFunction getInstance(LinkSpecGeneticLearnerConfig a_config, PseudoFM pfm, Cache c1, Cache c2) {
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
     * @return 1-PseudoFMeasure. Or if something wents wrong either 5d, iff p isn't fulfilling all constraints. 8d if executing p results in memory error.
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
        logger.info("ls = " + spec);
        try {
            actualMapping = getMapping(spec);
        } catch (java.lang.OutOfMemoryError e) {
            e.printStackTrace(); // should not happen
            System.err.println(e.getMessage());
            return MappingFactory.createDefaultMapping();
        }
        return actualMapping;
    }

    public Double calculatePseudoMeasure(IGPProgram p) {
        return pfm.getPseudoFMeasure(sourceCache.getAllUris(), targetCache.getAllUris(), calculateMapping(p), beta);
    }

    /**
     * Executes metric to get mapping for given metric.
     *
     * @param metric
     *         Metric String.
     * @param threshold
     *         Acceptance threshold: 0<=threshold<=1.
     * @return Mapping m={sURI, tURI} of all pairs who satisfy the metric.
     */
    public AMapping getMapping(LinkSpecification spec) {
        try {
            IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT,
                    sourceCache, targetCache);
            return engine.execute(spec, planner);
        } catch (Exception e) {
            e.printStackTrace();
            String out = "Error getMapping() in PFM (" + config.source.getId() + " - " + config.target.getId() + ") with metric: " + spec + " \n" + e.getMessage();
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

    public PseudoFM getMeasure() {
        return pfm;
    }

    public void setMeasure(PseudoFM pfm) {
        this.pfm = pfm;
    }

    public double getBeta() {
        return this.beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    @Override
    public AMapping getMapping(LinkSpecification spec, boolean full) {
        return getMapping(spec);
    }

    public double calculateRawMeasure(IGPProgram p) {
        return calculatePseudoMeasure(p);
    }


//	public void addPropertyChangeListener(PropertyChangeListener l) {
//		changes.addPropertyChangeListener(l);
//	}
//	
//	public void removePropertyChangeListener(PropertyChangeListener l) {
//		changes.removePropertyChangeListener(l);
//	}
}
