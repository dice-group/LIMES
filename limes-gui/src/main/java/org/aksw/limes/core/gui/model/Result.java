package org.aksw.limes.core.gui.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;


/**
 * Contains all important information of the result of an instance match
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class Result {

    /**
     * URI of the source
     */
    private final SimpleStringProperty sourceURI;

    /**
     * URI of the target
     */
    private final SimpleStringProperty targetURI;

    /**
     * Value of the Result
     */
    private final SimpleDoubleProperty value;

    /**
     * Constructor
     *
     * @param sourceURI
     *         URI of the source
     * @param targetURI
     *         URI of the target
     * @param value
     *         value of the result
     */
    public Result(String sourceURI, String targetURI, Double value) {
        this.sourceURI = new SimpleStringProperty(sourceURI);
        this.targetURI = new SimpleStringProperty(targetURI);
        this.value = new SimpleDoubleProperty(value);
    }

    /**
     * returns URI of the source
     *
     * @return sourceURI
     */
    public String getSourceURI() {
        return sourceURI.get();
    }

    /**
     * returns URI of the target
     *
     * @return targetURI
     */
    public String getTargetURI() {
        return targetURI.get();
    }

    /**
     * returns value of the result
     *
     * @return value
     */
    public double getValue() {
        return value.get();
    }
}
