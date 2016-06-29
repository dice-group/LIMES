package org.aksw.limes.core.gui.model;

import java.net.URI;
import java.util.List;

/**
 * used to display classes in {@link org.aksw.limes.core.gui.view.EditClassMatchingView}
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class ClassMatchingNode {
    /**
     * uri of class
     */
    private URI uri;
    /**
     * name of class
     */
    private String name;
    /**
     * children of class
     */
    private List<ClassMatchingNode> children;

    /**
     * constructor
     * @param uri uri of class
     * @param children children of class
     */
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

    /**
     * returns uri
     * @return uri
     */
    public URI getUri() {
        return uri;
    }

    /**
     * returns name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * returns children
     * @return children
     */
    public List<ClassMatchingNode> getChildren() {
        return children;
    }
}
