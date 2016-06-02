package org.aksw.limes.core.measures.mapper.string;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author ngonga
 */
public class ExactMatchMapper extends Mapper {

    static Logger logger = Logger.getLogger(ExactMatchMapper.class.getName());

    /**
     * Computes a mapping between a source and a target.
     *
     * @param source
     *         Source cache
     * @param target
     *         Target cache
     * @param sourceVar
     *         Variable for the source dataset
     * @param targetVar
     *         Variable for the target dataset
     * @param expression
     *         Expression to process.
     * @param threshold
     *         Similarity threshold
     * @return A mapping which contains links between the source instances and
     * the target instances
     */
    @Override
    public AMapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
                               double threshold) {

        logger.info("Starting ExactMatchMapper");
        List<String> properties = PropertyFetcher.getProperties(expression, threshold);
        // if no properties then terminate
        if (properties.get(0) == null || properties.get(1) == null) {
            logger.fatal("Property values could not be read. Exiting");
            System.exit(1);
        }
        Map<String, Set<String>> sourceIndex = getValueToUriMap(source, properties.get(0));
        Map<String, Set<String>> targetIndex = getValueToUriMap(target, properties.get(1));
        AMapping m = MappingFactory.createDefaultMapping();
        boolean swapped = sourceIndex.keySet().size() > targetIndex.keySet().size();
        (swapped ? sourceIndex : targetIndex).keySet().stream().filter(targetIndex::containsKey).forEach(value -> {
            for (String sourceUri : (swapped ? sourceIndex : targetIndex).get(value)) {
                for (String targetUri : (swapped ? targetIndex : sourceIndex).get(value)) {
                    m.add(sourceUri, targetUri, 1d);
                }
            }
        });
        return m;
    }

    public Map<String, Set<String>> index(Cache c, String property) {
        Map<String, Set<String>> index = new HashMap<String, Set<String>>();
        for (String uri : c.getAllUris()) {
            TreeSet<String> values = c.getInstance(uri).getProperty(property);
            for (String v : values) {
                if (!index.containsKey(v)) {
                    index.put(v, new HashSet<>());
                }
                index.get(v).add(uri);
            }
        }
        return index;
    }

    @Override
    public String getName() {
        return "exactMatch";
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }
}
