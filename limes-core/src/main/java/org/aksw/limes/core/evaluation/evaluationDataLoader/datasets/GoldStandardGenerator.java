package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;


import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GoldStandardGenerator {
    ArrayList<String> content = new ArrayList<String>();


    public String fileCheck() throws Exception {

        String filepath = null;
        try {

            String filename = "src\\main\\resources\\Dumpfile\\dumpfile.txt";
            filepath = null;
            File file = new File(filename);
            boolean exists = file.exists();
            if (exists) {
                filepath = fileReadWrite(filename);
                //System.out.println("file exist: " + exists);
            } else {
                Evaluation evl = new Evaluation();
                evl.queryExecution();
                fileReadWrite(filename);

                //System.out.println("file exist: " + exists);
            }


        } catch (NullPointerException e) {
            System.out.print("Caught the NullPointerException");
        }
        return filepath;
    }


    public String fileReadWrite(String filename) throws Exception {

        File file = new File(filename);
        String tempfilepath = null;
        try {
            String st;
            /*header of the file. the header remain same for all examples*/
            String header = "<?xml version='1.0' encoding='utf-8' standalone='no'?>\n" +
                    "<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'\n" +
                    "         xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'\n" +
                    "         xmlns:xsd='http://www.w3.org/2001/XMLSchema#'\n" +
                    "         xmlns:align='http://knowledgeweb.semanticweb.org/heterogeneity/alignment#'>\n" +
                    "<Alignment>\n" +
                    "  <xml>yes</xml>\n" +
                    "  <level>0</level>\n" +
                    "  <type>**</type>\n" +
                    "  <onto1>\n" +
                    "    <Ontology>\n" +
                    "      <location>null</location>\n" +
                    "    </Ontology>\n" +
                    "  </onto1>\n" +
                    "  <onto2>\n" +
                    "    <Ontology>\n" +
                    "      <location>null</location>\n" +
                    "    </Ontology>\n" +
                    "  </onto2>";

            /*reading the dumpfile file and finding the resources*/

            BufferedReader br = new BufferedReader(new FileReader(file));
            //String line = br.readLine();
            while ((st = br.readLine()) != null)
                if (st.contains("rdf:resource")) {

                    //System.out.println(st.substring(st.indexOf("=") + 2, st.lastIndexOf(">") - 2));
                    content.add(st.substring(st.indexOf("=") + 2, st.lastIndexOf(">") - 2));
                }
                /*creating the file */

            //FileWriter fw = new FileWriter("src/main/resources/gold_standard_dumpfile.xml", true);
            /*creating a temporary file in specified directory*/
            File tempFile =
                    File.createTempFile("goldstandard_dumpfile", ".xml");
            tempfilepath = tempFile.getAbsolutePath();
            FileWriter fileWriter = new FileWriter(tempFile, true);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter out = new PrintWriter(bw);

            System.out.println(tempfilepath);
            out.println(header);
            Evaluation ev = new Evaluation();
            List<String> clist = ev.getclasses();

            /*for classes */
            Iterator<String> citerator = clist.iterator();
            while (citerator.hasNext()) {
                String iValaue =citerator.next();
                String body = "<map>\n" +
                        "<cell>\n" +
                        "<entity1 rdf:resource=" + iValaue + "/>\n" +
                        "<entity2 rdf:resource=" + iValaue + "/>\n" +
                        "<relation>=</relation>\n" +
                        "<measure rdf:datatype='http://www.w3.org/2001/XMLSchema#float'>1.0</measure>\n" +
                        "</Cell>\n" +
                        "</map>\n";
                //System.out.println(body);
                out.println(body);
                // checking for the last elemen

            }

            /*for resources */

            //System.out.println(content);
            Iterator<String> iterator = content.iterator();
            while (iterator.hasNext()) {
                String iValaue =iterator.next();
                String body = "<map>\n" +
                        "<cell>\n" +
                        "<entity1 rdf:resource=" + iValaue + "/>\n" +
                        "<entity2 rdf:resource=" + iValaue + "/>\n" +
                        "<relation>=</relation>\n" +
                        "<measure rdf:datatype='http://www.w3.org/2001/XMLSchema#float'>1.0</measure>\n" +
                        "</Cell>\n" +
                        "</map>\n";
                //System.out.println(body);
                out.println(body);
                // checking for the last elemen

            }
            out.println("</Alignment>\n" +
                    "</rdf:RDF>");
            out.close();
            tempFile.deleteOnExit();/*deletes the temp file as soon as JVM closes*/

        }catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(tempfilepath);
        return tempfilepath;



    }
}
