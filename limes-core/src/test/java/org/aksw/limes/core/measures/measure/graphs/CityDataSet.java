package org.aksw.limes.core.measures.measure.graphs;

import org.apache.jena.query.*;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdfxml.xmloutput.impl.Abbreviated;


import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class CityDataSet {

    String path = "src\\main\\resources\\CityDataSetDBpedia.txt";

    public void getcities() {


        String qcity =
                "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                        "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
                        "SELECT ?cityName WHERE {\n" +
                        " ?cityName  a dbo:City \n" +
                        "}\n" +
                        "LIMIT 100";
        Query city_query = QueryFactory.create(qcity);
        QueryExecution Exe =
                QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", city_query);

        try {
            ResultSet classes_results = Exe.execSelect();
            for (; classes_results.hasNext(); ) {
                QuerySolution soln = classes_results.nextSolution();
                RDFNode a = soln.get("cityName");
                String result = a.asNode() + "";

                //String res = result.replaceAll("\\s",""); // trying to remove the blank spaces
                String res = result.substring(result.lastIndexOf('/') + 1);
                System.out.println(result);
                try {
                    Model model = Exe.execDescribe();
                    StringWriter sw = new StringWriter();
                    Abbreviated abb = new Abbreviated();
                    abb.write(model, sw, null);
                    //File

                    File file = new File(path);
                    file.createNewFile();
                    Files.write(Paths.get(path), sw.toString().getBytes(), StandardOpenOption.APPEND);

                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finally {
                    Exe.close();
                }

            }
        }finally {
            Exe.close();
        }
    }
}
