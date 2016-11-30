package org.aksw.limes.core.io.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 25, 2015
 */
public class SerializerFactory {
    private static Logger logger = LoggerFactory.getLogger(SerializerFactory.class.getName());

    public static ISerializer createSerializer(String name) {
        logger.info("Getting serializer with name " + name);
        if (name == null) 
            return new NtSerializer();
        if (name.toLowerCase().trim().startsWith("tab")) 
            return new TabSeparatedSerializer();
        if (name.toLowerCase().trim().startsWith("csv")) 
            return new CSVSerializer();
        if (name.toLowerCase().trim().startsWith("ttl") || name.toLowerCase().trim().startsWith("turtle"))
            return new TTLSerializer();
        if (name.toLowerCase().trim().startsWith("nt") || name.toLowerCase().trim().startsWith("turtle"))
            return new NtSerializer();
        else {
            logger.info("Serializer with name " + name + " not found. Using .nt as default format.");
            return new NtSerializer();
        }
    }


    /**
     * Get all available serializer.
     *
     * @return Array of Serializers.
     */
    public static ISerializer[] getAllSerializers() {
        return new ISerializer[]{createSerializer("nt"), createSerializer("csv"), createSerializer("tab"), createSerializer("ttl")};
    }
}
