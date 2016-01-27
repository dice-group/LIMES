package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.MLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLResult;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 */
public class ActiveLearningSetting extends BatchLearningSetting {
	
	private int numQueries = 10;
	private int numIterations = 5;
	
	private int currentIteration = 0;

	public int getCurrentIteration() {
		return currentIteration;
	}

	public ActiveLearningSetting(MLAlgorithm algorithm) {
		super(algorithm);
	}

	@Override
	public void learn() {
		// TODO Auto-generated method stub
		// will use the following
		currentIteration = 0;
		Mapping trainingData = selectExamples();
		while(!terminate()) {
			currentIteration ++;
			MLResult result = algorithm.learn(trainingData);
			trainingData = selectExamples();
			
		}
	}
	
	public Mapping selectExamples() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Terminate when the number of iterations This method can be overridden.
	 * 
	 * @return
	 */
	public boolean terminate() {
		return currentIteration > numIterations;
	}

	public int getNumQueries() {
		return numQueries;
	}
	
	public int getNumIterations() {
		return numIterations;
	}

	public void setNumQueries(int numQueries) {
		this.numQueries = numQueries;
	}

	public void setNumIterations(int numIterations) {
		this.numIterations = numIterations;
	}

}
