package org.aksw.limes.core.gui.model;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * Stores information about link candidates during the 
 * active learning process
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class ActiveLearningResult extends Result {

    /**
     * Flag to set if source URI and target URI are indeed a match (or not)
     */
    private SimpleBooleanProperty isMatch;

    /**
     * Default constructor
     *
     * @param sourceURI
     *         Source Node
     * @param targetURI
     *         Target Node
     * @param value
     *         Matching value calculated with the currently best metric
     */
    public ActiveLearningResult(String sourceURI, String targetURI, Double value) {
        super(sourceURI, targetURI, value);
        isMatch = new SimpleBooleanProperty(false);
    }

    /**
     * return isMatch
     * @return isMatch
     */
    public SimpleBooleanProperty isMatchProperty() {
        return isMatch;
    }
}
