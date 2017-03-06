package org.aksw.limes.core.gui.view;

import javafx.scene.Parent;

/**
 * Interface for steps in {@link org.aksw.limes.core.gui.view.WizardView}
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public interface IEditView {
	
	/**
	 * sets the value boolean that contains mode and sets the visibility of the switchModeButton accordingly
	 * @param automated
	 */
	public void setAutomated(boolean automated);
	/**
	 * returns if this view is in automated mode
	 * @return true if automated mode is active/preferred
	 */
	public Boolean isAutomated();
	
    /**
     * Return the Pane
     *
     * @return Returns the used Pane
     */
    public Parent getPane();

    /**
     * Saves something to the Controller
     */
    public void save();
}
