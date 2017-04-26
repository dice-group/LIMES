package org.aksw.limes.core.measures.mapper;

import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
/**
 * TODO: move to the mapping package
 *  
 * Implements the mapping operations abstract class.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class MappingOperations {

    /**
     * Computes the difference of two mappings.
     *
     * @param map1
     *            First mapping
     * @param map2
     *            Second mapping
     * @return map1 \ map2
     */
    public static AMapping difference(AMapping map1, AMapping map2) {
        AMapping map = MappingFactory.createDefaultMapping();
        
        // go through all the keys in map1
        for (String key : map1.getMap().keySet()) {
            // if the first term (key) can also be found in map2
            if (map2.getMap().containsKey(key)) {
                // then go through the second terms and checks whether they can
                // be found in map2 as well
                for (String value : map1.getMap().get(key).keySet()) {
                    // if no, save the link
                    if (!map2.getMap().get(key).containsKey(value)) {
                        map.add(key, value, map1.getMap().get(key).get(value));
                    }
                }
            } else {
                map.add(key, map1.getMap().get(key));
            }
        }
        return map;
    }

    /**
     * Computes the intersection of two mappings. In case an entry exists in
     * both mappings the minimal similarity is taken.
     *
     * @param map1 First mapping
     * @param map2 Second mapping
     * @return Intersection of map1 and map2
     */
    public static AMapping intersection(AMapping map1, AMapping map2) {
        AMapping map = MappingFactory.createDefaultMapping();
        // takes care of not running the filter if some set is empty
        if (map1.size() == 0 || map2.size() == 0) {
            return MappingFactory.createDefaultMapping();
        }
        // go through all the keys in map1
        for (String key : map1.getMap().keySet()) {
            // if the first term (key) can also be found in map2
            if (map2.getMap().containsKey(key)) {
                // then go through the second terms and checks whether they can
                // be found in map2 as well
                for (String value : map1.getMap().get(key).keySet()) {
                    // if yes, take the highest similarity
                    if (map2.getMap().get(key).containsKey(value)) {
                        if (map1.getMap().get(key).get(value) <= map2.getMap().get(key).get(value)) {
                            map.add(key, value, map1.getMap().get(key).get(value));
                        } else {
                            map.add(key, value, map2.getMap().get(key).get(value));
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * Computes the union of two mappings. In case an entry exists in both
     * mappings the maximal similarity is taken.
     *
     * @param map1 First mapping
     * @param map2 Second mapping
     * @return Union of map1 and map2
     */
    public static AMapping union(AMapping map1, AMapping map2) {
        AMapping map = MappingFactory.createDefaultMapping();
        // go through all the keys in map1
        for (String key : map1.getMap().keySet()) {
            for (String value : map1.getMap().get(key).keySet()) {
                map.add(key, value, map1.getMap().get(key).get(value));
            }
        }
        for (String key : map2.getMap().keySet()) {
            // if the first term (key) can also be found in map2
            for (String value : map2.getMap().get(key).keySet()) {
                map.add(key, value, map2.getMap().get(key).get(value));
            }
        }
        return map;
    }
    
    
    /**
     * @param m1 AMapping
     * @param m2 AMapping
     * @param useScores, if set use usual similarity values otherwise use boolean similarity
     * @return m = m1 * m2 (Matrix multiplication)
     */
    public static AMapping multiply(AMapping m1, AMapping m2, boolean useScores) {
        AMapping result = MappingFactory.createDefaultMapping();
        Set<String> allM2TargetUris = new HashSet<String>();
        for (String m2SourceUri : m2.getMap().keySet()) {
            for (String m2TargetUri : m2.getMap().get(m2SourceUri).keySet()) {
                allM2TargetUris.add(m2TargetUri);
            }
        }
        for (String m1SourceUri : m1.getMap().keySet()) {
            double value;
            for (String m2TargetUri : allM2TargetUris) {
                value = 0;
                //compute score for (m1SourceUri, allM2TargetUris) via other uris
                for (String m1TargetUri : m1.getMap().get(m1SourceUri).keySet()) {
                    if (m2.contains(m1TargetUri, m2TargetUri)) {
                        if (useScores) {
                            value += m1.getConfidence(m1SourceUri, m1TargetUri) * m2.getConfidence(m1TargetUri, m2TargetUri);
                        } else {
                            value++;
                        }
                        result.add(m1SourceUri, m2TargetUri, value);
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param m1 AMapping
     * @param m2 AMapping
     * @param useScores, if set use usual similarity values otherwise use boolean similarity
     * @return m = m1 - m2
     * @author Sherif
     */
    public static AMapping subtract(AMapping m1, AMapping m2, boolean useScores) {
        AMapping result = MappingFactory.createDefaultMapping();
        double value = 0;
        for (String m1SourceUri : m1.getMap().keySet()) {
            for (String m1TargetUri : m1.getMap().get(m1SourceUri).keySet()) {
                if (m2.contains(m1SourceUri, m1TargetUri)) {
                    value = Math.abs(result.getConfidence(m1SourceUri, m1TargetUri) - m2.getConfidence(m1SourceUri, m1TargetUri));
                    result.add(m1SourceUri, m1TargetUri, value);
                } else {
                    result.add(m1SourceUri, m1TargetUri, m2.getConfidence(m1SourceUri, m1TargetUri));
                }
            }
        }
        return result;
    }


    /**
     * @param m1 AMapping
     * @param m2 AMapping
     * @param useScores, if set use usual similarity values otherwise use boolean similarity
     * @return m = m1 + m2
     * @author Sherif
     */
    public static AMapping add(AMapping m1, AMapping m2, boolean useScores) {
        AMapping result = MappingFactory.createDefaultMapping();
        double simValue = 0d;
        for (String m1SourceUri : m1.getMap().keySet()) {
            for (String m1TargetUri : m1.getMap().get(m1SourceUri).keySet()) {
                simValue = 0d;
                if (m2.contains(m1SourceUri, m1TargetUri)) {
                    if (useScores) {
                        simValue = m1.getConfidence(m1SourceUri, m1TargetUri) + m2.getConfidence(m1SourceUri, m1TargetUri);
                    } else {
                        simValue = Math.ceil(m1.getConfidence(m1SourceUri, m1TargetUri)) + Math.ceil(m2.getConfidence(m1SourceUri, m1TargetUri));
                    }
                } else {
                    if (useScores) {
                        simValue = m1.getConfidence(m1SourceUri, m1TargetUri);
                    } else {
                        simValue = Math.ceil(m1.getConfidence(m1SourceUri, m1TargetUri));
                    }
                }
                result.add(m1SourceUri, m1TargetUri, simValue);
            }
        }
        // Check if some of m2 not included in the result and also add them
        for (String m2SourceUri : m2.getMap().keySet()) {
            for (String m2TargetUri : m2.getMap().get(m2SourceUri).keySet()) {
                if (!result.contains(m2SourceUri, m2TargetUri)) {
                    if (useScores) {
                        result.add(m2SourceUri, m2TargetUri, m2.getConfidence(m2SourceUri, m2TargetUri));
                    } else {
                        result.add(m2SourceUri, m2TargetUri, Math.ceil(m2.getConfidence(m2SourceUri, m2TargetUri)));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Multiply each confidence value of mapping by the given scalar scalar
     *
     * @param mapping input AMapping
     * @param scalar for multiplication
     * @return mapping with entries (uri1, uri2, value / n)
     */
    
    public static AMapping scalarMultiply(AMapping mapping, double scalar) {
        AMapping result = MappingFactory.createDefaultMapping();
        for (String sourceUri : mapping.getMap().keySet()) {
            for (String targetUri : mapping.getMap().get(sourceUri).keySet()) {
                double val = mapping.getConfidence(sourceUri, targetUri);
                result.add(sourceUri, targetUri, val / scalar);
            }
        }
        return result;
    }

}
