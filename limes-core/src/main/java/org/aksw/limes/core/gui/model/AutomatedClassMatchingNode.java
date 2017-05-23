package org.aksw.limes.core.gui.model;

import java.io.Serializable;
import java.net.URI;
import java.util.Comparator;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * used to display classes in org.aksw.limes.core.gui.view.EditAutomatedClassMatchingView
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class AutomatedClassMatchingNode implements Serializable {
    private static final long serialVersionUID = -2983091498542870621L;
    /**
     * source uri of class
     */
    private URI sourceUri;
    /**
     * target uri of class
     */
    private URI targetUri;
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
     * @param sourceUri source uri of class
     * @param targetUri target uri of class
     */
    public AutomatedClassMatchingNode(URI sourceUri, URI targetUri) {
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
        String sourceFragment = sourceUri.getFragment();
        String targetFragment = targetUri.getFragment();
        if (sourceFragment != null) {
            this.sourceName = sourceFragment;
        } else {
            String path = sourceUri.getPath();
            this.sourceName = path.substring(path.lastIndexOf('/') + 1);
        }
        if (targetFragment != null) {
            this.targetName = targetFragment;
        } else {
            String path = targetUri.getPath();
            this.targetName = path.substring(path.lastIndexOf('/') + 1);
        }
    }

    public URI getSourceUri() {
		return sourceUri;
	}


	public URI getTargetUri() {
		return targetUri;
	}


	public String getSourceName() {
		return sourceName;
	}


	public String getTargetName() {
		return targetName;
	}

	@Override
	public String toString(){
		String s = "";
		s += "sourceUri: " + sourceUri;
		s += "targetUri: " + targetUri;
		s += "sourceName: " + sourceName;
		s += "targetName: " + targetName;
		return s;
	}

    public int hashCode() {
      return new HashCodeBuilder(19, 37).
        append(sourceUri).
        append(targetUri).
        append(sourceName).
        append(targetName).
        toHashCode();
    }

    public final static Comparator<AutomatedClassMatchingNode> 
    	AUTOMATED_CLASS_MATCHING_NODE_COMPARATOR = new Comparator<AutomatedClassMatchingNode>(){
    	@Override public int compare( AutomatedClassMatchingNode c1, AutomatedClassMatchingNode c2 ) {
            return c1.sourceName.compareTo( c2.sourceName );
          }
    };
}
