package org.aksw.limes.core.io.serializer;

import org.apache.log4j.Logger;



/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 25, 2015
 */
public class CSVSerializer extends TabSeparatedSerializer {
	private static Logger logger = Logger.getLogger(CSVSerializer.class.getName());
	public String SEPARATOR = ",";

	public String getName() {
		return "CommaSeparatedSerializer";
	}

	@Override
	public void printStatement(String subject, String predicate, String object, double similarity) {
		try {
			writer.println("\"" + subject + "\"" + SEPARATOR + "\"" + object + "\"" + SEPARATOR + similarity);
		}
		catch (Exception e) {
			logger.warn("Error writing");
		}
	}

	public String getFileExtension() {
		return "csv";
	}


}
