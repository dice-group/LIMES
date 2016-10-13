package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class DatasetsInitTest {
    static Logger logger = LoggerFactory.getLogger(DatasetsInitTest.class.getName());
    final public double factor =0.1;
    final public String[] defultDatasetsList = {"PERSON1"/*, "PERSON1_CSV", "PERSON2", "PERSON2_CSV", "RESTAURANTS", "OAEI2014BOOKS"*/};
    // public Set<TaskData> tasks =new TreeSet<TaskData>();


    @Test
    public void test() {

        initializeDataSets(null);
    }

    public Set<TaskData> initializeDataSets(String[] datasetsList) {
        if(datasetsList==null)
            datasetsList=defultDatasetsList;
        Set<TaskData> tasks =new TreeSet<TaskData>();
        TaskData task = new TaskData();
        try {
            for (String ds : datasetsList) {
                logger.info(ds);
                EvaluationData c = DataSetChooser.getData(ds);
                GoldStandard gs = new GoldStandard(c.getReferenceMapping(),c.getSourceCache(),c.getTargetCache());
                //extract training data

                AMapping reference =  c.getReferenceMapping();
                AMapping training = extractTrainingData(reference, factor);
                // assertTrue(training.size() == 50);
                task = new TaskData(gs, c.getSourceCache(), c.getTargetCache());
                task.dataName = ds;
                task.training = training;
                tasks.add(task);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
        assertTrue(true);
        return tasks;
    }
    private AMapping extractTrainingData(AMapping reference, double factor)
    {
        AMapping training = MappingFactory.createDefaultMapping();
        int trainingSize = (int) Math.ceil(factor*reference.getSize());
        HashMap<String, HashMap<String,Double>> refMap = reference.getMap();

        Random       random    = new Random();
        List<String> keys      = new ArrayList<String>(refMap.keySet());
        for(int i=0 ; i< trainingSize ;i++)
        {
            String       sourceInstance = keys.get( random.nextInt(keys.size()) );
            HashMap<String,Double>       targetInstance     = refMap.get(sourceInstance);
            keys.remove(sourceInstance);
            training.add(sourceInstance, targetInstance);
        }

        return training;

    }

}
