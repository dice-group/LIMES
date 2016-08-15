/**
 *
 */
package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Test;

/**
 * @author mofeed
 * @author Klaus Lyko
 */
public class QualitativeMeasuresTest {

    @Test
    public void test() {

        List<String> dataSet = initDataSet();
        AMapping predictions = initPredictionsList();
        AMapping goldStandard = initGoldStandardList();
        Set<EvaluatorType> measure = initEvalMeasures();

        GoldStandard gs = new GoldStandard(goldStandard, dataSet, dataSet);

        Map<EvaluatorType, Double> calculations = testQualitativeEvaluator(predictions, gs, measure);

        double precision = calculations.get(EvaluatorType.PRECISION);
        assertTrue(precision == 0.7);

        double recall = calculations.get(EvaluatorType.RECALL);
        assertTrue(recall == 0.7);

        double fmeasure = calculations.get(EvaluatorType.F_MEASURE);
        assertTrue(fmeasure == 0.7);

        double accuracy = calculations.get(EvaluatorType.ACCURACY);
        assertTrue(accuracy == 4.85);

        double pprecision = calculations.get(EvaluatorType.P_PRECISION);
        assertTrue(pprecision == 0.8);

        double precall = calculations.get(EvaluatorType.P_RECALL);
        assertTrue(precall == 0.8);


        double pfmeasure = calculations.get(EvaluatorType.PF_MEASURE);
        assertTrue(pfmeasure > 0.7 && pfmeasure < 0.9);


    }

    private Map<EvaluatorType, Double> testQualitativeEvaluator(AMapping predictions, GoldStandard gs, Set<EvaluatorType> evaluationMeasure) {
        return new QualitativeMeasuresEvaluator().evaluate(predictions, gs, evaluationMeasure);
    }

    private AMapping initGoldStandardList() {

        AMapping gold = MappingFactory.createDefaultMapping();
        gold.add("http://dbpedia.org/resource/A", "http://dbpedia.org/resource/A", 1);
        gold.add("http://dbpedia.org/resource/B", "http://dbpedia.org/resource/B", 1);
        gold.add("http://dbpedia.org/resource/C", "http://dbpedia.org/resource/C", 1);
        gold.add("http://dbpedia.org/resource/D", "http://dbpedia.org/resource/D", 1);
        gold.add("http://dbpedia.org/resource/E", "http://dbpedia.org/resource/E", 1);
        gold.add("http://dbpedia.org/resource/F", "http://dbpedia.org/resource/F", 1);
        gold.add("http://dbpedia.org/resource/G", "http://dbpedia.org/resource/G", 1);
        gold.add("http://dbpedia.org/resource/H", "http://dbpedia.org/resource/H", 1);
        gold.add("http://dbpedia.org/resource/I", "http://dbpedia.org/resource/I", 1);
        gold.add("http://dbpedia.org/resource/J", "http://dbpedia.org/resource/J", 1);
        return gold;

    }

    private AMapping initPredictionsList() {

        AMapping pred = MappingFactory.createDefaultMapping();
        pred.add("http://dbpedia.org/resource/A", "http://dbpedia.org/resource/A", 1);
        pred.add("http://dbpedia.org/resource/B", "http://dbpedia.org/resource/B", 1);
        pred.add("http://dbpedia.org/resource/C", "http://dbpedia.org/resource/C", 1);
        pred.add("http://dbpedia.org/resource/C", "http://dbpedia.org/resource/D", 1);
        pred.add("http://dbpedia.org/resource/D", "http://dbpedia.org/resource/F", 1);
        pred.add("http://dbpedia.org/resource/F", "http://dbpedia.org/resource/F", 1);
        pred.add("http://dbpedia.org/resource/G", "http://dbpedia.org/resource/G", 1);
        pred.add("http://dbpedia.org/resource/H", "http://dbpedia.org/resource/H", 1);
        pred.add("http://dbpedia.org/resource/I", "http://dbpedia.org/resource/H", 1);
        pred.add("http://dbpedia.org/resource/I", "http://dbpedia.org/resource/I", 1);

        return pred;

    }

    private List<String> initDataSet() {
        List<String> dataSet = new ArrayList<String>();
        dataSet.add("http://dbpedia.org/resource/A");
        dataSet.add("http://dbpedia.org/resource/B");
        dataSet.add("http://dbpedia.org/resource/C");
        dataSet.add("http://dbpedia.org/resource/C");
        dataSet.add("http://dbpedia.org/resource/D");
        dataSet.add("http://dbpedia.org/resource/F");
        dataSet.add("http://dbpedia.org/resource/G");
        dataSet.add("http://dbpedia.org/resource/H");
        dataSet.add("http://dbpedia.org/resource/I");
        dataSet.add("http://dbpedia.org/resource/I");
        return dataSet;

    }

    private Set<EvaluatorType> initEvalMeasures() {
        Set<EvaluatorType> measure = new HashSet<EvaluatorType>();

        measure.add(EvaluatorType.PRECISION);
        measure.add(EvaluatorType.RECALL);
        measure.add(EvaluatorType.F_MEASURE);
        measure.add(EvaluatorType.ACCURACY);
        measure.add(EvaluatorType.P_PRECISION);
        measure.add(EvaluatorType.P_RECALL);
        measure.add(EvaluatorType.PF_MEASURE);


        return measure;

    }

}
