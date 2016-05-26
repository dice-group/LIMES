package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

import java.util.*;

public abstract class Mapper implements IMapper {

    /**
     * Helper method, refactored from common setup code of Mappers
     *
     * @param c
     *         Input cache
     * @param property
     *         Maps underlying property
     * @return reversed Map from literal values to resource uris for a specified property
     */
    protected Map<String, Set<String>> getValueToUriMap(Cache c, String property) {
        Map<String, Set<String>> result = new HashMap<>();
        List<String> uris = c.getAllUris();
        for (String uri : uris) {
            Set<String> values = c.getInstance(uri).getProperty(property);
            for (String value : values) {
                if (!result.containsKey(value)) {
                    result.put(value, new HashSet<>());
                }
                result.get(value).add(uri);
            }
        }
        return result;
    }

    /**
     * Helper method, refactored from common return code blocks.
     *
     * @param valueMap
     *         Mapping from values to values with similarity score
     * @param sourceValueToUriMap
     *         ValueToUriMap constructed from a source cache
     * @param targetValueToUriMap
     *         ValueToUriMap constructed from a target cache
     * @param swapped
     *         True if source and target have been swapped in the valueMap
     * @return Mapping from source resource uri to target resource uri
     */
    protected AMapping getUriToUriMapping(Map<String, Map<String, Double>> valueMap, Map<String, Set<String>> sourceValueToUriMap, Map<String, Set<String>> targetValueToUriMap, boolean swapped) {
        AMapping result = MappingFactory.createDefaultMapping();
        for (String s : valueMap.keySet()) {
            for (String t : valueMap.get(s).keySet()) {
                if (sourceValueToUriMap.get(swapped ? t : s) != null)
                    for (String sourceUri : sourceValueToUriMap.get(swapped ? t : s)) {
                        for (String targetUri : targetValueToUriMap.get(swapped ? s : t)) {
                            result.add(sourceUri, targetUri, valueMap.get(s).get(t));
                        }
                    }
            }
        }
        return result;
    }

    protected AMapping getUriToUriMapping(Map<String, Map<String, Double>> valueMap, Map<String, Set<String>> sourceValueToUriMap, Map<String, Set<String>> targetValueToUriMap) {
        return getUriToUriMapping(valueMap, sourceValueToUriMap, targetValueToUriMap, false);
    }

}
