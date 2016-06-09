package org.aksw.limes.core.gui.controller.ml;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import org.aksw.limes.core.gui.controller.TaskProgressController;
import org.aksw.limes.core.gui.model.ActiveLearningResult;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.ml.ActiveLearningModel;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.gui.view.ml.ActiveLearningResultView;
import org.aksw.limes.core.gui.view.ml.ActiveLearningView;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.io.cache.Cache;

public class ActiveLearningController extends MachineLearningController {

    public ActiveLearningController(Config config, Cache sourceCache, Cache targetCache) {
	this.mlModel = new ActiveLearningModel(config, sourceCache, targetCache);
    }

    @Override
    public void learn(MachineLearningView view) {
	Task<Void> learnTask = this.mlModel.createLearningTask();

	TaskProgressView taskProgressView = new TaskProgressView("Learning");
	TaskProgressController taskProgressController = new TaskProgressController(taskProgressView);
	taskProgressController.addTask(learnTask, items -> {
	    view.getLearnButton().setDisable(false);
	    // view.mapButton.setOnAction(e -> {
	    // ObservableList<Result> results = FXCollections
	    // .observableArrayList();
	    // this.mlModel.getLearnedMapping().getMap().forEach(
	    // (sourceURI, map2) -> {
	    // map2.forEach((targetURI, value) -> {
	    // results.add(new Result(sourceURI,
	    // targetURI, value));
	    // });
	    // });
	    //
	    // Platform.runLater(new Runnable() {
	    // @Override
	    // public void run() {
	    // ResultView resultView = new ResultView(mlModel.getConfig());
	    // resultView.showResults(results);
	    // }
	    // });
	    // // });
	    // if (this.mlModel.getLearnedMapping() != null &&
	    // this.mlModel.getLearnedMapping().size() > 0) {
	    // // view.mapButton.setDisable(false);
	    // logger.info(this.mlModel.getConfig().getMetricExpression());
	    // view.getMainView().graphBuild.graphBuildController
	    // .setConfigFromGraph();
	    // } else {
	    // Platform.runLater(new Runnable() {
	    // @Override
	    // public void run() {
	    // view.showErrorDialog("Error", "Empty mapping!");
	    // }
	    // });
	    //
	    // }
	    // ActiveLearningResultView activeLearningResultView = new
	    // ActiveLearningResultView(
		// mlModel.getConfig(), (ActiveLearningModel)mlModel,
		// (ActiveLearningView)view);
		ObservableList<ActiveLearningResult> results = FXCollections.observableArrayList();
		((ActiveLearningModel) this.mlModel).getNextExamples().getMap().forEach((sourceURI, map2) -> {
		    System.out.println(sourceURI + " " + map2);
		    map2.forEach((targetURI, value) -> {
			results.add(new ActiveLearningResult(sourceURI, targetURI, value));
		    });
		});
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		ActiveLearningResultView activeLearningResultView = new ActiveLearningResultView(mlModel.getConfig(), (ActiveLearningModel) mlModel,
			(ActiveLearningView) view);
		activeLearningResultView.showResults(results);
		    }
		    //TODO error handling
		});
	    }, error -> {
		view.showErrorDialog("Error during learning", error.getMessage());
	    });

    }

}
