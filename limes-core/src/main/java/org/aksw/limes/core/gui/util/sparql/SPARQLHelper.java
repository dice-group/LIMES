package org.aksw.limes.core.gui.util.sparql;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.util.AdvancedKBInfo;
import org.aksw.limes.core.gui.util.AdvancedMemoryCache;
import org.aksw.limes.core.gui.util.GetAllSparqlQueryModule;
import org.aksw.limes.core.io.config.KBInfo;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.OWL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;


/**
 * Helper class for sparql queries
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
@SuppressWarnings("all")
public class SPARQLHelper {

    public static final String DBPEDIA_ENDPOINT_OFFICIAL = "http://dbpedia.org/sparql";
    public static final String DBPEDIA_ENDPOINT_LIVE = "http://live.dbpedia.org/sparql";
    public static final String DBPEDIA_ENDPOINT = DBPEDIA_ENDPOINT_OFFICIAL;
    protected static final Map<String, AdvancedMemoryCache> samples = new HashMap<String, AdvancedMemoryCache>();
    /**
     */
    static final Set<String> blackset = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[]
            {"http://dbpedia.org/property/wikiPageUsesTemplate", "http://dbpedia.org/property/wikiPageExternalLink"})));
    private final static Logger logger = LoggerFactory.getLogger(SPARQLHelper.class.getName());


    /**
     * puts prefixes in a usable format for sparql query
     * @param prefixes prefixes to format
     * @return formatted prefixes
     */
    public static String formatPrefixes(Map<String, String> prefixes) {
        if (prefixes.isEmpty()) return "";
        StringBuffer prefixSPARQLString = new StringBuffer();
        for (String key : prefixes.keySet()) {
            prefixSPARQLString.append("PREFIX " + key + ": <" + prefixes.get(key) + ">" + '\n');
        }
        return prefixSPARQLString.substring(0, prefixSPARQLString.length() - 1);
    }

    /**
     * gets the last part of a URL
     * @param url url to get the last part of
     * @return the last part of a RDF resource url, e.g. http://dbpedia.org/ontology/City - {@literal >} City,
     * http://example.org/ontology#something -{@literal >} something
     */
    public static String lastPartOfURL(String url) {
        return url.substring(Math.max(url.lastIndexOf('#'), url.lastIndexOf('/')) + 1);
    }

    /**
     * returns subclasses of class
     * @param endpoint endpoint
     * @param graph graph
     * @param clazz class to get subclasses from
     * @param model model
     * @return subclasses
     */
    public static Set<String> subclassesOf(String endpoint, String graph, String clazz, Model model) {
        Cache cache = CacheManager.getInstance().getCache("subclasses");
        List<String> key = Arrays.asList(new String[]{endpoint, graph, clazz});
        Element element;
        if (cache.isKeyInCache(key)) {
            element = cache.get(key);
        } else {
            element = new Element(key, subClassesOfUncached(endpoint, graph, clazz, model));
            cache.put(element);
        }
        cache.flush();
        return (Set<String>) element.getValue();
    }

    /**
     * returns subclasses of uncached class
     * @param endpoint endpoint
     * @param graph graph
     * @param clazz class to get subclasses from
     * @param model model
     * @return subclasses
     */
    public static Set<String> subClassesOfUncached(String endpoint, String graph, String clazz, Model model) {
        final int MAX_CHILDREN = 100;
        String query = "SELECT distinct(?class) WHERE { ?class rdfs:subClassOf " + wrapIfNecessary(clazz) + ". } LIMIT " + MAX_CHILDREN;
        query = PrefixHelper.addPrefixes(query); // in case rdfs and owl prefixes are not known
        	Map<String, String> prefixesToAdd = PrefixHelper.restrictPrefixes(PrefixHelper.getPrefixes(), query);
        return resultSetToList(querySelect(query, endpoint, graph, model));
    }

    /**
     * returns the root classes of a SPARQL endpoint's ontology ({owl:Thing} normally).
     * @param endpoint endpoint
     * @param graph graph
     * @param model model
     * @return rootClasses
     */
    public static Set<String> rootClasses(String endpoint, String graph, Model model) {
        Cache cache = CacheManager.getInstance().getCache("rootclasses");
        if (cache == null) logger.info("cachenull");
        List<String> key = Arrays.asList(new String[]{endpoint, graph});
        Element element;
        if (cache.isKeyInCache(key)) {
            element = cache.get(key);
        } else {
            element = new Element(key, rootClassesUncached(endpoint, graph, model, null));
            cache.put(element);
        }
        cache.flush();
        return (Set<String>) element.getObjectValue();
    }

    /**
     * returns the root classes of a SPARQL endpoint's ontology ({owl:Thing} normally).
     * @param endpoint endpoint
     * @param graph graph
     * @param model model
     * @return rootClasses
     */
    public static Set<String> rootClassesUncached(String endpoint, String graph, Model model, Config config) {
        {
            // if owl:Thing exists and has at least one subclass, so use owl:Thing
            String queryForOWLThing = "SELECT ?class WHERE {?class rdfs:subClassOf owl:Thing} limit 1";
            if (!resultSetToList(querySelect(PrefixHelper.addPrefixes(queryForOWLThing), endpoint, graph, model)).isEmpty()) {
        	Map<String, String> prefixesToAdd = PrefixHelper.restrictPrefixes(PrefixHelper.getPrefixes(), queryForOWLThing);
        	for(String key: prefixesToAdd.keySet()){
        	    config.addPrefix(key, prefixesToAdd.get(key));
        	}
                return Collections.singleton(OWL.Thing.toString());
            }
        }
        System.err.println("no owl:Thing found for endpoint " + endpoint + ", using fallback.");
        // bad endpoint, use fallback: classes (instances of owl:Class) which don't have superclasses
        {
            String queryForParentlessClasses =
                    "SELECT distinct(?class) WHERE {{?class a owl:Class} UNION {?class a rdfs:Class}. OPTIONAL {?class rdfs:subClassOf ?superClass.} FILTER (!BOUND(?superClass))}";

            Set<String> classes = resultSetToList(querySelect(PrefixHelper.addPrefixes(queryForParentlessClasses), endpoint, graph, model));

            if (!classes.isEmpty()) {
        	Map<String, String> prefixesToAdd = PrefixHelper.restrictPrefixes(PrefixHelper.getPrefixes(), queryForParentlessClasses);
        	for(String key: prefixesToAdd.keySet()){
        	    config.addPrefix(key, prefixesToAdd.get(key));
        	}
                return classes;
            }
        }
        System.err.println("no root owl:Class instance for endpoint " + endpoint + ", using fallback fallback.");
        // very bad endpoint, use fallback fallback: objects of type property which don't have superclasses
        {
            String query =
                    "SELECT distinct(?class) WHERE {?x a ?class. OPTIONAL {?class rdfs:subClassOf ?superClass.} FILTER (!BOUND(?superClass))}";
            Set<String> classes = resultSetToList(querySelect(PrefixHelper.addPrefixes(query), endpoint, graph, model));

            // we only want classes of instances
            classes.remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
            classes.remove("http://www.w3.org/2000/01/rdf-schema#Class");
            classes.remove("http://www.w3.org/2002/07/owl#DatatypeProperty");
            classes.remove("http://www.w3.org/2002/07/owl#DatatypeProperty");
	    if (!classes.isEmpty()){
        	Map<String, String> prefixesToAdd = PrefixHelper.restrictPrefixes(PrefixHelper.getPrefixes(), query);
        	for(String key: prefixesToAdd.keySet()){
        	    config.addPrefix(key, prefixesToAdd.get(key));
        	}
		return classes;
	    }else {
		// very very bad endpoint
		// using objects of rdf:type property
		System.err.println("very very bad endpoint using objects of rdf:type property");
		query = "SELECT distinct(?class) WHERE{?x a ?class.}";
		classes = resultSetToList(querySelect(PrefixHelper.addPrefixes(query), endpoint, graph, model));
		classes.remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
		classes.remove("http://www.w3.org/2000/01/rdf-schema#Class");
		classes.remove("http://www.w3.org/2002/07/owl#DatatypeProperty");
		classes.remove("http://www.w3.org/2002/07/owl#DatatypeProperty");
		if (!classes.isEmpty()) {
        	Map<String, String> prefixesToAdd = PrefixHelper.restrictPrefixes(PrefixHelper.getPrefixes(), query);
        	for(String key: prefixesToAdd.keySet()){
        	    config.addPrefix(key, prefixesToAdd.get(key));
        	}
		    return classes;
		} else {
		    query = "SELECT distinct ?x WHERE{ ?y <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?x}";
		    classes = resultSetToList(querySelect(PrefixHelper.addPrefixes(query), endpoint, graph, model));
		    classes.remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
		    classes.remove("http://www.w3.org/2000/01/rdf-schema#Class");
		    classes.remove("http://www.w3.org/2002/07/owl#DatatypeProperty");
		    classes.remove("http://www.w3.org/2002/07/owl#DatatypeProperty");
        	Map<String, String> prefixesToAdd = PrefixHelper.restrictPrefixes(PrefixHelper.getPrefixes(), query);
        	for(String key: prefixesToAdd.keySet()){
        	    config.addPrefix(key, prefixesToAdd.get(key));
        	}
		    return classes;
		}
	    }
        }


    }

    /**
     * helper function for wrapping
     * @param uriString string to wrap
     * @return wrapped string
     */
    public static String wrapIfNecessary(String uriString) {
        if (uriString.startsWith("http://")) return "<" + uriString + ">";
        return uriString;
    }

    /**
     * gets properties
     * @param endpoint endpoint
     * @param graph graph
     * @param className class name to get properties from
     * @param model model
     * @return set of properties
     */
    public static Set<String> properties(String endpoint, String graph, String className, Model model) {
        Cache cache = CacheManager.getInstance().getCache("properties");
        List<String> key = Arrays.asList(new String[]{endpoint, graph, className});
        Element element;
        if (cache.isKeyInCache(key)) {
            element = cache.get(key);
        } else {
            element = new Element(key, propertiesUncached(endpoint, graph, className, model));
            cache.put(element);
        }
        cache.flush();
        return (Set<String>) element.getObjectValue();
    }

    /**
     * Get all Properties of the given knowledge base
     *
     * @param endpoint endpoint
     * @param graph
     *         can be null (recommended as e.g. rdf:label doesn't have to be in the graph)
     * @param className name of class to get properties from
     * @param model model
     * @return set of properties
     */
    public static Set<String> propertiesUncached(String endpoint, String graph, String className, Model model) {
        if (className.isEmpty()) {
            className = null;
        }
        if (className != null) {
            className = className.trim();
            className = className.replaceAll("<", "");
            className = className.replaceAll(">", "");
        }
        if ("owl:Thing".equals(className) || "http://www.w3.org/2002/07/owl#Thing".equals(className)) {
            className = null;
        }
        KBInfo info = className != null ?
                new AdvancedKBInfo("", endpoint, "s", graph, "rdf:type", className) : new AdvancedKBInfo("", endpoint, "s", graph);
        try {
            Set<String> properties = new HashSet<String>(Arrays.asList(commonProperties(info, 0.8, 20, 50)));
            if (className != null) {
                properties.addAll(getPropertiesWithDomain(endpoint, graph, className, model));
            }
            properties.removeAll(blackset);
            return properties;
        } catch (Exception e) {
            throw new RuntimeException("error getting the properties for endpoint " + endpoint, e);
        }
    }

    /**
     * executes sparql query to get properties
     * @param endpoint
     * @param graph
     * @param clazz
     * @param model
     * @return
     */
    static Set<String> getPropertiesWithDomain(String endpoint, String graph, String clazz, Model model) {
        long start = System.currentTimeMillis();
//        String query = PrefixHelper.addPrefixes("select ?p where {?p rdfs:domain " + wrapIfNecessary(clazz) + "}");
        String query = PrefixHelper.addPrefixes("select distinct ?p where { ?i a " + wrapIfNecessary(clazz) + " .  ?i ?p ?o . }");
        Set<String> properties = resultSetToList(querySelect(query, endpoint, graph, model));
        long end = System.currentTimeMillis();
        logger.trace(properties.size() + " properties with domain " + clazz + " from endpoint " + endpoint + " in " + (end - start) + " ms.");
        return properties;
    }
    //
    //	public static QueryExecution queryExecutionDirect(String query,String graph, String endpoint)
    //	{
    //		QueryExecution qexec = new QueryEngineHTTP(endpoint, query);
    //		return qexec;
    //	}
    ////
    //	public static boolean hasNext(ResultSet rs)
    //	{
    //		try
    //		{
    //			return rs.hasNext();
    //		}
    //		catch(Exception e)
    //		{
    //			return false;
    //		}
    //	}
    //

    /**
     * creates a new object of QueryExecution
     * @param query query
     * @param graph graph
     * @param endpoint endpoint
     * @param model model
     * @return QueryExecution object
     */
    public static QueryExecution queryExecution(String query, String graph, String endpoint, Model model) {
        ARQ.setNormalMode();
        Query sparqlQuery = QueryFactory.create(query, Syntax.syntaxARQ);
        QueryExecution qexec;

        // take care of graph issues. Only takes one graph. Seems like some sparql endpoint do
        // not like the FROM option.
        // it is important to
        if (model == null) {
            if (graph != null) {
                qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery, graph);
            } //
            else {
                qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);
            }
        } else {
            logger.info("Query to Model...");
            qexec = QueryExecutionFactory.create(sparqlQuery, model);
        }

        return qexec;
    }

    /**
     * puts result to list
     * @param rs set of results
     * @return set of results
     */
    public static Set<String> resultSetToList(ResultSet rs) {
        Set<String> list = new HashSet<String>();
        while (rs.hasNext()) {
            QuerySolution qs = rs.nextSolution();
            logger.trace("qs: " + qs.toString());
            if(!qs.toString().equals("")){
            try {
                list.add(URLDecoder.decode(qs.get(qs.varNames().next()).toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                list.add(qs.get(qs.varNames().next()).toString());
                e.printStackTrace();
            }
            }
        }
        return list;
    }

    /**
     * executes query
     * @param query query
     * @param endpoint endpoint
     * @param graph graph
     * @param model model
     * @return ResultSet
     */
    public static ResultSet querySelect(String query, String endpoint, String graph, Model model) {
        try {
        	logger.trace("Endpoint: " + endpoint + " graph: " + graph + "\n" + query);
            //QueryExecution qexec = queryExecutionDirect(query,graph,endpoint);
            ResultSet results = queryExecution(query, graph, endpoint, model).execSelect();
            return results;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error with query \"" + query + "\" at endpoint \"" + endpoint + "\" and graph \"" + graph + "\"", e);
        }
    }

    /**
     * returns samples
     * @param kb
     * @param sampleSize
     * @return
     */
    protected static AdvancedMemoryCache getSample(KBInfo kb, int sampleSize) {
        String hashString = Integer.toString(kb.hashCode());
        if (!samples.containsKey(hashString)) {
            samples.put(hashString, generateSample(kb, sampleSize));
        }
        return samples.get(hashString);
    }

    /**
     * generates samples
     * @param kb
     * @param sampleSize
     * @return
     */
    protected static AdvancedMemoryCache generateSample(KBInfo kb, int sampleSize) {
        GetAllSparqlQueryModule queryModule = new GetAllSparqlQueryModule(kb, sampleSize);
        AdvancedMemoryCache cache = new AdvancedMemoryCache();
        try {
            queryModule.fillCache(cache, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cache;
    }

    /**
     * returns the common properties
     * @param kb knowledgebase 
     * @param threshold threshold
     * @param limit limit
     * @param sampleSize size of sample
     * @return string array of common properties
     * @throws Exception thrown if something goes wrong
     */
    public static String[] commonProperties(KBInfo kb, double threshold, Integer limit, Integer sampleSize) throws Exception {
        return getSample(kb, sampleSize).getCommonProperties(threshold, limit);
    }

}