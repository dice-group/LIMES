package org.aksw.limes.core.io.query;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.KBInfo;
import org.apache.log4j.Logger;

/**
 * @author ngonga
 * Can load from a resource as well.
 */
public class FileQueryModule implements IQueryModule {

	private Logger logger = Logger.getLogger(FileQueryModule.class.getName());

	KBInfo kb;
	Model model;

	/** Constructor
	 * 
	 * @param kbinfo
	 * Loads the endpoint as a file and if that fails as a resource. 
	 */
	@SuppressWarnings("resource")
	public FileQueryModule(KBInfo kbinfo) {
		try{
			InputStream in;
			kb = kbinfo;
			model = ModelFactory.createDefaultModel();
			System.out.println("Trying to get reader " + kb.getType());
			RDFReader r = model.getReader(kb.getType());

			try{
				in = new FileInputStream(kb.getEndpoint());
			} catch(FileNotFoundException e){
				in = getClass().getClassLoader().getResourceAsStream(kb.getEndpoint());
				if(in == null){
					logger.fatal("endpoint could not be loaded as a file or resource");
					return;
				}
			}       
			InputStreamReader reader = new InputStreamReader(in, "UTF8");
			r.read(model, reader, null);
			logger.info("RDF model read from "+ kb.getEndpoint() + " is of size " + model.size());
			ModelRegistry.register(kb.getEndpoint(), model);
			reader.close();
			in.close();
		} catch(Exception e){
			logger.fatal("Error loading endpoint", e);
		} 		
	}

	/** Reads data from a model in the model registry
	 * 
	 * @param c Cache to be filled
	 */
	public void fillCache(Cache c) {
		SparqlQueryModule sqm = new SparqlQueryModule(kb);
		sqm.fillCache(c, false);

	}

}
