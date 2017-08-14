package org.aksw.limes.core.gui.model.ml;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.MLResults;

import javafx.concurrent.Task;

/**
 * this class is responsible for the data handling according to the MVC Pattern for the supervised batch learning
 *  
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class BatchLearningModel extends MachineLearningModel {
    
    private AMapping trainingMapping;
    
    /**
     * constructor
     * @param config contains the information
     * @param sourceCache source
     * @param targetCache target
     */
    public BatchLearningModel(Config config, ACache sourceCache, ACache targetCache) {
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
//                    RDFMappingReader mappingReader = new RDFMappingReader(config.getMlTrainingDataFile());
//                    AMapping trainingData = mappingReader.read(); 
                    model = mlalgorithm.asSupervised().learn(trainingMapping);
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

    public AMapping getTrainingMapping() {
        return trainingMapping;
    }

    public void setTrainingMapping(AMapping trainingMapping) {
        this.trainingMapping = trainingMapping;
    }

}
