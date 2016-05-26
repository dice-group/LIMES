/**
 *
 */
package org.aksw.limes.core.evaluation;

import com.google.common.collect.Table;
import org.aksw.limes.core.datastrutures.Task;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.Evaluator;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.ml.oldalgorithm.EagleSupervised;
import org.aksw.limes.core.ml.oldalgorithm.EagleUnsupervised;
import org.aksw.limes.core.ml.oldalgorithm.Lion;
import org.aksw.limes.core.ml.oldalgorithm.MLAlgorithm;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * @author mofeed
 */
public class EvaluatorTest {
    Evaluator evaluator = new Evaluator();

    @Test
    public void test() {
        fail("Not yet implemented");
    }

    /**
     * @param algorithms:
     *         the set of algorithms used to generate the predicted mappings
     * @param datasets:
     *         the set of dataets to apply the algorithms on them. The should include source Cache, target Cache, goldstandard and predicted mapping
     * @param QlMeasures:
     *         set of qualitative measures
     * @param QnMeasures;
     *         set of quantitative measures
     * @return table contains the results corresponding to the algorithms and measures (algorithm,measure,{measure,value})
     */
    public void testEvaluator() {
        try {
            Set<MLAlgorithm> algorithms = new TreeSet<MLAlgorithm>();
            Set<Task> tasks = initializeDataSets();
            Set<EvaluatorType> evaluators = initializeEvaluators();
            for (Task task : tasks) {
                algorithms.add(new EagleSupervised(null, null, null));
                algorithms.add(new EagleUnsupervised(null, null, null));
                algorithms.add(new Lion(null, null, null));
            }
            Table<String, String, Map<EvaluatorType, Double>> results = evaluator.evaluate(algorithms, tasks, evaluators, null);
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
    }

    private Set<EvaluatorType> initializeEvaluators() {
        Set<EvaluatorType> evaluators = new TreeSet<EvaluatorType>();
        evaluators.add(EvaluatorType.PRECISION);
        evaluators.add(EvaluatorType.RECALL);
        evaluators.add(EvaluatorType.F_MEASURE);
        evaluators.add(EvaluatorType.P_PRECISION);
        evaluators.add(EvaluatorType.P_RECALL);
        evaluators.add(EvaluatorType.PF_MEASURE);
        evaluators.add(EvaluatorType.ACCURACY);

        return evaluators;
    }

    private Set<Task> initializeDataSets() {
        Set<Task> tasks = new TreeSet<Task>();
        Task task = null;
        String[] datasets = {"PERSON1", "PERSON1_CSV", "PERSON2", "PERSON2_CSV", "RESTAURANTS", "OAEI2014BOOKS"};
        DataSetChooser dataSets = new DataSetChooser();
        try {
            for (String ds : datasets) {
                System.out.println(ds);
                EvaluationData c = DataSetChooser.getData(ds);
                task = new Task(c.getReferenceMapping(), null, c.getSourceCache(), c.getTargetCache());
                tasks.add(task);
            }


            //	DataSetChooser.getData("DRUGS");
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
        return tasks;

    }


}
