package org.aksw.limes.core.gui.model;

import java.io.Serializable;
import java.util.Comparator;

import javafx.beans.property.SimpleStringProperty;

public class AutomatedPropertyMatchingNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8197131712673610290L;
	private SimpleStringProperty sourceProperty;
	private SimpleStringProperty targetProperty;
	
	public AutomatedPropertyMatchingNode(String sourceProperty, String targetProperty){
		this.sourceProperty = new SimpleStringProperty(sourceProperty);
		this.targetProperty = new SimpleStringProperty(targetProperty);
	}

	public SimpleStringProperty getSourceProperty() {
		return sourceProperty;
	}

	public void setSourceProperty(SimpleStringProperty sourceProperty) {
		this.sourceProperty = sourceProperty;
	}

	public SimpleStringProperty getTargetProperty() {
		return targetProperty;
	}

	public void setTargetProperty(SimpleStringProperty targetProperty) {
		this.targetProperty = targetProperty;
	}

    public SimpleStringProperty sourcePropertyProperty() {
        return sourceProperty;
    }

    public SimpleStringProperty targetPropertyProperty() {
        return targetProperty;
    }
    
    @Override
    public String toString(){
    	return sourceProperty + " | " + targetProperty;
    }
    
    
    public final static Comparator<AutomatedPropertyMatchingNode> 
    	AUTOMATED_PROPERTY_MATCHING_NODE_COMPARATOR = new Comparator<AutomatedPropertyMatchingNode>(){
    	
    	@Override public int compare( AutomatedPropertyMatchingNode c1, AutomatedPropertyMatchingNode c2 ) {
            return c1.sourceProperty.toString().compareTo( c2.sourceProperty.toString() );
          }
    };

}
