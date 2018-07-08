package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;


import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdfxml.xmloutput.impl.Abbreviated;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

public class Evaluation {



    /*Variables that can be accessed in the class*/
    // private String qpram = "dbpedia.org/ontology";

    List<String> resourselist = new ArrayList<>();// to save the resources of the class
    List<String> classlist = new ArrayList<>();// to save classes
    String path = "src\\main\\resources\\Dumpfile\\dumpfile.txt";

//    private String[] classes =
//            {"Publisher", "School", "Abbey", "AcademicConference",
//                    "AcademicJournal", "AcademicSubject", "AdultActor"
//                    ,"Agglomeration", "Airline", "Airport"};


    public String  queryExecution() {

        // to get the describtion of the each recource of The Class.
        getclasses();
        for (int i=0;i<classlist.size();i++){
            if (classlist.get(i)!= null)
                getResources(classlist.get(i).toString());

            for (int j=0; j<resourselist.size();j++){
                //if (resourselist.get(j)!= " ") //&& (resourselist.get(j).startsWith("_")))
                getResourceDescription(resourselist.get(j));
                //System.out.println(resourselist.get(j));
            }

        }

    return path;
    }

    public List<String> getclasses() {


        String qclass =
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                        "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                        "SELECT distinct ?class WHERE {\n" +
                        "  ?class a owl:Class.\n" +
                        "   FILTER regex(str(?class), \"dbpedia.org/ontology\")\n" +
                        "  FILTER NOT EXISTS { ?x rdfs:subClassOf ?class}\n" +
                        "  \n" +
                        "} limit 10";
        Query classes_query = QueryFactory.create(qclass);
        QueryExecution Exe =
                QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", classes_query);

        try {
            ResultSet classes_results = Exe.execSelect();
            for (; classes_results.hasNext(); ) {
                QuerySolution soln = classes_results.nextSolution();
                RDFNode a = soln.get("class");
                String result = a.asNode() + "";

                //String res = result.replaceAll("\\s",""); // trying to remove the blank spaces
                String res = result.substring(result.lastIndexOf('/') + 1);
                //System.out.println(result);
                classlist.add(result);
            }
        }finally {
            Exe.close();
        }
    return classlist;
    }




    /*Method to get the description of the classes*/
    public void getResources(String cname) {


        String qstring =
                "PREFIX dbo: <http://dbpedia.org/ontology/>" +
                        "Select Distinct ?r where {" +
                        "?r a <"+cname+">." +
                        "}" +
                        "limit 100";

        Query resource_query = QueryFactory.create(qstring);
        QueryExecution Exe =
                QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", resource_query);

        try {
            ResultSet resource_results = Exe.execSelect();
            for ( ; resource_results.hasNext() ; )
            {
                QuerySolution soln = resource_results.nextSolution() ;
                RDFNode a = soln.get("r") ;
                String result = a.asNode()+"";
                //System.out.println(result);
                //String res = result.replaceAll("\\s",""); // trying to remove the blank spaces
                resourselist.add(result);
                //System.out.println(result);
            }

        } finally {
            Exe.close();
        }

    } // ending of the GetResources Method

    public void getResourceDescription(String rname){

        String desptstring =
//                "Prefix dbo: <http://dbpedia.org/resource/>" +
//                        "SELECT "+rname+" ?r ?o where{"
//                        +rname+" ?r ?o." +
//                        "}";
                "Prefix dbo: <http://dbpedia.org/resource/>" +
                        "Describe ?r  where{" +
                        "?r ?o <"+rname+">." +
                        "}";

        Writer writer = null;
        Query des_query = QueryFactory.create(desptstring);
        QueryExecution des_Exe = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", des_query);

        try {
            Model model = des_Exe.execDescribe();
            //model = (Model) set;
            //ResultSet description_result = des_Exe.execSelect();

            StringWriter strwrt = new StringWriter();
            //for ( ; description_result.hasNext() ; ) {
            //QuerySolution res = description_result.nextSolution();

            Abbreviated abb = new Abbreviated();
            abb.write(model, strwrt, null);
            //System.out.println(strwrt);

            File file = new File(path);
            file.createNewFile();
            Files.write(Paths.get(path), strwrt.toString().getBytes(), StandardOpenOption.APPEND);
            // }

        }

        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            des_Exe.close();
        }




    } // ending of the GetResourceDescription Method
}
