package org.aksw.limes.core.gui.controller;

import org.aksw.limes.core.gui.view.IEditView;
import org.aksw.limes.core.gui.view.TaskProgressView;

/**
 * Interface for controllers for step in wizard
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public interface IEditController {
    /**
     * Load data entered in this wizard step
     */
    public void load();

    /**
     * Save data entered in this wizard step
     */
    public default void save() {
        getView().save();
    }

    /**
     * return view
     * @return the view
     */
    public IEditView getView();
    
    /**
     * validates the fields
     * @return true if valid, false else
     */
    public boolean validate();
    
    /**
     * 
     * @return the TaskProgressView of the controller that gets called in the {@link #load} method
     */
    public TaskProgressView getTaskProgressView();
    
    public void setTaskProgressView(TaskProgressView tpv);
}
