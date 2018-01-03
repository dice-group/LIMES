package org.aksw.limes.core.evaluation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.APRF;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;

public class APRFTest {
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
	public void test1to1(){
		double truePositive = APRF.trueFalsePositive(pred, gold1to1.referenceMappings, true);
		assertEquals(7.0, truePositive, 0.0);
		double trueNegative = APRF.trueNegative(pred, gold1to1);
		assertEquals(87.0, trueNegative, 0.0);
		double falsePositive = APRF.trueFalsePositive(pred, gold1to1.referenceMappings, false);
		assertEquals(3.0, falsePositive, 0.0);
		double falseNegative = APRF.falseNegative(pred, gold1to1.referenceMappings);
		assertEquals(3.0, falseNegative, 0.0);
	}

	@Test
	public void test1toN(){
		//11 positive 89 negative in goldstandard
		//10 positives 90 negative in pred
		double truePositive = APRF.trueFalsePositive(pred, gold1toN.referenceMappings, true);
		assertEquals(8.0, truePositive, 0.0);
		double trueNegative = APRF.trueNegative(pred, gold1toN);
		assertEquals(87.0, trueNegative, 0.0);
		double falsePositive = APRF.trueFalsePositive(pred, gold1toN.referenceMappings, false);
		assertEquals(2.0, falsePositive, 0.0);
		double falseNegative = APRF.falseNegative(pred, gold1toN.referenceMappings);
		assertEquals(3.0, falseNegative, 0.0);
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
}
