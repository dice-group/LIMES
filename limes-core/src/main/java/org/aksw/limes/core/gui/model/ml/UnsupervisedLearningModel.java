package org.aksw.limes.core.gui.model.ml;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
	public void learn(MachineLearningView view) {
		learningThread = new Thread() {
			public void run() {
				try {
					mlalgorithm.init(learningsetting, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mlalgorithm.learn(null);
				learnedMapping = mlalgorithm.computePredictions();
				onFinish(view);
			}
		};
		learningThread.start();
	}

	private void onFinish(MachineLearningView view) {
		view.getLearnButton().setDisable(false);
		// view.mapButton.setOnAction(e -> {
		ObservableList<Result> results = FXCollections.observableArrayList();
		learnedMapping.getMap().forEach((sourceURI, map2) -> {
			map2.forEach((targetURI, value) -> {
				results.add(new Result(sourceURI, targetURI, value));
			});
		});

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ResultView resultView = new ResultView(config);
				resultView.showResults(results);
			}
		});
		// });
		if (learnedMapping != null && learnedMapping.size() > 0) {
			// view.mapButton.setDisable(false);
			view.getLearningProgress().setVisible(false);
			logger.info(config.getMetricExpression());
			view.getMainView().graphBuild.graphBuildController
					.setConfigFromGraph();
		} else {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					view.getLearningProgress().setVisible(false);
					view.createErrorWindow();
				}
			});

		}
	}

}
