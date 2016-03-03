package org.aksw.limes.core.ml.algorithm;


import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionProblem;
import org.aksw.limes.core.ml.algorithm.eagle.core.LinkSpecGeneticLearnerConfig;
import org.aksw.limes.core.ml.algorithm.eagle.core.PseudoFMeasureFitnessFunction;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.aksw.limes.core.ml.setting.UnsupervisedLearningSetting;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.GPPopulation;
import org.jgap.gp.impl.ProgramChromosome;


/**
 * Basic implementation of the unsupervised EAGLE algorithm. Working with so called
 * pseudo F-Measure (PFM) it learns link specifications based on the genetic programming 
 * algorithm EAGLE whereas the fitness value of n individual (a link specification)
 * is computed according to its PFM.
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 */
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
	public void init(LearningSetting parameters, Mapping trainingData) throws InvalidConfigurationException {
		this.parameters = (UnsupervisedLearningSetting) parameters;
		setUp();
	}

	@Override
	public String getName() {
		return "EAGLE Unsupervised";
	}

	@Override
	public MLResult learn(Mapping trainingData) {
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
	public Mapping computePredictions() {
		if(allBest != null) {
			
		} else {
			logger.error("No link specification was learned yet. Returning empty Mapping.");
			return new MemoryMapping();
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
	 * @param gp
	 * @param gen
	 * @return
	 */
	private IGPProgram determinFittest(GPGenotype gp, int gen) {

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
		/* consider population if neccessary */
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
	 * Configures EAGLE.
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
	 * @return
	 */
	private MLResult createMLResult() {
		MLResult result = new MLResult();
		result.setLinkSpecification(getLinkSpecification(allBest));
		result.setMapping(fitness.calculateMapping(allBest));
		result.setQuality(allBest.getFitnessValue());
		result.addDetail("specifiactions", specifications);
		return result;
	}
	
	
	
	
	public static void main(String args[]) {
		String base = "/home/lyko/svn/LIMES/";
		String configFile = "../../../svn/LIMES/Examples/GeneticEval/PublicationData.xml";
		XMLConfigurationReader reader = new XMLConfigurationReader();
		Configuration config = reader.read(configFile);
		config.getSourceInfo().setEndpoint(base+config.getSourceInfo().getEndpoint());
		config.getTargetInfo().setEndpoint(base+config.getTargetInfo().getEndpoint());
		
		
		Cache sC = HybridCache.getData(config.getSourceInfo());
		
		HybridCache tC = HybridCache.getData(config.getTargetInfo());
		
		EagleUnsupervised eagle = new EagleUnsupervised(sC,tC,config);
		
		UnsupervisedLearningSetting setting = new UnsupervisedLearningSetting(eagle);
		PropertyMapping propMap = new PropertyMapping();
		propMap.addStringPropertyMatch("title", "title");
		propMap.addStringPropertyMatch("authors", "authors");
		propMap.addStringPropertyMatch("venue", "venue");
		propMap.addNumberPropertyMatch("year", "year");
		setting.setPropMap(propMap);
		
		setting.setGenerations(3);
		setting.setPopulation(10);
		
		try {
			eagle.init(setting, new MemoryMapping());
			MLResult result = eagle.learn(new MemoryMapping());
			System.out.println(result);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
