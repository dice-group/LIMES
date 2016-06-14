package org.aksw.limes.core.gui.controller;

import javafx.scene.control.ListView;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.GetPropertiesTask;
import org.aksw.limes.core.gui.view.EditPropertyMatchingView;
import org.aksw.limes.core.gui.view.IEditView;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.gui.view.TaskProgressView;


import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

/**
 * Controller class for property matching step in create wizard
 *
 * @author Manuel Jacob
 */
public class EditPropertyMatchingController implements IEditController {
    private Config config;
    private EditPropertyMatchingView view;

    public EditPropertyMatchingController(Config config,
                                          EditPropertyMatchingView view) {
        this.config = config;
        this.view = view;
        view.setController(this);
    }

    @Override
    public void load() {
        GetPropertiesTask getSourcePropertiesTask = config.getSourceEndpoint()
                .createGetPropertiesTask();
        GetPropertiesTask getTargetPropertiesTask = config.getTargetEndpoint()
                .createGetPropertiesTask();
        TaskProgressView taskProgressView = new TaskProgressView(
                "Get properties");
        TaskProgressController taskProgressController = new TaskProgressController(
                taskProgressView);
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

    @Override
    public IEditView getView() {
        return view;
    }

    public void save(ListView<String> sourceProperties, ListView<String> targetProperties) {
        config.setPropertiesMatching(sourceProperties, targetProperties);
    }
}
