package org.aksw.limes.core.gui.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Represents one pair of a property matching
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class PropertyPair {
	private final SimpleStringProperty sourceProperty = new SimpleStringProperty();
	private final SimpleStringProperty targetProperty = new SimpleStringProperty();

	/**
	 * Constructor
	 * 
	 * @param sourceProperty
	 *            sourceProperty
	 * @param targetProperty
	 *            targetProperty
	 */
	public PropertyPair(String sourceProperty, String targetProperty) {
		this.setSourceProperty(sourceProperty);
		this.setTargetProperty(targetProperty);
	}

	/**
	 * returns sourceProperty
	 * 
	 * @return sourceProperty
	 */
	public String getSourceProperty() {
		return this.sourceProperty.get();
	}

	/**
	 * sets sourceProperty
	 * 
	 * @param sourceProperty
	 *            sourceProperty
	 */
	public void setSourceProperty(String sourceProperty) {
		this.sourceProperty.set(sourceProperty);
	}

	/**
	 * returns targetProperty
	 * 
	 * @return targetProperty
	 */
	public String getTargetProperty() {
		return this.targetProperty.get();
	}

	/**
	 * sets targetProperty
	 * 
	 * @param targetProperty
	 *            targetProperty
	 */
	public void setTargetProperty(String targetProperty) {
		this.targetProperty.set(targetProperty);
	}
}
