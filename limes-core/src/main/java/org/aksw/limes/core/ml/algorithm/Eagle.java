package org.aksw.limes.core.ml.algorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.IQualitativeMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.NoSuchParameterException;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.eagle.core.ALDecider;
import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionFitnessFunction;
import org.aksw.limes.core.ml.algorithm.eagle.core.PseudoFMeasureFitnessFunction;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.setting.LearningParameter;
import org.aksw.limes.core.ml.algorithm.eagle.util.TerminationCriteria;
import org.apache.log4j.Logger;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.GPPopulation;
import org.jgap.gp.impl.ProgramChromosome;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Eagle extends ACoreMLAlgorithm {
	
	//======================= COMMON VARIABLES ======================
    private IGPProgram allBest = null;
    private ExpressionFitnessFunction fitness;
    private GPGenotype gp;
	
	//================ SUPERVISED-LEARNING VARIABLES ================
    private int turn = 0;
    private List<IGPProgram> bestSolutions = new LinkedList<IGPProgram>();
    private ALDecider alDecider = new ALDecider();

    //=============== UNSUPERVISED-LEARNING VARIABLES ===============
    private List<LinkSpecification> specifications;

    //======================= PARAMETER NAMES =======================
	
    protected static final String ALGORITHM_NAME = "Eagle";
    
    protected static final String GENERATIONS = "generations";
    int generations = 10;
    protected static final String PRESERVE_FITTEST = "preserve_fittest";
    boolean preserveFittest = true;
    protected static final String MAX_DURATION = "max_duration";
    protected long maxDuration = 60;
    protected static final String INQUIRY_SIZE = "inquiry_size";
    protected static final String MAX_ITERATIONS = "max_iterations";
    protected static final String MAX_QUALITY = "max_quality";
    protected static final String TERMINATION_CRITERIA = "termination_criteria";
    protected static final String TERMINATION_CRITERIA_VALUE = "termination_criteria_value";
    protected static final String BETA = "beta";
    protected static final String POPULATION = "population";
    protected static final String MUTATION_RATE = "mutation_rate";
    protected static final String REPRODUCTION_RATE = "reproduction_rate";
    protected static final String CROSSOVER_RATE = "crossover_rate";
    protected static final String GAMMA_SCORE = "gamma_score";
    protected static final String EXPANSION_PENALTY = "expansion_penalty";
    protected static final String REWARD = "reward";
    protected static final String PRUNE = "prune";

    protected static final String MEASURE = "measure";
    protected static final String PROPERTY_MAPPING = "property_mapping";
    
    
    
    
    /**
     * maximal duration in seconds
     */
    int inquerySize = 10;
    /**
     * maximal number of iterations
     */
    int maxIteration = 500;
    /**
     * maximal quality in F-Measure // Pseudo-F. The implementing ML algorithm should it interpret as wished.
     */
    double maxQuality = 0.5;
    TerminationCriteria terminationCriteria = TerminationCriteria.iteration;
    double terminationCriteriaValue = 0;
    /**
     * beta for (pseudo) F-Measure
     */
    double beta = 1.0;
    // - EAGLE parameters
    int population = 20;
    float mutationRate = 0.4f;
    float reproductionRate = 0.4f;
    float crossoverRate = 0.3f;
    // supervised
    //LION parameters
    double gammaScore = 0.15d;
    /**
     * Expansion penalty
     */
    double expansionPenalty = 0.7d;
    /**
     * reward for better then parent
     */
    double reward = 1.2;
    /**
     * switch pruning on /off
     */
    boolean prune = true;
 
    IQualitativeMeasure measure = new FMeasure();
    PropertyMapping propMap = new PropertyMapping();
    
    // ========================================================================
    
    
    protected static Logger logger = Logger.getLogger(Eagle.class);
    
    protected Eagle() {
        //
    }

    @Override
    protected String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    protected void init(List<LearningParameter> lp, Cache source, Cache target) {
        super.init(lp, source, target);
        this.parameters = lp;
    }
    
    @Override
    protected MLResults learn(AMapping trainingData) {
    	
        turn++;
        fitness.addToReference(extractPositiveMatches(trainingData));
        fitness.fillCachesIncrementally(trainingData);

        Integer nGen = (Integer) getParameter(GENERATIONS);
        
        for (int gen = 1; gen <= nGen; gen++) {
            gp.evolve();
            bestSolutions.add(determineFittest(gp, gen));
        }

        MLResults result = createResult();
        return result;
        
    }

    @Override
    protected MLResults learn(PseudoFMeasure pfm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AMapping predict(Cache source, Cache target, MLResults mlModel) {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        
    }

    @Override
    protected MLResults activeLearn() throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }
    
    //====================== SPECIFIC METHODS =======================
    
    /**
     * Returns only positive matches, that are those with a confidence higher then 0.
     *
     * @param trainingData
     * @return
     */
    private AMapping extractPositiveMatches(AMapping trainingData) {
        AMapping positives = MappingFactory.createDefaultMapping();
        for (String sUri : trainingData.getMap().keySet())
            for (String tUri : trainingData.getMap().get(sUri).keySet()) {
                double confidence = trainingData.getConfidence(sUri, tUri);
                if (confidence > 0)
                    positives.add(sUri, tUri, confidence);
            }
        return positives;
    }

    /**
     * Method to compute best individuals by hand.
     *
     * @param gp
     * @param gen
     * @return
     */
    private IGPProgram determineFittest(GPGenotype gp, int gen) {

        GPPopulation pop = gp.getGPPopulation();
        pop.sortByFitness();

        IGPProgram bests[] = {gp.getFittestProgramComputed(), pop.determineFittestProgram(),
                // gp.getAllTimeBest(),
                pop.getGPProgram(0),};
        IGPProgram bestHere = null;
        double fittest = Double.MAX_VALUE;

        for (IGPProgram p : bests) {
            if (p != null) {
                double fitM = fitness.calculateRawFitness(p);
                if (fitM < fittest) {
                    fittest = fitM;
                    bestHere = p;
                }
            }
        }
        /* consider population if necessary */
        if (bestHere == null) {
            logger.debug("Determining best program failed, consider the whole population");
            System.err.println("Determining best program failed, consider the whole population");
            for (IGPProgram p : pop.getGPPrograms()) {
                if (p != null) {
                    double fitM = fitness.calculateRawFitness(p);
                    if (fitM < fittest) {
                        fittest = fitM;
                        bestHere = p;
                    }
                }
            }
        }

        if ((Boolean) getParameter(PRESERVE_FITTEST)) {
            if (allBest == null || fitness.calculateRawFitness(allBest) > fittest) {
                allBest = bestHere;
                logger.info("Generation " + gen + " new fittest (" + fittest + ") individual: " + getLinkSpecification(bestHere));
            }
        }

        return bestHere;
    }

    /**
     * Computes for a given jgap Program its corresponding link specification.
     *
     * @param p
     * @return
     */
    private LinkSpecification getLinkSpecification(IGPProgram p) {
        Object[] args = {};
        ProgramChromosome pc = p.getChromosome(0);
        return (LinkSpecification) pc.getNode(0).execute_object(pc, 0, args);
    }

    private MLResults createResult() {
        MLResults result = new MLResults();
        result.setLinkSpecification(getLinkSpecification(allBest));
//		result.setMapping(fitness.getMapping(getLinkSpecification(allBest), true));
        result.setQuality(allBest.getFitnessValue());
        result.addDetail("specifiactions", bestSolutions);
        result.addDetail("controversyMatches", calculateOracleQuestions((Integer) getParameter(INQUIRY_SIZE)));
        return result;
    }

    private AMapping calculateOracleQuestions(int size) {
        // first get all Mappings for the current population
        logger.info("Getting mappings for output");
        GPPopulation pop = this.gp.getGPPopulation();
        pop.sortByFitness();
        HashSet<LinkSpecification> metrics = new HashSet<LinkSpecification>();
        List<AMapping> candidateMaps = new LinkedList<AMapping>();
        // and add the all time best

        metrics.add(getLinkSpecification(allBest));

        for (IGPProgram p : pop.getGPPrograms()) {
            LinkSpecification m = getLinkSpecification(p);
            if (m != null && !metrics.contains(m)) {
                //logger.info("Adding metric "+m);
                metrics.add(m);
            }
        }
        // fallback solution if we have too less candidates
        if (metrics.size() <= 1) {
            // TODO implement
            logger.error("Not implemented yet.");
        }

        // get mappings for all distinct metrics
        logger.info("Getting " + metrics.size() + " full mappings to determine controversy matches...");
        for (LinkSpecification m : metrics) {
            candidateMaps.add(fitness.getMapping(m, true));
        }
        // get most controversy matches
        logger.info("Getting " + size + " controversy match candidates from " + candidateMaps.size() + " maps...");
        ;
        List<ALDecider.Triple> controversyMatches = alDecider.getControversyCandidates(candidateMaps, size);
        // construct answer
        AMapping answer = MappingFactory.createDefaultMapping();
        for (ALDecider.Triple t : controversyMatches) {
            answer.add(t.getSourceUri(), t.getTargetUri(), t.getSimilarity());
        }
        return answer;
    }


}
