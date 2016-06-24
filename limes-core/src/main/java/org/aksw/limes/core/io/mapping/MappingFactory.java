package org.aksw.limes.core.io.mapping;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Mohamed Sherif {@literal <}sherif {@literal @} informatik.uni-leipzig.de{@literal >}
 * @version Nov 12, 2015
 */
public class MappingFactory {
    private static final Logger logger = LoggerFactory.getLogger(MappingFactory.class.getName());

    /**
     * @return the default Mapping implementation
     */
    public static AMapping createDefaultMapping() {
        return new MemoryMapping();
    }

    /**
     * @param type of mapping
     * @return a specific module instance given its module's name
     * @author sherif
     */
    public static AMapping createMapping(MappingType type) {
        if (type == MappingType.DEFAULT)
            return createDefaultMapping();
        if (type == MappingType.MEMORY_MAPPING)
            return new MemoryMapping();
        if (type == MappingType.HYBIRD_MAPPING)
            return new HybridMapping();
        if (type == MappingType.FILE_MAPPING)
            return new FileMapping();
        logger.warn("Sorry, " + type + " is not yet implemented. Generating " + MappingType.DEFAULT + " map ...");
        return createDefaultMapping();
    }

    public enum MappingType {
        DEFAULT, // currently memory mapping
        MEMORY_MAPPING,
        HYBIRD_MAPPING,
        FILE_MAPPING
    }


}
