/**
 *
 */
package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertEquals;

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
 * @author Daniel Obraczka
 */
public class QualitativeMeasuresTest {
	public static final double epsilon = 0.00001;

	public GoldStandard gold1to1;
	public GoldStandard gold1toN;
	public GoldStandard gold1to1WithNeg;
	public GoldStandard gold1toNWithNeg;
	
	public AMapping pred;
	public AMapping predWithNeg;
	public List<String> dataset;
	
	@Before
	public void setupData(){
		dataset = initDataSet();
		gold1to1 = initGoldStandard1to1List();
		gold1toN = initGoldStandard1toNList();
		gold1to1WithNeg = initGoldStandard1to1ListWithNeg();
		gold1toNWithNeg = initGoldStandard1toNListWithNeg();
		pred = initPredictionsList();
		predWithNeg = initPredictionsListWithNeg();
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
		assertEquals(0.979238, accuracy, epsilon);

        double pprecision = calculations.get(EvaluatorType.P_PRECISION);
        assertEquals(0.8,pprecision,epsilon);

        double precall = calculations.get(EvaluatorType.P_RECALL);
		assertEquals(0.4705882, precall, epsilon);


        double pfmeasure = calculations.get(EvaluatorType.PF_MEASURE);
		assertEquals(0.5925925, pfmeasure, epsilon);
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
		assertEquals(0.9793103, accuracy, epsilon);

        double pprecision = calculations.get(EvaluatorType.P_PRECISION);
        assertEquals(0.8,pprecision,epsilon);

        double precall = calculations.get(EvaluatorType.P_RECALL);
		assertEquals(0.4705882, precall, epsilon);


        double pfmeasure = calculations.get(EvaluatorType.PF_MEASURE);
		assertEquals(0.5925925, pfmeasure, epsilon);


    }

	@Test
	public void test1to1WithNeg() {
		Set<EvaluatorType> measure = initEvalMeasures();

		Map<EvaluatorType, Double> calculations = testQualitativeEvaluator(predWithNeg, gold1to1WithNeg, measure);
		double precision = calculations.get(EvaluatorType.PRECISION);
		assertEquals(0.7, precision, epsilon);

		double recall = calculations.get(EvaluatorType.RECALL);
		assertEquals(0.7, recall, epsilon);

		double fmeasure = calculations.get(EvaluatorType.F_MEASURE);
		assertEquals(0.7, fmeasure, epsilon);

		double accuracy = calculations.get(EvaluatorType.ACCURACY);
		assertEquals(0.979238, accuracy, epsilon);

		double pprecision = calculations.get(EvaluatorType.P_PRECISION);
		assertEquals(0.8, pprecision, epsilon);

		double precall = calculations.get(EvaluatorType.P_RECALL);
		assertEquals(0.529411, precall, epsilon);

		double pfmeasure = calculations.get(EvaluatorType.PF_MEASURE);
		assertEquals(0.637168, pfmeasure, epsilon);
	}

	@Test
	public void test1toNWithNeg() {
		Set<EvaluatorType> measure = initEvalMeasures();

		Map<EvaluatorType, Double> calculations = testQualitativeEvaluator(predWithNeg, gold1toNWithNeg, measure);
		double expectedPrecision = 0.8;
		double expectedRecall = 8.0 / 11.0;
		double expectedFMeasure = 2.0 * ((expectedPrecision * expectedRecall) / (expectedPrecision + expectedRecall));

		double precision = calculations.get(EvaluatorType.PRECISION);
		assertEquals(expectedPrecision, precision, epsilon);

		double recall = calculations.get(EvaluatorType.RECALL);
		assertEquals(expectedRecall, recall, epsilon);

		double fmeasure = calculations.get(EvaluatorType.F_MEASURE);
		assertEquals(expectedFMeasure, fmeasure, epsilon);

		double accuracy = calculations.get(EvaluatorType.ACCURACY);
		assertEquals(0.9793103, accuracy, epsilon);

		double pprecision = calculations.get(EvaluatorType.P_PRECISION);
		assertEquals(0.8, pprecision, epsilon);

		double precall = calculations.get(EvaluatorType.P_RECALL);
		assertEquals(0.5294117, precall, epsilon);

		double pfmeasure = calculations.get(EvaluatorType.PF_MEASURE);
		assertEquals(0.637168, pfmeasure, epsilon);

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

	private GoldStandard initGoldStandard1to1ListWithNeg() {

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

		gold.add("http://dbpedia.org/resource/K", "http://dbpedia.org/resource/Z", 0);
		gold.add("http://dbpedia.org/resource/L", "http://dbpedia.org/resource/X", 0);
		gold.add("http://dbpedia.org/resource/M", "http://dbpedia.org/resource/Y", 0);
		gold.add("http://dbpedia.org/resource/N", "http://dbpedia.org/resource/U", 0);
		return new GoldStandard(gold, dataset, dataset);
	}

	private GoldStandard initGoldStandard1toNListWithNeg() {

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

		gold.add("http://dbpedia.org/resource/A", "http://dbpedia.org/resource/Z", 0);
		gold.add("http://dbpedia.org/resource/L", "http://dbpedia.org/resource/X", 0);
		gold.add("http://dbpedia.org/resource/C", "http://dbpedia.org/resource/A", 0);
		gold.add("http://dbpedia.org/resource/N", "http://dbpedia.org/resource/U", 0);
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

	private AMapping initPredictionsListWithNeg() {

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

		pred.add("http://dbpedia.org/resource/A", "http://dbpedia.org/resource/I", 0);
		pred.add("http://dbpedia.org/resource/C", "http://dbpedia.org/resource/A", 0);
		pred.add("http://dbpedia.org/resource/N", "http://dbpedia.org/resource/U", 0);
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
		dataSet.add("http://dbpedia.org/resource/K");
		dataSet.add("http://dbpedia.org/resource/L");
		dataSet.add("http://dbpedia.org/resource/M");
		dataSet.add("http://dbpedia.org/resource/N");
		dataSet.add("http://dbpedia.org/resource/U");
		dataSet.add("http://dbpedia.org/resource/X");
		dataSet.add("http://dbpedia.org/resource/Y");
		dataSet.add("http://dbpedia.org/resource/Z");
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
