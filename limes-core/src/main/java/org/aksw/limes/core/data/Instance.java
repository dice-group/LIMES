package org.aksw.limes.core.data;

import java.util.HashMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class Instance {
    private static final long serialVersionUID = -8613951110508439148L;
    private String uri;
    private HashMap<String, TreeSet<String>> properties;
    public double distance;

    public Instance(String string) {
	// TODO Auto-generated constructor stub
    }

    public String getUri() {
	// TODO Auto-generated method stub
	return null;
    }

    /**
     * Return all the values for a given property
     * 
     * @param propUri
     * @return TreeSet of values associated with this URI
     */
    public TreeSet<String> getProperty(String propUri) {
	// propUri = propUri.toLowerCase();
	if (properties.containsKey(propUri)) {
	    return properties.get(propUri);
	} else {
	    Logger logger = Logger.getLogger("LIMES");
	    logger.warn("Failed to access property <" + propUri + "> on " + uri);
	    // System.out.println(properties);
	    // System.exit(1);
	    return new TreeSet<String>();
	}
    }

    public void addProperty(String string, String string2) {
	// TODO Auto-generated method stub
	
    }

}
