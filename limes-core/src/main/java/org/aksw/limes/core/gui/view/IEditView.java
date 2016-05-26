package org.aksw.limes.core.gui.view;

import javafx.scene.Parent;

/**
 * Interface for the EditEndPoint Dialog
 *
 * @author Manuel Jacob, Felix Brei
 */
public interface IEditView {
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
