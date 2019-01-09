package org.aksw.limes.core.gui.model.metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.gui.model.Config;

/**
 * Output of metric
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class Output extends Node {

	/**
	 * unmodifiable HashSet of validChildClasses
	 */
	@SuppressWarnings("unchecked")
	static public final Set<Class<? extends Node>> validChildClasses = Collections
			.unmodifiableSet(new HashSet<>(
					Arrays.asList((Class<? extends Node>[]) new Class[] { Measure.class, Operator.class })));

	/**
	 * Constructor
	 */
	public Output() {
		super("output");
		this.param1 = Config.defaultAcceptanceThreshold;
		this.param2 = Config.defaultReviewThreshold;
	}

	/**
	 * returns HashSet of identifiers
	 *
	 * @return identifiers
	 */
	@Override
	public Set<String> identifiers() {
		return new HashSet<>(Arrays.asList(new String[] { "output" }));
	}

	/**
	 * returns validChildClasses
	 *
	 * @return validChildClasses
	 */
	@Override
	public Set<Class<? extends Node>> validChildClasses() {
		return validChildClasses;
	}

	/**
	 * returns maxChilds
	 *
	 * @return 1
	 */
	@Override
	public byte getMaxChilds() {
		return 1;
	}

	/**
	 * Output to String
	 */
	@Override
	public String toString() {
		return this.getChilds().isEmpty() ? "" : this.getChilds().iterator().next().toString();// +(param1!=null?"|"+param1:"")+(param2!=null?"|"+param2:"");
	}
}