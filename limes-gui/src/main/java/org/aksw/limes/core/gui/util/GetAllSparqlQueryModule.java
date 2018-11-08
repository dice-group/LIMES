package org.aksw.limes.core.gui.util;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.lang.reflect.Field;

import org.aksw.limes.core.gui.util.sparql.SPARQLHelper;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.aksw.limes.core.io.query.SparqlQueryModule;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple modification of SparqlQueryModule but instead of only returning a part
 * this class gets everything. With everything is meant: all properties and
 * their names and their objects but still only the restricted ones.
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
@SuppressWarnings("all")
public class GetAllSparqlQueryModule extends SparqlQueryModule {
	private final static Logger LOGGER = LoggerFactory.getLogger(GetAllSparqlQueryModule.class);
	// amout of subjects read in
	Integer subjectLimit = null;

	/**
	 * constructor calling super constructor
	 * 
	 * @param kbinfo
	 *            kbinfo
	 */
	public GetAllSparqlQueryModule(KBInfo kbinfo) {
		super(kbinfo);
	}

	/**
	 * constructor calling super construcor and setting subjectLimit
	 * 
	 * @param kbinfo
	 *            kbinfo
	 * @param subjectLimit
	 *            subjectLimit
	 */
	public GetAllSparqlQueryModule(KBInfo kbinfo, Integer subjectLimit) {
		super(kbinfo);
		this.subjectLimit = subjectLimit;
	}

	/**
	 * executes query by calling
	 * {@link SPARQLHelper#querySelect(String, String, String, Model)}
	 * 
	 * @param query
	 *            query
	 * @param kb
	 *            kb
	 * @return ResultSet
	 */
	public static ResultSet querySelect(String query, KBInfo kb) {
		final Model model = ModelRegistry.getInstance().getMap().get(kb.getEndpoint());
		SPARQLHelper.formatPrefixes(kb.getPrefixes());
		return SPARQLHelper.querySelect(query, kb.getEndpoint(), kb.getGraph(), model);
	}

	/**
	 * superclass member variable kb is private, access via reflection
	 */
	protected KBInfo getKB() {
		// this.kb=null;
		try {
			// System.out.println(Arrays.toString(SparqlQueryModule.class.getDeclaredFields()));
			final Field field = SparqlQueryModule.class.getDeclaredField("kb");
			field.setAccessible(true);
			return (KBInfo) field.get(this);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param paginate
	 *            with pagination set to false, no additional offset queries
	 *            will be generated after the first query
	 */
	@Override
	public void fillCache(ACache cache, boolean paginate) {
		try {
			this.fillCache(cache, null, paginate);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Jena hasNext() seems to be buggy and throws exceptions when resultset is
	 * empty, this is the workaround
	 */
	private boolean hasNext(ResultSet rs) {
		try {
			return rs.hasNext();
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * fills cache
	 * 
	 * @param cache
	 *            cache
	 * @param getOnlyThisProperty
	 *            getOnlyThisProperty
	 * @param paginate
	 *            with pagination set to false, no additional offset queries
	 *            will be generated after the first query
	 * @throws Exception
	 *             thrown if something goes wrong
	 */
	public void fillCache(final ACache cache, final String getOnlyThisProperty, final boolean paginate)
			throws Exception {
		final String var = this.getKB().getVar().replace("?", "");
		final long startTime = System.currentTimeMillis();
		String query = "";
		// write prefixes
		LOGGER.debug(this.getKB().getPrefixes().toString());
		// ***** begin workaround TODO: change back when error is resolved with
		// the drugbank sparql endpoint
		for (final String key : this.getKB().getPrefixes().keySet()) {
			query = query + "PREFIX " + key + ": <" + this.getKB().getPrefixes().get(key) + ">\n";
		}

		// query= query + "PREFIX drugbank:
		// <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>\n";
		query = query + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";

		// **** end workaround

		// get all triples of the chosen restriction
		query = query + "SELECT DISTINCT ?" + var + " ?p ?o ";
		query = query + "\n";

		// restriction
		query = query + "WHERE {?" + var + " ?p ?o. \n";
		if (this.subjectLimit != null) {
			query = query + "{ select ?" + var + " where {";
		}
		if (this.getKB().getRestrictions().size() > 0) {
			String where;

			for (final String restrictionStr : this.getKB().getRestrictions()) {
				where = Restriction.fromString(restrictionStr).toString(var);
				query = query + where + " .\n";
			}
		} else {
			query = query + '?' + var + " ?p ?o.";
		}
		if (getOnlyThisProperty != null) {
			query = query + '?' + var + " <" + getOnlyThisProperty + "> ?o.";
		}
		if (this.subjectLimit != null) {
			query = query + "} limit " + this.subjectLimit + "}";
		}
		// close where
		query = query + "}\n";

		// query = query + " LIMIT 1000";

		LOGGER.info("Querying the endpoint.");
		// run query

		int offset = 0;
		boolean moreResults = false;
		int counter = 0;
		final String basicQuery = query;
		do {
			if (this.getKB().getPageSize() > 0) {
				query = basicQuery + " LIMIT " + this.getKB().getPageSize() + " OFFSET " + offset;
			}
			// LOGGER.info("Following query was sent to endpoint <" +
			// getKB().endpoint + ">\n\n" + query);
			// System.out.println(query);
			// Query sparqlQuery = QueryFactory.create(query);
			// QueryExecution qexec;
			// // take care of graph issues. Only takes one graph. Seems like
			// some sparql endpoint do
			// // not like the FROM option.
			// // it is important to
			// if (getKB().graph != null) {
			// qexec = QueryExecutionFactory.sparqlService(getKB().endpoint,
			// sparqlQuery, getKB().graph);
			// } //
			// else {
			// qexec = QueryExecutionFactory.sparqlService(getKB().endpoint,
			// sparqlQuery);
			// }
			// ResultSet results = qexec.execSelect();
			ResultSet results = null;
			try {
				results = querySelect(query, this.getKB());
			} catch (final Exception e) {
				throw new Exception("error with query " + query + " " + e.getLocalizedMessage(), e);
			}

			// write
			String uri, property, value;

			moreResults = this.hasNext(results);

			while (this.hasNext(results)) {
				final QuerySolution soln = results.nextSolution();
				// process query here
				{
					try {
						// first get uri
						uri = soln.get("?" + var).toString();
						property = soln.get("?p").toString();
						value = soln.get("?o").toString();
						cache.addTriple(uri, property, value);
						// LOGGER.info("Adding (" + uri + ", " + property + ", "
						// + value + ")");
					} catch (final Exception e) {
						throw new Exception("Error while processing " + soln, e);
						// LOGGER.warn("Error while processing: " +
						// soln.toString());
						// LOGGER.warn("Following exception occured: " +
						// e.getMessage());
						// LOGGER.info("Processing further ...");
					}

				}
				counter++;

				// LOGGER.info(soln.get("v0").toString()); // Get a result
				// variable by name.
			}
			offset = offset + this.getKB().getPageSize();
		} while (paginate && moreResults && this.getKB().getPageSize() > 0);
		LOGGER.info("Retrieved " + counter + " instances.");
		LOGGER.info(cache.size() + " of these instances contained valid data.");
		LOGGER.info("Retrieving statements took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
	}

}
