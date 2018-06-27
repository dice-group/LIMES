package org.aksw.limes.core.measures.measure.graphs;

import com.bordercloud.sparql.Endpoint;
import com.bordercloud.sparql.EndpointException;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfxml.xmloutput.impl.Abbreviated;
import static org.junit.Assert.fail;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

public class CityDataSetWiki {

    static void writeFile(String path, String input) throws IOException {
        Files.write(Paths.get(path), input.getBytes(),
                new OpenOption[] { StandardOpenOption.TRUNCATE_EXISTING ,
                        StandardOpenOption.CREATE});
    }

    public static void getQuery() throws IOException {
        String path = "src\\main\\resources\\CityDataSetwikidata.txt";

        String
                queryString =
                "PREFIX bd: <http://www.bigdata.com/rdf#>\n" +
                        "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
                        "PREFIX wikibase: <http://wikiba.se/ontology#>\n" +
                        " PREFIX wd: <http://www.wikidata.org/entity/>\n" +
                        "SELECT ?Name ?NameLabel WHERE {\n" +
                        "   ?Name wdt:P31 wd:Q515.\n" +
                        "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"[AUTO_LANGUAGE],en\". }\n" +
                        "}\n" +
                        "LIMIT 100";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("https://query.wikidata.org/sparql", queryString);
        try {
            ResultSet results = qexec.execSelect();
            String resultString = ResultSetFormatter.asText(results);
            writeFile(path , resultString.toString() );
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            qexec.close();
        }
        // System.out.println(res);

//        try {
//            //Whatever the file path is.
//            File statText = new File(path);
//            FileOutputStream is = new FileOutputStream(statText);
//            OutputStreamWriter osw = new OutputStreamWriter(is);
//            Writer w = new BufferedWriter(osw);
//                w.write(results.toString());
//                w.close();
//        } catch (IOException e) {
//            System.err.println("Problem writing to the file statsTest.txt");
//        }



    }

}
