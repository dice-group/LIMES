package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.aksw.limes.core.datastrutures.TaskAlgorithm;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.Eagle;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.WombatComplete;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.junit.Test;

public class AlgorithmsInitTest {
    final public String[] algorithmsListData = {"UNSUPERVISED:WOMBATSIMPLE","SUPERVISED_BATCH:WOMBATSIMPLE"};


    @Test
    public void test() {
        initializeMLAlgorithms(algorithmsListData,1);
    }
    //remember
    //---------AMLAlgorithm(concrete:SupervisedMLAlgorithm,ActiveMLAlgorithm or UnsupervisedMLAlgorithm--------
    //---                                                                                              --------
    //---         ACoreMLAlgorithm (concrete: EAGLE,WOMBAT,LION) u can retrieve it by get()            --------
    //---                                                                                              --------
    //---------------------------------------------------------------------------------------------------------

        public List<TaskAlgorithm> initializeMLAlgorithms(String[] algorithmsList, int datasetsNr) {
        if(algorithmsList==null)
            algorithmsList = algorithmsListData;
        List<TaskAlgorithm> mlAlgorithms = null;

        try
        {
            mlAlgorithms = new ArrayList<TaskAlgorithm>();
            for (String algorithmItem : algorithmsList) {
                String[] algorithmTitles = algorithmItem.split(":");// split to get the type and the name of the algorithm
                MLImplementationType algType = MLImplementationType.valueOf(algorithmTitles[0]);// get the type of the algorithm

                List<LearningParameter> mlParameter = null;
                AMLAlgorithm mlAlgorithm = null;
                //check the mlImplementation Type
                if(algType.equals(MLImplementationType.SUPERVISED_ACTIVE))
                {
                    if(algorithmTitles[1].equals("EAGLE"))//create its core as eagle - it will be enclosed inside SupervisedMLAlgorithm that extends AMLAlgorithm
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asActive(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asActive(); //create an wombat simple learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATCOMPLETE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatComplete.class,algType).asActive(); //create an wombat complete learning algorithm
                    
                   mlParameter = initializeLearningParameters(MLImplementationType.SUPERVISED_ACTIVE,algorithmTitles[1]);

                }
                else if(algType.equals(MLImplementationType.SUPERVISED_BATCH))
                {
                    if(algorithmTitles[1].equals("EAGLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asSupervised(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asSupervised(); //create an wombat simple learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATCOMPLETE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatComplete.class,algType).asSupervised(); //create an wombat complete learning algorithm
                    
                    mlParameter = initializeLearningParameters(MLImplementationType.SUPERVISED_BATCH,algorithmTitles[1]);

                }
                else if(algType.equals(MLImplementationType.UNSUPERVISED))
                {
                    if(algorithmTitles[1].equals("EAGLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asUnsupervised(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asUnsupervised(); //create an wombat simple learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATCOMPLETE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatComplete.class,algType).asUnsupervised(); //create an wombat complete learning algorithm
                    
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

     private List<LearningParameter> initializeLearningParameters(MLImplementationType mlType, String className) {
        List<LearningParameter> lParameters = null;
        if(mlType.equals(MLImplementationType.UNSUPERVISED))
        {
            if(className.equals("WOMBATSIMPLE") || className.equals("WOMBATCOMPLETE"))
                lParameters = initializeWombatSimple();

        }
        else  if(mlType.equals(MLImplementationType.SUPERVISED_ACTIVE))
        {
            if(className.equals("WOMBATSIMPLE") || className.equals("WOMBATCOMPLETE"))
                lParameters = initializeWombatSimple();

        }
        else  if(mlType.equals(MLImplementationType.SUPERVISED_BATCH))
        {
            if(className.equals("WOMBATSIMPLE") || className.equals("WOMBATCOMPLETE"))
                lParameters = initializeWombatSimple();

        }
        return lParameters;

    }

    private List<LearningParameter> initializeWombatSimple()
    {
        List<LearningParameter> wombaParameters = new ArrayList<>() ;

        wombaParameters.add(new LearningParameter("max refinement tree size", 2000, Integer.class, 10, Integer.MAX_VALUE, 10d, "max refinement tree size"));
        wombaParameters.add(new LearningParameter("max iterations number", 3, Integer.class, 1d, Integer.MAX_VALUE, 10d, "max iterations number"));
        wombaParameters.add(new LearningParameter("max iteration time in minutes", 20, Integer.class, 1d, Integer.MAX_VALUE,1, "max iteration time in minutes"));
        wombaParameters.add(new LearningParameter("max execution time in minutes", 600, Integer.class, 1d, Integer.MAX_VALUE,1, "max execution time in minutes"));
        wombaParameters.add(new LearningParameter("max fitness threshold", 1, Double.class, 0d, 1d, 0.01d, "max fitness threshold"));
        wombaParameters.add(new LearningParameter("minimum properity coverage", 0.4, Double.class, 0d, 1d, 0.01d, "minimum properity coverage"));
        wombaParameters.add(new LearningParameter("properity learning rate", 0.9, Double.class, 0d, 1d, 0.01d, "properity learning rate"));
        wombaParameters.add(new LearningParameter("overall penalty weit", 0.5, Double.class, 0d, 1d, 0.01d, "overall penalty weit"));
        wombaParameters.add(new LearningParameter("children penalty weit", 1, Double.class, 0d, 1d, 0.01d, "children penalty weit"));
        wombaParameters.add(new LearningParameter("complexity penalty weit", 1, Double.class, 0d, 1d, 0.01d, "complexity penalty weit"));
        wombaParameters.add(new LearningParameter("verbose", false, Boolean.class, 0, 1, 0, "verbose"));
        wombaParameters.add(new LearningParameter("measures", new HashSet<String>(Arrays.asList("jaccard", "trigrams", "cosine", "qgrams")), MeasureType.class, 0, 0, 0, "measures"));
        wombaParameters.add(new LearningParameter("save mapping", true, Boolean.class, 0, 1, 0, "save mapping"));

        return wombaParameters;
    }

}
