package org.aksw.limes.core.gui.util.sparql;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.OWL;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.aksw.limes.core.gui.util.AdvancedKBInfo;
import org.aksw.limes.core.gui.util.AdvancedMemoryCache;
import org.aksw.limes.core.gui.util.GetAllSparqlQueryModule;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.query.FileQueryModule;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.aksw.limes.core.io.query.QueryModuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;


//// TODO: move all sparql stuff into aksw commons
@SuppressWarnings("all")
public class SPARQLHelper {

    //	protected static transient final Logger log = LoggerFactory.getLogger(SPARQLHelper.class.toString());
    //	public static final String GEONAMES_ENDPOINT_INTERNAL = "http://lgd.aksw.org:8900/sparql";
    public static final String DBPEDIA_ENDPOINT_OFFICIAL = "http://dbpedia.org/sparql";
    public static final String DBPEDIA_ENDPOINT_LIVE = "http://live.dbpedia.org/sparql";
    public static final String DBPEDIA_ENDPOINT = DBPEDIA_ENDPOINT_OFFICIAL;
    protected static final Map<String, AdvancedMemoryCache> samples = new HashMap<String, AdvancedMemoryCache>();
    /**
     */
    static final Set<String> blackset = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[]
            {"http://dbpedia.org/property/wikiPageUsesTemplate", "http://dbpedia.org/property/wikiPageExternalLink"})));
    //
    //	public static final String DBPEDIA_ENDPOINT = DBPEDIA_ENDPOINT_OFFICIAL;
    //
    //	public static final String LGD_ENDPOINT = "http://linkedgeodata.org/sparql/";
    //	//public static int TIMEOUT = 10000;
    //
    //	/**
    //	 * @param text a string in two-row csv format.
    //	 * @return a map with an entry for each line where the first row is the key and the second row the value
    //	 */
    //	public static Map<String, String> textToMap(String text)
    //	{
    //		HashMap<String,String> prefixes = new HashMap<String,String>();
    //		Scanner in = new Scanner(text);
    //		while(in.hasNext())
    //		{
    //			String[] tokens = in.nextLine().split("\t");
    //			if(tokens.length==2) prefixes.put(tokens[0],tokens[1]);
    //		}
    //		return prefixes;
    //	}
    //
    //	public static Map<String,String> getDefaultPrefixes()
    //	{
    //		try
    //		{
    //			return textToMap(FileUtils.readFileToString(new File("config/default_prefixes.csv")));
    //		} catch (IOException e)
    //		{
    //			e.printStackTrace();
    //			return new HashMap<String, String>();
    //		}
    //	}
    //
    private final static Logger logger = LoggerFactory.getLogger(SPARQLHelper.class.getName());

    public static void main(String args[]) {
        SPARQLHelper h = new SPARQLHelper();
        KBInfo info = new KBInfo();
//		File file = new File("C:/Users/Lyko/SAIM/EPStore/person11.nt");

        File file = new File("/home/ohdorno/workspace/LIMES/resources/Persons1/person11.nt");
        info.setEndpoint(file.getAbsolutePath());
        try {
            FileQueryModule fQModule = (FileQueryModule) QueryModuleFactory.getQueryModule("N-TRIPLE", info);
            Model model = ModelRegistry.getInstance().getMap().get(info.getEndpoint());
            if (model == null) {
                throw new RuntimeException("No model with id '" + info.getEndpoint() + "' registered");
            } else {
                logger.info("Successfully read data of type: " + info.getType());
                logger.info("Registered Model of size ... " + model.size());
                Set<String> set = SPARQLHelper.rootClassesUncached(info.getEndpoint(), null, model);
                System.out.println("Retrieved Classes...");
                int i = 0;
                for (String className : set) {
                    System.out.println((i++) + ".: " + className);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String formatPrefixes(Map<String, String> prefixes) {
        if (prefixes.isEmpty()) return "";
        StringBuffer prefixSPARQLString = new StringBuffer();
        for (String key : prefixes.keySet()) {
            prefixSPARQLString.append("PREFIX " + key + ": <" + prefixes.get(key) + ">" + '\n');
        }
        return prefixSPARQLString.substring(0, prefixSPARQLString.length() - 1);
    }

    /**
     * @return the last part of a RDF resource url, e.g. http://dbpedia.org/ontology/City -> City,
     * http://example.org/ontology#something -> something
     */
    public static String lastPartOfURL(String url) {
        return url.substring(Math.max(url.lastIndexOf('#'), url.lastIndexOf('/')) + 1);
    }

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

    public static Set<String> subClassesOfUncached(String endpoint, String graph, String clazz, Model model) {
        final int MAX_CHILDREN = 100;
        String query = "SELECT distinct(?class) WHERE { ?class rdfs:subClassOf " + wrapIfNecessary(clazz) + ". } LIMIT " + MAX_CHILDREN;
        query = PrefixHelper.addPrefixes(query); // in case rdfs and owl prefixes are not known
        return resultSetToList(querySelect(query, endpoint, graph, model));
    }

    /**
     * returns the root classes of a SPARQL endpoint's ontology ({owl:Thing} normally).
     */
    public static Set<String> rootClasses(String endpoint, String graph, Model model) {
        Cache cache = CacheManager.getInstance().getCache("rootclasses");
        if (cache == null) System.out.println("cachenull");
        List<String> key = Arrays.asList(new String[]{endpoint, graph});
        Element element;
        if (cache.isKeyInCache(key)) {
            element = cache.get(key);
        } else {
            element = new Element(key, rootClassesUncached(endpoint, graph, model));
            cache.put(element);
        }
        cache.flush();
        return (Set<String>) element.getObjectValue();
    }

    /**
     * returns the root classes of a SPARQL endpoint's ontology ({owl:Thing} normally).
     */
    public static Set<String> rootClassesUncached(String endpoint, String graph, Model model) {
        {
            // if owl:Thing exists and has at least one subclass, so use owl:Thing
            String queryForOWLThing = "SELECT ?class WHERE {?class rdfs:subClassOf owl:Thing} limit 1";
            if (!resultSetToList(querySelect(PrefixHelper.addPrefixes(queryForOWLThing), endpoint, graph, model)).isEmpty()) {
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
            if (!classes.isEmpty())
                return classes;
            else {
                // very very bad endpoint
                // using objects of rdf:type property
                query = "SELECT distinct(?class) WHERE{?x a ?class.}";
                classes = resultSetToList(querySelect(PrefixHelper.addPrefixes(query), endpoint, graph, model));
                classes.remove("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
                classes.remove("http://www.w3.org/2000/01/rdf-schema#Class");
                classes.remove("http://www.w3.org/2002/07/owl#DatatypeProperty");
                classes.remove("http://www.w3.org/2002/07/owl#DatatypeProperty");
                return classes;
            }
        }


    }

    public static String wrapIfNecessary(String uriString) {
        if (uriString.startsWith("http://")) return "<" + uriString + ">";
        return uriString;
    }

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
     * @param endpoint
     * @param graph
     *         can be null (recommended as e.g. rdf:label doesn't have to be in the graph)
     * @return
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

    public static Set<String> resultSetToList(ResultSet rs) {
        Set<String> list = new HashSet<String>();
        while (rs.hasNext()) {
            QuerySolution qs = rs.nextSolution();
            System.out.println("qs: " + qs.toString());
            try {
                list.add(URLDecoder.decode(qs.get(qs.varNames().next()).toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                list.add(qs.get(qs.varNames().next()).toString());
                e.printStackTrace();
            }
        }
        return list;
    }

    public static ResultSet querySelect(String query, String endpoint, String graph, Model model) {
        try {
            //QueryExecution qexec = queryExecutionDirect(query,graph,endpoint);
            ResultSet results = queryExecution(query, graph, endpoint, model).execSelect();
//			System.out.println("res: " + ResultSetFormatter.asText(results));
            return results;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error with query \"" + query + "\" at endpoint \"" + endpoint + "\" and graph \"" + graph + "\"", e);
        }
    }

    protected static AdvancedMemoryCache getSample(KBInfo kb, int sampleSize) {
        String hashString = Integer.toString(kb.hashCode());
        if (!samples.containsKey(hashString)) {
            samples.put(hashString, generateSample(kb, sampleSize));
        }
        return samples.get(hashString);
    }

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

    public static String[] commonProperties(KBInfo kb, double threshold, Integer limit, Integer sampleSize) throws Exception {
        return getSample(kb, sampleSize).getCommonProperties(threshold, limit);
    }

}