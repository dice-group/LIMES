package org.aksw.limes.core.gui.controller.ml;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.ml.UnsupervisedLearningModel;
import org.aksw.limes.core.io.cache.Cache;

public class UnsupervisedLearningController extends MachineLearningController {

	public UnsupervisedLearningController(Config config, Cache sourceCache, Cache targetCache) {
		this.mlModel = new UnsupervisedLearningModel(config, sourceCache, targetCache);
	}

}
