package org.aksw.limes.core.gui.model.ml;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.Result;
import org.aksw.limes.core.gui.view.ResultView;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.io.cache.Cache;

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
					mlalgorithm.init(learningsetting, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mlalgorithm.learn(null);
				setLearnedMapping(mlalgorithm.computePredictions());
				return null;
			}
		};
	}

}
