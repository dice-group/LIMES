/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.matching;

import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.log4j.Logger;

import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;

/**
 *
 * @author ngonga
 * @author Klaus Lyko
 */
public class LabelBasedPropertyMapper implements PropertyMapper {

    static Logger logger = Logger.getLogger("LIMES");
    QGramsDistance metric;
    Model sourceModel, targetModel;
    
    public LabelBasedPropertyMapper() {
        metric = new QGramsDistance();
    }
    
    /**
     * Constructor to use Model for query execution, thereby making it possible to use 
     * registered local dumps insted of regular SPARQL endpoints.
     * @param sourceModel
     * @param targetModel
     */
    public LabelBasedPropertyMapper(Model sourceModel, Model targetModel) {
    	this();
    	this.sourceModel = sourceModel;
    	this.targetModel = targetModel;
    }

    public AMapping getPropertyMapping(String endpoint1, String endpoint2, String classExpression1, String classExpression2) {
        Set<Node> properties1 = getProperties(endpoint1, classExpression1, sourceModel);
        Set<Node> properties2 = getProperties(endpoint2, classExpression2, targetModel);
        String s, t;
        AMapping result = MappingFactory.createDefaultMapping();
        for (Node a : properties1) {
            for (Node b : properties2) {                
                s = a.getLocalName().toLowerCase();
                t = b.getLocalName().toLowerCase();
                result.add(a.getURI(), b.getURI(), metric.getSimilarity(s, t));
            }
        }
        return result;
    }

    public Model getTargetModel() {
		return targetModel;
	}

	public void setTargetModel(Model targetModel) {
		this.targetModel = targetModel;
	}
	 public Model getSourceModel() {
			return sourceModel;
	}

	public void setSourceModel(Model sourceModel) {
		this.sourceModel = sourceModel;
	}
	/**
	 * Retrieves all nodes from the endpoint that are classes.
     * @param endpoint
	 * @param classExpression
	 * @param model
	 * @return Set of all nodes that are classes
     */
    private Set<Node> getProperties(String endpoint, String classExpression, Model model) {
        Set<Node> result = new HashSet<Node>();
        try {
            String query = "SELECT DISTINCT ?p WHERE { ?s ?p ?y. ?s a <" + classExpression + "> }";
            Query sparqlQuery = QueryFactory.create(query);
            QueryExecution qexec;
            if(model == null)
            	qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);
            else
            	qexec = QueryExecutionFactory.create(sparqlQuery, model);
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                result.add(soln.get("p").asNode());
            }
        } catch (Exception e) {
            logger.warn("Error while processing classes");
        }
        return result;
    }
    
}
