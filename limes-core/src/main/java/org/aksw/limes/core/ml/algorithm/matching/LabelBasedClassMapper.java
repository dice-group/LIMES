/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.ml.algorithm.matching;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.apache.jena.graph.Node;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.log4j.Logger;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a label-based similarity for classes in two ontologies
 *
 * @author ngonga
 * @author Klaus Lyko
 */
public class LabelBasedClassMapper {

    static Logger logger = Logger.getLogger("LIMES");
    QGramsDistance metric;
    private Model sourceModel;
    private Model targetModel;

    public LabelBasedClassMapper() {
        metric = new QGramsDistance();
    }

    /**
     * Constructor for registering Models.
     * @param sourceModel
     * @param targetModel
     */
    public LabelBasedClassMapper(Model sourceModel, Model targetModel) {
        this();
        this.sourceModel = sourceModel;
        this.targetModel = targetModel;
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

    /**
     * Method to get simple Name based Class Mappings. If no models were set, we'll assume
     * endpoints point to a SPARQL endpoint, otherwise we are using the configured QueryModule.
     * @param endpoint1
     * @param endpoint2
     * @return
     */
    public AMapping getEntityMapping(String endpoint1, String endpoint2) {
        Set<Node> classes1 = getClasses(endpoint1, sourceModel);
        Set<Node> classes2 = getClasses(endpoint2, targetModel);
        String s, t;
        AMapping result = MappingFactory.createDefaultMapping();
        for (Node a : classes1) {
            for (Node b : classes2) {
                s = a.getLocalName().toLowerCase();
                t = b.getLocalName().toLowerCase();
                result.add(a.getURI(), b.getURI(), metric.getSimilarity(s, t));
            }
        }
        return result;
    }

    /**
     * Retrieves all nodes from the endpoint that are classes
     *
     * @param endpoint
     * @return Set of all nodes that are classes
     */
    private Set<Node> getClasses(String endpoint, Model model) {
        Set<Node> result = new HashSet<Node>();
        try {
            String query = "SELECT DISTINCT ?y WHERE { ?s a ?y }";
            Query sparqlQuery = QueryFactory.create(query);
            QueryExecution qexec;
            if(model == null)
                qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);
            else
                qexec = QueryExecutionFactory.create(sparqlQuery, model);
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                result.add(soln.get("y").asNode());
            }
        } catch (Exception e) {
            logger.warn("Error while processing classes");
        }
        return result;
    }

}
