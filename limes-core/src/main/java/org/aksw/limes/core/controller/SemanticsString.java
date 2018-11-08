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
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.measures.mapper.MapperFactory;
import org.aksw.limes.core.measures.mapper.semantic.edgecounting.EdgeCountingSemanticMapper;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

public class SemanticsString {

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

    public int no = 0;
    public String measure = null;
    public String sourceV = null;
    public String targetV = null;

    public void init(String[] args, int iteration) {

        String currentDataset = args[0];
        measure = args[1];

        no = Integer.valueOf(args[2]);

        int index = datasets.indexOf(currentDataset);
        currentPredicates = predicates.get(index);
        currentConfigFile = baseFolderName + configNames.get(index);

        AConfigurationReader cR = new XMLConfigurationReader(currentConfigFile);
        cR.read();
        source = HybridCache.getData(cR.getConfiguration().getSourceInfo());
        target = HybridCache.getData(cR.getConfiguration().getTargetInfo());
        sourceV = cR.getConfiguration().getSourceInfo().getVar();
        targetV = cR.getConfiguration().getTargetInfo().getVar();

        String tempName = "-" + measure + "-" + iteration  + "-" + no;
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
        csvWriter.writeNext(new String[] { "LS", "theta", "Runtime", "Mapping" }, false);
        try {
            csvWriter.close();
        } catch (IOException e) {
            logger.error("Couldn't close csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    public void writeResults(String measureExpression, double thrs, long runtime, AMapping mapping) {

        String[] values = new String[4];
        values[0] = measureExpression;
        values[1] = String.valueOf(thrs);
        values[2] = String.valueOf((double) runtime / 1000);
        values[3] = String.valueOf((double) mapping.size());

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
        if (args.length != 3) {
            logger.error("Not enough arguments");
            System.exit(1);
        }

        for (int i = 0; i < 3; i++) {

            SemanticsString controller = new SemanticsString();
            controller.init(args, i);

            double thrs = 0.1d;

            while (thrs <= 1.0d) {
                long begin = System.currentTimeMillis();

                MeasureType type = MeasureFactory.getMeasureType(controller.measure);
                IMapper mapper = MapperFactory.createMapper(type);

                mapper.setNo(controller.no);

                long b = System.currentTimeMillis();
                String expression = controller.measure + "(" + controller.sourceV.charAt(1) + "."
                        + controller.currentPredicates.split("-")[0] + "," + controller.targetV.charAt(1) + "."
                        + controller.currentPredicates.split("-")[1] + ")";

                System.out.println(expression + " " + (Math.round(thrs * 100.0) / 100.0));

                AMapping mapping = mapper.getMapping(controller.source, controller.target, controller.sourceV,
                        controller.targetV, expression, (Math.round(thrs * 100.0) / 100.0));

                long e = System.currentTimeMillis();

                controller.writeResults(controller.measure, (Math.round(thrs * 100.0) / 100.0), (e - b), mapping);
                thrs += 0.1d;

                long end = System.currentTimeMillis();
                long duration = end - begin;
                if (duration >= 7200000)
                    System.exit(1);
            }

        }

    }

}
