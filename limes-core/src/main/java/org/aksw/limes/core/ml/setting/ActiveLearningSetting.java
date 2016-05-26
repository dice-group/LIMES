package org.aksw.limes.core.ml.setting;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.oldalgorithm.MLAlgorithm;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko
 */
public class ActiveLearningSetting extends BatchLearningSetting {

    private int numQueries = 10;
    private int numIterations = 5;

    private int currentIteration = 0;

    public ActiveLearningSetting(MLAlgorithm algorithm) {
        super(algorithm);
    }

    public int getCurrentIteration() {
        return currentIteration;
    }

    @Override
    public void learn() {
        // TODO Auto-generated method stub
        // will use the following
        currentIteration = 0;
        AMapping trainingData = selectExamples();
        while (!terminate()) {
            currentIteration++;
            MLModel result = algorithm.learn(trainingData);
            trainingData = selectExamples();

        }
    }

    public AMapping selectExamples() {
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

    public void setNumQueries(int numQueries) {
        this.numQueries = numQueries;
    }

    public int getNumIterations() {
        return numIterations;
    }

    public void setNumIterations(int numIterations) {
        this.numIterations = numIterations;
    }

}
