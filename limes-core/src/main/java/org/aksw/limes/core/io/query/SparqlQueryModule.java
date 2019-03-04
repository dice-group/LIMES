package org.aksw.limes.core.io.query;

import java.util.Iterator;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.KBInfo;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class SparqlQueryModule implements IQueryModule {

    protected KBInfo kb;
    private Logger logger = LoggerFactory.getLogger(SparqlQueryModule.class.getName());

    public SparqlQueryModule(KBInfo kbinfo) {
        kb = kbinfo;
    }

    /**
     * Reads from a SPARQL endpoint and writes the results in a cache
     *
     * @param cache
     *         The cache in which the content on the SPARQL endpoint is to be
     *         written
     */
    public void fillCache(ACache cache) {
        fillCache(cache, true);
    }

    /**
     * Reads from a SPARQL endpoint or a file and writes the results in a cache
     *
     * @param cache
     *         The cache in which the content on the SPARQL endpoint is to be
     *         written
     * @param isSparql
     *         True if the endpoint is a remote SPARQL endpoint, else assume
     *         that is is a Jena model
     */
    public void fillCache(ACache cache, boolean isSparql) {
        long startTime = System.currentTimeMillis();
        String query = generateQuery();

        // run query
        logger.info("Querying the endpoint.");
        int offset = 0;
        if (kb.getMinOffset() > 0) {
            offset = kb.getMinOffset();
        }

        boolean moreResults = false;
        int counter = 0;
        String basicQuery = query;
        do {
            int nextOffset = offset + kb.getPageSize();
            if(kb.getMaxOffset() > 0) {
                nextOffset = Math.min(kb.getMaxOffset(), nextOffset);
            }

            logger.info("Getting statements " + offset + " to " + nextOffset);

            if (kb.getPageSize() > 0) {
                int limit = kb.getPageSize();
                if(kb.getMaxOffset() > 0) {
                    limit = nextOffset - offset;
                }
                query = basicQuery + " LIMIT " + limit + " OFFSET " + offset;
            } else {
                query = basicQuery;
                if(kb.getMaxOffset() > 0) {
                    query = query + " LIMIT " + kb.getMaxOffset();
                }
            }

            Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
            QueryExecution qexec;

            // take care of graph issues. Only takes one graph. Seems like some
            // sparql endpoint do
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

            // write
            String uri, value;
            try {
                if (results.hasNext()) {
                    moreResults = true;
                } else {
                    moreResults = false;
                    break;
                }

                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    try {
                        uri = soln.get(kb.getVar().substring(1)).toString();
                        int i = 1;
                        for (String propertyLabel : kb.getProperties()) {
                            if (soln.contains("v" + i)) {
                                value = soln.get("v" + i).toString();
                                cache.addTriple(uri, propertyLabel, value);
                            }
                            i++;
                        }
                        if(kb.getOptionalProperties() != null){
                            for (String propertyLabel : kb.getOptionalProperties()) {
                                if (soln.contains("v" + i)) {
                                    value = soln.get("v" + i).toString();
                                    cache.addTriple(uri, propertyLabel, value);
                                }
                            }
                            i++;
                        }
                    } catch (Exception e) {
                        logger.warn("Error while processing: " + soln.toString());
                        logger.warn("Following exception occurred: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException();
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

        } while (moreResults && kb.getPageSize() > 0 && (offset < kb.getMaxOffset() || kb.getMaxOffset() < 0));
        logger.info("Retrieved " + counter + " triples and " + cache.size() + " entities.");
        logger.info("Retrieving statements took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
    }

    protected String generateQuery() {
        // write prefixes
        Iterator<String> iter = kb.getPrefixes().keySet().iterator();
        String key, query = "";
        while (iter.hasNext()) {
            key = iter.next();
            query = query + "PREFIX " + key + ": <" + kb.getPrefixes().get(key) + ">\n";
        }
        // fill in variable for the different properties to be retrieved
        query = query + "SELECT DISTINCT " + kb.getVar();
        final int numVars = kb.getProperties().size() +
                (kb.getOptionalProperties() == null ? 0 : kb.getOptionalProperties().size());
        for (int i = 1; i <= numVars; i++) {
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
        // restriction
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
        // properties
        int varCount = 1;
        int i = 1;
        String propertiesStr;
        if (kb.getProperties().size() > 0) {
            propertiesStr = "";
            for (String property : kb.getProperties()) {
                propertiesStr = propertiesStr + kb.getVar() + " " + property + " ?v" + i++ + " .\n";
            }
            // some endpoints and parsers do not support property paths. We
            // replace them here with variables
            while (propertiesStr.contains("/")) {
                propertiesStr = propertiesStr.replaceFirst("/", " ?w" + varCount + " .\n?w" + varCount + " ");
                varCount++;
            }
            // close optional
            query = query + propertiesStr;
        }
        // optional properties
        String optionalPropertiesStr;
        if (kb.getOptionalProperties() != null && kb.getOptionalProperties().size() > 0) {
            logger.info("Optional properties are " + kb.getOptionalProperties());
            optionalPropertiesStr = "OPTIONAL {\n";
            for (String optionalProperty : kb.getOptionalProperties()) {
                optionalPropertiesStr += kb.getVar() + " " + optionalProperty + " ?v" + i++ + " .\n";
            }
            // some endpoints and parsers do not support property paths. We
            // replace them here with variables
            while (optionalPropertiesStr.contains("/")) {
                optionalPropertiesStr = optionalPropertiesStr.replaceFirst("/", " ?w" + varCount + " .\n?w" + varCount + " ");
                varCount++;
            }
            // close optional
            query = query + optionalPropertiesStr + "}";
        }
        // finally replace variables in inverse properties
        String q[] = query.split("\n");
        query = "";
        for (int ql = 0; ql < q.length; ql++) {
            if (q[ql].contains("regex"))
                query = query + q[ql] + "\n";
            else if (q[ql].contains("^")) {
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
