package org.aksw.limes.core.measures.measure.graphs;

import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.describe.Descriptor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.Test;

public class DescriptionTest {

    @Test
    public void test(){

        KBInfo info = new KBInfo();
        info.setEndpoint("https://dbpedia.org/sparql");
        info.setVar("x");
        info.setPageSize(1000);

        Descriptor descriptor = new Descriptor(info);

        Model m = descriptor.describe("http://dbpedia.org/resource/Berlin",1).queryDescription();

        StmtIterator iterator = m.listStatements();
        while (iterator.hasNext()){

            Statement stmt = iterator.nextStatement();

            System.out.println(String.format("<%s> <%s> <%s>.", stmt.getSubject().toString(), stmt.getPredicate().toString(), stmt.getObject().toString()));

        }


    }

}
