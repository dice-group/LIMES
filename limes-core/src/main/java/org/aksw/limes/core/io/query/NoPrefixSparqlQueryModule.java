package org.aksw.limes.core.io.query;

import com.hp.hpl.jena.query.*;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.KBInfo;
import org.apache.log4j.*;


/**
 *
 * @author ngonga
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 23, 2015
 */
public class NoPrefixSparqlQueryModule implements IQueryModule{

	KBInfo kb;

	public NoPrefixSparqlQueryModule(KBInfo kbinfo) {
		kb = kbinfo;
	}

	/** Reads from a SPARQL endpoint and writes the results in a cache
	 *
	 * @param cache The cache in which the content on the SPARQL endpoint is
	 * to be written
	 */
	public void fillCache(Cache cache) {

		Logger logger = Logger.getLogger("LIMES");
		long startTime = System.currentTimeMillis();
		String query = "";
		// fill in variable for the different properties to be retrieved
		query = query + "SELECT DISTINCT " + kb.getVar();
		for (int i = 0; i < kb.getProperties().size(); i++) {
			query = query + " ?v" + i;
		}
		query = query + "\n";
		//restrictions
		if (kb.getRestrictions().size() > 0) {
			String where;
			kb.getRestrictions().iterator();
			query = query + "WHERE {\n";
			for (int i = 0; i < kb.getRestrictions().size(); i++) {
				where = kb.getRestrictions().get(i);
				query = query +where + " .\n";
			}
		}
		//properties
		String optional;
		query = query + "OPTIONAL {";
		if (kb.getProperties().size() > 0) {
			logger.info("Properties are " + kb.getProperties());
			//optional = "OPTIONAL {\n";
			optional = "";
			//iter = kb.properties.iterator();
			for (int i = 0; i < kb.getProperties().size(); i++) {
				//optional = optional + kb.var + " " + kb.properties.get(i) + " ?v" + i + " .\n";
				optional = optional + kb.getVar() + " <" + kb.getProperties().get(i) + "> ?v" + i + " .\n";
			}
			query = query + optional;
		}
		query = query + "}";
		// close where
		if (kb.getRestrictions().size() > 0) {
			query = query + "}\n";
		}
		logger.info("Query issued is \n" + query);
		//query = query + " LIMIT 1000";
		logger.info("Querying the endpoint.");
		//run query
		int offset = 0;
		boolean moreResults = false;
		int counter=0;
		String basicQuery = query;
		do {
			logger.info("Getting statements " + offset + " to " + (offset + kb.getPageSize()));
			if (kb.getPageSize() > 0) {
				query = basicQuery + " LIMIT " + kb.getPageSize() + " OFFSET " + offset;
			}

			Query sparqlQuery = QueryFactory.create(query);
			QueryExecution qexec;
			// take care of graph issues. Only takes one graph. Seems like some sparql endpoint do
			// not like the FROM option.
			qexec = QueryExecutionFactory.sparqlService(kb.getEndpoint(), sparqlQuery);
			logger.info("No default graph "+kb.getGraph());
			ResultSet results = qexec.execSelect();

			//write
			String uri, property, value;
			try {
				if (results.hasNext()) {
					moreResults = true;
				} else {
					moreResults = false;
					break;
				}
				while (results.hasNext()) {
					QuerySolution soln = results.nextSolution();
					// process query here
					{
						try {
							//first get uri
							uri = soln.get(kb.getVar().substring(1)).toString();
							//now get (p,o) pairs for this s
							String split[];
							for (int i = 0; i < kb.getProperties().size(); i++) {
								property = kb.getProperties().get(i);
								if (soln.contains("v" + i)) {
									value = soln.get("v" + i).toString();
									//remove localization information, e.g. @en
									if (value.contains("@")) {
										value = value.substring(0, value.indexOf("@"));
									}
									if (value.contains("^^")) {
										if(value.contains(":date"))
										{
											value = value.substring(0, value.indexOf("^^"));

											if(value.contains(" ")) value = value.substring(0, value.indexOf(" "));
											split = value.split("-");
											value = Integer.parseInt(split[0])*365
													+ Integer.parseInt(split[1])*12
													+ Integer.parseInt(split[2])+"";
										}else {
											value = value.substring(0, value.indexOf("^^"));
										}
									}
									cache.addTriple(uri, property, value);
								}else{
									cache.addTriple(uri, property, "");
								}
							}

						} catch (Exception e) {
							logger.warn("Error while processing: " + soln.toString());
							logger.warn("Following exception occured: " + e.getMessage());
							logger.info("Processing further ...");
						}
					}
					counter++;
				}

			} catch (Exception e) {
				logger.warn("Exception while handling query");
				logger.warn(e.toString());
				logger.warn("XML = \n"+ResultSetFormatter.asXMLString(results));
			} finally {
				qexec.close();
			}
			offset = offset + kb.getPageSize();
		} while (moreResults && kb.getPageSize() > 0);
		logger.info("Retrieved " + counter + " triples and " + cache.size() + " entities.");
		logger.info("Retrieving statements took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
	}

}
