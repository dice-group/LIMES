package org.aksw.limes.core.evaluation;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.datastrutures.TaskAlgorithm;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.evaluator.Evaluator;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import com.google.common.collect.Table;

public class EvaluatorsTest {
    static Logger logger = LoggerFactory.getLogger(EvaluatorsTest.class);


    final private String[] datasetsList = {"PERSON1"/*, "PERSON1_CSV", "PERSON2", "PERSON2_CSV", "RESTAURANTS", "OAEI2014BOOKS"*/};
    final private String[] algorithmsListData = {"UNSUPERVISED:WOMBATSIMPLE"/*,"SUPERVISED_BATCH:WOMBATSIMPLE"*/};

    private static final int folds=5;
    private static final boolean crossValidate=false; 
    
    @Test
    public void test() {
/*        if(crossValidate)
            testCrossValidate();
        else*/
            testEvaluator();
    }
    public void testEvaluator() {
        try {
            
            DatasetsInitTest ds = new DatasetsInitTest();
            EvaluatorsInitTest ev = new EvaluatorsInitTest();
            AlgorithmsInitTest al = new AlgorithmsInitTest();
            Evaluator evaluator = new Evaluator();
            
            Set<TaskData> tasks = ds.initializeDataSets(datasetsList);
            Set<EvaluatorType> evaluators = ev.initializeEvaluators();
            List<TaskAlgorithm> algorithms = al.initializeMLAlgorithms(algorithmsListData);
            
            Table<String, String, Map<EvaluatorType, Double>> results = evaluator.evaluate(algorithms, tasks, evaluators, null);
            
            for (String mlAlgorithm : results.rowKeySet()) {
                for (String dataset : results.columnKeySet()) {
                    for (EvaluatorType measure : results.get(mlAlgorithm, dataset).keySet()) {
                        System.out.println(mlAlgorithm+"\t"+dataset+"\t"+measure+"\t"+results.get(mlAlgorithm, dataset).get(measure));
                    }
                }
            }
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
        assertTrue(true);
    }
    
    
    public void testCrossValidate() {
        try {
            
            DatasetsInitTest ds = new DatasetsInitTest();
            EvaluatorsInitTest ev = new EvaluatorsInitTest();
            AlgorithmsInitTest al = new AlgorithmsInitTest();
            Evaluator evaluator = new Evaluator();
            
            Set<TaskData> tasks = ds.initializeDataSets(datasetsList);
            Set<EvaluatorType> evaluators = ev.initializeEvaluators();
            List<TaskAlgorithm> algorithms = al.initializeMLAlgorithms(algorithmsListData);
            
            for (TaskAlgorithm tAlgorithm : algorithms) {
                Table<String, String, Map<EvaluatorType, Double>> results = evaluator.crossValidate(tAlgorithm.getMlAlgorithm(), tasks,folds, evaluators, null);

            }
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
        assertTrue(true);
    }

}
