package org.aksw.limes.core.io.query;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.KBInfo;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.riot.RiotNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import weka.Run;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class FileQueryModule implements IQueryModule {

    KBInfo kb;
    Model model;
    private Logger logger = LoggerFactory.getLogger(FileQueryModule.class.getName());

    /**
     * Constructor
     *
     * @param kbinfo
     *         Loads the endpoint as a file and if that fails as a resource.
     */
    @SuppressWarnings("resource")
    public FileQueryModule(KBInfo kbinfo) {
        try {

            kb = kbinfo;
            model = ModelFactory.createDefaultModel();
            System.out.println("Trying to get reader " + kb.getType());
            try {
                model.read(kb.getEndpoint(), kb.getType());
            } catch (RiotNotFoundException e) {
                InputStream in = getClass().getClassLoader().getResourceAsStream(kb.getEndpoint());
                if (in == null) {
                    logger.error(MarkerFactory.getMarker("FATAL"),"endpoint could not be loaded as a file or resource");
                    throw new RuntimeException(e);
                }
                RDFReader r = model.getReader(kb.getType());
                InputStreamReader reader = new InputStreamReader(in, "UTF8");
                r.read(model, reader, null);
                reader.close();
                in.close();
            }
            logger.info("RDF model read from " + kb.getEndpoint() + " is of size " + model.size());
            ModelRegistry.register(kb.getEndpoint(), model);


        } catch (Exception e) {
            logger.error(MarkerFactory.getMarker("FATAL"),"Error loading endpoint", e);
        }
    }

    /**
     * Reads data from a model in the model registry
     *
     * @param c
     *         Cache to be filled
     */
    public void fillCache(ACache c) {
        SparqlQueryModule sqm = new SparqlQueryModule(kb);
        sqm.fillCache(c, false);

    }

}
