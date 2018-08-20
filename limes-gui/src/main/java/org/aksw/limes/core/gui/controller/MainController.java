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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * This class handles the user input from
 * {@link org.aksw.limes.core.gui.view.MainView}
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class MainController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	/**
	 * Corresponding view to the controller
	 */
	private final MainView view;
	/**
	 * Config of the current limes-query
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

	/**
	 * Opens a new {@link WizardView} an drops the current config
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
	public void newConfig(WizardView createWizardView, EditEndpointsView editEndpointsView,
			EditClassMatchingView editClassMatchingView, EditPropertyMatchingView editPropertyMatchingView) {
		this.confirmPotentialDataLoss();
		this.setCurrentConfig(null);
		final Config newConfig = new Config();
		new WizardController(() -> {
			this.setCurrentConfig(newConfig);
			this.view.getGraphBuild().graphBuildController.deleteGraph();
		}, () -> {
		}, createWizardView, new EditEndpointsController(newConfig, editEndpointsView),
				new EditClassMatchingController(newConfig, editClassMatchingView),
				new EditPropertyMatchingController(newConfig, editPropertyMatchingView));
	}

	public void editConfig(WizardView wizardView, EditClassMatchingView editClassMatchingView,
			EditPropertyMatchingView editPropertyMatchingView) {
		this.confirmPotentialDataLoss();
		new WizardController(() -> {
			this.setCurrentConfig(this.currentConfig);
			this.view.getGraphBuild().graphBuildController.deleteGraph();
		}, () -> {
		}, wizardView, new EditClassMatchingController(this.currentConfig, editClassMatchingView),
				new EditPropertyMatchingController(this.currentConfig, editPropertyMatchingView));
	}

	public void editConfig(WizardView wizardView, EditPropertyMatchingView editPropertyMatchingView) {
		this.confirmPotentialDataLoss();
		new WizardController(() -> {
			this.setCurrentConfig(this.currentConfig);
			this.view.getGraphBuild().graphBuildController.deleteGraph();
		}, () -> {
		}, wizardView, new EditPropertyMatchingController(this.currentConfig, editPropertyMatchingView));
	}

	/**
	 * Reads config from file, drops current config
	 *
	 * @param file
	 *            Linkpec-Config File in XML format
	 */
	public void loadConfig(File file) {
		this.confirmPotentialDataLoss();
		try {
			this.setCurrentConfig(Config.loadFromFile(file));
		} catch (final Exception e) {
			e.printStackTrace();
			// to display stack trace in error window
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			MainView.showErrorWithStacktrace("An error occured", "Exception while loading config: " + e.getMessage(),
					e);
		}
	}

	/**
	 * Saves current config to a file
	 *
	 * @param file
	 *            Location to save the File
	 */
	public void saveConfig(File file) {
		if (this.currentConfig == null) {
			return;
		}
		this.checkAndUpdateMetric();
		try {
			this.currentConfig.save(file);
		} catch (final Exception e) {
			MainView.showErrorWithStacktrace("An error occured", "Exception while saving config: " + e.getMessage(), e);
		}
	}

	/**
	 * Terminates the program
	 */
	public void exit() {
		this.confirmPotentialDataLoss();
		Platform.exit();
	}

	/**
	 * Tests if data could be lost
	 */
	private void confirmPotentialDataLoss() {
		if (this.currentConfig == null) {
			return;
		}
		// TODO check if data changed, since we cannot really save anything this
		// is left for future implementation
		// view.showDataLossDialog();
	}

	/**
	 * Starts the limes-query as a new task and shows the results
	 */
	public void map() {
		if (this.currentConfig == null) {
			return;
		}
		if (this.checkAndUpdateMetric()) {
			final ObservableList<Result> results = FXCollections.observableArrayList();
			final Task<Void> mapTask = this.currentConfig.createMappingTask(results);

			final TaskProgressView taskProgressView = new TaskProgressView("Mapping");
			final TaskProgressController taskProgressController = new TaskProgressController(taskProgressView);
			taskProgressController.addTask(mapTask, items -> {
				final ResultView resultView = new ResultView(this.currentConfig);
				resultView.showResults(results, this.currentConfig.getMapping());
			}, error -> {
				MainView.showErrorWithStacktrace("Error during mapping", error.getMessage(), mapTask.getException());
			});
		}
	}

	/**
	 * Check if metric is complete
	 */
	private boolean checkAndUpdateMetric() {
		if (this.view.getGraphBuild().edited
				&& !this.view.getGraphBuild().graphBuildController.getOutputNode().nodeData.isComplete()) {
			final Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("Metric is not complete!");
			alert.showAndWait();
			return false;
		}
		this.view.getGraphBuild().graphBuildController.setConfigFromGraph();
		return true;
	}

	/**
	 * Creates a new {@link MachineLearningView} for batch learning
	 */
	public void showBatchLearning() {
		if (this.currentConfig != null) {
			new MachineLearningView(this.view,
					new BatchLearningController(this.currentConfig, this.currentConfig.getSourceEndpoint().getCache(),
							this.currentConfig.getTargetEndpoint().getCache(), this),
					MLImplementationType.SUPERVISED_BATCH);
		} else {
			logger.error("Config is null!");
		}
	}

	/**
	 * Creates a new {@link MachineLearningView} for unsupervised learning
	 */
	public void showUnsupervisedLearning() {
		if (this.currentConfig != null) {
			new MachineLearningView(this.view,
					new UnsupervisedLearningController(this.currentConfig,
							this.currentConfig.getSourceEndpoint().getCache(),
							this.currentConfig.getTargetEndpoint().getCache(), this),
					MLImplementationType.UNSUPERVISED);
		} else {
			logger.error("Config is null!");
		}
	}

	/**
	 * Creates a new {@link MachineLearningView} for active learning
	 */
	public void showActiveLearning() {
		if (this.currentConfig != null) {
			new MachineLearningView(this.view,
					new ActiveLearningController(this.currentConfig, this.currentConfig.getSourceEndpoint().getCache(),
							this.currentConfig.getTargetEndpoint().getCache(), this),
					MLImplementationType.SUPERVISED_ACTIVE);
		} else {
			logger.error("Config is null!");
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
	 * 
	 * @param currentConfig
	 */
	public void setCurrentConfig(Config currentConfig) {
		this.currentConfig = currentConfig;
		this.view.showLoadedConfig(currentConfig != null);
		if (currentConfig != null) {
			this.view.showLoadedConfig(true);
			this.view.toolBox.showLoadedConfig(currentConfig);
			this.view.getGraphBuild().graphBuildController.setConfig(currentConfig);
			if (!(currentConfig.getMetricExpression() == null || currentConfig.getMetricExpression().equals(""))) {
				this.view.getGraphBuild().graphBuildController.generateGraphFromConfig();
			} else {
				this.view.getGraphBuild().graphBuildController.deleteGraph();
			}
		}
	}
}
