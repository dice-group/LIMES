/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.matching;

import java.util.HashMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.matching.stablematching.HospitalResidents;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ngonga
 * @author Klaus Lyko
 */
public class DefaultPropertyMapper implements PropertyMapper{

    public int LIMIT = 500;
    Logger logger = LoggerFactory.getLogger(getClass());
    public int MINSIM = 1;
    Model sourceModel, targetModel;

    public DefaultPropertyMapper(){}
    public DefaultPropertyMapper(Model sourceModel, Model targetModel){
    	this.sourceModel = sourceModel;
    	this.targetModel = targetModel;
    }
    /** Applies stable matching to determine the best possible mapping of 
     * properties from two endpoints
     * @param endpoint1 Source endpoint
     * @param endpoint2 Target endpoint
     * @param classExpression1 Source class expression
     * @param classExpression2 Target class expression
     * @return 
     */
    public AMapping getPropertyMapping(String endpoint1,
            String endpoint2, String classExpression1, String classExpression2) {
        AMapping m = getMappingProperties(endpoint1, endpoint2, classExpression1, classExpression2);
        HospitalResidents hr = new HospitalResidents();
        m = hr.getMatching(m);
        AMapping copy = MappingFactory.createDefaultMapping();
        //clean from nonsense, i.e., maps of weight 0
        for (String s : m.getMap().keySet()) {
            for (String t : m.getMap().get(s).keySet()) {
                if (m.getConfidence(s, t) >= MINSIM) {
                    copy.add(s, t, m.getConfidence(s, t));
                }
            }
        }
        return copy;
    }

    public Model getSourceModel() {
		return sourceModel;
	}

	public void setSourceModel(Model sourceModel) {
		this.sourceModel = sourceModel;
	}

	public Model getTargetModel() {
		return targetModel;
	}

	public void setTargetModel(Model targetModel) {
		this.targetModel = targetModel;
	}

	/** Computes the mapping of property from enpoint1 to endpoint2 by using the
     * restriction classExpression 1 and classExpression2
     *
     */
    public AMapping getMappingProperties(String endpoint1,
            String endpoint2, String classExpression1, String classExpression2) {

        logger.info("Getting mapping from " + classExpression1 + " to " + classExpression2);
        AMapping m2 = getMonoDirectionalMap(endpoint1, endpoint2, classExpression1, classExpression2);
        logger.debug("m2="+m2.size());
        logger.info("Getting mapping from " + classExpression2 + " to " + classExpression1);
        AMapping m1 = getMonoDirectionalMap(endpoint2, endpoint1, classExpression2, classExpression1);
        logger.debug("m1="+m1.size());
        logger.debug("Merging the mappings...");
        double sim1, sim2;
        for (String key : m1.getMap().keySet()) {
            for (String value : m1.getMap().get(key).keySet()) {
                sim2 = m2.getConfidence(value, key);
                sim1 = m1.getConfidence(key, value);
                m2.add(value, key, sim1 + sim2);
            }
        }
        logger.debug("Property mapping is \n" + m2);
        return m2;
    }

    public AMapping getMonoDirectionalMap(String endpoint1,
            String endpoint2, String classExpression1, String classExpression2) {


        HashMap<String, TreeSet<String>> propertyValueMap =
                new HashMap<String, TreeSet<String>>();
        AMapping propertyToProperty = MappingFactory.createDefaultMapping();

        //get property values from first knowledge base
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "SELECT ?s ?p ?o \n"
                + "WHERE { ?s rdf:type <" + classExpression1 + "> . \n"
                + "?s ?p ?o . "
                //+ "FILTER(lang(?o) = \"en\"). "
                + //"FILTER REGEX (str(?o), \"^^xsd:string\" " +
                "\n}";
        if (LIMIT > 0) {
            query = query + " LIMIT " + LIMIT;
        }

        logger.debug("Query:\n" + query);
        Query sparqlQuery = QueryFactory.create(query);
        QueryExecution qexec;
        if(sourceModel == null)
        	qexec = QueryExecutionFactory.sparqlService(endpoint1, sparqlQuery);
        else
        	qexec = QueryExecutionFactory.create(sparqlQuery, sourceModel);
        ResultSet results = qexec.execSelect();
        // first get LIMIT instances from
        String s, p, o;
        int count = 0;
        while (results.hasNext()) {
        	count++;
            QuerySolution soln = results.nextSolution();
            {
                try {
                    s = soln.get("s").toString();
                    p = soln.get("p").toString();
                    o = soln.get("o").toString();
                    //gets rid of all numeric properties
                    if (!isNumeric(o)) {
                        if (!propertyValueMap.containsKey(p)) {
                            propertyValueMap.put(p, new TreeSet<String>());
                        }
                        propertyValueMap.get(p).add(o);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        logger.debug("Had "+count+ "results");
//        logger.debug("Got " + instanceToClassMap.size() + " classes");
//        logger.debug(instanceToClassMap);
//        logger.debug(instanceToInstanceMap);
        double sim;
        for (String property : propertyValueMap.keySet()) {

            for (String object : propertyValueMap.get(property)) {
                object = object.split("@")[0];
                if (!object.contains("\\") && !object.contains("\n") && !object.contains("\"")) {
                    //System.out.println(object);
                	String objectString;
                	if(!object.startsWith("http"))
                		objectString = "\"" + object.replaceAll(" ", "_") + "\"";
                	else
                		objectString = "<"+object.replaceAll(" ", "_") +">";
                    query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"+
                            "SELECT ?p " +
                            "WHERE { ?s rdf:type <" + classExpression2 + "> . "+
                            "?s ?p "+objectString+"}" +
                            "LIMIT 50";
                    logger.debug(query);
                    sparqlQuery = QueryFactory.create(query);
                    if(targetModel == null)
                    	qexec = QueryExecutionFactory.sparqlService(endpoint2, sparqlQuery);
                    else
                    	qexec = QueryExecutionFactory.create(sparqlQuery, targetModel);
                    results = qexec.execSelect();
                    int count2 = 0;
                    QuerySolution soln;
                    while (results.hasNext()) {
                        soln = results.nextSolution();
                        {
                        	count2++;
                            try {
                                p = soln.get("p").toString();
                                sim = propertyToProperty.getConfidence(property, p);
                                if (sim > 0) {
                                    propertyToProperty.getMap().get(property).put(p, sim + 1);
                                } else {
                                    propertyToProperty.add(property, p, 1.0);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    logger.debug("lead to "+count2+" results");
                }
            }
        }

        //logger.debug(classToClassMapping.map);
        return propertyToProperty;
    }
    
    /** Test whether the input string is numeric
     *
     * @param input String to test
     * @return True is numeric, else false
     */
    public static boolean isNumeric(String input) {
        if(input.contains("^^"))
            input = input.split("^^")[0];
        if(input.contains("%"))
            input = input.split("\\%")[0];
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
