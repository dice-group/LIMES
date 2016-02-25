package org.aksw.limes.core.io.query;

import com.hp.hpl.jena.query.*;

import java.util.Iterator;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.preprocessing.Preprocessor;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;


/**
 *
 * @author ngonga
 */
public class SparqlQueryModule implements IQueryModule {

	private Logger logger = Logger.getLogger(SparqlQueryModule.class.getName());
	protected KBInfo kb;

	public SparqlQueryModule(KBInfo kbinfo) {
		kb = kbinfo;
	}

	/**
	 * Reads from a SPARQL endpoint and writes the results in a cache
	 *
	 * @param cache The cache in which the content on the SPARQL endpoint is to be written
	 */
	public void fillCache(Cache cache) {
		fillCache(cache, true);
	}

	/**
	 * Reads from a SPARQL endpoint or a file and writes the results in a cache
	 *
	 * @param cache The cache in which the content on the SPARQL endpoint is to be written
	 * @param isSparql True if the endpoint is a remote SPARQL endpoint, else assume that is is a Jena model
	 */
	public void fillCache(Cache cache, boolean isSparql) {
		long startTime = System.currentTimeMillis();
		String query = generateQuery();

		//run query
		logger.info("Querying the endpoint.");
		int offset = 0;
		boolean moreResults = false;
		int counter = 0;
		String basicQuery = query;
		do {
			logger.info("Getting statements " + offset + " to " + (offset + kb.getPageSize()));
			if (kb.getPageSize() > 0) {
				query = basicQuery + " LIMIT " + kb.getPageSize() + " OFFSET " + offset;
			}else{
				query = basicQuery;
			}
			Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
			QueryExecution qexec;

			// take care of graph issues. Only takes one graph. Seems like some sparql endpoint do
			// not like the FROM option.
			if (!isSparql) {
				Model model = ModelRegistry.getInstance().getMap().get(kb.getEndpoint());
				if (model == null) {
					throw new RuntimeException("No model with id '" + kb.getEndpoint() + "' registered");
				}
				qexec = QueryExecutionFactory.create(sparqlQuery, model);
			} else {
				if (kb.getGraph() != null) {
					qexec = QueryExecutionFactory.sparqlService(kb.getEndpoint(), sparqlQuery, kb.getGraph());
				} //
				else {
					qexec = QueryExecutionFactory.sparqlService(kb.getEndpoint(), sparqlQuery);
				}
			}
			ResultSet results = qexec.execSelect();

			//write            
			String uri, propertyLabel, rawValue, value;
			try {
				if (results.hasNext()) {
					moreResults = true;
				} else {
					moreResults = false;
					break;
				}

				while (results.hasNext()) {
					QuerySolution soln = results.nextSolution();
					{
						try {
							uri = soln.get(kb.getVar().substring(1)).toString();
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
			} catch (Exception e) {
				logger.warn("Exception while handling query");
				logger.warn(e.toString());
				logger.warn("XML = \n" + ResultSetFormatter.asXMLString(results));
			} finally {
				qexec.close();
			}
			offset = offset + kb.getPageSize();
		} while (moreResults && kb.getPageSize() > 0);
		logger.info("Retrieved " + counter + " triples and " + cache.size() + " entities.");
		logger.info("Retrieving statements took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
	}

	protected String generateQuery() {
		//write prefixes
		Iterator<String> iter = kb.getPrefixes().keySet().iterator();
		String key, query = "";
		while (iter.hasNext()) {
			key = iter.next();
			query = query + "PREFIX " + key + ": <" + kb.getPrefixes().get(key) + ">\n";
		}
		// fill in variable for the different properties to be retrieved
		query = query + "SELECT DISTINCT " + kb.getVar();
		for (int i = 0; i < kb.getProperties().size(); i++) {
			query = query + " ?v" + i;
		}
		query = query + "\n";
		// graph
		if (kb.getGraph() != null) {
			if (!kb.getGraph().equals(" ") && kb.getGraph().length() > 3) {
				logger.info("Query Graph: " + kb.getGraph());
				query = query + "FROM <" + kb.getGraph() + ">\n";
			} else {
				kb.setGraph(null);
			}
		}
		//restriction
		if (kb.getRestrictions().size() > 0) {
			String where;
			iter = kb.getRestrictions().iterator();
			query = query + "WHERE {\n";
			for (int i = 0; i < kb.getRestrictions().size(); i++) {
				where = kb.getRestrictions().get(i).trim();
				if (where.length() > 3) {
					query = query + where + " .\n";
				}
			}
		}
		//properties
		String propertiesStr;
		if (kb.getProperties().size() > 0) {
			logger.info("Properties are " + kb.getProperties());
			propertiesStr = "";
			for (int i = 0; i < kb.getProperties().size(); i++) {
				propertiesStr = propertiesStr + kb.getVar() + " " + kb.getProperties().get(i) + " ?v" + i + " .\n";
			}
			//some endpoints and parsers do not support property paths. We replace them here with variables
			int varCount = 1;
			while (propertiesStr.contains("/")) {
				propertiesStr = propertiesStr.replaceFirst("/", " ?w" + varCount + " .\n?w" + varCount + " ");
				varCount++;
			}
			//close optional
			query = query + propertiesStr;
		}
		//properties
		String optionalPropertiesStr;
		if (kb.getOptionalProperties() != null && kb.getOptionalProperties().size() > 0) {
			logger.info("Optipnal properties are " + kb.getOptionalProperties());
			optionalPropertiesStr = "OPTIONAL {\n";
			for (int i = 0; i < kb.getProperties().size(); i++) {
				optionalPropertiesStr += kb.getVar() + " " + kb.getOptionalProperties().get(i) + " ?v" + i + " .\n";
			}
			//some endpoints and parsers do not support property paths. We replace them here with variables
			int varCount = 1;
			while (optionalPropertiesStr.contains("/")) {
				propertiesStr = optionalPropertiesStr.replaceFirst("/", " ?w" + varCount + " .\n?w" + varCount + " ");
				varCount++;
			}
			//close optional
			query = query + optionalPropertiesStr;
		}
		//finally replace variables in inverse properties
		String q[] = query.split("\n");
		query = "";
		for (int ql = 0; ql < q.length; ql++) {
			if(q[ql].contains("regex"))
				query = query + q[ql]+ "\n";
			else
				if (q[ql].contains("^")) {
					System.out.println(q[ql]);
					String[] sp = q[ql].replaceAll("\\^", "").split(" ");
					query = query + sp[2] + " " + sp[1] + " " + sp[0] + " " + sp[3] + "\n";
				} else {
					query = query + q[ql] + "\n";
				}
		}
		// close where
		if (kb.getRestrictions().size() > 0) {
			query = query + "}";
		}
		logger.info("Query issued is \n" + query);
		return query;
	}
}
