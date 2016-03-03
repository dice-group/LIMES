package org.aksw.limes.core.io.query;

import com.hp.hpl.jena.query.*;

import java.sql.SQLException;

import org.aksw.jena_sparql_api.cache.core.QueryExecutionFactoryCacheEx;
import org.aksw.jena_sparql_api.cache.extra.CacheBackend;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontend;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontendImpl;
//import org.aksw.jena_sparql_api.cache.h2.CacheCoreH2;
import org.aksw.jena_sparql_api.delay.core.QueryExecutionFactoryDelay;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
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
	private int retryDelayimMS 	= 500;
	private int delayer 		= 1000;
	private int pageSize 		= 900;
	private long timeToLive 	= 24l * 60l * 60l * 1000l;
	private String cacheDirectory = "cache";

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
		org.aksw.jena_sparql_api.core.QueryExecutionFactory qef = null;
		try {
			qef = initQueryExecution(kb);
		} catch (Exception e) {
			e.printStackTrace();
		} 
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

	protected org.aksw.jena_sparql_api.core.QueryExecutionFactory initQueryExecution(KBInfo kbInfo) throws ClassNotFoundException, SQLException {
		org.aksw.jena_sparql_api.core.QueryExecutionFactory qef;
		if (kbInfo.getGraph() != null) {
			qef = new QueryExecutionFactoryHttp(kbInfo.getEndpoint(), kbInfo.getGraph());
		} else {
			qef = new QueryExecutionFactoryHttp(kbInfo.getEndpoint());
		}
		qef = new QueryExecutionFactoryDelay(qef, retryDelayimMS);
		//TODO FIX chache issue
//		if (cacheDirectory != null) {
//			CacheBackend cacheBackend = CacheCoreH2.create(true, cacheDirectory,
//					kbInfo.getEndpoint().replaceAll("[:/]", "_"), timeToLive, true);
//			CacheFrontend cacheFrontend = new CacheFrontendImpl(cacheBackend);
//			qef = new QueryExecutionFactoryCacheEx(qef, cacheFrontend);
//		} else {
//			logger.info("The cache directory has not been set. Creating an uncached SPARQL client.");
//		}
		try {
			return  new QueryExecutionFactoryPaginated(qef, pageSize);
		} catch (Exception e) {
			logger.warn("Couldn't create Factory with pagination. Returning Factory without pagination. Exception: " +
					e.getLocalizedMessage() );
			return qef;
		}
	}
}
