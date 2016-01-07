package org.aksw.limes.core.ml;

import java.util.HashMap;

/**
 * Map of Linksets by predicate URI. Used as Mapping for Generic Link Discovery
 * (i.e., non-OWL.sameAs links).
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-09
 *
 */
public class LinksetMap extends HashMap<String, Linkset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1599000235158907579L;
	
	/**
	 * @param linkset
	 */
	public void add(Linkset linkset) {
		this.put(linkset.getPredicate(), linkset);
	}

}
