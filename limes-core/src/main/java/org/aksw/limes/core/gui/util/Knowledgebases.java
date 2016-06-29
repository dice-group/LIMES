package org.aksw.limes.core.gui.util;


/**
 * Helper class which contains knowledgebases as Strings
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
@SuppressWarnings("all")
public class Knowledgebases {
    public static final String DBPEDIA_ENDPOINT = "http://dbpedia.org/sparql";
    public static final String LINKEDGEODATA_ENDPOINT = "http://linkedgeodata.org/sparql";


    public static final String DBPEDIA_GRAPH = "http://dbpedia.org";
    public static final String LINKEDGEODATA_GRAPH = "http://linkedgeodata.org";
    public static final String LINKEDMDB_GRAPH = null; // unknown, no results by "SELECT ?s ?g {GRAPH ?g { ?s ?p ?o. ?s a <http://data.linkedmdb.org/resource/movie/film> } } limit 10"
    public static final String FACTBOOK_GRAPH = null; // unknown, no results by "SELECT ?s ?g {GRAPH ?g  { ?s ?p ?o. ?s a <http://www4.wiwiss.fu-berlin.de/factbook/ns#Country> } } limit 10"

    public static final String DBPEDIA_MOVIE_CLASS = "http://dbpedia.org/ontology/Film";
    public static final String LINKEDMDB_MOVIE_CLASS = "http://data.linkedmdb.org/resource/movie/film";
    public static final String DBPEDIA_COUNTRY_CLASS = "http://dbpedia.org/ontology/Country";
    public static final String FACTBOOK_COUNTRY_CLASS = "http://www4.wiwiss.fu-berlin.de/factbook/ns#Country";
    public static final String LINKEDGEODATA_CITY_CLASS = "http://linkedgeodata.org/ontology/City";
    public static final String LINKEDGEODATA_COUNTRY_CLASS = "http://linkedgeodata.org/ontology/Country";

    // DBPEDIA **********************************************************************************************************************
    public static final AdvancedKBInfo DBPEDIA_SETTLEMENT = new AdvancedKBInfo("dbpedia_settlement", DBPEDIA_ENDPOINT,
            "?var123", DBPEDIA_GRAPH, "a", "dbpedia-owl:Settlement");
    public static final AdvancedKBInfo DBPEDIA_CITY = new AdvancedKBInfo("dbpedia_city", DBPEDIA_ENDPOINT,
            "?var234", DBPEDIA_GRAPH, "a", "dbpedia-owl:City");
    public static final AdvancedKBInfo DBPEDIA_MOVIE = new AdvancedKBInfo("dbpedia_disney_movie", DBPEDIA_ENDPOINT,
            "?var456", DBPEDIA_GRAPH, "a", "<" + DBPEDIA_MOVIE_CLASS + ">");
    public static final AdvancedKBInfo DBPEDIA_DRUG = new AdvancedKBInfo("dbpedia_drug", DBPEDIA_ENDPOINT,
            "?var567", DBPEDIA_GRAPH, "a", "dbpedia-owl:Drug");
    public static final AdvancedKBInfo DBPEDIA_COUNTRY = new AdvancedKBInfo("dbpedia_country", DBPEDIA_ENDPOINT,
            "?var890", DBPEDIA_GRAPH, "a", "<" + DBPEDIA_COUNTRY_CLASS + ">");

    // LINKEDGEODATA ******************************************************************************************************************
    public static final AdvancedKBInfo LINKEDGEODATA_CITY = new AdvancedKBInfo("linkedgeodata_city", "http://linkedgeodata.org/sparql/",
            "?var345", LINKEDGEODATA_GRAPH, "a", "<" + LINKEDGEODATA_CITY_CLASS + ">");
    public static final AdvancedKBInfo LINKEDGEODATA_COUNTRY = new AdvancedKBInfo("linkedgeodata_country", "http://linkedgeodata.org/sparql/",
            "?lgdcountry", LINKEDGEODATA_GRAPH, "a", "<" + LINKEDGEODATA_COUNTRY_CLASS + ">");


    public static final AdvancedKBInfo LINKEDMDB_MOVIE = new AdvancedKBInfo("linkedmdb_disney_movie", "http://data.linkedmdb.org/sparql",
            "?var678", LINKEDMDB_GRAPH, "a", "<" + LINKEDMDB_MOVIE_CLASS + ">");
    public static final AdvancedKBInfo FACTBOOK_COUNTRY = new AdvancedKBInfo("factbook_country", "http://www4.wiwiss.fu-berlin.de/factbook/sparql",
            "?var789", FACTBOOK_GRAPH, "a", "<" + FACTBOOK_COUNTRY_CLASS + ">");

}
