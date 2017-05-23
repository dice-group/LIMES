package org.aksw.limes.core.ml.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.classifier.SimpleClassifier;

/**
 * Wraps around results of a ML algorithm.
 *
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 */

public class MLResults {
    protected LinkSpecification linkspec;
    protected AMapping mapping;
    protected double quality;
    protected Map<String, Object> details = new HashMap<>();
    protected List<SimpleClassifier> classifiers = null;
    
    /**
     * MLResults constructor.
     */
    public MLResults() {
    	super();
    }

    /**
     * MLResults full constructor.
     * 
     * @param linkspec the link specification
     * @param mapping the mapping
     * @param quality the value of quality measure
     * @param details additional computation details
     */
    public MLResults(LinkSpecification linkspec, AMapping mapping, double quality, Map<String, Object> details) {
        super();
        this.linkspec = linkspec;
        this.mapping = mapping;
        this.quality = quality;
        this.details = details;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("RESULT:\n");
        s.append("LS: " + linkspec + "\n");
        s.append("Quality: " + quality + "\n");
        if(mapping != null)
        	s.append("Mapping: " + mapping.size() + "\n");
        return s.toString();
    }

    /**
     * @return the link specification
     */
    public LinkSpecification getLinkSpecification() {
        return linkspec;
    }

    /**
     * @param spec the link specification
     */
    public void setLinkSpecification(LinkSpecification spec) {
        this.linkspec = spec;
    }

    /**
     * @return the mapping
     */
    public AMapping getMapping() {
        return mapping;
    }

    /**
     * @param mapping the mapping
     */
    public void setMapping(AMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * @return the value of quality measure
     */
    public double getQuality() {
        return quality;
    }

    /**
     * @param quality the value of quality measure
     */
    public void setQuality(double quality) {
        this.quality = quality;
    }

    /**
     * Add additional computation detail.
     * 
     * @param key computation detail name
     * @param value computation detail value
     */
    public void addDetail(String key, Object value) {
        details.put(key, value);
    }

    /**
     * @return the additional computation details
     */
    public Map<String, Object> getDetails() {
        return details;
    }

	public List<SimpleClassifier> getClassifiers() {
		return classifiers;
	}

	public void setClassifiers(List<SimpleClassifier> classifiers) {
		this.classifiers = classifiers;
	}
	
	/**
	 * Some ML algorithms such as Euclid don't produce LS but Mapping build by so called
	 * classifiers. This method checks whether this (also) holds those.
	 * @return
	 */
	public boolean classifiersSet() {
		return classifiers != null && classifiers.size()>0;
	}

}
