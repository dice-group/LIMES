package org.aksw.limes.core.gui.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Represents one pair of a property matching
 * 
 * @author Manuel Jacob
 */
public class PropertyPair {
	private SimpleStringProperty sourceProperty = new SimpleStringProperty();
	private SimpleStringProperty targetProperty = new SimpleStringProperty();

	public PropertyPair(String sourceProperty, String targetProperty) {
		setSourceProperty(sourceProperty);
		setTargetProperty(targetProperty);
	}

	public String getSourceProperty() {
		return sourceProperty.get();
	}

	public void setSourceProperty(String sourceProperty) {
		this.sourceProperty.set(sourceProperty);
	}

	public String getTargetProperty() {
		return targetProperty.get();
	}

	public void setTargetProperty(String targetProperty) {
		this.targetProperty.set(targetProperty);
	}
}
