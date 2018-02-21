package org.aksw.limes.core.gui.controller;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.Endpoint;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.aksw.limes.core.gui.view.EditEndpointsView;
import org.aksw.limes.core.gui.view.IEditView;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.io.config.KBInfo;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * 
 * Controller class for editing endpoint step in {@link WizardController}
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class EditEndpointsController implements IEditController {
    private static final String emptyFieldError = "This field must not be empty!";
    /**
     * Config of the LIMES Query
     */
    private Config config;

    /**
     * Corresponding view
     */
    private EditEndpointsView view;

    /**
     * Constructor initializes object variables and sets this controller to the
     * corresponding view
     *
     * @param config
     *            Config of Limes Query
     * @param view
     *            corresponding EditEndpointsView
     */
    EditEndpointsController(Config config, EditEndpointsView view) {
	this.config = config;
	this.view = view;
	view.setController(this);
    }

    @Override
    public void load() {
	KBInfo sourceEndpoint = config.getSourceInfo();
	view.setFields(SOURCE, sourceEndpoint.getEndpoint(), sourceEndpoint.getId(), sourceEndpoint.getGraph(), Integer.toString(sourceEndpoint.getPageSize()));
	KBInfo targetEndpoint = config.getTargetInfo();
	view.setFields(TARGET, targetEndpoint.getEndpoint(), targetEndpoint.getId(), targetEndpoint.getGraph(), Integer.toString(targetEndpoint.getPageSize()));
    }
    
    /**
     * Since there are no different modes this just calls {@link #load()}
     */
    @Override
    public void load(boolean automated){
    	load();
    }

    /**
     * Saves edited Endpoint
     *
     * @param sourceOrTarget
     *            if True Source else Target
     * @param idNamespace
     *            Namespace of Endpoint
     * @param graph
     *            graph
     * @param pageSize
     *            length of Query
     * @param endpointURL
     *            URL of the Endpoint
     */
    public void save(SourceOrTarget sourceOrTarget, String endpointURL, String idNamespace, String graph, String pageSize) {
	Endpoint endpoint = sourceOrTarget == SOURCE ? config.getSourceEndpoint() : config.getTargetEndpoint();
	KBInfo info = endpoint.getInfo();
	info.setEndpoint(endpointURL);
	if(idNamespace == null){
	    idNamespace = sourceOrTarget == SOURCE ? "source" : "target"; 
	}
	info.setId(idNamespace);
	info.setGraph(graph);
	info.setPageSize(Integer.parseInt(pageSize));
	endpoint.update();
    }

    /**
     * Returns the corresponding view
     */
    @Override
    public IEditView getView() {
	return view;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean validate() {
	boolean valid = true;
	if (((ComboBox<String>)view.getSourceFields()[0]).getValue() == null || ((ComboBox<String>)view.getSourceFields()[0]).getValue().equals("") || ((ComboBox<String>)view.getSourceFields()[0]).getValue().equals(emptyFieldError)) {
	   ((ComboBox<String>)view.getSourceFields()[0]).setValue(emptyFieldError);
	   ((ComboBox<String>)view.getSourceFields()[0]).setStyle("-fx-text-inner-color: red;");
	   ((ComboBox<String>)view.getSourceFields()[0]).setOnMouseClicked(e -> {
		if (((ComboBox<String>)view.getSourceFields()[0]).getValue().equals(emptyFieldError)) {
		    ((ComboBox<String>)view.getSourceFields()[0]).setStyle("");
		    ((ComboBox<String>)view.getSourceFields()[0]).setValue("");
		}
	    });
	    valid = false;
	}
	if (((ComboBox<String>)view.getTargetFields()[0]).getValue() == null || ((ComboBox<String>)view.getTargetFields()[0]).getValue().equals("")|| ((ComboBox<String>)view.getTargetFields()[0]).getValue().equals(emptyFieldError)) {
	    ((ComboBox<String>)view.getTargetFields()[0]).setValue(emptyFieldError);
	    ((ComboBox<String>)view.getTargetFields()[0]).setStyle("-fx-text-inner-color: red;");
	    ((ComboBox<String>)view.getTargetFields()[0]).setOnMouseClicked(e -> {
		if (((ComboBox<String>)view.getTargetFields()[0]).getValue().equals(emptyFieldError)) {
		    ((ComboBox<String>)view.getTargetFields()[0]).setStyle("");
		    ((ComboBox<String>)view.getTargetFields()[0]).setValue("");
		}
	    });
	    valid = false;
	}
	if (((TextField)view.getSourceFields()[3]).getText() == null || ((TextField)view.getSourceFields()[3]).getText().equals("")|| ((TextField)view.getSourceFields()[3]).getText().equals(emptyFieldError)) {
	    ((TextField)view.getSourceFields()[3]).setText(emptyFieldError);
	    ((TextField)view.getSourceFields()[3]).setStyle("-fx-text-inner-color: red;");
	    ((TextField)view.getSourceFields()[3]).setOnMouseClicked(e -> {
		if (((TextField)view.getSourceFields()[3]).getText().equals(emptyFieldError)) {
		    ((TextField)view.getSourceFields()[3]).setStyle("");
		    ((TextField)view.getSourceFields()[3]).setText("");
		}
	    });
	    valid = false;
	}
	if (((TextField)view.getTargetFields()[3]).getText() == null || ((TextField)view.getTargetFields()[3]).getText().equals("")|| ((TextField)view.getTargetFields()[3]).getText().equals(emptyFieldError)) {
	    ((TextField)view.getTargetFields()[3]).setText(emptyFieldError);
	    ((TextField)view.getTargetFields()[3]).setStyle("-fx-text-inner-color: red;");
	    ((TextField)view.getTargetFields()[3]).setOnMouseClicked(e -> {
		if (((TextField)view.getTargetFields()[3]).getText().equals(emptyFieldError)) {
		    ((TextField)view.getTargetFields()[3]).setStyle("");
		    ((TextField)view.getTargetFields()[3]).setText("");
		}
	    });
	    valid = false;
	}
	return valid;
    }

    @Override
    public TaskProgressView getTaskProgressView() {
	return null;
    }
    @Override
    public void setTaskProgressView(TaskProgressView tpv) {
	return;
    }

	@Override
	public void checkIfAutomationIsPossible() {
		//Automation for this step is not possible
	}
}
