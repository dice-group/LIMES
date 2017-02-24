package org.aksw.limes.core.gui.controller;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

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
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * This class handles the user input from {@link org.aksw.limes.core.gui.view.MainView}
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class MainController {
    /**
     * Corresponding view to the controller
     */
    private MainView view;
    /**
     * Config of the current limes-query
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
     * Opens a new {@link WizardView} an drops the current config
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
    
    public void editConfig(WizardView wizardView, 
    					   EditClassMatchingView editClassMatchingView, 
    					   EditPropertyMatchingView editPropertyMatchingView){
    	confirmPotentialDataLoss();
        new WizardController(() -> {
        	setCurrentConfig(currentConfig);
            view.graphBuild.graphBuildController.deleteGraph();
        }, () -> {
        }, wizardView, new EditClassMatchingController(currentConfig,
                editClassMatchingView), new EditPropertyMatchingController(
                currentConfig, editPropertyMatchingView));
    }
    
    public void editConfig(WizardView wizardView, 
    					   EditPropertyMatchingView editPropertyMatchingView){
    	confirmPotentialDataLoss();
        new WizardController(() -> {
        	setCurrentConfig(currentConfig);
            view.graphBuild.graphBuildController.deleteGraph();
        }, () -> {
        }, wizardView, new EditPropertyMatchingController(
                currentConfig, editPropertyMatchingView));
    }

    /**
     * Reads config from file, drops current config
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
     * Saves current config to a file
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
     * Terminates the program
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
        //TODO check if data changed, since we cannot really save anything this is left for future implementation
        // view.showDataLossDialog();
    }

    /**
     * Starts the limes-query as a new task and shows the results
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
                        resultView.showResults(results, currentConfig.getMapping());
                    },
                    error -> {
                        MainView.showErrorWithStacktrace("Error during mapping",
                                error.getMessage(), mapTask.getException());
                    });
        }
    }

    /**
     * Check if metric is complete
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

    /**
     * Creates a new {@link MachineLearningView} for batch learning
     */
    public void showBatchLearning() {
        if (currentConfig != null) {
            new MachineLearningView(view, new BatchLearningController(currentConfig, currentConfig.getSourceEndpoint().getCache(), currentConfig.getTargetEndpoint().getCache(), this), MLImplementationType.SUPERVISED_BATCH);
        } else {
            System.err.println("Config is null!");
        }
    }

    /**
     * Creates a new {@link MachineLearningView} for unsupervised learning
     */
    public void showUnsupervisedLearning() {
        if (currentConfig != null) {
            new MachineLearningView(view, new UnsupervisedLearningController(currentConfig, currentConfig.getSourceEndpoint().getCache(), currentConfig.getTargetEndpoint().getCache(), this), MLImplementationType.UNSUPERVISED);
        } else {
            System.err.println("Config is null!");
        }
    }

    /**
     * Creates a new {@link MachineLearningView} for active learning
     */
    public void showActiveLearning() {
        if (currentConfig != null) {
            new MachineLearningView(view, new ActiveLearningController(currentConfig, currentConfig.getSourceEndpoint().getCache(), currentConfig.getTargetEndpoint().getCache(), this), MLImplementationType.SUPERVISED_ACTIVE);
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

    /**
     * sets a config and updates the view accordingly
     * @param currentConfig
     */
    public void setCurrentConfig(Config currentConfig) {
        this.currentConfig = currentConfig;
        view.showLoadedConfig(currentConfig != null);
        if (currentConfig != null) {
            view.showLoadedConfig(true);
            view.toolBox.showLoadedConfig(currentConfig);
            view.graphBuild.graphBuildController.setConfig(currentConfig);
            if (!(currentConfig.getMetricExpression() == null || currentConfig.getMetricExpression().equals(""))) {
                view.graphBuild.graphBuildController.generateGraphFromConfig();
            }else{
        	view.graphBuild.graphBuildController.deleteGraph();
            }
        }
    }
}
