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
 * Controller class for class matching step in create wizard
 *
 * @author Manuel Jacob
 */
public class EditClassMatchingController implements IEditController {
    private Config config;
    private EditClassMatchingView view;

    EditClassMatchingController(Config config, EditClassMatchingView view) {
        this.config = config;
        this.view = view;
        view.setController(this);
    }

    @Override
    public void load() {
        TaskProgressView taskProgressView = new TaskProgressView("Get classes");
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

    public void save(ClassMatchingNode sourceClass,
                     ClassMatchingNode targetClass) {
        config.getSourceEndpoint().setCurrentClass(sourceClass);
        config.getTargetEndpoint().setCurrentClass(targetClass);
    }

    @Override
    public IEditView getView() {
        return view;
    }
}
