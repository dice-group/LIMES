package org.aksw.limes.core.gui.model.ml;

import javafx.concurrent.Task;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;

public class BatchLearningModel extends MachineLearningModel {

    public BatchLearningModel(Config config, Cache sourceCache, Cache targetCache) {
        super(config, sourceCache, targetCache);
    }

    @Override
    public Task<Void> createLearningTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
        	MLModel model = null;
                try {
                    mlalgorithm.init(learningParameters, sourceCache, targetCache);
                    RDFMappingReader mappingReader = new RDFMappingReader(config.getMlTrainingDataFile());
                    AMapping trainingData = mappingReader.read(); 
                    model = mlalgorithm.asSupervised().learn(trainingData);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                setLearnedMapping(mlalgorithm.predict(sourceCache, targetCache, model));
                return null;
            }
        };
    }

}
