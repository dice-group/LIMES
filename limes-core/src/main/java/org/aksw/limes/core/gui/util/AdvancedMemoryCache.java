package org.aksw.limes.core.gui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;

/**
 * subclass of memory cache extended with {@link #getCommonProperties(Double, Integer)}
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
@SuppressWarnings("all")
public class AdvancedMemoryCache extends MemoryCache implements Serializable {
    private static final long serialVersionUID = 1L;

    public static AdvancedMemoryCache loadFromFile(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        ObjectInputStream deSerializer = new ObjectInputStream(in);
        AdvancedMemoryCache cache;
        try {
            cache = (AdvancedMemoryCache) deSerializer.readObject();
            return cache;
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        } finally {
            in.close();
        }
    }

    /**
     * @param threshold
     *         a value between 0 and 1, specifying what fraction of the instances must have this property
     *         for it to be counted as common property. Set to 0 if you want no restriction on this.
     * @param limit
     *         a non-negative integer value, specifying the maximum amount of properties to return.
     *         If there are more than limit after the exclusion with threshold, the most common properties of those are returned.
     * @return the most common properties sorted by occurrence in descending order.
     * Each property p is counted at most once for each instance s, even if there are multiple triples (s,p,o).  	 * Example: getCommonProperties(0.5) will only return properties which are used by at least half of the uris in the cache.
     */
    public String[] getCommonProperties(Double threshold, Integer limit) {
        if (threshold != null && (threshold < 0 || threshold > 1))
            throw new IllegalArgumentException("parameter relativeThreshold must lie between 0 and 1 inclusively.");
        final HashMap<String, Integer> propertyOccurrences = new HashMap<String, Integer>();
        for (String uri : this.getAllUris()) {
            Instance instance = this.getInstance(uri);
            for (String property : instance.getAllProperties()) {
                if (!propertyOccurrences.containsKey(property)) {
                    propertyOccurrences.put(property, 1);
                } else {
                    propertyOccurrences.put(property, propertyOccurrences.get(property) + 1);
                }
            }
        }

        List<String> allProperties = new LinkedList<String>(propertyOccurrences.keySet());
        // sort by occurrence in descending order

        Collections.sort(allProperties,
                new Comparator<String>() {
                    @Override
                    public int compare(String p1, String p2) {
                        int c = -(new Integer(propertyOccurrences.get(p1)).compareTo(propertyOccurrences.get(p2)));
                        // natural order is ascending but we want descending order, thats why the minus is there
                        if (c != 0) return c;
                        return p1.compareTo(p2);
                    }
                }
        );

        //for(String property:allProperties) {System.out.println("\\nolinkurl{"+PrefixHelper.abbreviate(URLDecoder.decode(property))+"}			&"+propertyOccurrences.get(property)+"\\\\");}

        if (threshold == null && limit == null) {
            return propertyOccurrences.keySet().toArray(new String[0]);
        }

        int absoluteThreshold = (int) (threshold * this.getAllInstances().size());

        List<String> properties = new LinkedList<String>();

        for (String property : allProperties) {
            if (properties.size() >= limit) break;
            if (propertyOccurrences.get(property) >= absoluteThreshold) {
                properties.add(property);
            }
        }


        return properties.toArray(new String[0]);
    }

}
