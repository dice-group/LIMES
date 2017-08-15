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
import weka.gui.SysErrLog;

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
	private Config config;

	/**
	 * corresponding view
	 */
	private EditPropertyMatchingView view;

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
		checkIfAutomationIsPossible();
		if (view.isAutomated()) {
			loadAutomatedPropertyMatching();
		} else {
			loadManualPropertyMatching();
		}
	}

	@Override
	public void load(boolean automated){
		if(automated){
			loadAutomatedPropertyMatching();
		}else{
			loadManualPropertyMatching();
		}
	}

	private void loadAutomatedPropertyMatching() {
		GetAutomatedPropertiesTask getPropertiesTask = new GetAutomatedPropertiesTask(config.getSourceInfo(),
				config.getTargetInfo(), config.getSourceEndpoint().getModel(), config.getTargetEndpoint().getModel(),
				config.getSourceEndpoint().getCurrentClass().getUri().toString(),
				config.getTargetEndpoint().getCurrentClass().getUri().toString());
		taskProgressView = new TaskProgressView("Getting properties");
		TaskProgressController taskProgressController = new TaskProgressController(taskProgressView);
		taskProgressController.addTask(getPropertiesTask, properties -> {
			view.showAutomatedProperties(properties);
		}, error -> {
			MainView.showErrorWithStacktrace("An error occured", "Error while loading source properties",
					getPropertiesTask.getException());
		});
	}

	private void loadManualPropertyMatching() {
		GetPropertiesTask getSourcePropertiesTask = config.getSourceEndpoint().createGetPropertiesTask();
		GetPropertiesTask getTargetPropertiesTask = config.getTargetEndpoint().createGetPropertiesTask();
		taskProgressView = new TaskProgressView("Getting properties");
		TaskProgressController taskProgressController = new TaskProgressController(taskProgressView);
		taskProgressController.addTask(getSourcePropertiesTask, properties -> {
			view.showAvailableProperties(SOURCE, properties);
		}, error -> {
			MainView.showErrorWithStacktrace("An error occured", "Error while loading source properties",
					getSourcePropertiesTask.getException());
		});
		taskProgressController.addTask(getTargetPropertiesTask, properties -> {
			view.showAvailableProperties(TARGET, properties);
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
		return view;
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
		config.setPropertiesMatching(sourceProperties, targetProperties);
	}

	@Override
	public boolean validate() {
		boolean valid = true;
		if (view.isAutomated()) {
			if (view.getAddedAutomatedPropsList().getItems().size() == 0) {
				view.getMissingPropertiesLabel().setVisible(true);
				view.getMissingPropertiesLabel().setTextFill(Color.RED);
				valid = false;
			}
		} else {
			if (view.getAddedSourcePropsList().getItems().size() == 0
					|| view.getAddedTargetPropsList().getItems().size() == 0) {
				view.getMissingPropertiesLabel().setVisible(true);
				view.getMissingPropertiesLabel().setTextFill(Color.RED);
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public TaskProgressView getTaskProgressView() {
		return taskProgressView;
	}

	@Override
	public void setTaskProgressView(TaskProgressView tpv) {
		this.taskProgressView = tpv;
	}

	public Config getConfig() {
		return config;
	}

	@Override
	public void checkIfAutomationIsPossible() {
    	//if the uris of the class are the same automation is useless
    	boolean automated = ! config.getSourceEndpoint().getCurrentClass().getUri().equals(config.getTargetEndpoint().getCurrentClass().getUri());
    	view.setAutomated(automated);
	}
}
