package org.aksw.limes.core.ml.algorithm;

import java.util.HashMap;
import java.util.Map;

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
	Map<String, Object> details = new HashMap<String, Object>();
	@Override
	public String toString() {
		String s = "RESULT:\n";
		s+="LS: "+linkspec+"\n";
		s+="Quality: "+quality+"\n";
		s+="Mapping: "+mapping.size()+"\n";
		
		return s;
	}
	
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
	
	public void addDetail(String key, Object value) {
		details.put(key, value);
	}
	public Map<String, Object> getDetails() {
		return details;
	}
	
}
