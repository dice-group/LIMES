package org.aksw.limes.core.gui.model;

import java.io.Serializable;
import java.net.URI;
import java.util.Comparator;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * used to display classes in
 * org.aksw.limes.core.gui.view.EditAutomatedClassMatchingView
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class AutomatedClassMatchingNode implements Serializable {
	private static final long serialVersionUID = -2983091498542870621L;
	/**
	 * source uri of class
	 */
	private final URI sourceUri;
	/**
	 * target uri of class
	 */
	private final URI targetUri;
	/**
	 * source name of class
	 */
	private String sourceName;
	/**
	 * target name of class
	 */
	private String targetName;

	/**
	 * constructor
	 * 
	 * @param sourceUri
	 *            source uri of class
	 * @param targetUri
	 *            target uri of class
	 */
	public AutomatedClassMatchingNode(URI sourceUri, URI targetUri) {
		this.sourceUri = sourceUri;
		this.targetUri = targetUri;
		final String sourceFragment = sourceUri.getFragment();
		final String targetFragment = targetUri.getFragment();
		if (sourceFragment != null) {
			this.sourceName = sourceFragment;
		} else {
			final String path = sourceUri.getPath();
			this.sourceName = path.substring(path.lastIndexOf('/') + 1);
		}
		if (targetFragment != null) {
			this.targetName = targetFragment;
		} else {
			final String path = targetUri.getPath();
			this.targetName = path.substring(path.lastIndexOf('/') + 1);
		}
	}

	public URI getSourceUri() {
		return this.sourceUri;
	}

	public URI getTargetUri() {
		return this.targetUri;
	}

	public String getSourceName() {
		return this.sourceName;
	}

	public String getTargetName() {
		return this.targetName;
	}

	@Override
	public String toString() {
		String s = "";
		s += "sourceUri: " + this.sourceUri;
		s += "targetUri: " + this.targetUri;
		s += "sourceName: " + this.sourceName;
		s += "targetName: " + this.targetName;
		return s;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(19, 37).append(this.sourceUri).append(this.targetUri).append(this.sourceName)
				.append(this.targetName).toHashCode();
	}

	public final static Comparator<AutomatedClassMatchingNode> AUTOMATED_CLASS_MATCHING_NODE_COMPARATOR = (c1,
			c2) -> c1.sourceName.compareTo(c2.sourceName);
}
