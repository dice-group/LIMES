package org.aksw.limes.core.measures.measure.graphs;

import org.aksw.limes.core.measures.measure.Configuration.Configuration;
import org.aksw.limes.core.measures.measure.DataModel.ResourcePair;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DBPediaFetcher {

    private final String OntologyPREFIX = "PREFIX dbo: <http://dbpedia.org/ontology/> ";
    private final String RDFSPREFIX = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
    private final String RDFPREFIX = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";

    /// Execute Query on the dbpedia Source
    private ResultSet executeQuery(String exeQuery) {
        Query query = QueryFactory.create(exeQuery);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
        ((QueryEngineHTTP) qexec).addParam("timeout", "10000");

        // Execute.
        ResultSet rs = qexec.execSelect();
        return rs;
    }


    public List<ResourcePair> getResourcePair(String relation) throws IOException {

        String query = String.format("%s SELECT ?x ?xlabel ?y ?ylabel " +
                        "WHERE {?x %s ?y.}" +
                        "LIMIT %s"
                        ,OntologyPREFIX, relation, Configuration.LABEL_LIMIT);

        ResultSet resultSet = executeQuery(query);
        List<ResourcePair> resourceList = new ArrayList<ResourcePair>();

        int count = 0;
        while (resultSet.hasNext()) {
            count++;
            QuerySolution soln = resultSet.nextSolution();

            Resource resourceSrc = soln.getResource("x");
            Resource resourceTarget = soln.getResource("y");

            ResourcePair resourcePair = new ResourcePair(resourceSrc.toString(), relation, resourceTarget.toString());
            resourceList.add(resourcePair);
        }
        return resourceList;

    }


}
