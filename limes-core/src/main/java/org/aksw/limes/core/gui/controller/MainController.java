package org.aksw.limes.core.gui.controller;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import org.aksw.limes.core.gui.controller.ml.ActiveLearningController;
import org.aksw.limes.core.gui.controller.ml.BatchLearningController;
import org.aksw.limes.core.gui.controller.ml.UnsupervisedLearningController;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.Result;
import org.aksw.limes.core.gui.view.EditClassMatchingView;
import org.aksw.limes.core.gui.view.EditEndpointsView;
import org.aksw.limes.core.gui.view.EditPropertyMatchingView;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.gui.view.ResultView;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.gui.view.WizardView;
import org.aksw.limes.core.gui.view.ml.ActiveLearningView;
import org.aksw.limes.core.gui.view.ml.BatchLearningView;
import org.aksw.limes.core.gui.view.ml.UnsupervisedLearningView;

/**
 * Controller of MainView
 *
 * @author Manuel Jacob
 */
public class MainController {
    /**
     * Corresponding View to the Controller
     */
    private MainView view;
    /**
     * Config of the Current Limes-Query
     */
    private Config currentConfig;

    /**
     * Constructor
     *
     * @param view
     *         Corresponding View
     */
    public MainController(MainView view) {
        this.view = view;
        view.showLoadedConfig(false);
    }

    /**
     * Starts a New Limes Query-Config, drops the current Config
     *
     * @param createWizardView
     *         Basic View
     * @param editEndpointsView
     *         EndpointsView
     * @param editClassMatchingView
     *         Classmatching VIew
     * @param editPropertyMatchingView
     *         Property Matching View
     */
    public void newConfig(WizardView createWizardView,
                          EditEndpointsView editEndpointsView,
                          EditClassMatchingView editClassMatchingView,
                          EditPropertyMatchingView editPropertyMatchingView) {
        confirmPotentialDataLoss();
        setCurrentConfig(null);
        Config newConfig = new Config();
        new WizardController(() -> {
            setCurrentConfig(newConfig);
            view.graphBuild.graphBuildController.deleteGraph();
        }, () -> {
        }, createWizardView, new EditEndpointsController(newConfig,
                editEndpointsView), new EditClassMatchingController(newConfig,
                editClassMatchingView), new EditPropertyMatchingController(
                newConfig, editPropertyMatchingView));
    }

    /**
     * Reads Config from File, drops currentConfig
     *
     * @param file
     *         Linkpec-Config File in XML format
     */
    public void loadConfig(File file) {
        confirmPotentialDataLoss();
        try {
            setCurrentConfig(Config.loadFromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
            //to display stack trace in error window
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            MainView.showErrorWithStacktrace("An error occured",
                    "Exception while loading config: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Saves Current Config in Valid XML Format to a File
     *
     * @param file
     *         Location to save the File
     */
    public void saveConfig(File file) {
        if (currentConfig == null) {
            return;
        }
        checkAndUpdateMetric();
        try {
            currentConfig.save(file);
        } catch (Exception e) {
            MainView.showErrorWithStacktrace("An error occured", "Exception while saving config: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Terminates the Program
     */
    public void exit() {
        confirmPotentialDataLoss();
        Platform.exit();
    }

    /**
     * Tests if data could be lost
     */
    private void confirmPotentialDataLoss() {
        if (currentConfig == null) {
            return;
        }
        // TODO: Prüfe, ob Änderungen seit letztem Speichervorgang vorliegen. Im
        // Moment sind Änderungen noch nicht implementiert, also muss auch kein
        // Dialog angezeigt werden.
        // view.showDataLossDialog();
    }

    /**
     * Starts the Limes-Query and shows the Results
     */
    public void map() {
        if (currentConfig == null) {
            return;
        }
        if (checkAndUpdateMetric()) {
            ObservableList<Result> results = FXCollections
                    .observableArrayList();
            Task<Void> mapTask = currentConfig.createMappingTask(results);

            TaskProgressView taskProgressView = new TaskProgressView("Mapping");
            TaskProgressController taskProgressController = new TaskProgressController(
                    taskProgressView);
            taskProgressController.addTask(
                    mapTask,
                    items -> {
                        ResultView resultView = new ResultView(currentConfig);
                        resultView.showResults(results);
                    },
                    error -> {
                        MainView.showErrorWithStacktrace("Error during mapping",
                                error.getMessage(), mapTask.getException());
                    });
        }
    }

    /**
     * Check if Metric is complete
     */
    private boolean checkAndUpdateMetric() {
        if (view.graphBuild.edited
                && !view.graphBuild.graphBuildController.getOutputNode().nodeData.isComplete()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("Metric is not complete!");
            alert.showAndWait();
            return false;
        }
        view.graphBuild.graphBuildController.setConfigFromGraph();
        return true;
    }

    public void showBatchLearning() {
        if (currentConfig != null) {
            BatchLearningView blv = new BatchLearningView(view, new BatchLearningController(currentConfig, currentConfig.getSourceEndpoint().getCache(), currentConfig.getTargetEndpoint().getCache()));
        } else {
            System.err.println("Config is null!");
        }
    }

    public void showUnsupervisedLearning() {
        if (currentConfig != null) {
            UnsupervisedLearningView ulv = new UnsupervisedLearningView(view, new UnsupervisedLearningController(currentConfig, currentConfig.getSourceEndpoint().getCache(), currentConfig.getTargetEndpoint().getCache()));
        } else {
            System.err.println("Config is null!");
        }
    }

    public void showActiveLearning() {
        if (currentConfig != null) {
            ActiveLearningView alv = new ActiveLearningView(view, new ActiveLearningController(currentConfig, currentConfig.getSourceEndpoint().getCache(), currentConfig.getTargetEndpoint().getCache()));
        } else {
            System.err.println("Config is null!");
        }
    }

    /**
     * returns the currentConfig
     *
     * @return currentConfig
     */
    public Config getCurrentConfig() {
        return this.currentConfig;
    }

    private void setCurrentConfig(Config currentConfig) {
        this.currentConfig = currentConfig;
        view.showLoadedConfig(currentConfig != null);
        if (currentConfig != null) {
            view.showLoadedConfig(true);
            view.toolBox.showLoadedConfig(currentConfig);
            view.graphBuild.graphBuildController.setConfig(currentConfig);
            if (!(currentConfig.getMetricExpression() == null || currentConfig.getMetricExpression().equals(""))) {
                view.graphBuild.graphBuildController.generateGraphFromConfig();
            }
        }
    }
}
