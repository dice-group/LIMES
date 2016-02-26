package org.aksw.limes.core.gui.model.metric;

import java.util.Collections;
import java.util.Set;

/**
 * Property of metric
 * 
 * @author Daniel Obraczka, Sascha Hahne
 *
 */
public class Property extends Node {
	/**
	 * returns identifiers
	 * 
	 * @return identifier
	 */
	@Override
	public Set<String> identifiers() {
		return Collections.<String> emptySet();
	}

	/**
	 * returns maxChilds
	 */
	public byte getMaxChilds() {
		return 0;
	}

	/**
	 * Origin
	 *
	 */
	public enum Origin {
		SOURCE, TARGET
	};

	/**
	 * Origin
	 */
	protected final Origin origin;

	/**
	 * validChildClasses
	 */
	@Override
	public Set<Class<? extends Node>> validChildClasses() {
		return Collections.emptySet();
	}

	/**
	 * return if Child is accepted
	 */
	@Override
	public boolean acceptsChild(Node n) {
		return false;
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            of property
	 * @param origin
	 *            of property
	 */
	public Property(String id, Origin origin) {
		super(id);
		String regex = "\\w+\\.\\w+:?\\w+";
		if (!id.matches(regex))
			throw new MetricFormatException("id \"" + id
					+ "\" does not confirm to the regex " + regex);
		this.origin = origin;
	}

	/**
	 * Getter Origin
	 * 
	 * @return This Origin
	 */
	public Origin getOrigin() {
		return this.origin;
	}
}