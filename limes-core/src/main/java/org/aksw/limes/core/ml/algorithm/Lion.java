package org.aksw.limes.core.ml.algorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.IQualitativeMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFM;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.NotYetImplementedException;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.Planner;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.eagle.core.ALDecider;
import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionFitnessFunction;
import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionProblem;
import org.aksw.limes.core.ml.algorithm.eagle.core.IGPFitnessFunction;
import org.aksw.limes.core.ml.algorithm.eagle.core.LinkSpecGeneticLearnerConfig;
import org.aksw.limes.core.ml.algorithm.eagle.core.PseudoFMeasureFitnessFunction;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.algorithm.eagle.util.TerminationCriteria;
import org.aksw.limes.core.ml.algorithm.lion.DefaultRefinementHeuristic;
import org.aksw.limes.core.ml.algorithm.lion.RefinementHeuristic;
import org.aksw.limes.core.ml.algorithm.lion.SearchTreeNode;
import org.aksw.limes.core.ml.algorithm.lion.operator.LengthLimitedRefinementOperator;
import org.aksw.limes.core.ml.algorithm.lion.operator.UpwardLengthLimitRefinementOperator;
import org.aksw.limes.core.ml.setting.LearningParameter;
import org.apache.log4j.Logger;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.GPPopulation;
import org.jgap.gp.impl.ProgramChromosome;
import org.slf4j.LoggerFactory;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 *
 */
public class Lion extends ACoreMLAlgorithm {
	
	//==========================  VARIABLES =========================

    // TODO either move to setting or finalize strategy
    public static boolean hardRootExpansion = true;
    protected static int globalTime = 1000;
    // bMofeed
    protected static int IdCounter = 0;
    /* for experiments */
    protected static boolean debuggingInput = false;
    public boolean advancedThreholdSearch = false;
    // eMofeed
    public int thresholdGrid = 3;
    protected LengthLimitedRefinementOperator operator;
    // all nodes in the search tree (used for selecting most promising node)
    protected TreeSet<SearchTreeNode> nodes;
    protected RefinementHeuristic heuristic;
    // redundancy
    // root of search tree
    protected SearchTreeNode startNode; // it is null
    protected HashSet<LinkSpecification> specs; // m:note- here check the
    protected Planner planner;
    protected ExecutionEngine engine;
    protected PseudoFMeasure pfm;
    protected List<Integer> loopsRootExpanded = new LinkedList<Integer>();
    /**
     * Best Score so far
     **/
    protected double bestScore = 0;
    /**
     * Link specification with highest PFM
     */
    protected SearchTreeNode best = null;
    protected boolean newBest = false; // needed to decide whether we log a new

    //======================= PARAMETER NAMES =======================
	
    protected static final String ALGORITHM_NAME = "Lion";
        
    public static final String TERMINATION_CRITERIA = "termination_criteria";
    public static final String TERMINATION_CRITERIA_VALUE = "termination_criteria_value";
    public static final String PROPERTY_MAPPING = "property_mapping";

    protected static final String GAMMA_SCORE = "gamma_score";
    protected static final String EXPANSION_PENALTY = "expansion_penalty";
    protected static final String REWARD = "reward";
    protected static final String PRUNE = "prune";
    
    // ========================================================================
    
    
    protected static Logger logger = Logger.getLogger(Lion.class);
    
    protected Lion() {
    	super();
    	setDefaultParameters();
        nodes = new TreeSet<SearchTreeNode>();
        heuristic = new DefaultRefinementHeuristic();
        operator = new UpwardLengthLimitRefinementOperator();
        operator.setConfiguration(getConfiguration());
        ExecutionPlannerType executionPlanerType = ExecutionPlannerFactory.getExecutionPlannerType(getConfiguration().getExecutionPlanner().toLowerCase());
        planner = ExecutionPlannerFactory.getPlanner(executionPlanerType, sourceCache, targetCache);
    }

    @Override
    protected String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    protected void init(List<LearningParameter> lp, Cache source, Cache target) {
        super.init(lp, source, target);
        this.loopsRootExpanded = new LinkedList<Integer>();
        
        pfm = new PseudoFMeasure();
        
//        heuristic.setLearningSetting(setting);
        heuristic.setLearningParameters(lp);
//        operator.setLearningSetting(setting);
        operator.setLearningParameters(lp);
        
        engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache,
                targetCache, this.getConfiguration().getSourceInfo().getVar(),
                this.getConfiguration().getTargetInfo().getVar());

    }
    
	@Override
    protected MLResults learn(AMapping trainingData) throws UnsupportedMLImplementationException {
		throw new UnsupportedMLImplementationException(this.getName());
    }

    @Override
    protected MLResults learn(PseudoFMeasure pfm) {

//    	parameters.add(new LearningParameter(PSEUDO_FMEASURE, pfm, PseudoFMeasure.class, 
//    			Double.NaN, Double.NaN, Double.NaN, PSEUDO_FMEASURE));
    	
		try {
			setUp(null);
		} catch (InvalidConfigurationException e) {
			logger.error(e.getMessage());
			return null;
		}
		
		// TODO
        
		
        return null;
        
    }

    @Override
    protected AMapping predict(Cache source, Cache target, MLResults mlModel) {
    	// TODO
//        if (allBest != null)
//            return fitness.getMapping(mlModel.getLinkSpecification(), true);
//        logger.error("No link specification calculated so far.");
        return MappingFactory.createDefaultMapping();
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
        return mlType == MLImplementationType.SUPERVISED_BATCH || mlType == MLImplementationType.UNSUPERVISED;
    }

    @Override
    protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

    @Override
    protected MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

    @Override
    public void setDefaultParameters() {
        
    	parameters.add(new LearningParameter(TERMINATION_CRITERIA, TerminationCriteria.duration, TerminationCriteria.class, Double.NaN, Double.NaN, Double.NaN, TERMINATION_CRITERIA));
    	parameters.add(new LearningParameter(TERMINATION_CRITERIA_VALUE, 10, Double.class, 0d, Double.MAX_VALUE, Double.NaN, TERMINATION_CRITERIA_VALUE));
    	parameters.add(new LearningParameter(PROPERTY_MAPPING, new PropertyMapping(), PropertyMapping.class, Double.NaN, Double.NaN, Double.NaN, PROPERTY_MAPPING));
    	
    	// LION parameters
    	parameters.add(new LearningParameter(GAMMA_SCORE, 0.15d, Double.class, 0d, Double.MAX_VALUE, Double.NaN, GAMMA_SCORE));
    	parameters.add(new LearningParameter(EXPANSION_PENALTY, 0.7d, Double.class, 0d, Double.MAX_VALUE, Double.NaN, EXPANSION_PENALTY));
    	parameters.add(new LearningParameter(REWARD, 1.2, Double.class, 0d, Double.MAX_VALUE, Double.NaN, REWARD));
    	parameters.add(new LearningParameter(PRUNE, true, Boolean.class, Double.NaN, Double.NaN, Double.NaN, PRUNE));
        
    }

    @Override
    protected MLResults activeLearn() throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }
    
    //====================== SPECIFIC METHODS =======================
    
    /**
     * Configures LION.
     * @throws InvalidConfigurationException 
     *
     */
    private void setUp(AMapping trainingData) throws InvalidConfigurationException {
    	
    	logger.info("Setting up LION...");
    	
    	// TODO

    }



}
