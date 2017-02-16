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
     * Calls {@link #checkIfAutomationIsPossible()} 
	 * Creates and starts the tasks to load the classes from this source and
	 * target endpoint. After finishing the classes are displayed in the view or
	 * if errors are encountered an error window is shown
     */
    public void load();
    
    /**
     * Loads the appropriate mode according to the boolean value
     * @param automated true if automated mode is wanted false if manual mode is desired
     */
    public void load(boolean automated);

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

    /**
     * checks if automation is possible calls {@link IEditView#setAutomated(boolean)} with the result
     */
	public void checkIfAutomationIsPossible();
}
