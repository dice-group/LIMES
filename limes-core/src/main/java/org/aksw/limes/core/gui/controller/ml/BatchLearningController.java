package org.aksw.limes.core.gui.controller.ml;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.ml.BatchLearningModel;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.io.cache.Cache;

public class BatchLearningController extends MachineLearningController {

    public BatchLearningController(Config config, Cache sourceCache, Cache targetCache) {
        this.mlModel = new BatchLearningModel(config, sourceCache, targetCache);
    }

    @Override
    public void learn(MachineLearningView view) {
        // TODO Auto-generated method stub

    }

}
