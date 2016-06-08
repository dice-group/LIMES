package org.aksw.limes.core.evaluation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.aksw.limes.core.datastrutures.TaskAlgorithm;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.Eagle;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.setting.LearningParameters;
import org.junit.Test;

public class AlgorithmsInitTest {
    final public String[] algorithmsListData = {"UNSUPERVISED:WOMBATSIMPLE"/*,"SUPERVISED_BATCH:WOMBATSIMPLE"*/};


    @Test
    public void test() {
        initializeMLAlgorithms(null);
    }
    //remember
    //---------AMLAlgorithm(concrete:SupervisedMLAlgorithm,ActiveMLAlgorithm or UnsupervisedMLAlgorithm--------
    //---                                                                                              --------
    //---         ACoreMLAlgorithm (concrete: EAGLE,WOMBAT,LION) u can retrieve it by get()            --------
    //---                                                                                              --------
    //---------------------------------------------------------------------------------------------------------
    
    public List<TaskAlgorithm> initializeMLAlgorithms(String[] algorithmsList) {
        if(algorithmsList==null)
            algorithmsList = algorithmsListData;
        List<TaskAlgorithm> mlAlgorithms = null;

        try
        {
            mlAlgorithms = new ArrayList<TaskAlgorithm>();
            for (String algorithmItem : algorithmsList) {
                String[] algorithmTitles = algorithmItem.split(":");// split to get the type and the name of the algorithm
                MLImplementationType algType = MLImplementationType.valueOf(algorithmTitles[0]);// get the type of the algorithm
                
                LearningParameters mlParameter = null;
                AMLAlgorithm mlAlgorithm = null;
                //check the mlImplementation Type
                if(algType.equals(MLImplementationType.SUPERVISED_ACTIVE))
                {
                    if(algorithmTitles[0].equals("EAGLE"))//create its core as eagle - it will be enclosed inside SupervisedMLAlgorithm that extends AMLAlgorithm
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asActive(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                    {
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asActive(); //create an eagle learning algorithm
                        WombatSimple ws = (WombatSimple)mlAlgorithm.getMl();
                        ws.setDefaultParameters();
                    }
                   mlParameter = initializeLearningParameters(MLImplementationType.SUPERVISED_ACTIVE,algorithmTitles[1]);

                }
                else if(algType.equals(MLImplementationType.SUPERVISED_BATCH))
                {
                    if(algorithmTitles[0].equals("EAGLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asSupervised(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                    {
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asSupervised(); //create an eagle learning algorithm
/*                        WombatSimple ws = (WombatSimple)algorithm.getMl();
                        ws.setDefaultParameters();*/
                    }
                    mlParameter = initializeLearningParameters(MLImplementationType.SUPERVISED_BATCH,algorithmTitles[1]);

                }
                else if(algType.equals(MLImplementationType.UNSUPERVISED))
                {
                    if(algorithmTitles[0].equals("EAGLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asUnsupervised(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                    {
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asUnsupervised(); //create an eagle learning algorithm
/*                        WombatSimple ws = (WombatSimple)algorithm.getMl();
                        ws.setDefaultParameters();*/
                    }
                    mlParameter = initializeLearningParameters(MLImplementationType.UNSUPERVISED,algorithmTitles[1]);

                }
                
                //TODO add other classes cases
                
                mlAlgorithms.add(new TaskAlgorithm(algType, mlAlgorithm, mlParameter));// add to list of algorithms
            }

        }catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            assertTrue(false);
        }
        assertTrue(true);
        return mlAlgorithms;
    }
    
    private LearningParameters initializeLearningParameters(MLImplementationType mlType, String className) {
        LearningParameters lParameters = null;
        if(mlType.equals(MLImplementationType.UNSUPERVISED))
            {
                if(className.equals("WOMBATSIMPLE"))
                    lParameters = initializeWombatSimple();
                
            }
        else  if(mlType.equals(MLImplementationType.SUPERVISED_ACTIVE))
            {
                if(className.equals("WOMBATSIMPLE"))
                    lParameters = initializeWombatSimple();

            }
        else  if(mlType.equals(MLImplementationType.SUPERVISED_BATCH))
            {
                if(className.equals("WOMBATSIMPLE"))
                    lParameters = initializeWombatSimple();

            }
        return lParameters;

    }
    
    private LearningParameters initializeWombatSimple()
    {
        LearningParameters wombaParameters = new LearningParameters();
        
        wombaParameters.put("max refinement tree size",String.valueOf(2000));
        wombaParameters.put("max iterations number", String.valueOf(3));
        wombaParameters.put("max iteration time in minutes", String.valueOf(20));
        wombaParameters.put("max execution time in minutes", String.valueOf(600));
        wombaParameters.put("max fitness threshold", String.valueOf(1));
        wombaParameters.put("minimum properity coverage", String.valueOf(0.4));
        wombaParameters.put("properity learning rate", String.valueOf(0.9));
        wombaParameters.put("overall penalty weit", String.valueOf(0.5d));
        wombaParameters.put("children penalty weit", String.valueOf(1));
        wombaParameters.put("complexity penalty weit", String.valueOf(1));
        wombaParameters.put("verbose", String.valueOf(false));
        wombaParameters.put("measures", String.valueOf(new HashSet<String>(Arrays.asList("jaccard", "trigrams", "cosine", "qgrams"))));
        wombaParameters.put("save mapping", String.valueOf(true));
        
        return wombaParameters;
    }

}
