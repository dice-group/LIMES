package org.aksw.limes.core.gui.model.ml;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.MappingFactory;

import javafx.concurrent.Task;

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
					mlalgorithm.init(learningsetting, MappingFactory.createDefaultMapping());
					mlalgorithm.learn(MappingFactory.createDefaultMapping());
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
