package org.aksw.limes.core.ml.oldalgorithm;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps around results of a ML algorithm.
 *
 * @author Klaus Lyko
 */

public class MLModel {
    protected LinkSpecification linkspec;
    protected AMapping mapping;
    protected double quality;
    protected Map<String, Object> details = new HashMap<String, Object>();

    public MLModel() {
    }

    public MLModel(LinkSpecification linkspec, AMapping mapping, double quality, Map<String, Object> details) {
        super();
        this.linkspec = linkspec;
        this.mapping = mapping;
        this.quality = quality;
        this.details = details;
    }

    @Override
    public String toString() {
        String s = "RESULT:\n";
        s += "LS: " + linkspec + "\n";
        s += "Quality: " + quality + "\n";
        s += "Mapping: " + mapping.size() + "\n";

        return s;
    }

    public LinkSpecification getLinkSpecification() {
        return linkspec;
    }

    public void setLinkSpecification(LinkSpecification spec) {
        this.linkspec = spec;
    }

    public AMapping getMapping() {
        return mapping;
    }

    public void setMapping(AMapping mapping) {
        this.mapping = mapping;
    }

    public double getQuality() {
        return quality;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    public void addDetail(String key, Object value) {
        details.put(key, value);
    }

    public Map<String, Object> getDetails() {
        return details;
    }

}
