/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.evaluation;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.APRF;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class APRFTest {
    public GoldStandard gold1to1;
    public GoldStandard gold1toN;
    public GoldStandard gold1to1WithNegativeExamples;
    public GoldStandard gold1toNWithNegativeExamples;

    public AMapping pred;
    public AMapping predWithNegativeExamples;
    public List<String> dataset;

    public static final double truePositiveExp1to1 = 7.0;
    public static final double truePositiveExp1toN = 8.0;
    public static final double trueNegativeExp = 276.0;
    public static final double falsePositiveExp1to1 = 3.0;
    public static final double falsePositiveExp1toN = 2.0;
    public static final double falseNegativeExp = 3.0;

    @Before
    public void setupData(){
        dataset = initDataSet();
        gold1to1 = initGoldStandard1to1List();
        gold1toN = initGoldStandard1toNList();
        gold1to1WithNegativeExamples = initGoldStandard1to1ListWithNeg();
        gold1toNWithNegativeExamples = initGoldStandard1toNListWithNeg();
        pred = initPredictionsList();
        predWithNegativeExamples = initPredictionsWithNegativeExamplesList();
    }

    @Test
    public void test1to1(){
        double truePositive = APRF.trueFalsePositive(pred, gold1to1.referenceMappings, true);
        assertEquals(truePositiveExp1to1, truePositive, 0.0);
        double trueNegative = APRF.trueNegative(pred, gold1to1);
        assertEquals(trueNegativeExp, trueNegative, 0.0);
        double falsePositive = APRF.trueFalsePositive(pred, gold1to1.referenceMappings, false);
        assertEquals(falsePositiveExp1to1, falsePositive, 0.0);
        double falseNegative = APRF.falseNegative(pred, gold1to1.referenceMappings);
        assertEquals(falseNegativeExp, falseNegative, 0.0);
    }

    @Test
    public void test1to1WithNeg() {
        double truePositiveNeg = APRF.trueFalsePositive(predWithNegativeExamples,
                gold1to1WithNegativeExamples.referenceMappings, true);
        assertEquals(truePositiveExp1to1, truePositiveNeg, 0.0);
        double trueNegativeNeg = APRF.trueNegative(predWithNegativeExamples, gold1to1WithNegativeExamples);
        assertEquals(trueNegativeExp, trueNegativeNeg, 0.0);
        double falsePositiveNeg = APRF.trueFalsePositive(predWithNegativeExamples,
                gold1to1WithNegativeExamples.referenceMappings, false);
        assertEquals(falsePositiveExp1to1, falsePositiveNeg, 0.0);
        double falseNegativeNeg = APRF.falseNegative(predWithNegativeExamples,
                gold1to1WithNegativeExamples.referenceMappings);
        assertEquals(falseNegativeExp, falseNegativeNeg, 0.0);
    }

    @Test
    public void test1toN(){
        //11 positive 89 negative in goldstandard
        //10 positives 90 negative in pred
        double truePositive = APRF.trueFalsePositive(pred, gold1toN.referenceMappings, true);
        assertEquals(truePositiveExp1toN, truePositive, 0.0);
        double trueNegative = APRF.trueNegative(pred, gold1toN);
        assertEquals(trueNegativeExp, trueNegative, 0.0);
        double falsePositive = APRF.trueFalsePositive(pred, gold1toN.referenceMappings, false);
        assertEquals(falsePositiveExp1toN, falsePositive, 0.0);
        double falseNegative = APRF.falseNegative(pred, gold1toN.referenceMappings);
        assertEquals(falseNegativeExp, falseNegative, 0.0);
    }

    @Test
    public void test1toNWithNeg() {
        double truePositiveNeg = APRF.trueFalsePositive(predWithNegativeExamples,
                gold1toNWithNegativeExamples.referenceMappings, true);
        assertEquals(truePositiveExp1toN, truePositiveNeg, 0.0);
        double trueNegativeNeg = APRF.trueNegative(predWithNegativeExamples, gold1toNWithNegativeExamples);
        assertEquals(trueNegativeExp, trueNegativeNeg, 0.0);
        double falsePositiveNeg = APRF.trueFalsePositive(predWithNegativeExamples,
                gold1toNWithNegativeExamples.referenceMappings, false);
        assertEquals(falsePositiveExp1toN, falsePositiveNeg, 0.0);
        double falseNegativeNeg = APRF.falseNegative(predWithNegativeExamples,
                gold1toNWithNegativeExamples.referenceMappings);
        assertEquals(falseNegativeExp, falseNegativeNeg, 0.0);
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

    private AMapping initPredictionsWithNegativeExamplesList() {

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
        pred.add("http://dbpedia.org/resource/F", "http://dbpedia.org/resource/A", 0);
        pred.add("http://dbpedia.org/resource/G", "http://dbpedia.org/resource/B", 0);
        pred.add("http://dbpedia.org/resource/H", "http://dbpedia.org/resource/D", 0);
        pred.add("http://dbpedia.org/resource/I", "http://dbpedia.org/resource/C", 0);
        pred.add("http://dbpedia.org/resource/I", "http://dbpedia.org/resource/F", 0);
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
}
