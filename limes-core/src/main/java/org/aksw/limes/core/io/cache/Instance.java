package org.aksw.limes.core.io.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all the data related to a particular URI, i.e., all the (s p o)
 * statements where s is a particular URI. From the point of view of linking, it
 * an instance contains all the data linked to a particular instance ;)
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public class Instance implements Comparable<Object>, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Instance.class);

    /**
     *
     */
    private static final long serialVersionUID = -8613951110508439148L;
    public double distance;
    private String uri;
    private HashMap<String, TreeSet<String>> properties;

    /**
     * Constructor
     *
     * @param _uri
     *         URI of the instance. This is the key to accessing it.
     */
    public Instance(String _uri) {
        uri = _uri;
        properties = new HashMap<String, TreeSet<String>>();
        // distance to exemplar
        distance = -1;
    }

    /**
     * Add a new (property, value) pair
     *
     * @param propUri
     *         URI of the property
     * @param value
     *         value of the property for this instance
     */
    public void addProperty(String propUri, String value) {
        if (properties.containsKey(propUri)) {
            properties.get(propUri).add(value);
        } else {
            TreeSet<String> values = new TreeSet<String>();
            values.add(value);
            properties.put(propUri, values);
        }
    }

    public void addProperty(String propUri, TreeSet<String> values) {
        // propUri = propUri.toLowerCase();
        if (properties.containsKey(propUri)) {
            Iterator<String> iter = values.iterator();
            while (iter.hasNext()) {
                properties.get(propUri).add(iter.next());
            }
        } else {
            properties.put(propUri, values);
        }
    }

    /*
     * Removes the old values of propUri and replaces them with values
     */
    public void replaceProperty(String propUri, TreeSet<String> values) {
        if (properties.containsKey(propUri)) {
            properties.remove(propUri);
        }
        addProperty(propUri, values);
    }

    /**
     * Returns the URI of this instance
     *
     * @return URI of this instance
     */
    public String getUri() {
        return uri;
    }

    /**
     * Return all the values for a given property
     *
     * @param propUri property URI
     * @return TreeSet of values associated with this URI
     */
    public TreeSet<String> getProperty(String propUri) {
        if (properties.containsKey(propUri)) {
            return properties.get(propUri);
        } else {
            logger.debug("Failed to access property <" + propUri + "> on " + uri);
            return new TreeSet<String>();
        }
    }

    /**
     * Returns all the properties associated with this instance
     *
     * @return A set of property Uris
     */
    public Set<String> getAllProperties() {
        return properties.keySet();
    }

    @Override
    public String toString() {
        String s = uri;
        String propUri;
        Iterator<String> iter = properties.keySet().iterator();
        while (iter.hasNext()) {
            propUri = iter.next();
            s = s + "; " + "\n" + propUri + " -> " + properties.get(propUri);
        }
        return s + "; distance = " + distance + "\n";
    }

    /**
     * Comparison with other Instances
     *
     * @param o
     *         Instance for comparison
     * @return 1 if the distance from the exemplar to the current instance is
     * smaller than the distance from the exemplar to o.
     */
    public int compareTo(Object o) {
        if (!o.getClass().equals(Instance.class))
            return -1;
        double diff = distance - ((Instance) o).distance;
        if (diff < 0) {
            return 1;
        } else if (diff > 0) {
            return -1;
        } else {
            return ((Instance) o).uri.compareTo(uri);
        }
    }

    public Instance copy() {
        Instance instance = new Instance(uri);
        HashMap<String, TreeSet<String>> ps = new HashMap<String, TreeSet<String>>();
        for (String p : properties.keySet()) {
            ps.put(p, new TreeSet<String>());
            for (String s : properties.get(p)) {
                ps.get(p).add(s);
            }
        }
        instance.properties = ps;
        return instance;
    }

    /**
     * Removes property with URI uri from this Instance
     *
     * @param uri of the property to be removed
     */
    public void removePropery(String uri) {
        if (properties.containsKey(uri)) {
            properties.remove(uri);
        }
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(distance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instance other = (Instance) obj;
		if (Double.doubleToLongBits(distance) != Double.doubleToLongBits(other.distance))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
    
    
}

