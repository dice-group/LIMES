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
	private final Config config;
	/**
	 * corresponding view
	 */
	private final EditClassMatchingView view;

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
		this.checkIfAutomationIsPossible();
		if (this.view.isAutomated()) {
			this.loadAutomated();
		} else {
			this.loadManual();
		}
	}

	@Override
	public void load(boolean automated) {
		if (automated) {
			this.loadAutomated();
		} else {
			this.loadManual();
		}
	}

	private void loadAutomated() {
		this.taskProgressView = new TaskProgressView("Get classes");

		final GetAutomatedClassMatchingTask getClassesTask = new GetAutomatedClassMatchingTask(
				this.config.getSourceInfo(), this.config.getTargetInfo(), this.config.getSourceEndpoint().getModel(),
				this.config.getTargetEndpoint().getModel());
		final TaskProgressController taskProgressController = new TaskProgressController(this.taskProgressView);
		taskProgressController.addTask(getClassesTask, items -> {
			this.view.showTable(items);
		}, error -> {
			MainView.showErrorWithStacktrace("Error while loading source classes", error.getMessage(),
					getClassesTask.getException());
		});
	}

	private void loadManual() {
		this.taskProgressView = new TaskProgressView("Get classes");
		final Endpoint sourceEndpoint = this.config.getSourceEndpoint();
		final GetClassesTask getSourceClassesTask = sourceEndpoint.createGetClassesTask(this.taskProgressView);
		final Endpoint targetEndpoint = this.config.getTargetEndpoint();
		final GetClassesTask getTargetClassesTask = targetEndpoint.createGetClassesTask(this.taskProgressView);

		final TaskProgressController taskProgressController = new TaskProgressController(this.taskProgressView);
		taskProgressController.addTask(getSourceClassesTask, items -> {
			this.view.showTree(SOURCE, items, sourceEndpoint.getCurrentClass());
		}, error -> {
			MainView.showErrorWithStacktrace("Error while loading source classes", error.getMessage(),
					getSourceClassesTask.getException());
		});
		taskProgressController.addTask(getTargetClassesTask, items -> {
			this.view.showTree(TARGET, items, targetEndpoint.getCurrentClass());
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
		this.config.getSourceEndpoint().setCurrentClass(sourceClass);
		this.config.getTargetEndpoint().setCurrentClass(targetClass);
	}

	public void save(String sourceClass, String targetClass) {
		this.config.getSourceEndpoint().setCurrentClassAsString(sourceClass);
		this.config.getTargetEndpoint().setCurrentClassAsString(targetClass);
	}

	/**
	 * returns the corresponding view
	 */
	@Override
	public IEditView getView() {
		return this.view;
	}

	@Override
	public boolean validate() {
		boolean valid = true;
		if (this.view.isAutomated()) {
			if (this.view.getTableView().getSelectionModel().getSelectedItem() == null) {
				this.view.getErrorAutomatedMissingClassMatchingLabel().setVisible(true);
				this.view.getErrorAutomatedMissingClassMatchingLabel().setTextFill(Color.RED);
				valid = false;
			}
		} else {
			if (this.view.getSourceTreeView().getSelectionModel().getSelectedItem() == null
					|| this.view.getTargetTreeView().getSelectionModel().getSelectedItem() == null) {
				this.view.getErrorManualMissingClassMatchingLabel().setVisible(true);
				this.view.getErrorManualMissingClassMatchingLabel().setTextFill(Color.RED);
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
		// If the endpoints are the same automation is useless
		final boolean automated = !this.config.getSourceInfo().getEndpoint()
				.equals(this.config.getTargetInfo().getEndpoint());
		this.view.setAutomated(automated);
	}

}
