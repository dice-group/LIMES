package org.aksw.limes.core.ml.algorithm;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.IQualitativeMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.NotYetImplementedException;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
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
import org.apache.log4j.Logger;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.GPPopulation;
import org.jgap.gp.impl.ProgramChromosome;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 *
 */
public class Eagle extends ACoreMLAlgorithm {
    
    //======================= COMMON VARIABLES ======================
    private IGPProgram allBest = null;
    private IGPFitnessFunction fitness;
    private GPGenotype gp;
    
    //================ SUPERVISED-LEARNING VARIABLES ================
    private int turn = 0;
    private List<IGPProgram> bestSolutions = new LinkedList<IGPProgram>();
    private ALDecider alDecider = new ALDecider();

    //=============== UNSUPERVISED-LEARNING VARIABLES ===============
    private List<LinkSpecification> specifications;

    //======================= PARAMETER NAMES =======================
    
    protected static final String ALGORITHM_NAME = "Eagle";
    
    public static final String GENERATIONS = "generations";
    public static final String PRESERVE_FITTEST = "preserve_fittest";
    public static final String MAX_DURATION = "max_duration";
    public static final String INQUIRY_SIZE = "inquiry_size";
    public static final String MAX_ITERATIONS = "max_iterations";
    public static final String MAX_QUALITY = "max_quality";
    public static final String TERMINATION_CRITERIA = "termination_criteria";
    public static final String TERMINATION_CRITERIA_VALUE = "termination_criteria_value";
    public static final String BETA = "beta";
    public static final String POPULATION = "population";
    public static final String MUTATION_RATE = "mutation_rate";
    public static final String REPRODUCTION_RATE = "reproduction_rate";
    public static final String CROSSOVER_RATE = "crossover_rate";
    public static final String PSEUDO_FMEASURE = "pseudo_fmeasure";

    public static final String MEASURE = "measure";
    public static final String PROPERTY_MAPPING = "property_mapping";
    
    
    // ========================================================================
    
    
    protected static Logger logger = Logger.getLogger(Eagle.class);
    
    /**
     * Eagle constructor.
     */
    protected Eagle() {
        super();
        setDefaultParameters();
    }

    @Override
    protected String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    protected void init(List<LearningParameter> lp, ACache source, ACache target) {
        super.init(lp, source, target);
        this.turn = 0;
        this.bestSolutions = new LinkedList<IGPProgram>();
    }
    
    @Override
    protected MLResults learn(AMapping trainingData) {
        
        try {
            setUp(trainingData);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
        
        turn++;
        fitness.addToReference(extractPositiveMatches(trainingData));
        fitness.fillCachesIncrementally(trainingData);

        Integer nGen = (Integer) getParameter(GENERATIONS);
        
        for (int gen = 1; gen <= nGen; gen++) {
            gp.evolve();
            bestSolutions.add(determineFittest(gp, gen));
        }

        MLResults result = createSupervisedResult();
        return result;
        
    }

    @Override
    protected MLResults learn(PseudoFMeasure pfm) {

        learningParameters.add(new LearningParameter(PSEUDO_FMEASURE, pfm, PseudoFMeasure.class, 
                Double.NaN, Double.NaN, Double.NaN, PSEUDO_FMEASURE));
        
        try {
            setUp(null);
        } catch (InvalidConfigurationException e) {
            logger.error(e.getMessage());
            return null;
        }
        
        Integer nGen = (Integer) getParameter(GENERATIONS);
        
        specifications = new LinkedList<LinkSpecification>();
        logger.info("Start learning");
        for (int gen = 1; gen <= nGen; gen++) {
            gp.evolve();
            IGPProgram currentBest = determineFittestUnsup(gp, gen);
            LinkSpecification currentBestMetric = getLinkSpecification(currentBest);
            //TODO: save the best LS of each generation
            specifications.add(currentBestMetric);
        }

        allBest = determineFittestUnsup(gp, nGen);
        return createUnsupervisedResult();
        
    }

    @Override
    protected AMapping predict(ACache source, ACache target, MLResults mlModel) {
//      if (allBest != null) {
            return fitness.getMapping(source, target, mlModel.getLinkSpecification());
//        } else {
//            logger.error("No link specification calculated so far.");
//          assert (allBest != null);
//        }     
//        return MappingFactory.createDefaultMapping();
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
        return mlType == MLImplementationType.SUPERVISED_BATCH || mlType == MLImplementationType.UNSUPERVISED || mlType == MLImplementationType.SUPERVISED_ACTIVE;
    }
    //*************** active learning implementation *****************************************
    @Override
    protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
//        throw new UnsupportedMLImplementationException(this.getName());
        return calculateOracleQuestions(size);
        
    }

    @Override
    protected MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        logger.info("EAGLE active learning started with "+oracleMapping.size()+" examples");
        return learn(oracleMapping);
    }
    
    @Override
    protected MLResults activeLearn() throws UnsupportedMLImplementationException {
        logger.info("Supposed to run an active EAGLE, but provided no oracle data. Running default unsupervised approach instead.");
        return learn(new PseudoFMeasure());     
    }

    @Override
    public void setDefaultParameters() {        
        learningParameters = new ArrayList<>();
        learningParameters.add(new LearningParameter(GENERATIONS, 20, Integer.class, 1, Integer.MAX_VALUE, 1, GENERATIONS));
        learningParameters.add(new LearningParameter(PRESERVE_FITTEST, true, Boolean.class, Double.NaN, Double.NaN, Double.NaN, PRESERVE_FITTEST));
        learningParameters.add(new LearningParameter(MAX_DURATION, 60, Long.class, 0, Long.MAX_VALUE, 1, MAX_DURATION));
        learningParameters.add(new LearningParameter(INQUIRY_SIZE, 10, Integer.class, 1, Integer.MAX_VALUE, 1, INQUIRY_SIZE));
        learningParameters.add(new LearningParameter(MAX_ITERATIONS, 500, Integer.class, 1, Integer.MAX_VALUE, 1, MAX_ITERATIONS));
        learningParameters.add(new LearningParameter(MAX_QUALITY, 0.5, Double.class, 0d, 1d, Double.NaN, MAX_QUALITY));
        learningParameters.add(new LearningParameter(TERMINATION_CRITERIA, TerminationCriteria.iteration, TerminationCriteria.class, Double.NaN, Double.NaN, Double.NaN, TERMINATION_CRITERIA));
        learningParameters.add(new LearningParameter(TERMINATION_CRITERIA_VALUE, 0.0, Double.class, 0d, Double.MAX_VALUE, Double.NaN, TERMINATION_CRITERIA_VALUE));
        learningParameters.add(new LearningParameter(BETA, 1.0, Double.class, 0d, 1d, Double.NaN, BETA));
        learningParameters.add(new LearningParameter(POPULATION, 20, Integer.class, 1, Integer.MAX_VALUE, 1, POPULATION));
        learningParameters.add(new LearningParameter(MUTATION_RATE, 0.4f, Float.class, 0f, 1f, Double.NaN, MUTATION_RATE));
        learningParameters.add(new LearningParameter(REPRODUCTION_RATE, 0.4f, Float.class, 0f, 1f, Double.NaN, REPRODUCTION_RATE));
        learningParameters.add(new LearningParameter(CROSSOVER_RATE, 0.3f, Float.class, 0f, 1f, Double.NaN, CROSSOVER_RATE));
        learningParameters.add(new LearningParameter(MEASURE, new FMeasure(), IQualitativeMeasure.class, Double.NaN, Double.NaN, Double.NaN, MEASURE));
        learningParameters.add(new LearningParameter(PSEUDO_FMEASURE, new PseudoFMeasure(), IQualitativeMeasure.class, Double.NaN, Double.NaN, Double.NaN, MEASURE));
        learningParameters.add(new LearningParameter(PROPERTY_MAPPING, new PropertyMapping(), PropertyMapping.class, Double.NaN, Double.NaN, Double.NaN, PROPERTY_MAPPING));        
    }


    
    //====================== SPECIFIC METHODS =======================
    
    /**
     * Configures EAGLE.
     *
     * @param trainingData training data
     * @throws InvalidConfigurationException
     */
    private void setUp(AMapping trainingData) throws InvalidConfigurationException {
        PropertyMapping pm = (PropertyMapping) getParameter(PROPERTY_MAPPING);
        if(!pm.wasSet()) {
            pm.setDefault(configuration.getSourceInfo(), configuration.getTargetInfo());
        }
        LinkSpecGeneticLearnerConfig jgapConfig = new LinkSpecGeneticLearnerConfig(configuration.getSourceInfo(), configuration.getTargetInfo(), pm);

        jgapConfig.sC = sourceCache;
        jgapConfig.tC = targetCache;
        
        jgapConfig.setPopulationSize((Integer) getParameter(POPULATION));
        jgapConfig.setCrossoverProb((Float) getParameter(CROSSOVER_RATE));
        jgapConfig.setMutationProb((Float) getParameter(MUTATION_RATE));
        jgapConfig.setPreservFittestIndividual((Boolean) getParameter(PRESERVE_FITTEST));
        jgapConfig.setReproductionProb((Float) getParameter(REPRODUCTION_RATE));
        jgapConfig.setPropertyMapping(pm);

        if(trainingData != null) { // supervised
            
            FMeasure fm = (FMeasure) getParameter(MEASURE);
            fitness = ExpressionFitnessFunction.getInstance(jgapConfig, fm, trainingData);
            org.jgap.Configuration.reset();
            jgapConfig.setFitnessFunction(fitness);
            
        } else { // unsupervised
            
            PseudoFMeasure pfm = (PseudoFMeasure) getParameter(PSEUDO_FMEASURE);
            fitness = PseudoFMeasureFitnessFunction.getInstance(jgapConfig, pfm, sourceCache, targetCache);
            org.jgap.Configuration.reset();
            jgapConfig.setFitnessFunction(fitness);
            
        }
        

        GPProblem gpP;

        gpP = new ExpressionProblem(jgapConfig);
        gp = gpP.create();
    }


    /**
     * Returns only positive matches, that are those with a confidence higher then 0.
     *
     * @param trainingData training data
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
     * @param gp GP genotype
     * @param gen number of generations
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
        // remember the best
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
     * @param p the GP program
     * @return the link specification
     */
    private LinkSpecification getLinkSpecification(IGPProgram p) {
        Object[] args = {};
        ProgramChromosome pc = p.getChromosome(0);
        return (LinkSpecification) pc.getNode(0).execute_object(pc, 0, args);
    }

    /**
     * @return wrap with results
     */
    private MLResults createSupervisedResult() {
        MLResults result = new MLResults();
        result.setMapping(fitness.getMapping(sourceCache, targetCache, getLinkSpecification(allBest)));
        result.setLinkSpecification(getLinkSpecification(allBest));
        result.setQuality(allBest.getFitnessValue());
        result.addDetail("specifiactions", bestSolutions);
        return result;
    }
    
    /**
     * Constructs the MLResult for this run.
     *
     * @return wrap with results
     */
    private MLResults createUnsupervisedResult() {
        MLResults result = new MLResults();
        result.setMapping(fitness.getMapping(sourceCache, targetCache, getLinkSpecification(allBest)));
        result.setLinkSpecification(getLinkSpecification(allBest));
        result.setQuality(allBest.getFitnessValue());
        result.addDetail("specifiactions", specifications);
        return result;
    }


    /**
     * @param size number of questions
     * @return the mapping
     */
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
            throw new NotYetImplementedException("Fallback solution if we have too less candidates.");
        }

        // get mappings for all distinct metrics
        logger.info("Getting " + metrics.size() + " full mappings to determine controversy matches...");
        for (LinkSpecification m : metrics) {
            candidateMaps.add(fitness.getMapping(sourceCache, targetCache, m));
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
    
    
    /**
     * Method to compute best individuals by hand.
     *
     * @param gp GP genotype
     * @param gen number of generations
     * @return the GP program
     */
    private IGPProgram determineFittestUnsup(GPGenotype gp, int gen) {

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
        /* consider population if neccessary */
        if (bestHere == null) {
            logger.debug("Determining best program failed, consider the whole population.");
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
     * @return current turn
     */
    public int getTurn() {
        return turn;
    }

}