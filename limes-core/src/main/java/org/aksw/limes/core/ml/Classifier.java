package org.aksw.limes.core.ml;

import java.util.Set;

import org.aksw.limes.core.model.Link;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public abstract class Classifier {
	
	protected Set<Link> dataset;
	protected LearningModel lModel;

	/**
	 * @param dataset
	 */
	public Classifier(Set<Link> dataset) {
		super();
		this.dataset = dataset;
	}

	public abstract String getName();
	
	protected abstract void learn(Set<Link> trainingSet);
	
	public abstract Prediction predict(Link link);
	
	public abstract Set<Link> computePredictions();
	
	public Set<Link> getDataset() {
		return dataset;
	}

	public LearningModel getModel() {
		return lModel;
	}
	
	/**
	 * @param n number of iterations
	 * @param k number of most informative examples for each iteration
	 */
	public void activeLearning(int n, int k) {
		// TODO using this.learn();
	}
	
	/**
	 * @param trainingRate rate of the training set size
	 */
	public void batchLearning(double trainingRate) {
		// TODO using this.learn();
	}
	
	/**
	 * @param folds number of folds
	 */
	public void crossValidation(int folds) {
		// TODO using this.learn();
	}

	/**
	 * @param folds number of folds
	 */
	public void reverseCrossValidation(int folds) {
		// TODO using this.learn();
	}

}
