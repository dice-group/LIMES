package org.aksw.limes.core.gui.model.ml;

import javafx.concurrent.Task;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.MemoryMapping;

public class UnsupervisedLearningModel extends MachineLearningModel {

	public UnsupervisedLearningModel(Config config, Cache sourceCache,
			Cache targetCache) {
		super(config, sourceCache, targetCache);
	}

	@Override
	public Task<Void> createLearningTask() {

		return new Task<Void>() {
			@Override
			protected Void call() {
				try {
					mlalgorithm.init(learningsetting, new MemoryMapping());
					mlalgorithm.learn(new MemoryMapping());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setLearnedMapping(mlalgorithm.computePredictions());
				return null;
			}
		};
	}

}
