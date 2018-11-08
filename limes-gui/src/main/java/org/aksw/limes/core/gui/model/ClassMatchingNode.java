package org.aksw.limes.core.gui.model;

import java.io.Serializable;
import java.net.URI;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * used to display classes in
 * {@link org.aksw.limes.core.gui.view.EditClassMatchingView}
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class ClassMatchingNode implements Serializable {
	private static final long serialVersionUID = -2983091498542870621L;
	/**
	 * uri of class
	 */
	private final URI uri;
	/**
	 * name of class
	 */
	private String name;
	/**
	 * children of class
	 */
	private final List<ClassMatchingNode> children;

	/**
	 * constructor
	 * 
	 * @param uri
	 *            uri of class
	 * @param children
	 *            children of class
	 */
	public ClassMatchingNode(URI uri, List<ClassMatchingNode> children) {
		this.uri = uri;
		final String fragment = uri.getFragment();
		if (fragment != null) {
			this.name = fragment;
		} else {
			final String path = uri.getPath();
			this.name = path.substring(path.lastIndexOf('/') + 1);
		}
		this.children = children;
	}

	/**
	 * returns uri
	 * 
	 * @return uri
	 */
	public URI getUri() {
		return this.uri;
	}

	/**
	 * returns name
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * returns children
	 * 
	 * @return children
	 */
	public List<ClassMatchingNode> getChildren() {
		return this.children;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(19, 37).append(this.uri).append(this.name).append(this.children).toHashCode();
	}

	public final static Comparator<ClassMatchingNode> CLASS_MATCHING_NODE_COMPARATOR = (c1, c2) -> c1.name
			.compareTo(c2.name);

}
