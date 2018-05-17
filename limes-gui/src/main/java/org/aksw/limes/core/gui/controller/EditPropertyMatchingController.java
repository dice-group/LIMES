package org.aksw.limes.core.gui.controller;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import java.util.List;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.GetAutomatedPropertiesTask;
import org.aksw.limes.core.gui.model.GetPropertiesTask;
import org.aksw.limes.core.gui.view.EditPropertyMatchingView;
import org.aksw.limes.core.gui.view.IEditView;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.gui.view.TaskProgressView;

import javafx.scene.paint.Color;

/**
 * Controller class for property matching step in {@link WizardController}
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class EditPropertyMatchingController implements IEditController {

	/**
	 * Config of the LIMES Query
	 */
	private final Config config;

	/**
	 * corresponding view
	 */
	private final EditPropertyMatchingView view;

	/**
	 * the view called in the {@link #load()} method
	 */
	private TaskProgressView taskProgressView;

	/**
	 * constructor initializes object variables and sets this controller to the
	 * corresponding view
	 *
	 * @param config
	 *            Config of Limes Query
	 * @param view
	 *            corresponding EditEndpointsView
	 */
	public EditPropertyMatchingController(Config config, EditPropertyMatchingView view) {
		this.config = config;
		this.view = view;
		view.setController(this);
	}

	@Override
	public void load() {
		this.checkIfAutomationIsPossible();
		if (this.view.isAutomated()) {
			this.loadAutomatedPropertyMatching();
		} else {
			this.loadManualPropertyMatching();
		}
	}

	@Override
	public void load(boolean automated) {
		if (automated) {
			this.loadAutomatedPropertyMatching();
		} else {
			this.loadManualPropertyMatching();
		}
	}

	private void loadAutomatedPropertyMatching() {
		final GetAutomatedPropertiesTask getPropertiesTask = new GetAutomatedPropertiesTask(this.config.getSourceInfo(),
				this.config.getTargetInfo(), this.config.getSourceEndpoint().getModel(),
				this.config.getTargetEndpoint().getModel(),
				this.config.getSourceEndpoint().getCurrentClass().getUri().toString(),
				this.config.getTargetEndpoint().getCurrentClass().getUri().toString());
		this.taskProgressView = new TaskProgressView("Getting properties");
		final TaskProgressController taskProgressController = new TaskProgressController(this.taskProgressView);
		taskProgressController.addTask(getPropertiesTask, properties -> {
			this.view.showAutomatedProperties(properties);
		}, error -> {
			MainView.showErrorWithStacktrace("An error occured", "Error while loading source properties",
					getPropertiesTask.getException());
		});
	}

	private void loadManualPropertyMatching() {
		final GetPropertiesTask getSourcePropertiesTask = this.config.getSourceEndpoint().createGetPropertiesTask();
		final GetPropertiesTask getTargetPropertiesTask = this.config.getTargetEndpoint().createGetPropertiesTask();
		this.taskProgressView = new TaskProgressView("Getting properties");
		final TaskProgressController taskProgressController = new TaskProgressController(this.taskProgressView);
		taskProgressController.addTask(getSourcePropertiesTask, properties -> {
			this.view.showAvailableProperties(SOURCE, properties);
		}, error -> {
			MainView.showErrorWithStacktrace("An error occured", "Error while loading source properties",
					getSourcePropertiesTask.getException());
		});
		taskProgressController.addTask(getTargetPropertiesTask, properties -> {
			this.view.showAvailableProperties(TARGET, properties);
		}, error -> {
			MainView.showErrorWithStacktrace("An error occured", "Error while loading target properties",
					getTargetPropertiesTask.getException());
		});
	}

	/**
	 * Returns the corresponding view
	 */
	@Override
	public IEditView getView() {
		return this.view;
	}

	/**
	 * Saves the properties
	 *
	 * @param sourceProperties
	 *            source properties to save
	 * @param targetProperties
	 *            target properties to save
	 */
	public void save(List<String> sourceProperties, List<String> targetProperties) {
		this.config.setPropertiesMatching(sourceProperties, targetProperties);
	}

	@Override
	public boolean validate() {
		boolean valid = true;
		if (this.view.isAutomated()) {
			if (this.view.getAddedAutomatedPropsList().getItems().size() == 0) {
				this.view.getMissingPropertiesLabel().setVisible(true);
				this.view.getMissingPropertiesLabel().setTextFill(Color.RED);
				valid = false;
			}
		} else {
			if (this.view.getAddedSourcePropsList().getItems().size() == 0
					|| this.view.getAddedTargetPropsList().getItems().size() == 0) {
				this.view.getMissingPropertiesLabel().setVisible(true);
				this.view.getMissingPropertiesLabel().setTextFill(Color.RED);
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public TaskProgressView getTaskProgressView() {
		return this.taskProgressView;
	}

	@Override
	public void setTaskProgressView(TaskProgressView tpv) {
		this.taskProgressView = tpv;
	}

	public Config getConfig() {
		return this.config;
	}

	@Override
	public void checkIfAutomationIsPossible() {
		// if the uris of the class are the same automation is useless
		final boolean automated = !this.config.getSourceEndpoint().getCurrentClass().getUri()
				.equals(this.config.getTargetEndpoint().getCurrentClass().getUri());
		this.view.setAutomated(automated);
	}
}
