/**
 * 
 */
package org.aksw.limes.core.measures.mapper.topology.im;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.log4j.Logger;

/**
 * @author sherif
 *
 */
public class Writer {

	private static final Logger logger = Logger.getLogger(Writer.class.getName());
	private static String subDir = "";

	public static void writeModel(Model model, String format, String outputFile) throws IOException
	{
		logger.info("Saving dataset to " + outputFile + "...");
		long starTime = System.currentTimeMillis();
		FileWriter fileWriter = new FileWriter(subDir + outputFile);
		model.write(fileWriter, format);
		logger.info("Saving file done in " + (System.currentTimeMillis() - starTime) +"ms.");
	}

    public static String getSubDir() {
        return subDir;
    }

    public static void setSubDir(String subDir) {
        Writer.subDir = subDir;
    }
}