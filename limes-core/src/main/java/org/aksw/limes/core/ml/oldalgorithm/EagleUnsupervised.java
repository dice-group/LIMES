package org.aksw.limes.core.ml.oldalgorithm;


import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionProblem;
import org.aksw.limes.core.ml.algorithm.eagle.core.LinkSpecGeneticLearnerConfig;
import org.aksw.limes.core.ml.algorithm.eagle.core.PseudoFMeasureFitnessFunction;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.aksw.limes.core.ml.setting.UnsupervisedLearningSetting;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.GPPopulation;
import org.jgap.gp.impl.ProgramChromosome;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Basic implementation of the unsupervised EAGLE algorithm. Working with so called
 * pseudo F-Measure (PFM) it learns link specifications based on the genetic programming
 * algorithm EAGLE whereas the fitness value of n individual (a link specification)
 * is computed according to its PFM.
 *
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @author Klaus Lyko
 */
@Deprecated
public class EagleUnsupervised extends MLAlgorithm {

    UnsupervisedLearningSetting parameters;

    PseudoFMeasureFitnessFunction fitness;
    GPGenotype gp;

    IGPProgram allBest = null;
    List<LinkSpecification> specifications;

    public EagleUnsupervised(Cache sourceCache, Cache targetCache,
                             Configuration config) {
        super(sourceCache, targetCache, config);
    }

    @Override
    public void init(LearningSetting parameters, AMapping trainingData) throws InvalidConfigurationException {
        this.parameters = (UnsupervisedLearningSetting) parameters;
        setUp();
    }

    @Override
    public String getName() {
        return "EAGLE Unsupervised";
    }

    @Override
    public MLModel learn(AMapping trainingData) {
        specifications = new LinkedList<LinkSpecification>();
        logger.info("Start learning");
        for (int gen = 1; gen <= parameters.getGenerations(); gen++) {
            gp.evolve();
            IGPProgram currentBest = determinFittest(gp, gen);
            LinkSpecification currentBestMetric = getLinkSpecification(currentBest);
            //TODO: save the best LS of each generation
            specifications.add(currentBestMetric);
        }

        allBest = determinFittest(gp, parameters.getGenerations());
        return createMLResult();
    }

    @Override
    public AMapping computePredictions() {
        if (allBest != null) {

        } else {
            logger.error("No link specification was learned yet. Returning empty Mapping.");
            return MappingFactory.createDefaultMapping();
        }
        return fitness.calculateMapping(allBest);
    }

    @Override
    public void terminate() {
        fitness.destroy();
        fitness = null;
    }

    /**
     * Method to compute best individuals by hand.
     *
     * @param gp
     * @param gen
     * @return
     */
    private IGPProgram determinFittest(GPGenotype gp, int gen) {

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

        if (parameters.isPreserveFittest()) {
            if (allBest == null || fitness.calculateRawFitness(allBest) > fittest) {
                allBest = bestHere;
                logger.info("Generation " + gen + " new fittest (" + fittest + ") individual: " + getLinkSpecification(bestHere));
            }
        }

        return bestHere;
    }

    /**
     * Configures EAGLE.
     *
     * @throws InvalidConfigurationException
     */
    private void setUp() throws InvalidConfigurationException {
        LinkSpecGeneticLearnerConfig jgapConfig = new LinkSpecGeneticLearnerConfig(getConfiguration().getSourceInfo(), getConfiguration().getTargetInfo(), parameters.getPropMap());

        jgapConfig.setPopulationSize(parameters.getPopulation());
        jgapConfig.setCrossoverProb(parameters.getCrossoverRate());
        jgapConfig.setMutationProb(parameters.getMutationRate());
        jgapConfig.setPreservFittestIndividual(parameters.isPreserveFittest());
        jgapConfig.setReproductionProb(parameters.getReproductionRate());
        jgapConfig.setPropertyMapping(parameters.getPropMap());

        fitness = PseudoFMeasureFitnessFunction.getInstance(jgapConfig, parameters.getPseudoMeasure(), getSourceCache(), getTargetCache());
//		fitness.setBeta(parameters.getBeta());
        jgapConfig.setFitnessFunction(fitness);

        GPProblem gpP;

        gpP = new ExpressionProblem(jgapConfig);
        gp = gpP.create();
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

    /**
     * Constructs the MLResult for this run.
     *
     * @return
     */
    private MLModel createMLResult() {
        MLModel result = new MLModel();
        result.setLinkSpecification(getLinkSpecification(allBest));
//		result.setMapping(fitness.calculateMapping(allBest));
        result.setQuality(allBest.getFitnessValue());
        result.addDetail("specifiactions", specifications);
        return result;
    }

    @Override
    public Set<String> parameters() {
        // TODO Auto-generated method stub
        return null;
    }


}
