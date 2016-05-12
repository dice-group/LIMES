package org.aksw.limes.core.io.mapping;


import org.apache.log4j.Logger;


/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class MappingFactory {
	private static final Logger logger = Logger.getLogger(MappingFactory.class.getName());
	
	public enum MappingType{
		DEFAULT, // currently memory mapping 
		MEMORY_MAPPING,
		HYBIRD_MAPPING,
		FILE_MAPPING
	}
	

	/**
	 * @param type
	 * @return a specific module instance given its module's name
	 * @author sherif
	 */
	public static Mapping createMapping(MappingType type) {
		logger.info("Getting Mapping with name " + type);
		if(type == MappingType.DEFAULT)
			return new MemoryMapping();
		if(type == MappingType.MEMORY_MAPPING)
			return new MemoryMapping();
		if(type == MappingType.HYBIRD_MAPPING)
			return new HybridMapping();
		if(type == MappingType.FILE_MAPPING)
			return new FileMapping();
		logger.error("Sorry, " + type + " is not yet implemented. Exit with error ...");
		System.exit(1);
		return null;
	}



}
