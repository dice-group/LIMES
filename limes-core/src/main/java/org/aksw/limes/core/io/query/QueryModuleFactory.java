package org.aksw.limes.core.io.query;


import org.aksw.limes.core.io.config.KBInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author ngonga
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 23, 2015
 */
public class QueryModuleFactory {

    static Logger logger = Logger.getLogger(QueryModuleFactory.class.getName());

    public static IQueryModule getQueryModule(String name, KBInfo kbinfo) {
        logger.info("Generating <" + name + "> reader");
        if (name.toLowerCase().startsWith("csv")) {
            return new CsvQueryModule(kbinfo);
        } 
        //processes N3 files
        else if (name.toLowerCase().startsWith("n3")||name.toLowerCase().startsWith("nt")) {
            kbinfo.setType("N3");
            return new FileQueryModule(kbinfo);
        } 
        //processes N-TRIPLE files
        else if (name.toLowerCase().startsWith("n-triple")) {
            kbinfo.setType("N-TRIPLE");
            return new FileQueryModule(kbinfo);
        }
                //process turtle files
        else if (name.toLowerCase().startsWith("turtle") || name.toLowerCase().startsWith("ttl")) {
            kbinfo.setType("TURTLE");
            return new FileQueryModule(kbinfo);
        } 
        //process rdf/xml files        
        else if (name.toLowerCase().startsWith("rdf") || name.toLowerCase().startsWith("xml")) {
            kbinfo.setType("RDF/XML");
            return new FileQueryModule(kbinfo);
        }
        else if (name.toLowerCase().startsWith("sparql")) {
            return new SparqlQueryModule(kbinfo);
        }
        else if (name.toLowerCase().startsWith("vector")) {
            return new VectorQueryModule(kbinfo);
        }
        //default
        return new ResilientSparqlQueryModule(kbinfo);
    }
}
