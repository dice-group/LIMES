/**
 *
 */
package org.aksw.limes.core.evaluation;

import static org.junit.Assert.*;

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
import org.junit.Before;
import org.junit.Test;

/**
 * @author mofeed
 * @author Klaus Lyko
 */
public class QualitativeMeasuresTest {
	public static final double epsilon = 0.00001;

	GoldStandard gold1to1;
	GoldStandard gold1toN;
	
	AMapping pred;
	List<String> dataset;
	
	@Before
	public void setupData(){
		dataset = initDataSet();
		gold1to1 = initGoldStandard1to1List();
		gold1toN = initGoldStandard1toNList();
		pred = initPredictionsList();
	}

    @Test
    public void test1to1() {
        Set<EvaluatorType> measure = initEvalMeasures();

        Map<EvaluatorType, Double> calculations = testQualitativeEvaluator(pred, gold1to1, measure);

        double precision = calculations.get(EvaluatorType.PRECISION);
        assertEquals(0.7,precision,epsilon);

        double recall = calculations.get(EvaluatorType.RECALL);
        assertEquals(0.7,recall,epsilon);

        double fmeasure = calculations.get(EvaluatorType.F_MEASURE);
        assertEquals(0.7,fmeasure,epsilon);

        double accuracy = calculations.get(EvaluatorType.ACCURACY);
        assertEquals(0.94,accuracy,epsilon);

        double pprecision = calculations.get(EvaluatorType.P_PRECISION);
        assertEquals(0.8,pprecision,epsilon);

        double precall = calculations.get(EvaluatorType.P_RECALL);
        assertEquals(0.8,precall,epsilon);


        double pfmeasure = calculations.get(EvaluatorType.PF_MEASURE);
        assertTrue(pfmeasure > 0.7 && pfmeasure < 0.9);
    }

    @Test
    public void test1toN() {
        Set<EvaluatorType> measure = initEvalMeasures();

        Map<EvaluatorType, Double> calculations = testQualitativeEvaluator(pred, gold1toN, measure);
        double expectedPrecision = 0.8;
        double expectedRecall = 8.0/11.0;
        double expectedFMeasure = 2.0*((expectedPrecision*expectedRecall)/(expectedPrecision + expectedRecall));
        

        double precision = calculations.get(EvaluatorType.PRECISION);
        assertEquals(expectedPrecision,precision,epsilon);

        double recall = calculations.get(EvaluatorType.RECALL);
        assertEquals(expectedRecall,recall,epsilon);

        double fmeasure = calculations.get(EvaluatorType.F_MEASURE);
        assertEquals(expectedFMeasure,fmeasure,epsilon);

        double accuracy = calculations.get(EvaluatorType.ACCURACY);
        assertEquals(0.95,accuracy,epsilon);

        double pprecision = calculations.get(EvaluatorType.P_PRECISION);
        assertEquals(0.8,pprecision,epsilon);

        double precall = calculations.get(EvaluatorType.P_RECALL);
        assertEquals(0.8,precall,epsilon);


        double pfmeasure = calculations.get(EvaluatorType.PF_MEASURE);
        assertTrue(pfmeasure > 0.7 && pfmeasure < 0.9);


    }

    private Map<EvaluatorType, Double> testQualitativeEvaluator(AMapping predictions, GoldStandard gs, Set<EvaluatorType> evaluationMeasure) {
        return new QualitativeMeasuresEvaluator().evaluate(predictions, gs, evaluationMeasure);
    }

    private GoldStandard initGoldStandard1to1List() {

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
        return new GoldStandard(gold, dataset, dataset);
    }

    private GoldStandard initGoldStandard1toNList() {

        AMapping gold = MappingFactory.createDefaultMapping();
        gold.add("http://dbpedia.org/resource/A", "http://dbpedia.org/resource/A", 1);
        gold.add("http://dbpedia.org/resource/B", "http://dbpedia.org/resource/B", 1);
        gold.add("http://dbpedia.org/resource/C", "http://dbpedia.org/resource/C", 1);
        gold.add("http://dbpedia.org/resource/C", "http://dbpedia.org/resource/D", 1); 
        gold.add("http://dbpedia.org/resource/D", "http://dbpedia.org/resource/D", 1);
        gold.add("http://dbpedia.org/resource/E", "http://dbpedia.org/resource/E", 1);
        gold.add("http://dbpedia.org/resource/F", "http://dbpedia.org/resource/F", 1);
        gold.add("http://dbpedia.org/resource/G", "http://dbpedia.org/resource/G", 1);
        gold.add("http://dbpedia.org/resource/H", "http://dbpedia.org/resource/H", 1);
        gold.add("http://dbpedia.org/resource/I", "http://dbpedia.org/resource/I", 1);
        gold.add("http://dbpedia.org/resource/J", "http://dbpedia.org/resource/J", 1);
        return new GoldStandard(gold, dataset, dataset);
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
