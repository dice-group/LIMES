
package org.aksw.limes.core.io.serializer;

import org.aksw.limes.core.io.mapping.Mapping;
import org.apache.log4j.Logger;


/**
 *
 * @author ngonga
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 25, 2015
 */
public class TabSeparatedSerializer extends NtSerializer{
	
	private static Logger logger = Logger.getLogger(TabSeparatedSerializer.class.getName());
	protected String seperator = "\t";

	@Override
	public void addStatement(String subject, String predicate, String object, double similarity)
	{
		statements.add(subject + seperator + object + seperator + similarity);
	}

	@Override
	public void printStatement(String subject, String predicate, String object, double similarity) {
		try {
			writer.println(subject + seperator + object + seperator + similarity);
		} catch (Exception e) {
			logger.warn("Error writing");
		}
	}

	public String getName()
	{
		return "TabSeparatedSerializer";
	}
	/**
	 * Gets a mapping and serializes it to a file in the N3 format. The method
	 * assume that the class already knows all the prefixes used in the uris and
	 * expands those.
	 *
	 * @param m Mapping to serialize
	 * @param predicate Predicate to use while serializing
	 * @param file File in which the mapping is to be serialized
	 */
	public void writeToFile(Mapping m, String predicate, String file) {
		open(file);

		if (m.size() > 0) {
			//first get the prefix used in the subjects
			//            String source = m.getMap().keySet().iterator().next();
			//            String target = m.getMap().get(source).keySet().iterator().next();
			for (String s : m.getMap().keySet()) {
				for (String t : m.getMap().get(s).keySet()) {
					writer.println("<" + s + ">\t<" + t + ">\t"+m.getConfidence(s, t));
				}
			}
		}
		close();
	}

	public String getFileExtension() {
		return "tsv";
	}
	
	public String getSeperator() {
		return seperator;
	}

	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}
}