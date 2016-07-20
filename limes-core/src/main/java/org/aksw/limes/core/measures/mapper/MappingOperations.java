package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
/**
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
     * @param map1
     *            First mapping
     * @param map2
     *            Second mapping
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
     * @param map1
     *            First mapping
     * @param map2
     *            Second mapping
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

}
