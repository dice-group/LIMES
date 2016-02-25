package org.aksw.limes.core.ml.algorithm;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.ml.algorithm.eagle.core.ALDecider;
import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionFitnessFunction;
import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionProblem;
import org.aksw.limes.core.ml.algorithm.eagle.core.LinkSpecGeneticLearnerConfig;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.GPPopulation;
import org.jgap.gp.impl.ProgramChromosome;


public class EagleSupervised extends MLAlgorithm{
	int turn = 0; 
	List<IGPProgram> bestSolutions = new LinkedList<IGPProgram>();
	IGPProgram allBest = null;
	LearningSetting parameters;
	ExpressionFitnessFunction fitness;
	GPGenotype gp;
	ALDecider alDecider = new ALDecider();
	
	public EagleSupervised(Cache sourceCache, Cache targetCache,
			Configuration config) {
		super(sourceCache, targetCache, config);
	}

	@Override
	public String getName() {
		return "Eagle Supervised";
	}

	@Override
	public MLResult learn(Mapping trainingData) {
		turn++;
		fitness.addToReference(extractPositiveMatches(trainingData));
		fitness.fillCachesIncrementally(trainingData);
		
		for(int gen=1; gen<=parameters.getGenerations(); gen++) {
			gp.evolve();
			bestSolutions.add(determineFittest(gp, gen));
		}
		
		MLResult result = createResult();
		return result;
	}

	@Override
	public Mapping computePredictions() {
		if(allBest != null)
			return fitness.getMapping(getLinkSpecification(allBest), true);
		logger.error("No link specification calculated so far.");
		return new MemoryMapping();
	}

	@Override
	public void init(LearningSetting parameters, Mapping trainingData) throws Exception {
		this.parameters = parameters;
		turn = 0;
		bestSolutions = new LinkedList<IGPProgram>();
		setUp(trainingData);
	}
	
	@Override
	public void terminate() {
		fitness.destroy();
		fitness = null;
		allBest = null;
		bestSolutions.clear();
	}
	
	/**
	 * Configures EAGLE.
	 * @throws InvalidConfigurationException
	 */
	private void setUp(Mapping trainingData) throws InvalidConfigurationException {
		LinkSpecGeneticLearnerConfig jgapConfig = new LinkSpecGeneticLearnerConfig(getConfiguration().getSourceInfo(), getConfiguration().getTargetInfo(), parameters.getPropMap());
		
		jgapConfig.setPopulationSize(parameters.getPopulation());
		jgapConfig.setCrossoverProb(parameters.getCrossoverRate());
		jgapConfig.setMutationProb(parameters.getMutationRate());
		jgapConfig.setPreservFittestIndividual(parameters.isPreserveFittest());
		jgapConfig.setReproductionProb(parameters.getReproductionRate());
		jgapConfig.setPropertyMapping(parameters.getPropMap());
		
		fitness = ExpressionFitnessFunction.getInstance(jgapConfig, parameters.getMeasure(), trainingData);

		jgapConfig.setFitnessFunction(fitness);
		
		GPProblem gpP;
		
		gpP = new ExpressionProblem(jgapConfig);
		gp = gpP.create();
	}
	
	/**
	 * Returns only positive matches, that are those with a confidence higher then 0. 
	 * @param trainingData
	 * @return
	 */
	private Mapping extractPositiveMatches(Mapping trainingData) {
		Mapping positives = new MemoryMapping();
		for(String sUri : trainingData.getMap().keySet())
			for(String tUri : trainingData.getMap().get(sUri).keySet()) {
				double confidence = trainingData.getConfidence(sUri, tUri);
				if(confidence>0)
					positives.add(sUri, tUri, confidence);
			}
		return positives;
	}
	
	/**
	 * Method to compute best individuals by hand.
	 * @param gp
	 * @param gen
	 * @return
	 */
	private IGPProgram determineFittest(GPGenotype gp, int gen) {

		GPPopulation pop = gp.getGPPopulation();
		pop.sortByFitness();

		IGPProgram bests[] = { gp.getFittestProgramComputed(), pop.determineFittestProgram(),
			// gp.getAllTimeBest(),
			pop.getGPProgram(0), };
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
		    logger.debug("Determing best program failed, consider the whole population");
		    System.err.println("Determing best program failed, consider the whole population");
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
		
		if(parameters.isPreserveFittest()) {
			if(allBest==null || fitness.calculateRawFitness(allBest)>fittest) {
				allBest = bestHere;
				logger.info("Generation "+gen+" new fittest ("+fittest+") individual: "+getLinkSpecification(bestHere));
			}
		}
		
		return bestHere;
	}

	/**
	 * Computes for a given jgap Program its corresponding link specification.
	 * @param p
	 * @return
	 */
	private LinkSpecification getLinkSpecification(IGPProgram p) {
		Object[] args = {};
		ProgramChromosome pc = p.getChromosome(0);
		return (LinkSpecification) pc.getNode(0).execute_object(pc, 0, args);
	}
	
	private MLResult createResult() {
		MLResult result = new MLResult();
		result.setLinkSpecification(getLinkSpecification(allBest));
		result.setMapping(fitness.getMapping(getLinkSpecification(allBest), true));
		result.setQuality(allBest.getFitnessValue());
		result.addDetail("specifiactions", bestSolutions);
		result.addDetail("controversyMatches", calculateOracleQuestions(parameters.getInquerySize()));
		return result;
	}
	
	private Mapping calculateOracleQuestions(int size) {
		// first get all Mappings for the current population
        logger.info("Getting mappings for output");
        GPPopulation pop = this.gp.getGPPopulation();
        pop.sortByFitness();
        HashSet<LinkSpecification> metrics = new HashSet<LinkSpecification>();
        List<Mapping> candidateMaps = new LinkedList<Mapping>();
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
        logger.info("Getting " + size + " controversy match candidates from " + candidateMaps.size() + " maps...");;
        List<org.aksw.limes.core.ml.algorithm.eagle.core.ALDecider.Triple> controversyMatches = alDecider.getControversyCandidates(candidateMaps, size);
        // construct answer
        Mapping answer = new MemoryMapping();
        for (org.aksw.limes.core.ml.algorithm.eagle.core.ALDecider.Triple t : controversyMatches) {
            answer.add(t.getSourceUri(), t.getTargetUri(), t.getSimilarity());
        }
        return answer;
	}
	
}
