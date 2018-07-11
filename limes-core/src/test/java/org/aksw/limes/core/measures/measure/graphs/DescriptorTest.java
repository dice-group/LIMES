package org.aksw.limes.core.measures.measure.graphs;

import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.describe.Descriptor;
import org.aksw.limes.core.io.describe.IResourceDescriptor;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DescriptorTest {

    public static String wikidataUriToDescriotionTripleLines(String wikidata_URI) {
        KBInfo info = new KBInfo();
        info.setEndpoint("https://query.wikidata.org/sparql");
        info.setVar("x");
        info.setId("z");
        info.setPageSize(1000);


        Map<String, String> prefix = new HashMap<>();
        prefix.put("wd", "http://www.wikidata.org/entity/");
        //prefix.put("dbr", "http://dbpedia.org/resource/");
        info.setPrefixes(prefix);

        Descriptor desc = new Descriptor(info);

        IResourceDescriptor rd = desc.describe(wikidata_URI, 1);

        Model m = rd.queryDescription();

        StmtIterator iterator = m.listStatements();

        StringBuilder sb = new StringBuilder();

        while (iterator.hasNext()) {
            Statement stmt = iterator.nextStatement();
            String s = "<" + stmt.getSubject().toString() + "> <" + stmt.getPredicate() + "> <" + stmt.getObject() + "> .\n";
            sb.append(s);
        }
        return sb.toString();
    }

//    @Test
//    public void Test1() throws IOException {
//
//        KBInfo info = new KBInfo();
//        info.setEndpoint("https://query.wikidata.org/sparql");
//        info.setVar("x");
//        info.setId("z");
//        info.setPageSize(1000);
//
//
//        Map<String, String> prefix = new HashMap<>();
//        prefix.put("wd", "http://www.wikidata.org/entity/");
//        info.setPrefixes(prefix);
//
//        Descriptor desc = new Descriptor(info);
//
//        IResourceDescriptor rd = desc.describe("http://www.wikidata.org/entity/Q126084", 1);
//
//        Model m = rd.queryDescription();
//
//        StmtIterator iterator = m.listStatements();
//
//        String s = "";
//
//        while (iterator.hasNext()) {
//            Statement stmt = iterator.nextStatement();
//            s += "<" + stmt.getSubject().toString() + "> <" + stmt.getPredicate() + "> <" + stmt.getObject() + ">\n";
//
//            FileUtils.writeStringToFile(new File("C:\\Users\\onlym\\Downloads\\CSVReader\\test.csv"), s);
//            //System.out.println(s);
//        }
//
//    }


}


