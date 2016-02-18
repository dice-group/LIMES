package org.aksw.limes.core.io.query;

import com.hp.hpl.jena.query.*;

import java.util.Iterator;

import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.aksw.jena_sparql_api.retry.core.QueryExecutionFactoryRetry;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.preprocessing.Preprocessor;
import org.apache.log4j.Logger;



/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 *
 */
public class ResilientSparqlQueryModule extends SparqlQueryModule implements IQueryModule {

	private Logger logger = Logger.getLogger(ResilientSparqlQueryModule.class.getName());
	private KBInfo kb;

	public ResilientSparqlQueryModule(KBInfo kbinfo) {
		super(kbinfo);
	}


	/**
	 * Reads from a SPARQL endpoint or a file and writes the results in a cache
	 *
	 * @param cache The cache in which the content on the SPARQL endpoint is to be written
	 * @param sparql True if the endpoint is a remote SPARQL endpoint, else assume that is is a jena model
	 */
	public void fillCache(Cache cache, boolean sparql) {
		long startTime = System.currentTimeMillis();
		String query = generateQuery();

		logger.info("Querying the endpoint.");
		//run query
		org.aksw.jena_sparql_api.core.QueryExecutionFactory qef = new QueryExecutionFactoryHttp(kb.getEndpoint(), kb.getGraph());
//		qef = new QueryExecutionFactoryRetry(qef, 5, 10000);
		qef = new QueryExecutionFactoryDelay(qef, 5000);
		//		long timeToLive = 24l * 60l * 60l * 1000l; 
		QueryExecutionFactoryHttp foo = qef.unwrap(QueryExecutionFactoryHttp.class);
		System.out.println(foo);
		qef = new QueryExecutionFactoryPaginated(qef, 900);
		QueryExecution qe = qef.createQueryExecution(query);
		int counter = 0;
		ResultSet results = qe.execSelect();
		//write            
		String uri, propertyLabel, rawValue, value;
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			// process query here
			{
				try {
					//first get uri
					uri = soln.get(kb.getVar().substring(1)).toString();
					//now get (p,o) pairs for this s
					for (int i = 0; i < kb.getProperties().size(); i++) {
						propertyLabel = kb.getProperties().get(i);
						if (soln.contains("v" + i)) {
							rawValue = soln.get("v" + i).toString();
							//remove localization information, e.g. @en
							for (String propertyDub : kb.getFunctions().get(propertyLabel).keySet()) {
								value = Preprocessor.process(rawValue, kb.getFunctions().get(propertyLabel).get(propertyDub));
								cache.addTriple(uri, propertyDub, value);
							}
						}
					}
				} catch (Exception e) {
					logger.warn("Error while processing: " + soln.toString());
					logger.warn("Following exception occured: " + e.getMessage());
					e.printStackTrace();
					System.exit(1);
					logger.info("Processing further ...");
				}
			}
			counter++;
		}
		logger.info("Retrieved " + counter + " triples and " + cache.size() + " entities.");
		logger.info("Retrieving statements took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
	}
}
