package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.LinksetMap;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class EagleUnsupervised extends MLAlgorithm {

	/**
	 * TODO Example hyperparameter...
	 */
	private int generations = 10;

	public EagleUnsupervised(Cache sourceCache, Cache targetCache,
			Mapping mapping) {
		super(sourceCache, targetCache, mapping);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param generations
	 */
	public void setGenerations(int generations) {
		this.generations = generations;
	}
	
	/**
	 * @return
	 */
	public int getGenerations() {
		return generations;
	}

	@Override
	public String getName() {
		return "EAGLE Unsupervised";
	}

	@Override
	public void learn() {
		// TODO Auto-generated method stub
		System.out.println(getName()+" :: learn called.");
	}

	@Override
	public LinksetMap computePredictions() {
		// TODO Auto-generated method stub
		return null;
	}

}
