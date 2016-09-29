package org.aksw.limes.core.gui.controller;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import org.aksw.limes.core.gui.model.ClassMatchingNode;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.Endpoint;
import org.aksw.limes.core.gui.model.GetClassesTask;
import org.aksw.limes.core.gui.view.EditClassMatchingView;
import org.aksw.limes.core.gui.view.IEditView;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.gui.view.TaskProgressView;

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
     * constructor initializes object variables and sets this controller to the corresponding view
     * @param config
     *         Config of Limes Query
     * @param view
     *         corresponding EditEndpointsView
     */
    EditClassMatchingController(Config config, EditClassMatchingView view) {
        this.config = config;
        this.view = view;
        view.setController(this);
    }

    /**
     * Creates and starts the tasks to load the classes from this source and target endpoint.
     * After finishing the classes are displayed in the view or if errors are encountered an error window is shown
     */
    @Override
    public void load() {
        taskProgressView = new TaskProgressView("Get classes");
        Endpoint sourceEndpoint = config.getSourceEndpoint();
        GetClassesTask getSourceClassesTask = sourceEndpoint
                .createGetClassesTask(taskProgressView);
        Endpoint targetEndpoint = config.getTargetEndpoint();
        GetClassesTask getTargetClassesTask = targetEndpoint
                .createGetClassesTask(taskProgressView);

        TaskProgressController taskProgressController = new TaskProgressController(
                taskProgressView);
        taskProgressController.addTask(
                getSourceClassesTask,
                items -> {
                    view.showTree(SOURCE, items,
                            sourceEndpoint.getCurrentClass());
                },
                error -> {
                    MainView.showErrorWithStacktrace("Error while loading source classes",
                            error.getMessage(), getSourceClassesTask.getException());
                });
        taskProgressController.addTask(
                getTargetClassesTask,
                items -> {
                    view.showTree(TARGET, items,
                            targetEndpoint.getCurrentClass());
                },
                error -> {
                    MainView.showErrorWithStacktrace("Error while loading target classes",
                            error.getMessage(),getTargetClassesTask.getException());
                });
    }

    /**
     * Saves the selected classes
     * @param sourceClass class for source
     * @param targetClass class for target
     */
    public void save(ClassMatchingNode sourceClass,
                     ClassMatchingNode targetClass) {
        config.getSourceEndpoint().setCurrentClass(sourceClass);
        config.getTargetEndpoint().setCurrentClass(targetClass);
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
	if(view.getSourceTreeView().getSelectionModel().getSelectedItem() == null || view.getTargetTreeView().getSelectionModel().getSelectedItem() == null ){
	   view.getErrorMissingClassMatchingLabel().setVisible(true);
	   valid = false;
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

}
