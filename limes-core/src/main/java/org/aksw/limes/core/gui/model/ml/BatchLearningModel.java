package org.aksw.limes.core.gui.model.ml;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.cache.Cache;

import javafx.concurrent.Task;

public class BatchLearningModel extends MachineLearningModel {

	public BatchLearningModel(Config config, Cache sourceCache, Cache targetCache) {
		super(config, sourceCache, targetCache);
	}

	@Override
	public Task<Void> createLearningTask() {
		// TODO Auto-generated method stub
		return null;
	}

}
