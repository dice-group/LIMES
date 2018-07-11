package org.aksw.limes.core.measures.measure.graphs;


import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class LoadRefFile {

    public static void main(String[] args) throws IOException {

        String csvFile = "C:\\Users\\onlym\\Downloads\\CSVReader\\CitiesDataGoldStandard.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        String path = "C:\\Users\\onlym\\Downloads\\CSVReader\\test21.csv";
        new File(path).delete();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] URI = line.split(cvsSplitBy);
                String dbpedia_URI = URI[0];
                String tmp = URI[1].replace("/wiki","/entity");
                String wikidata_URI = tmp.replace("https://","http://");
                //System.out.println(dbpedia_URI);
                System.out.println(wikidata_URI);
                String descriptionLines = DescriptorTest.wikidataUriToDescriotionTripleLines(wikidata_URI);
                System.out.println(descriptionLines);
                FileUtils.writeStringToFile(new File(path), descriptionLines, true);

                // update = URI[0] + "," + URI[1].replace("/wiki","/entity") + "";
                //System.out.println(dbpedia_uri);
                ;

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        }

    }


