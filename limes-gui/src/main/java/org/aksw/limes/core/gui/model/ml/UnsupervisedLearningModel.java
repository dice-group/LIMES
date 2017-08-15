package org.aksw.limes.core.gui.model.ml;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.ml.algorithm.MLResults;

import javafx.concurrent.Task;

/**
 * this class is responsible for the data handling according to the MVC Pattern for the unsupervised learning
 *  
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class UnsupervisedLearningModel extends MachineLearningModel {

    /**
     * constructor
     * @param config contains the information
     * @param sourceCache source
     * @param targetCache target
     */
    public UnsupervisedLearningModel(Config config, ACache sourceCache,
                                     ACache targetCache) {
        super(config, sourceCache, targetCache);
    }

    /**
     * creates a new active learning task for the given algorithm
     */
    @Override
    public Task<Void> createLearningTask() {

        return new Task<Void>() {
            @Override
            protected Void call() {
        	MLResults model = null;
                try {
                    mlalgorithm.init(learningParameters, sourceCache, targetCache);
                    model = mlalgorithm.asUnsupervised().learn(new PseudoFMeasure());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                setLearnedMapping(mlalgorithm.predict(sourceCache, targetCache, model));
                learnedLS = model.getLinkSpecification();
                return null;
            }
        };
    }

}
