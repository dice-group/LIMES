package org.aksw.limes.core.gui.model;

import java.net.URI;
import java.util.List;

public class ClassMatchingNode {
	private URI uri;
	private String name;
	private List<ClassMatchingNode> children;

	public ClassMatchingNode(URI uri, List<ClassMatchingNode> children) {
		this.uri = uri;
		String fragment = uri.getFragment();
		if (fragment != null) {
			this.name = fragment;
		} else {
			String path = uri.getPath();
			this.name = path.substring(path.lastIndexOf('/') + 1);
		}
		this.children = children;
	}

	public URI getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public List<ClassMatchingNode> getChildren() {
		return children;
	}
}
