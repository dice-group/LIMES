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

    public static AMapping multiply(AMapping m1, AMapping m2, boolean useScores) {
        AMapping result = MappingFactory.createDefaultMapping();

        Set<String> allM2TargetUris = new HashSet<String>();
        for (String m2SourceUri : m2.map.keySet()) {
            for (String m2TargetUri : m2.map.get(m2SourceUri).keySet()) {
                allM2TargetUris.add(m2TargetUri);
            }
        }

        for (String m1SourceUri : m1.map.keySet()) {
            double value;
            for (String m2TargetUri : allM2TargetUris) {
                value = 0;
                //compute score for (m1SourceUri, allM2TargetUris) via other uris
                for (String m1TargetUri : m1.map.get(m1SourceUri).keySet()) {
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
     * @param m1
     * @param m2
     * @param useScores, if set use usual similarity values otherwise use
     * boolean similarity
     * @return m = m1 - m2
     * @author Sherif
     */
    public static AMapping subtract(AMapping m1, AMapping m2, boolean useScores) {
        AMapping result = MappingFactory.createDefaultMapping();
        double value = 0;
        for (String m1SourceUri : m1.map.keySet()) {
            for (String m1TargetUri : m1.map.get(m1SourceUri).keySet()) {
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
     * Removes subMap from mainMap
     * @param mainMap
     * @param subMap
     * @param useScores
     * @return subMap = mainMap / subMap
     * @author sherif
     */
    public static AMapping removeSubMap(AMapping mainMap, AMapping subMap) {
        AMapping result = MappingFactory.createDefaultMapping();
        double value = 0;
        for (String mainMapSourceUri : mainMap.map.keySet()) {
            for (String mainMapTargetUri : mainMap.map.get(mainMapSourceUri).keySet()) {
                if (!subMap.contains(mainMapSourceUri, mainMapTargetUri)) {
                    result.add(mainMapSourceUri, mainMapTargetUri, value);
                } 
            }
        }
        return result;
    }

    /**
     * @param m1
     * @param m2
     * @param useScores, if set use usual similarity values otherwise use
     * boolean similarity
     * @return m = m1 + m2
     * @author Sherif
     */
    public static AMapping add(AMapping m1, AMapping m2, boolean useScores) {
        AMapping result = MappingFactory.createDefaultMapping();
        double simValue = 0d;
        for (String m1SourceUri : m1.map.keySet()) {
            for (String m1TargetUri : m1.map.get(m1SourceUri).keySet()) {
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
        for (String m2SourceUri : m2.map.keySet()) {
            for (String m2TargetUri : m2.map.get(m2SourceUri).keySet()) {
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
     * Normalize a mapping by its values.
     *
     * @param m Mapping to normalize
     * @param Normalization factor.
     * @return m with entries (uri1, uri2, value / n)
     */
    public static AMapping normalize(AMapping m, int n) {
        for (String uri1 : m.map.keySet()) {
            for (String uri2 : m.map.get(uri1).keySet()) {
                double val = m.getConfidence(uri1, uri2);
                m.map.get(uri1).remove(uri2);
                m.add(uri1, uri2, val / n);
            }
        }
        return m;
    }

    /**
     * @param mapping
     * @param optimalSize
     * @return
     * @author sherif
     */
    public static double computeFMeasure(AMapping mapping, long optimalSize) {
        if (mapping == null || mapping.size() == 0 || optimalSize == 0) {
            return 0d;
        }
        double p = computePrecision(mapping, optimalSize);
        double r = computeRecall(mapping, optimalSize);
        if (p == 0 || r == 0) {
            return 0;
        }
        return 2 * p * r / (p + r);
    }

    /**
     * @param mapping
     * @param optimalMapSize
     * @return
     * @author sherif
     */
    public static double computeRecall(AMapping mapping, long optimalMapSize) {
        if (mapping == null || mapping.size() == 0 || optimalMapSize == 0) {
            return 0d;
        }

        long currentMappingSize = 0;
        for (String s : mapping.map.keySet()) {
            for (String t : mapping.map.get(s).keySet()) {
                if (s.equals(t)) {
                    currentMappingSize++;
                }
            }
        }
        return (double) currentMappingSize / (double) optimalMapSize;
    }

    /**
     * @param mapping
     * @param optimalMapSize
     * @return
     * @author Sherif
     */
    public static double computePrecision(AMapping mapping, long optimalMapSize) {
        if (mapping == null || optimalMapSize == 0 || mapping.getNumberofMappings() == 0) {
            return 0d;
        }

        long currentMappingSize = 0;
        for (String s : mapping.map.keySet()) {
            for (String t : mapping.map.get(s).keySet()) {
                if (s.equals(t)) {
                    currentMappingSize++;
                }
            }
        }
        return (double) currentMappingSize / (double) mapping.getNumberofMappings();
    }



}
