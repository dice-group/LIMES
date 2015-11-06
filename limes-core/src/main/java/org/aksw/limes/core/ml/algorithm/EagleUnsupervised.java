package org.aksw.limes.core.ml.algorithm;

import java.util.Set;

import org.aksw.limes.core.ml.LearningModel;
import org.aksw.limes.core.ml.Prediction;
import org.aksw.limes.core.model.Link;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class EagleUnsupervised implements MLAlgorithm {

	/**
	 * TODO Example hyperparameter...
	 */
	private int generations;

	/**
	 * @param dataset
	 */
	public EagleUnsupervised() {
		super();
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
	}

	@Override
	public Prediction predict(Link link) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Link> computePredictions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LearningModel getLearningModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
