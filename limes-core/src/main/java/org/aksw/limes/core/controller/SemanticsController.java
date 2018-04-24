package org.aksw.limes.core.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.mapping.AMapping;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

public class SemanticsController {

    static Logger logger = LoggerFactory.getLogger(SemanticsController.class);

    public String specificationsFile = null;
    public GoldStandard referenceSet = null;
    public String resultsFile = null;
    public CSVWriter csvWriter = null;
    public EvaluationData data = null;
    public String semanticMeasure = null;
    public String baseline = null;

    public void init(String[] args) {

        String datasetName = args[0];

        data = DataSetChooser.getData(datasetName);
        referenceSet = new GoldStandard(data.getReferenceMapping(), data.getSourceCache(), data.getTargetCache());

        baseline = args[1];
        semanticMeasure = args[2];

        resultsFile = (baseline.equals("true"))
                ? data.getDatasetFolder() + "Baseline_" + data.getEvaluationResultFileName()
                : data.getDatasetFolder() + semanticMeasure + "_" + data.getEvaluationResultFileName();

        specificationsFile = data.getDatasetFolder() + "specifications.txt";

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
            csvWriter = new CSVWriter(new FileWriter(resultsFile, false));
        } catch (IOException e) {
            logger.error("Can't create csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }
        csvWriter.writeNext(new String[] { "GoldStandard", "Predictions", "Precision", "Recall", "F-measure",
                "Accuracy", "Runtime" }, false);
        try {
            csvWriter.close();
        } catch (IOException e) {
            logger.error("Couldn't close csv writer");
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    public void evaluate(AMapping predictions, long runtime) {
        QualitativeMeasuresEvaluator evaluator = new QualitativeMeasuresEvaluator();
        Set<EvaluatorType> evaluationMeasures = new LinkedHashSet<EvaluatorType>();
        evaluationMeasures.add(EvaluatorType.PRECISION);
        evaluationMeasures.add(EvaluatorType.RECALL);
        evaluationMeasures.add(EvaluatorType.F_MEASURE);
        evaluationMeasures.add(EvaluatorType.ACCURACY);
        Map<EvaluatorType, Double> evaluations = evaluator.evaluate(predictions, referenceSet, evaluationMeasures);

        writeResults(evaluations, runtime, predictions.getNumberofMappings());
    }

    public void writeResults(Map<EvaluatorType, Double> evaluations, long runtime, int mappings) {

        String[] values = new String[7];
        values[0] = String.valueOf(referenceSet.referenceMappings.getNumberofMappings());
        values[1] = String.valueOf(mappings);

        int index = 2;
        for (Map.Entry<EvaluatorType, Double> entry : evaluations.entrySet()) {
            Double value = entry.getValue();
            values[index] = String.format("%.3f", value);
            index++;
        }
        values[index] = String.valueOf(runtime);

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

    public String replaceMeasure(String expression) {
        String newExpression = expression;
        String[] measures = new String[] { "qgrams", "overlap", "cosine", "trigrams", "levenshtein", "jaccard" };
        for (String measure : measures) {
            if (newExpression.contains(measure)) {
                newExpression = newExpression.replace(measure, "sem_" + measure + "_" + semanticMeasure);
            }
        }
        return newExpression;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            logger.error("Not enough arguments");
            System.exit(1);
        }

        SemanticsController controller = new SemanticsController();
        controller.init(args);

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(controller.specificationsFile);
            br = new BufferedReader(fr);
            String sCurrentLine;
            int i = 0;
            while ((sCurrentLine = br.readLine()) != null) {
                String expression = sCurrentLine.split(">=")[0];
                if (controller.baseline.equals("false"))
                    expression = controller.replaceMeasure(expression);
                Double threshold = Double.parseDouble(sCurrentLine.split(">=")[1]);
                logger.info(expression);
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                AMapping results = LSPipeline.execute(controller.data.getSourceCache(),
                        controller.data.getTargetCache(), expression, threshold, "?x", "?y",
                        RewriterFactory.getRewriterType("default"),
                        ExecutionPlannerFactory.getExecutionPlannerType("default"),
                        ExecutionEngineFactory.getExecutionEngineType("default"));
                long runtime = stopWatch.getTime();
                controller.evaluate(results, runtime);
                i++;
                if(i == 5)
                    break;
            }

        } catch (IOException e) {
            logger.error("Unable to open specification file.");
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
