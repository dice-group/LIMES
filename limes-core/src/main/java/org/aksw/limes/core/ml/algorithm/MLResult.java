package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Wraps around results of a ML algorithm.
 * @author Klaus Lyko
 *
 */
public class MLResult {
	LinkSpecification linkspec;
	Mapping mapping;
	double quality;
	
	public void setLinkSpecification(LinkSpecification spec) {
		this.linkspec = spec;
	}
	public LinkSpecification getLinkSpecification() {
		return linkspec;
	}
	public Mapping getMapping() {
		return mapping;
	}
	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}
	public double getQuality() {
		return quality;
	}
	public void setQuality(double quality) {
		this.quality = quality;
	}
	
}
