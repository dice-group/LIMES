package org.aksw.limes.core.gui.controller.ml;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.controller.TaskProgressController;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.Result;
import org.aksw.limes.core.gui.model.ml.UnsupervisedLearningModel;
import org.aksw.limes.core.gui.view.ResultView;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.io.cache.ACache;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * This class handles the interaction between the {@link MachineLearningView}
 *  and the {@link UnsupervisedLearningModel} according to the MVC Pattern for the unsupervised learning
 *
 *  
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class UnsupervisedLearningController extends MachineLearningController {

    private MainController mainController;
    /**
     * Constructor creates the according {@link UnsupervisedLearningModel}
     * @param config contains information
     * @param sourceCache source
     * @param targetCache target
     * @param mainController mainController
     */
    public UnsupervisedLearningController(Config config, ACache sourceCache,
                                          ACache targetCache, MainController mainController) {
	this.mainController = mainController;
        this.mlModel = new UnsupervisedLearningModel(config, sourceCache,
                targetCache);
    }

    /**
     * Creates a learning task and launches a {@link TaskProgressView}.
     * The results are shown in a {@link ResultView}
     * @param view MachineLearningView to manipulate elements in it
     */
    @Override
    public void learn(MachineLearningView view) {
        Task<Void> learnTask = this.mlModel.createLearningTask();

        TaskProgressView taskProgressView = new TaskProgressView("Learning");
	taskProgressView.getCancelled().addListener(new ChangeListener<Boolean>() {

	    @Override
	    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		view.getLearnButton().setDisable(false);
	    }
	});
        TaskProgressController taskProgressController = new TaskProgressController(
                taskProgressView);
        taskProgressController.addTask(
                learnTask,
                items -> {
                    view.getLearnButton().setDisable(false);
                    // view.mapButton.setOnAction(e -> {
                    ObservableList<Result> results = FXCollections
                            .observableArrayList();
                    this.mlModel.getLearnedMapping().getMap().forEach(
                            (sourceURI, map2) -> {
                                map2.forEach((targetURI, value) -> {
                                    results.add(new Result(sourceURI,
                                            targetURI, value));
                                });
                            });

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ResultView resultView = new ResultView(mlModel.getConfig(), mlModel.getLearnedLS(), mainController);
                            resultView.showResults(results, mlModel.getLearnedMapping());
                        }
                    });
                    // });
                    if (this.mlModel.getLearnedMapping() != null && this.mlModel.getLearnedMapping().size() > 0) {
                        // view.mapButton.setDisable(false);
                        logger.info(this.mlModel.getConfig().getMetricExpression());
//                        view.getMainView().graphBuild.graphBuildController
//                                .setConfigFromGraph();
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                view.showErrorDialog("Error", "Empty mapping!");
                            }
                        });

                    }
                }, error -> {
                    view.showErrorDialog("Error during learning",
                            error.getMessage());
                });

    }

}
