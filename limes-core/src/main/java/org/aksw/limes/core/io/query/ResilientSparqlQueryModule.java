package org.aksw.limes.core.io.query;

import com.hp.hpl.jena.query.*;

import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.aksw.jena_sparql_api.retry.core.QueryExecutionFactoryRetry;
import org.aksw.jena_sparql_api.timeout.QueryExecutionFactoryTimeout;
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
	
	private int retryCount 		= 5;
	private int retryDelayimMS 	= 10000;
	private int delayer 		= 1000;
	private int pageSize 		= 900;
	private long timeToLive 	= 24l * 60l * 60l * 1000l;

	public ResilientSparqlQueryModule(KBInfo kbInfo) {
		super(kbInfo);
	}
	
	public ResilientSparqlQueryModule(KBInfo kbInfo, int retryCount, int retryDelayimMS, int pageSize, long timeToLive) {
		this(kbInfo);
		this.retryCount = retryCount;
		this.retryDelayimMS = retryDelayimMS;
		this.pageSize = pageSize;
		this.timeToLive = timeToLive;
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
		qef = new QueryExecutionFactoryRetry(qef, retryCount, retryDelayimMS);
//		qef = new QueryExecutionFactoryDelay(qef, delayer);
//		qef = new QueryExecutionFactoryTimeout(qef, timeToLive);
//		QueryExecutionFactoryHttp foo = qef.unwrap(QueryExecutionFactoryHttp.class);
		qef = new QueryExecutionFactoryPaginated(qef, pageSize);
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
