/**
 *
 */
package org.aksw.limes.core.io.mapping;

import java.util.HashSet;
import java.util.Set;


/**
 * @author sherif
 *
 */
public class MappingMath {

    /**
     * @param m1 AMapping
     * @param m2 AMapping
     * @param useScores, if set use usual similarity values otherwise use boolean similarity
     * @return m = m1 * m2 (Matrix multip)
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
        for (String uri1 : mapping.getMap().keySet()) {
            for (String uri2 : mapping.getMap().get(uri1).keySet()) {
                double val = mapping.getConfidence(uri1, uri2);
                mapping.getMap().get(uri1).remove(uri2);
                mapping.add(uri1, uri2, val / scalar);
            }
        }
        return mapping;
    }




}
