package org.aksw.limes.core.measures.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

/**
 * Implements the mapper abstract class.
 *
 * @author Axel-C. Ngonga Ngomo {@literal <}ngonga {@literal @}
 *         informatik.uni-leipzig.de{@literal >}
 * @version 1.0
 */
public abstract class AMapper implements IMapper {

    /**
     * Helper method, re-factored from common setup code of Mappers.
     *
     * @param cache,
     *            Input cache
     * @param property,
     *            Input linking property
     * @return reversed Map from literal values to resource uris for a specified
     *         property
     */
    protected Map<String, Set<String>> getValueToUriMap(ACache cache, String property) {
        Map<String, Set<String>> result = new HashMap<>();
        List<String> uris = cache.getAllUris();
        for (String uri : uris) {
            Set<String> values = cache.getInstance(uri).getProperty(property);
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
     * Helper method, re-factored from common return code blocks.
     *
     * @param valueMap
     *            Mapping from values to values with similarity score
     * @param sourceValueToUriMap
     *            ValueToUriMap constructed from a source cache
     * @param targetValueToUriMap
     *            ValueToUriMap constructed from a target cache
     * @param swapped
     *            True if source and target have been swapped in the valueMap
     * @return Mapping from source resource uri to target resource uri
     */
    protected AMapping getUriToUriMapping(Map<String, Map<String, Double>> valueMap,
            Map<String, Set<String>> sourceValueToUriMap, Map<String, Set<String>> targetValueToUriMap,
            boolean swapped) {
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

    protected AMapping getUriToUriMapping(Map<String, Map<String, Double>> valueMap,
            Map<String, Set<String>> sourceValueToUriMap, Map<String, Set<String>> targetValueToUriMap) {
        return getUriToUriMapping(valueMap, sourceValueToUriMap, targetValueToUriMap, false);
    }

    @Override
    public void setNo(int no) {
        // TODO Auto-generated method stub
        
    }
}
