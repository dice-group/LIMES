package org.aksw.limes.core.gui.model.ml;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.MLResults;

import javafx.concurrent.Task;

/**
 * this class is responsible for the data handling according to the MVC Pattern for the supervised active learning
 *  
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class ActiveLearningModel extends MachineLearningModel {
    
    /**
     * next examples to show to the user
     */
    private AMapping nextExamples = MappingFactory.createDefaultMapping();
    /**
     * number of examples for user to evaluate
     */
    public static final int nextExamplesNum = 10;

    /**
     * constructor
     * @param config contains the information
     * @param sourceCache source
     * @param targetCache target
     */
    public ActiveLearningModel(Config config, ACache sourceCache, ACache targetCache) {
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
                    model = mlalgorithm.asActive().activeLearn();
                    nextExamples = mlalgorithm.asActive().getNextExamples(nextExamplesNum);
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

    /**
     * returns nextExamples
     * @return nextExamples
     */
    public AMapping getNextExamples() {
        return nextExamples;
    }

    /**
     * sets nextExamples
     * @param nextExamples mapping to be set
     */
    public void setNextExamples(AMapping nextExamples) {
        this.nextExamples = nextExamples;
    }


}
