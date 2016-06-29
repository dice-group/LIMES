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
