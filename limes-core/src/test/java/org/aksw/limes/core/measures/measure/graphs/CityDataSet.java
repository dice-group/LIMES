package org.aksw.limes.core.measures.measure.graphs;

import org.apache.jena.query.*;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdfxml.xmloutput.impl.Abbreviated;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class CityDataSet {

    String path = "src\\main\\resources\\CityDataSetDBpedia.nt";

    public void getcities() {
        try{
            FileWriter fostream = new FileWriter(path,true);
            BufferedWriter out = new BufferedWriter(fostream);
                       String query =
                               "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                                       "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
                                       "construct {?s ?p ?o} WHERE {\n" +
                                       " ?s a dbo:City.\n" +
                                       "?s ?p?o \n" +
                                       " }\n" +
                                       "LIMIT 100";

            QueryExecution qexecctest = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

            try {

                Model model = qexecctest.execDescribe();
                model.write(out, "N-Triples");

            } finally {
                qexecctest.close();
                out.close();}

        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());}

    }

}
