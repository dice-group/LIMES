package org.aksw.limes.core.io.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class MappingFactory {
	private static final Logger logger = Logger.getLogger(MappingFactory.class.getName());

	protected static final String MEMORY_MAPPING 	= "memorymapping";
	protected static final String HYBIRD_MAPPING 	= "hybirdmapping";
	protected static final String FILE_MAPPING 		= "filrmapping";


	/**
	 * @param name
	 * @return a specific module instance given its module's name
	 * @author sherif
	 */
	public static Mapping createMapping(String name) {
		logger.info("Getting Mapping with name " + name);

		if(name.equalsIgnoreCase(MEMORY_MAPPING))
			return new MemoryMapping();
		if(name.equalsIgnoreCase(HYBIRD_MAPPING ))
			return new HybridMapping();
		if(name.equalsIgnoreCase(FILE_MAPPING ))
			return new FileMapping();
		logger.error("Sorry, " + name + " is not yet implemented. Exit with error ...");
		System.exit(1);
		return null;
	}


	/**
	 * @return list of names of all implemented operators
	 * @author sherif
	 */
	public static List<String> getNames(){
		return new ArrayList<String>(Arrays.asList(MEMORY_MAPPING, FILE_MAPPING, HYBIRD_MAPPING));
	}
}
