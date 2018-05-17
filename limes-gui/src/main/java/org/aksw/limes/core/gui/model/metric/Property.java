package org.aksw.limes.core.gui.model.metric;

import java.util.Collections;
import java.util.Set;

import org.aksw.limes.core.gui.util.SourceOrTarget;

/**
 * Property of metric
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class Property extends Node {
	/**
	 * Origin
	 */
	protected final SourceOrTarget origin;

	private boolean optional;

	/**
	 * Constructor
	 *
	 * @param id
	 *            of property
	 * @param origin
	 *            of property
	 */
	public Property(String id, SourceOrTarget origin, boolean optional) {
		super(id);
		this.origin = origin;
		this.optional = optional;
	}

	/**
	 * returns identifiers
	 *
	 * @return identifier
	 */
	@Override
	public Set<String> identifiers() {
		return Collections.<String>emptySet();
	}

	;

	/**
	 * returns maxChilds
	 */
	@Override
	public byte getMaxChilds() {
		return 0;
	}

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
	 * Getter Origin
	 *
	 * @return This Origin
	 */
	public SourceOrTarget getOrigin() {
		return this.origin;
	}

	public boolean isOptional() {
		return this.optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

}