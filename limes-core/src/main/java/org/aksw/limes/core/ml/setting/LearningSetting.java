package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.evaluation.quality.FMeasure;
import org.aksw.limes.core.evaluation.quality.QualitativeMeasure;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.ml.algorithm.IMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 */
public abstract class LearningSetting {
	
	protected IMLAlgorithm algorithm;
	int inquerySize = 10;
	protected long maxDuration = 600;
	
	public long getMaxDuration() {
		return maxDuration;
	}

	public void setMaxDuration(long maxDuration) {
		this.maxDuration = maxDuration;
	}

	public int getInquerySize() {
		return inquerySize;
	}

	public void setInquerySize(int inquerySize) {
		this.inquerySize = inquerySize;
	}

	public LearningSetting(IMLAlgorithm algorithm) {
		super();
		this.algorithm = algorithm;
	}

	public abstract void learn();

	public IMLAlgorithm getAlgorithm() {
		return algorithm;
	}
	
	
	
	// - EAGLE parameters
	int generations = 10;
	int population = 20;
	float mutationRate = 0.4f;
	float reproductionRate = 0.4f;
	float crossoverRate = 0.3f;
	boolean preserveFittest = true;
//	double beta = 1;
	// supervised
	QualitativeMeasure measure = new FMeasure();
	
	//LION parameters
	double gammaScore = 0.15d;
	/**Expansion penalty*/
	double expansionPenalty = 0.7d;
	/**reward for better then parent*/
	double reward = 1.2;
	/**maximal number of iterations*/
	int maxIteration = 500;
	public int getMaxIteration() {
		return maxIteration;
	}

	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}

	public double getGammaScore() {
		return gammaScore;
	}

	public void setGammaScore(double gammaScore) {
		this.gammaScore = gammaScore;
	}

	public double getExpansionPenalty() {
		return expansionPenalty;
	}

	public void setExpansionPenalty(double expansionPenalty) {
		this.expansionPenalty = expansionPenalty;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	public QualitativeMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(QualitativeMeasure measure) {
		this.measure = measure;
	}

//	public double getBeta() {
//		return beta;
//	}
//	public void setBeta(double beta) {
//		this.beta = beta;
//	}
	PropertyMapping  propMap = new PropertyMapping();
	
	
	public PropertyMapping getPropMap() {
		return propMap;
	}
	public void setPropMap(PropertyMapping propMap) {
		this.propMap = propMap;
	}
	public int getGenerations() {
		return generations;
	}
	public void setGenerations(int generations) {
		this.generations = generations;
	}
	public int getPopulation() {
		return population;
	}
	public void setPopulation(int population) {
		this.population = population;
	}
	public float getMutationRate() {
		return mutationRate;
	}
	public void setMutationRate(float mutationRate) {
		this.mutationRate = mutationRate;
	}
	public float getReproductionRate() {
		return reproductionRate;
	}
	public void setReproductionRate(float reproductionRate) {
		this.reproductionRate = reproductionRate;
	}
	public float getCrossoverRate() {
		return crossoverRate;
	}
	public void setCrossoverRate(float crossoverRate) {
		this.crossoverRate = crossoverRate;
	}
	public boolean isPreserveFittest() {
		return preserveFittest;
	}
	public void setPreserveFittest(boolean preserveFittest) {
		this.preserveFittest = preserveFittest;
	}
	
	
	
	
}
