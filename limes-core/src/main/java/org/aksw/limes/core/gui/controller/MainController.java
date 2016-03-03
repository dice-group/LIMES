package org.aksw.limes.core.gui.controller;

import java.io.File;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.Result;
import org.aksw.limes.core.gui.view.ActiveLearningView;
import org.aksw.limes.core.gui.view.EditClassMatchingView;
import org.aksw.limes.core.gui.view.EditEndpointsView;
import org.aksw.limes.core.gui.view.EditPropertyMatchingView;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.gui.view.ResultView;
import org.aksw.limes.core.gui.view.SelfConfigurationView;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.gui.view.WizardView;

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
	 *            Corresponding View
	 */
	public MainController(MainView view) {
		this.view = view;
		view.showLoadedConfig(false);
	}

	private void setCurrentConfig(Config currentConfig) {
		this.currentConfig = currentConfig;
		view.showLoadedConfig(currentConfig != null);
		if (currentConfig != null) {
			view.showLoadedConfig(true);
			view.toolBox.showLoadedConfig(currentConfig);
			view.graphBuild.graphBuildController.setConfig(currentConfig);
			if (! (currentConfig.getMetricExpression() == null || currentConfig.getMetricExpression().equals(""))) {
				view.graphBuild.graphBuildController.generateGraphFromConfig();
			}
		}
	}

	/**
	 * Starts a New Limes Query-Config, drops the current Config
	 * 
	 * @param createWizardView
	 *            Basic View
	 * @param editEndpointsView
	 *            EndpointsView
	 * @param editClassMatchingView
	 *            Classmatching VIew
	 * @param editPropertyMatchingView
	 *            Property Matching View
	 */
	public void newConfig(WizardView createWizardView,
			EditEndpointsView editEndpointsView,
			EditClassMatchingView editClassMatchingView,
			EditPropertyMatchingView editPropertyMatchingView) {
		confirmPotentialDataLoss();
		setCurrentConfig(null);
		Config newConfig = new Config();
//System.out.println(newConfig.getSourceEndpoint().toString());
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
	 *            Linkpec-Config File in XML format
	 */
	public void loadConfig(File file) {
		confirmPotentialDataLoss();
		try {
			setCurrentConfig(Config.loadFromFile(file));
		} catch (Exception e) {
			e.printStackTrace();
			view.showErrorDialog(
					"Exception while loading config: " + e.getMessage(),
					e.getMessage());
		}
	}

	/**
	 * Saves Current Config in Valid XML Format to a File
	 * 
	 * @param file
	 *            Location to save the File
	 */
	public void saveConfig(File file) {
		if (currentConfig == null) {
			return;
		}
		checkAndUpdateMetric();
		try {
			currentConfig.save(file);
		} catch (Exception e) {
			view.showErrorDialog("Exception while saving config: " + e,
					e.getMessage());
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
						view.showErrorDialog("Error during mapping",
								error.getMessage());
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

	/**
	 * Show SelfConfig Window
	 */
	public void showSelfConfig() {
		SelfConfigurationView selfConfigView = new SelfConfigurationView(view);
		selfConfigView.controller.setCurrentConfig(currentConfig);
	}

	/**
	 * Show the Active Learning Window
	 */
	public void showActiveLearning() {
		if (currentConfig == null)
			return;
		new ActiveLearningView(view, currentConfig);
	}

	/**
	 * returns the currentConfig
	 * 
	 * @return currentConfig
	 */
	public Config getCurrentConfig() {
		return this.currentConfig;
	}
}
