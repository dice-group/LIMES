package org.aksw.limes.core.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.semantic.edgecounting.EdgeCountingSemanticMapper;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.AEdgeCountingSemanticMeasure.RuntimeStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

public class SemanticsBaseline {

    static Logger logger = LoggerFactory.getLogger(SemanticsBaseline.class);

    public ArrayList<String> datasets = new ArrayList<>(Arrays.asList("Abt-Buy", "Amazon-GoogleProducts", "drugs",
            "DBLP-ACM", "DBLP-Scholar", "DBPLINKEDMDB", "OAEI2014BOOKS"));

    public ArrayList<String> predicates = new ArrayList<>(
            Arrays.asList("description-description", "description-description", "name-name", "title-title",
                    "title-title", "title-title", "rdfs:label-rdfs:label"));

    public ArrayList<String> configNames = new ArrayList<>(
            Arrays.asList("Abt-Buy.xml", "Amazon-GoogleProducts.xml", "dailymed-drugbank.xml", "PublicationData.xml",
                    "DBLP-Scholar.xml", "dbpedia-linkedmdb.xml", "oaei2014_identity.xml"));

    public String resultsFile = null;
    public CSVWriter csvWriter = null;
    public String baseFolderName = System.getProperty("user.dir") + "/src/main/resources/datasets/";
    public String currentPredicates = null;
    public String currentConfigFile = null;
    public HybridCache source = null;
    public HybridCache target = null;
    public boolean index = true;
    public boolean filter = false;
    public int no = 0;
    public String measure = null;
    public String sourceV = null;
    public String targetV = null;

    public void init(String[] args, int iteration) {

        String currentDataset = args[0];
        measure = args[1];

        if (args[2].equalsIgnoreCase("filter"))
            filter = true;
        else
            filter = false;

        if (args[3].equalsIgnoreCase("index"))
            index = true;
        else
            index = false;

        no = Integer.valueOf(args[4]);

        int index = datasets.indexOf(currentDataset);
        currentPredicates = predicates.get(index);
        currentConfigFile = baseFolderName + configNames.get(index);

        AConfigurationReader cR = new XMLConfigurationReader(currentConfigFile);
        cR.read();
        source = HybridCache.getData(cR.getConfiguration().getSourceInfo());
        target = HybridCache.getData(cR.getConfiguration().getTargetInfo());
        sourceV = cR.getConfiguration().getSourceInfo().getVar();
        targetV = cR.getConfiguration().getTargetInfo().getVar();

        String tempName = "-" + measure + "-" + iteration + "-" + args[2].toLowerCase() + "-" + args[3].toLowerCase()
                + "-" + no;
        resultsFile = System.getProperty("user.dir") + "/"
                + cR.getConfiguration().getSourceInfo().getEndpoint().substring(0,
                        cR.getConfiguration().getSourceInfo().getEndpoint().lastIndexOf("/"))
                + "/" + "Runtimes" + tempName + ".csv";
        // create file if it doesn't exist
        File f = new File(resultsFile);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                logger.error("File can't be created");
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        // write header
        try {
            csvWriter = new CSVWriter(new FileWriter(resultsFile, true));
        } catch (IOException e) {
            logger.error("Can't create csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }
        csvWriter
                .writeNext(
                        new String[] { "LS", "theta", "Runtime", "IndexMinMax", "IndexPaths", "createDictionary",
                                "getSimilarityInstances", "sourceTokenizing", "targetTokenizing", "checkStopWords",
                                "checkSimilarity", "getIIndexWords", "getWordIDs", "getIWord", "getSynset",
                                "getMinMaxDepth", "filter", "getHypernymPaths", "getSynsetSimilarity", "Mapping" },
                        false);
        try {
            csvWriter.close();
        } catch (IOException e) {
            logger.error("Couldn't close csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    public void writeResults(String measureExpression, double thrs, long runtime, long IndexMinMax, long IndexPaths,
            EdgeCountingSemanticMapper mapper, AMapping mapping) {

        String[] values = new String[20];
        values[0] = measureExpression;
        values[1] = String.valueOf(thrs);
        values[2] = String.valueOf((double) runtime / 1000);
        values[3] = String.valueOf((double) IndexMinMax / 1000);
        values[4] = String.valueOf((double) IndexPaths / 1000);

        RuntimeStorage runtimes = mapper.getRuntimes();
        values[5] = String.valueOf((double) runtimes.createDictionary() / 1000);

        values[6] = String.valueOf((double) runtimes.getSimilarityInstances() / 1000);

        values[7] = String.valueOf((double) runtimes.getSourceTokenizing() / 1000);
        values[8] = String.valueOf((double) runtimes.getTargetTokenizing() / 1000);
        values[9] = String.valueOf((double) runtimes.checkStopWords() / 1000);

        ///////////////
        values[10] = String.valueOf((double) runtimes.getCheckSimilarity() / 1000);

        values[11] = String.valueOf((double) runtimes.getGetIIndexWords() / 1000);
        values[12] = String.valueOf((double) runtimes.getGetWordIDs() / 1000);
        values[13] = String.valueOf((double) runtimes.getGetIWord() / 1000);

        values[14] = String.valueOf((double) runtimes.getGetSynset() / 1000);
        values[15] = String.valueOf((double) runtimes.getGetMinMaxDepth() / 1000);
        values[16] = String.valueOf((double) runtimes.getFilter() / 1000);
        values[17] = String.valueOf((double) runtimes.getGetHypernymPaths() / 1000);

        values[18] = String.valueOf((double) runtimes.getGetSynsetSimilarity() / 1000);

        values[19] = String.valueOf((double) mapping.size());

        try {
            csvWriter = new CSVWriter(new FileWriter(resultsFile, true));
        } catch (IOException e) {
            logger.error("Can't create csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }
        csvWriter.writeNext(values, false);

        try {
            csvWriter.close();
        } catch (IOException e) {
            logger.error("Couldn't close csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            logger.error("Not enough arguments");
            System.exit(1);
        }

        for (int i = 0; i < 3; i++) {

            SemanticsBaseline controller = new SemanticsBaseline();
            controller.init(args, i);

            double thrs = 0.1d;

            while (thrs <= 1.0d) {
                long begin = System.currentTimeMillis();

                EdgeCountingSemanticMapper mapper = new EdgeCountingSemanticMapper();
                mapper.setValues(controller.index, controller.filter);
                mapper.setNo(controller.no);

                long b = System.currentTimeMillis();
                String expression = controller.measure + "(" + controller.sourceV.charAt(1) + "."
                        + controller.currentPredicates.split("-")[0] + "," + controller.targetV.charAt(1) + "."
                        + controller.currentPredicates.split("-")[1] + ")";

                System.out.println(expression + " " + (Math.round(thrs * 100.0) / 100.0));

                AMapping mapping = mapper.getMapping(controller.source, controller.target, controller.sourceV,
                        controller.targetV, expression, (Math.round(thrs * 100.0) / 100.0));

                long e = System.currentTimeMillis();

                controller.writeResults(controller.measure, (Math.round(thrs * 100.0) / 100.0),
                        (e - b) + mapper.getIndexMinMax() + mapper.getIndexPaths(), mapper.getIndexMinMax(),
                        mapper.getIndexPaths(), mapper, mapping);
                thrs += 0.1d;

                long end = System.currentTimeMillis();
                long duration = end - begin;
                if (duration >= 7200000)
                    System.exit(1);
            }

        }

    }

}
