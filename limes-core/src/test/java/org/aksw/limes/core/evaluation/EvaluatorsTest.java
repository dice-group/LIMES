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

import com.google.common.collect.ImmutableList;
import org.aksw.limes.core.datastrutures.EvaluationRun;
import org.aksw.limes.core.datastrutures.TaskAlgorithm;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.evaluator.Evaluator;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.evaluator.FoldData;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EvaluatorsTest {
    static Logger logger = LoggerFactory.getLogger(EvaluatorsTest.class);


    final private String[] datasetsList = {"RESTAURANTS"/*,"PERSON1",  "PERSON2", "PERSON1_CSV", "PERSON2_CSV", "OAEI2014BOOKS"*/};
    final private String[] algorithmsListData = {"UNSUPERVISED:WOMBATSIMPLE","SUPERVISED_BATCH:WOMBATSIMPLE","SUPERVISED_ACTIVE:WOMBATSIMPLE","UNSUPERVISED:WOMBATCOMPLETE","SUPERVISED_BATCH:WOMBATCOMPLETE"};

    private static final int folds=10;
    private static final boolean crossValidate=false;

    @Test
    public void test() {
        /*        if(crossValidate)
            testCrossValidate();
        else*/
        //        testEvaluator();
        // testCrossValidate(); //TODO: FIX
    }

    public void testEvaluator() {
        try {

            DatasetsInitTest ds = new DatasetsInitTest();
            EvaluatorsInitTest ev = new EvaluatorsInitTest();
            AlgorithmsInitTest al = new AlgorithmsInitTest();
            Evaluator evaluator = new Evaluator();

            Set<TaskData> tasks = ds.initializeDataSets(datasetsList);
            Set<EvaluatorType> evaluators = ev.initializeEvaluators();
            List<TaskAlgorithm> algorithms = al.initializeMLAlgorithms(algorithmsListData,datasetsList.length);

            List<EvaluationRun> results = evaluator.evaluate(algorithms, tasks, evaluators, null);
            for (EvaluationRun er : results) {
                er.display();
            }
            for (EvaluationRun er : results) {
                System.out.println(er);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
        assertTrue(true);
    }


    public void testCrossValidate() {


        DatasetsInitTest ds = new DatasetsInitTest();
        EvaluatorsInitTest ev = new EvaluatorsInitTest();
        AlgorithmsInitTest al = new AlgorithmsInitTest();
        Evaluator evaluator = new Evaluator();

        Set<TaskData> tasks = ds.initializeDataSets(datasetsList);
        Set<EvaluatorType> evaluators = ev.initializeEvaluators();
        List<TaskAlgorithm> algorithms = al.initializeMLAlgorithms(algorithmsListData,datasetsList.length);
        List<EvaluationRun> results =null;
        for (TaskAlgorithm tAlgorithm : algorithms) {
            System.out.println("testing "+tAlgorithm.getMlAlgorithm().toString());
            if(tAlgorithm.getMlAlgorithm() instanceof SupervisedMLAlgorithm)
                results = evaluator.crossValidate(tAlgorithm.getMlAlgorithm(), null, tasks,folds, evaluators, null);
            System.out.println(results);
        }
        assertTrue(true);
    }

    @Test
    public void testCreateParameterGrid() {
        LearningParameter lp1 = new LearningParameter("lp1", 0, Integer.class, 0d, 100d, 1d, "");
        LearningParameter lp2 = new LearningParameter("lp2", 0.1, Double.class, 0d, 1d, 0.1d, "");
        LearningParameter lp3 = new LearningParameter("lp3", "test", String.class, new String[] { "a", "b" }, "");
        List<Object> lp1Values = ImmutableList.of(0, 10, 20, 30, 50, 80);
        List<Object> lp2Values = ImmutableList.of(0.0, 0.1, 0.2, 0.3, 0.4, 0.5);
        List<Object> lp3Values = ImmutableList.of("a", "b");
        Map<LearningParameter, List<Object>> params = new HashMap<>();
        params.put(lp1, lp1Values);
        params.put(lp2, lp2Values);
        params.put(lp3, lp3Values);
        assertEquals(lp1Values.size() * lp2Values.size() * lp3Values.size(),
                new Evaluator().createParameterGrid(params).size());
    }

    @Test
    public void testUpdateSuccessesAndFailures() throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        AMapping ref = MappingFactory.createDefaultMapping();
        ref.add("i1", "i1", 1.0);
        ref.add("i2", "i2", 1.0);
        ref.add("i3", "i3", 1.0);
        ref.add("i4", "i4", 1.0);
        ref.add("i5", "i5", 1.0);
        FoldData testData = new FoldData();
        testData.map = ref;
        AMapping a1Map = MappingFactory.createDefaultMapping();
        a1Map.add("i1", "i1", 1.0);
        a1Map.add("i5", "i4", 1.0);
        AMapping a2Map = MappingFactory.createDefaultMapping();
        a2Map.add("i1", "i1", 1.0);
        a2Map.add("i5", "i5", 1.0);
        AMapping a3Map = MappingFactory.createDefaultMapping();
        a3Map.add("i3", "i3", 1.0);
        a3Map.add("i4", "i4", 1.0);
        a3Map.add("i5", "i5", 1.0);
        Map<String, AMapping> algoMappings = new HashMap<>();
        algoMappings.put("a1", a1Map);
        algoMappings.put("a2", a2Map);
        algoMappings.put("a3", a3Map);

        Map<String, Map<String, int[]>> expected = new HashMap<>();
        Map<String, int[]> a1a2a3Map = new HashMap<>();
        a1a2a3Map.put("a2", new int[] { 0, 1 });
        a1a2a3Map.put("a3", new int[] { 1, 3 });
        expected.put("a1", a1a2a3Map);
        Map<String, int[]> a2a3Map = new HashMap<>();
        a2a3Map.put("a3", new int[] { 1, 2 });
        expected.put("a2", a2a3Map);

        Method method = Evaluator.class.getDeclaredMethod("updateSuccessesAndFailures", Map.class, FoldData.class);
        method.setAccessible(true);
        Evaluator eval = new Evaluator();
        method.invoke(eval, algoMappings, testData);
        assertTrue(Arrays.equals(expected.get("a1").get("a2"), eval.successesAndFailures.get("a1").get("a2")));
        assertTrue(Arrays.equals(expected.get("a1").get("a3"), eval.successesAndFailures.get("a1").get("a3")));
        assertTrue(Arrays.equals(expected.get("a2").get("a3"), eval.successesAndFailures.get("a2").get("a3")));
    }
}