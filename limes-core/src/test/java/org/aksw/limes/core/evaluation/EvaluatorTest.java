/**
 *
 */
package org.aksw.limes.core.evaluation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.TaskAlgorithm;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.Evaluator;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.Eagle;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.setting.LearningParameters;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
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
    final public String[] datasetsList = {"PERSON1", "PERSON1_CSV", "PERSON2", "PERSON2_CSV", "RESTAURANTS", "OAEI2014BOOKS"};
    final public String[] algorithmsList = {"SUPERVISED_ACTIVE:EAGLE", "SUPERVISED_BATCH:EAGLE", "UNSUPERVISED:EAGLE"};

    public List<TaskAlgorithm> mlAlgorithms = new ArrayList<TaskAlgorithm>();
    public Set<EvaluatorType> evaluators=new TreeSet<EvaluatorType>();
    public Set<TaskData> tasks =new TreeSet<TaskData>();

    @Test
    public void test() {
        testEvaluator();
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
            
            initializeDataSets();
            initializeEvaluators();
            initializeMLAlgorithms();
           Table<String, String, Map<EvaluatorType, Double>> results = evaluator.evaluate(mlAlgorithms, tasks, evaluators, null);
           
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
    }

    private void initializeEvaluators() {
        try {
            evaluators.add(EvaluatorType.PRECISION);
            evaluators.add(EvaluatorType.RECALL);
            evaluators.add(EvaluatorType.F_MEASURE);
            evaluators.add(EvaluatorType.P_PRECISION);
            evaluators.add(EvaluatorType.P_RECALL);
            evaluators.add(EvaluatorType.PF_MEASURE);
            evaluators.add(EvaluatorType.ACCURACY);
            //  DataSetChooser.getData("DRUGS");
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
    }
    
    private LearningParameters initializeLearningParameters(MLImplementationType mlType) {
        LearningParameters lParameters = null;
        if(mlType.equals(MLImplementationType.UNSUPERVISED))
            {
            lParameters = new LearningParameters();
            }
        else  if(mlType.equals(MLImplementationType.SUPERVISED_ACTIVE))
            {
            lParameters = new LearningParameters();
            }
        else  if(mlType.equals(MLImplementationType.SUPERVISED_BATCH))
            {
            lParameters = new LearningParameters();
            }
        return lParameters;

    }
    //remember
    //---------AMLAlgorithm(concrete:SupervisedMLAlgorithm,ActiveMLAlgorithm or UnsupervisedMLAlgorithm--------
    //---                                                                                              --------
    //---         ACoreMLAlgorithm (concrete: EAGLE,WOMBAT,LION) u can retrieve it by get()            --------
    //---                                                                                              --------
    //---------------------------------------------------------------------------------------------------------
    
    private void initializeMLAlgorithms() {
        try
        {
            for (String alg : algorithmsList) {
                String[] algorithmInfo = alg.split(":");// split to get the type and the name of the algorithm
                MLImplementationType algType = MLImplementationType.valueOf(algorithmInfo[0]);// get the type of the algorithm
                //TODO implement initializeLearningParameters()
                LearningParameters mlParameter = null;
                AMLAlgorithm algorithm = null;
                if(algType.equals(MLImplementationType.SUPERVISED_ACTIVE))
                {
                    mlParameter = initializeLearningParameters(MLImplementationType.SUPERVISED_ACTIVE);
                    if(algorithmInfo[0].equals("EAGLE"))//create its core as eagle - it will be enclosed inside SupervisedMLAlgorithm that extends AMLAlgorithm
                        algorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asActive(); //create an eagle learning algorithm
                }
                else if(algType.equals(MLImplementationType.SUPERVISED_BATCH))
                {
                    mlParameter = initializeLearningParameters(MLImplementationType.SUPERVISED_BATCH);
                    if(algorithmInfo[0].equals("EAGLE"))
                        algorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asSupervised(); //create an eagle learning algorithm
                }
                else if(algType.equals(MLImplementationType.UNSUPERVISED))
                {
                    mlParameter = initializeLearningParameters(MLImplementationType.UNSUPERVISED);
                    if(algorithmInfo[0].equals("EAGLE"))
                        algorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asUnsupervised(); //create an eagle learning algorithm
                }
                
                //TODO add other classes cases
                
                mlAlgorithms.add(new TaskAlgorithm(algType, algorithm, mlParameter));// add to list of algorithms
            }

        }catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            assertTrue(false);
        }
        assertTrue(true);
    }

    private void initializeDataSets() {
        TaskData task = null;
        DataSetChooser dataSets = new DataSetChooser();
        try {
            for (String ds : datasetsList) {
                System.out.println(ds);
                task.dataName = ds;
                EvaluationData c = DataSetChooser.getData(ds);
                GoldStandard gs = new GoldStandard(c.getReferenceMapping());
                task = new TaskData(gs,null, c.getSourceCache(), c.getTargetCache());
                //TODO assign the training data and the pseudoFM
                tasks.add(task);
            }


            //	DataSetChooser.getData("DRUGS");
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
    }


}
