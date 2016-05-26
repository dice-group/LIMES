package org.aksw.limes.core.gui.util;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.limes.core.gui.util.sparql.SPARQLHelper;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.aksw.limes.core.io.query.SparqlQueryModule;

import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * Simple modification of SparqlQueryModule but instead of only returning a part this class gets everything.
 * With everything is meant: all properties and their names and their objects but still only the restricted ones.
 *
 * @author Konrad Höffner
 */
@SuppressWarnings("all")
public class GetAllSparqlQueryModule extends SparqlQueryModule {
    private final static Logger LOGGER = Logger.getLogger(GetAllSparqlQueryModule.class.getName());
    // amout of subjects read in
    Integer subjectLimit = null;


    public GetAllSparqlQueryModule(KBInfo kbinfo) {
        super(kbinfo);
    }

    public GetAllSparqlQueryModule(KBInfo kbinfo, Integer subjectLimit) {
        super(kbinfo);
        this.subjectLimit = subjectLimit;
    }

    /**
     * Reads from a SPARQL endpoint and writes the results in a cache
     *
     * @param cache
     *         The cache in which the content on the SPARQL endpoint is
     *         to be written
     */

    //	public Set<String> getProperties()
    //	{
    //		return getPropertiesSlowAndExact(20);
    //	}

    //SELECT distinct ?p,count(?var) WHERE { ?var rdf:type <http://dbpedia.org/ontology/Drug>. ?var ?p ?o} order by desc(count(?var))

    //	public Set<String> getPropertiesSlowAndExact(Integer propertyLimit)
    //	{
    //		String query;
    //		if(propertyLimit==null)
    //		{
    //			query = "SELECT distinct ?p where {"+getKB().var+" ?p ?o. "+getKB().getRestrictionQuerySubstring()+"}";
    //		}
    //		else
    //		{
    //			// only count each subject once at maximum, even if there are multiple objects for once subject and property
    //			query = "SELECT distinct ?p (count(distinct "+getKB().var+") AS ?count) where {"+getKB().var+" ?p ?o. "+getKB().getRestrictionQuerySubstring()+"}"
    //			+" order by desc(?count) limit "+propertyLimit;//count(distinct "+getKB().var+")
    //		}
    //		ResultSet rs = SPARQLHelper.querySelect(query, kb);
    //		Set<String> properties = new HashSet<String>();
    //		while(rs.hasNext())
    //		{
    //			QuerySolution qs = rs.next();
    //			//System.out.println(qs);
    //			properties.add(qs.get("p").toString());
    //		}
    //		return properties;
    //	}

    // may not work properly if the returned sample is not representative as the sample depends on the sparql server and is not randomly chosen
    // please tell me if you know a method of doing this better (i.e. getting a random sample with sparql or doing all this in one query with subqueries
    // or something like that, should not depend on inofficial sparql extentions (e.g. virtuoso) however.

    //	private Set<String> getSampleOfRelevantSubjects(int sampleSize)
    //	{
    //		System.out.println(getKB().getRestrictionQuerySubstring());
    //		String query = "SELECT distinct "+getKB().var+" where {"+getKB().var+" ?p ?o. "+getKB().getRestrictionQuerySubstring()+"} limit "+sampleSize;
    //		System.out.println(query);
    //		ResultSet rs = SPARQLHelper.querySelect(query, kb);
    //		Set<String> sample = new HashSet<String>();
    //		while(rs.hasNext())
    //		{
    //			sample.add(rs.next().get(getKB().var).toString());
    //		}
    //		return sample;
    //	}

    //	private String getFilterContent(Set<String> sample)
    //	{
    //		StringBuffer content = new StringBuffer();
    //		Iterator<String> it = sample.iterator();
    //		while(it.hasNext())
    //		{
    //			String resource = it.next();
    //			content.append("(?s = <"+resource+">)");
    //			if(it.hasNext())
    //			{
    //				content.append("|| ");
    //			}
    //			//break;
    //		}
    //		return content.toString();
    //	}

    //	public Set<String> getPropertiesFastAndLossy()
    //	{
    //		final int SAMPLE_SIZE = 1000;
    //		Set<String> sample = getSampleOfRelevantSubjects(SAMPLE_SIZE);
    //		String query = "SELECT distinct ?p where {?s ?p ?o. filter ("+getFilterContent(sample)+")}";
    //		System.out.println(query);
    //		ResultSet rs = SPARQLHelper.querySelect(query, kb);
    //		Set<String> properties = new HashSet<String>();
    //		while(rs.hasNext())
    //		{
    //			properties.add(rs.next().get("?p").toString());
    //		}
    //		return properties;
    //	}
    public static int queryNumberOfInstances(KBInfo kb) {
        String queryString = "SELECT DISTINCT (count(" + kb.getVar() + ") as ?count) WHERE {" +
                new AdvancedKBInfo(kb).getRestrictionQuerySubstring() + '}';
        //		WHERE {?x ?p ?o. { select ?x where {
        //		?x rdf:type dbpedia:Drug .} limit 10}
        //		}
        //System.out.println(queryString);
        QuerySolution soln = querySelect(queryString, kb).next();
        //System.out.println(soln);
        return Integer.valueOf(soln.getLiteral("count").getInt());
    }

    public static ResultSet querySelect(String query, KBInfo kb) {
        Model model = ModelRegistry.getInstance().getMap().get(kb.getEndpoint());
        String wholeQuery = SPARQLHelper.formatPrefixes(kb.getPrefixes()) + '\n' + query;
        // end workaround
        //System.out.println(wholeQuery);
        return SPARQLHelper.querySelect(query, kb.getEndpoint(), kb.getGraph(), model);
    }

    /**
     * superclass member variable kb is private, access via reflection
     */
    protected KBInfo getKB() {
//		this.kb=null;
        try {
//			System.out.println(Arrays.toString(SparqlQueryModule.class.getDeclaredFields()));
            Field field = SparqlQueryModule.class.getDeclaredField("kb");
            field.setAccessible(true);
            return (KBInfo) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param paginate
     *         with pagination set to false, no additional offset queries will be generated after the first query
     */
    public void fillCache(Cache cache, boolean paginate) {
        try {
            fillCache(cache, null, paginate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Jena hasNext() seems to be buggy and throws exceptions when resultset is empty, this is the workaround
     */
    private boolean hasNext(ResultSet rs) {
        try {
            return rs.hasNext();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param cache
     * @param getOnlyThisProperty
     * @param paginate
     *         with pagination set to false, no additional offset queries will be generated after the first query
     * @throws Exception
     */
    public void fillCache(final Cache cache, final String getOnlyThisProperty, final boolean paginate) throws Exception {
        String var = getKB().getVar().replace("?", "");
        //		SELECT DISTINCT *
        //		WHERE {?x ?p ?o. { select ?x where {
        //		?x rdf:type dbpedia:Drug .} limit 10}
        //		}

        //LimesLOGGER LOGGER = LimesLOGGER.getInstance();
        long startTime = System.currentTimeMillis();
        String query = "";
        //write prefixes
        System.out.println(getKB().getPrefixes());
        // ***** begin workaround TODO: change back when error is resolved with the drugbank sparql endpoint
        for (String key : getKB().getPrefixes().keySet()) {
            query = query + "PREFIX " + key + ": <" + getKB().getPrefixes().get(key) + ">\n";
        }

        //		query= query + "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>\n";
        query = query + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";

        // **** end workaround

        // get all triples of the chosen restriction
        query = query + "SELECT DISTINCT ?" + var + " ?p ?o ";
        query = query + "\n";

        //restriction
        query = query + "WHERE {?" + var + " ?p ?o. \n";
        if (subjectLimit != null) query = query + "{ select ?" + var + " where {";
        if (getKB().getRestrictions().size() > 0) {
            String where;

            for (String restrictionStr : getKB().getRestrictions()) {
                where = Restriction.fromString(restrictionStr).toString(var);
                query = query + where + " .\n";
            }
        } else {
            query = query + '?' + var + " ?p ?o.";
        }
        if (getOnlyThisProperty != null) {
            query = query + '?' + var + " <" + getOnlyThisProperty + "> ?o.";
        }
        if (subjectLimit != null) query = query + "} limit " + subjectLimit + "}";
        // close where
        query = query + "}\n";

        //query = query + " LIMIT 1000";

        LOGGER.info("Querying the endpoint.");
        //run query

        int offset = 0;
        boolean moreResults = false;
        int counter = 0;
        String basicQuery = query;
        do {
            if (getKB().getPageSize() > 0) {
                query = basicQuery + " LIMIT " + getKB().getPageSize() + " OFFSET " + offset;
            }
            //LOGGER.info("Following query was sent to endpoint <" + getKB().endpoint + ">\n\n" + query);
            //System.out.println(query);
            //			Query sparqlQuery = QueryFactory.create(query);
            //			QueryExecution qexec;
            //			// take care of graph issues. Only takes one graph. Seems like some sparql endpoint do
            //			// not like the FROM option.
            //			// it is important to
            //			if (getKB().graph != null) {
            //				qexec = QueryExecutionFactory.sparqlService(getKB().endpoint, sparqlQuery, getKB().graph);
            //			} //
            //			else {
            //				qexec = QueryExecutionFactory.sparqlService(getKB().endpoint, sparqlQuery);
            //			}
            //			ResultSet results = qexec.execSelect();
            ResultSet results = null;
            try {
                results = querySelect(query, this.getKB());
            } catch (Exception e) {
                throw new Exception("error with query " + query + " " + e.getLocalizedMessage(), e);
            }

            //write
            String uri, property, value;

            moreResults = hasNext(results);

            while (hasNext(results)) {
                QuerySolution soln = results.nextSolution();
                // process query here
                {
                    try {
                        //first get uri
                        uri = soln.get("?" + var).toString();
                        property = soln.get("?p").toString();
                        value = soln.get("?o").toString();
                        cache.addTriple(uri, property, value);
                        //LOGGER.info("Adding (" + uri + ", " + property + ", " + value + ")");
                    } catch (Exception e) {
                        throw new Exception("Error while processing " + soln, e);
                        //LOGGER.warn("Error while processing: " + soln.toString());
                        //LOGGER.warn("Following exception occured: " + e.getMessage());
                        //LOGGER.info("Processing further ...");
                    }

                }
                counter++;

                //LOGGER.info(soln.get("v0").toString());       // Get a result variable by name.
            }
            offset = offset + getKB().getPageSize();
        } while (paginate && moreResults && getKB().getPageSize() > 0);
        LOGGER.info("Retrieved " + counter + " instances.");
        LOGGER.info(cache.size() + " of these instances contained valid data.");
        LOGGER.info("Retrieving statements took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
    }

    //	public void fillCache2(Cache cache) {
    //
    //		LimesLOGGER LOGGER = LimesLOGGER.getInstance();
    //		long startTime = System.currentTimeMillis();
    //		//write prefixes
    //		Iterator<String> iter = getKB().prefixes.keySet().iterator();
    //		String key= "";
    //		StringBuffer queryProperties = new StringBuffer();
    //
    //		//        while (iter.hasNext()) {
    //		//            key = iter.next();
    //		//            queryProperties.append("PREFIX " + key + ": <" + getKB().prefixes.get(key) + ">\n");
    //		//        }
    //
    //		queryProperties.append("SELECT distinct ?p where { ?s ?p ?o. ");
    //
    //		//restriction
    //		if (getKB().restrictions.size() > 0) {
    //			String where;
    //			iter = getKB().restrictions.iterator();
    //			queryProperties.append("WHERE {\n");
    //			for (int i = 0; i < getKB().restrictions.size(); i++) {
    //				where = getKB().restrictions.get(i);
    //				queryProperties.append(where + " .\n");
    //			}
    //		}
    //
    //		//close where clause
    //		queryProperties.append("}");
    //
    //
    //		// get the names of all properties
    //		// however that is very time consuming, so we only get a certain amount of triples and then select their properties
    //		// statistically we should only miss very rare properties
    //		// this is done via subqueries, problem is that we can't assume getting random triples so that we may get only rdf:type triples or something
    //		// select distinct ?p where {{select ?p where {?s ?p ?o.} limit 1000}}
    //
    //		queryProperties.append("SELECT DISTINCT ?p where {?s ?p ?o.}");
    //
    //		// get the occurrences of the properties
    //		// "select distinct ?p,(count(?s) AS ?count) where {?s ?p ?o.}  ORDER BY DESC(?count)"
    //
    //		// fill in variable for the different properties to be retrieved
    //		queryProperties.append("SELECT DISTINCT " + getKB().var);
    //		for (int i = 0; i < getKB().properties.size(); i++) {
    //			queryProperties.append(" ?v" + i);
    //		}
    //		queryProperties.append("\n");
    //
    //		// graph
    //		/*
    //        if (!getKB().graph.equals(" ")) {
    //        query.append("FROM <" + getKB().graph + ">\n";
    //        } */
    //
    //		//properties
    //		if (getKB().properties.size() > 0) {
    //			queryProperties.append("OPTIONAL {\n");
    //			iter = getKB().properties.iterator();
    //			for (int i = 0; i < getKB().properties.size(); i++) {
    //				queryProperties.append(getKB().var + " " + getKB().properties.get(i) + " ?v" + i + " .\n");
    //			}
    //			//close optional
    //			queryProperties.append("}\n");
    //		}
    //
    //		// close where
    //		if (getKB().restrictions.size() > 0) {
    //			queryProperties.append("}\n");
    //		}
    //
    //		//query.append(" LIMIT 1000";
    //
    //		LOGGER.info("Querying the endpoint.");
    //		//run query
    //
    //		int offset = 0;
    //		boolean moreResults = false;
    //		int counter = 0;
    //		String basicQuery = queryProperties.toString();
    //		do {
    //
    //			if (getKB().pageSize > 0) {
    //				queryProperties = new StringBuffer(basicQuery + " LIMIT " + getKB().pageSize + " OFFSET " + offset);
    //			}
    //			//LOGGER.info("Following query was sent to endpoint <" + getKB().endpoint + ">\n\n" + query);
    //			Query sparqlQuery = QueryFactory.create(queryProperties.toString());
    //			QueryExecution qexec;
    //			// take care of graph issues. Only takes one graph. Seems like some sparql endpoint do
    //			// not like the FROM option.
    //			// it is important to
    //			if (getKB().graph != null) {
    //				qexec = QueryExecutionFactory.sparqlService(getKB().endpoint, sparqlQuery, getKB().graph);
    //			} //
    //			else {
    //				qexec = QueryExecutionFactory.sparqlService(getKB().endpoint, sparqlQuery);
    //			}
    //			ResultSet results = qexec.execSelect();
    //
    //
    //			//write
    //			String uri, property, value;
    //			try {
    //				if (results.hasNext()) {
    //					moreResults = true;
    //				} else {
    //					moreResults = false;
    //				}
    //				while (results.hasNext()) {
    //
    //					QuerySolution soln = results.nextSolution();
    //					// process query here
    //					{
    //						try {
    //							//first get uri
    //							uri = soln.get(getKB().var.substring(1)).toString();
    //
    //							//now get (p,o) pairs for this s
    //							for (int i = 0; i < getKB().properties.size(); i++) {
    //								property = getKB().properties.get(i);
    //								if (soln.contains("v"+i)) {
    //									value = soln.get("v" + i).toString();
    //									//remove localization information, e.g. @en
    //									if (value.contains("@")) {
    //										value = value.substring(0, value.indexOf("@"));
    //									}
    //									cache.addTriple(uri, property, value);
    //									//LOGGER.info("Adding (" + uri + ", " + property + ", " + value + ")");
    //								}
    //								//else LOGGER.warn(soln.toString()+" does not contain "+property);
    //							}
    //							//else
    //							//    cache.addTriple(uri, property, "");
    //						}
    //						catch (Exception e) {
    //							//LOGGER.warn("Error while processing: " + soln.toString());
    //							//LOGGER.warn("Following exception occured: " + e.getMessage());
    //							//LOGGER.info("Processing further ...");
    //						}
    //
    //					}
    //					counter++;
    //
    //					//LOGGER.info(soln.get("v0").toString());       // Get a result variable by name.
    //				}
    //
    //			} catch (Exception e) {
    //				//LOGGER.warn("Exception while handling query");
    //				//LOGGER.warn(e.toString());
    //			} finally {
    //				qexec.close();
    //			}
    //			offset = offset + getKB().pageSize;
    //		} while (moreResults && getKB().pageSize > 0);
    //		LOGGER.info("Retrieved " + counter + " instances.");
    //		LOGGER.info(cache.size() + " of these instances contained valid data.");
    //		LOGGER.info("Retrieving statements took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds.");
    //	}
}
