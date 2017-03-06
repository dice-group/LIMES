package org.aksw.limes.core.gui.controller;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import org.aksw.limes.core.gui.model.ClassMatchingNode;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.Endpoint;
import org.aksw.limes.core.gui.model.GetAutomatedClassMatchingTask;
import org.aksw.limes.core.gui.model.GetClassesTask;
import org.aksw.limes.core.gui.view.EditClassMatchingView;
import org.aksw.limes.core.gui.view.IEditView;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.gui.view.TaskProgressView;

import javafx.scene.paint.Color;

/**
 * 
 * Controller class for class matching step in {@link WizardController}
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class EditClassMatchingController implements IEditController {
	/**
	 * Config of the LIMES Query
	 */
	private Config config;
	/**
	 * corresponding view
	 */
	private EditClassMatchingView view;

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
	EditClassMatchingController(Config config, EditClassMatchingView view) {
		this.config = config;
		this.view = view;
		view.setController(this);
	}

	@Override
	public void load() {
		checkIfAutomationIsPossible();
		if (view.isAutomated()) {
			loadAutomated();
		} else {
			loadManual();
		}
	}
	
	@Override
	public void load(boolean automated){
		if(automated){
			loadAutomated();
		}else{
			loadManual();
		}
	}

	private void loadAutomated() {
		taskProgressView = new TaskProgressView("Get classes");

		GetAutomatedClassMatchingTask getClassesTask = new GetAutomatedClassMatchingTask(config.getSourceInfo(),
				config.getTargetInfo(), config.getSourceEndpoint().getModel(), config.getTargetEndpoint().getModel());
		TaskProgressController taskProgressController = new TaskProgressController(taskProgressView);
		taskProgressController.addTask(getClassesTask, items -> {
			view.showTable(items);
		}, error -> {
			MainView.showErrorWithStacktrace("Error while loading source classes", error.getMessage(),
					getClassesTask.getException());
		});
	}

	private void loadManual() {
		taskProgressView = new TaskProgressView("Get classes");
		Endpoint sourceEndpoint = config.getSourceEndpoint();
		GetClassesTask getSourceClassesTask = sourceEndpoint.createGetClassesTask(taskProgressView);
		Endpoint targetEndpoint = config.getTargetEndpoint();
		GetClassesTask getTargetClassesTask = targetEndpoint.createGetClassesTask(taskProgressView);

		TaskProgressController taskProgressController = new TaskProgressController(taskProgressView);
		taskProgressController.addTask(getSourceClassesTask, items -> {
			view.showTree(SOURCE, items, sourceEndpoint.getCurrentClass());
		}, error -> {
			MainView.showErrorWithStacktrace("Error while loading source classes", error.getMessage(),
					getSourceClassesTask.getException());
		});
		taskProgressController.addTask(getTargetClassesTask, items -> {
			view.showTree(TARGET, items, targetEndpoint.getCurrentClass());
		}, error -> {
			MainView.showErrorWithStacktrace("Error while loading target classes", error.getMessage(),
					getTargetClassesTask.getException());
		});
	}

	/**
	 * Saves the selected classes
	 * 
	 * @param sourceClass
	 *            class for source
	 * @param targetClass
	 *            class for target
	 */
	public void save(ClassMatchingNode sourceClass, ClassMatchingNode targetClass) {
		config.getSourceEndpoint().setCurrentClass(sourceClass);
		config.getTargetEndpoint().setCurrentClass(targetClass);
	}

	public void save(String sourceClass, String targetClass) {
		config.getSourceEndpoint().setCurrentClassAsString(sourceClass);
		config.getTargetEndpoint().setCurrentClassAsString(targetClass);
	}

	/**
	 * returns the corresponding view
	 */
	@Override
	public IEditView getView() {
		return view;
	}

	@Override
	public boolean validate() {
		boolean valid = true;
		if (view.isAutomated()) {
			if (view.getTableView().getSelectionModel().getSelectedItem() == null) {
				view.getErrorAutomatedMissingClassMatchingLabel().setVisible(true);
				view.getErrorAutomatedMissingClassMatchingLabel().setTextFill(Color.RED);
				valid = false;
			}
		} else {
			if (view.getSourceTreeView().getSelectionModel().getSelectedItem() == null
					|| view.getTargetTreeView().getSelectionModel().getSelectedItem() == null) {
				view.getErrorManualMissingClassMatchingLabel().setVisible(true);
				view.getErrorManualMissingClassMatchingLabel().setTextFill(Color.RED);
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
	public void checkIfAutomationIsPossible(){
		//If the endpoints are the same automation is useless
		boolean automated = !config.getSourceInfo().getEndpoint().equals(config.getTargetInfo().getEndpoint());
		view.setAutomated(automated);
	}

}
