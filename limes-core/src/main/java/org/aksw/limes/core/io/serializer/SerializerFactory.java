package org.aksw.limes.core.io.serializer;

import org.apache.log4j.Logger;

/**
 *
 * @author ngonga
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 25, 2015
 */
public class SerializerFactory {
	private static Logger logger = Logger.getLogger(SerializerFactory.class.getName());
	
    public static ISerializer getSerializer(String name){
        logger.info("Getting serializer with name "+name);
        if(name==null) return new NtSerializer();
        if(name.toLowerCase().trim().startsWith("tab")) return new TabSeparatedSerializer();
        if(name.toLowerCase().trim().startsWith("csv")) return new CSVSerializer();
        if(name.toLowerCase().trim().startsWith("ttl") || name.toLowerCase().trim().startsWith("turtle")) return new TtlSerializer();
        if(name.toLowerCase().trim().startsWith("nt") || name.toLowerCase().trim().startsWith("turtle")) return new NtSerializer();
        else 
        {
            logger.info("Serializer with name " + name + " not found. Using .nt as default format.");
            return new NtSerializer();
        }
    }
    
    
    /**
     * Get all available serializer.
     * @return Array of Serializers.
     */
    public static ISerializer[] getAllSerializers() {
    	return new ISerializer[] {getSerializer("nt"), getSerializer("csv"), getSerializer("tab"), getSerializer("ttl")}; 
    }
}
