/**
 * 
 */
package org.aksw.limes.core.measures.mapper.topology.im;

import org.apache.log4j.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

/**
 * @author sherif
 *
 */
public class Reader {
	private static final Logger logger = Logger.getLogger(Reader.class);

	public static Model readModel(String fileNameOrUri) {
		return readModel(fileNameOrUri, "");
	}

	public static Model readModel(String fileNameOrUri, String subDir)
	{
		long startTime = System.currentTimeMillis();
		Model model=ModelFactory.createDefaultModel();
		java.io.InputStream in = FileManager.get().open( fileNameOrUri );
		if (in == null) {
			throw new IllegalArgumentException(fileNameOrUri + " not found");
		}
		if(fileNameOrUri.contains(".ttl") || fileNameOrUri.contains(".n3")){
			logger.info("Opening Turtle file");
			model.read(in, null, "TTL");
		}else if(fileNameOrUri.contains(".rdf")){
			logger.info("Opening RDFXML file");
			model.read(in, null);
		}else if(fileNameOrUri.contains(".nt")){
			logger.info("Opening N-Triples file");
			model.read(in, null, "N-TRIPLE");
		}else{
			logger.info("Content negotiation to get RDFXML from " + fileNameOrUri);

			model.read(fileNameOrUri);
		}
		logger.info("Loading " + fileNameOrUri + " is done in " + (System.currentTimeMillis()-startTime) + "ms.");
		return model;
	}

	public static void main(String args[]){
		readModel(args[0]).write(System.out, "TTL");
	}

}

