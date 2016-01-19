package org.aksw.limes.core.ml.setting;

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
	double beta = 1;
	
	public double getBeta() {
		return beta;
	}
	public void setBeta(double beta) {
		this.beta = beta;
	}
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
