package org.aksw.limes.core.ml.algorithm.decisionTreeLearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.datastrutures.EvaluationRun;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.TaskAlgorithm;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.Evaluator;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.evaluation.quantitativeMeasures.IQuantitativeMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.Eagle;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatComplete;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DTLExperiment {
    static Logger logger = LoggerFactory.getLogger(DTLExperiment.class);
//    public static final String[] datasetsList = {DataSetChooser.DataSets.DRUGS.toString()};
    public static final String[] datasetsList = {/*DataSetChooser.DataSets.AMAZONGOOGLEPRODUCTS.toString(),*/ DataSetChooser.DataSets.DBPLINKEDMDB.toString(), DataSetChooser.DataSets.PERSON1.toString(), DataSetChooser.DataSets.PERSON2.toString(), DataSetChooser.DataSets.DBLPACM.toString(), DataSetChooser.DataSets.DRUGS.toString()};
    public static final String[] algorithmsListData = {"SUPERVISED_ACTIVE:WOMBATSIMPLE","UNSUPERVISED:WOMBATSIMPLE","SUPERVISED_ACTIVE:DECISIONTREELEARNING"};
    private static QualitativeMeasuresEvaluator eval = new QualitativeMeasuresEvaluator();
    static List<EvaluationRun> runsList = new ArrayList<EvaluationRun>();
    private static final int expRuns = 10;
    public DTLExperiment() {
	// TODO Auto-generated constructor stub
    }
    
    public static Set<TaskData> initializeDataSets(String[] datasetsList) {
        Set<TaskData> tasks =new TreeSet<TaskData>();
        TaskData task = new TaskData();
        try {
            for (String ds : datasetsList) {
                logger.info(ds);
                EvaluationData c = DataSetChooser.getData(ds);
                GoldStandard gs = new GoldStandard(c.getReferenceMapping(),c.getSourceCache(),c.getTargetCache());
                //extract training data

                AMapping reference =  c.getReferenceMapping();
                AMapping training = extractTrainingData(reference, 0.9);
                // assertTrue(training.size() == 50);
                task = new TaskData(gs, c.getSourceCache(), c.getTargetCache(), c);
                task.initialMapping = extractTrainingData(reference, -1.0);
                task.dataName = ds;
                task.training = training;
                tasks.add(task);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return tasks;
    }

    public static Set<EvaluatorType> initializeEvaluators() {
        Set<EvaluatorType> evaluators=null;
        try {
            evaluators=new TreeSet<EvaluatorType>();
            evaluators.add(EvaluatorType.PRECISION);
            evaluators.add(EvaluatorType.RECALL);
            evaluators.add(EvaluatorType.F_MEASURE);
            evaluators.add(EvaluatorType.P_PRECISION);
            evaluators.add(EvaluatorType.P_RECALL);
            evaluators.add(EvaluatorType.PF_MEASURE);
            evaluators.add(EvaluatorType.ACCURACY);
            return evaluators;
        } catch (Exception e) {
        }
        return evaluators;
    }
    public static AMapping extractTrainingData(AMapping reference, double factor)
    {
        AMapping training = MappingFactory.createDefaultMapping();
        int trainingSize = 0;
        if(factor == -1.0){
            trainingSize = 10;
        }else{
        	trainingSize = (int) Math.ceil(factor*reference.getSize());
        }
        HashMap<String, HashMap<String,Double>> refMap = reference.getMap();

        Random       random    = new Random();
        List<String> keys      = new ArrayList<String>(refMap.keySet());
        List<String> values = null;
        if(reference instanceof MemoryMapping){
            values = new ArrayList<String>(((MemoryMapping)reference).reverseSourceTarget().getMap().keySet());
        }else{
            logger.error("HybridMapping not implemented");
        }
        for(int i=0 ; i< trainingSize ;i++){
            if(keys.size()> 0 && i<values.size()-2){
            if(i%2 != 0){
                String sourceInstance = keys.get( random.nextInt(keys.size()) );
//                String sourceInstance = keys.get(keys.size() - 1);
                HashMap<String,Double> targetInstance = refMap.get(sourceInstance);
                keys.remove(sourceInstance);
                training.add(sourceInstance, targetInstance);
            }else{
                String sourceInstance = keys.get( random.nextInt(keys.size()) );
                String targetInstance = getRandomTargetInstance(values, random, refMap, sourceInstance, -1);
//                String sourceInstance = keys.get(keys.size() - 1);
//                String targetInstance = values.get(i+1);
                keys.remove(sourceInstance);
                training.add(sourceInstance, targetInstance, 0d);
            }
            }
        }
        return training;
    }
    
    public static String getRandomTargetInstance(List<String> values, Random random, HashMap<String, HashMap<String,Double>> refMap, String sourceInstance, int previousRandom){
                int randomInt;
                do{
                    randomInt = random.nextInt(values.size());
                }while(randomInt == previousRandom);
                
                String tmpTarget = values.get(randomInt);
                if(refMap.get(sourceInstance).get(tmpTarget) == null){
                    return tmpTarget;
                }
                return getRandomTargetInstance(values, random, refMap, sourceInstance, randomInt);
    }

        public static List<TaskAlgorithm> initializeMLAlgorithms(String[] algorithmsList, int datasetsNr) {
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
                    else if(algorithmTitles[1].equals("DECISIONTREELEARNING")){
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,algType).asActive(); 
                    }
                }
                else if(algType.equals(MLImplementationType.SUPERVISED_BATCH))
                {
                    if(algorithmTitles[1].equals("EAGLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asSupervised(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asSupervised(); //create an wombat simple learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATCOMPLETE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatComplete.class,algType).asSupervised(); //create an wombat complete learning algorithm
                }
                else if(algType.equals(MLImplementationType.UNSUPERVISED))
                {
                    if(algorithmTitles[1].equals("EAGLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(Eagle.class,algType).asUnsupervised(); //create an eagle learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATSIMPLE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,algType).asUnsupervised(); //create an wombat simple learning algorithm
                    else if(algorithmTitles[1].equals("WOMBATCOMPLETE"))
                        mlAlgorithm =  MLAlgorithmFactory.createMLAlgorithm(WombatComplete.class,algType).asUnsupervised(); //create an wombat complete learning algorithm
                }

                //TODO add other classes cases

                mlAlgorithms.add(new TaskAlgorithm(algType, mlAlgorithm, mlParameter));// add to list of algorithms
            }

        }catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
        }
        return mlAlgorithms;
    }
        
        public static void testAgainstOthers(){
            Evaluator evaluator = new Evaluator();
            Set<TaskData> tasks = initializeDataSets(datasetsList);
            Set<EvaluatorType> evaluators = initializeEvaluators();
            List<TaskAlgorithm> algorithms = initializeMLAlgorithms(algorithmsListData,datasetsList.length);

            List<EvaluationRun> results = evaluator.evaluate(algorithms, tasks, evaluators, null);
            for (EvaluationRun er : results) {
                er.display();
            }
            for (EvaluationRun er : results) {
                System.out.println(er);
            }
        }

        public static void testFeedback(){
            Evaluator evaluator = new Evaluator();
            Set<TaskData> tasks = initializeDataSets(datasetsList);
            Set<EvaluatorType> evaluators = initializeEvaluators();
            String[] tmp = {"SUPERVISED_ACTIVE:DECISIONTREELEARNING"};
            List<TaskAlgorithm> algorithms = initializeMLAlgorithms(tmp,datasetsList.length);

            List<EvaluationRun> results = evaluateFeedback(evaluator,algorithms, tasks, evaluators, null);
            for (EvaluationRun er : results) {
                er.display();
            }
            for (EvaluationRun er : results) {
                System.out.println(er);
            }
        }
    public static List<EvaluationRun> evaluateFeedback(Evaluator ev, List<TaskAlgorithm> TaskAlgorithms, Set<TaskData> datasets ,Set<EvaluatorType> QlMeasures, Set<IQuantitativeMeasure> QnMeasures) {
        AMapping predictions = null;
        Map<EvaluatorType, Double> evaluationResults = null;
        try{
            for (TaskAlgorithm tAlgorithm : TaskAlgorithms) {     //iterate over algorithms tasks(type,algorithm,parameter)
                logger.info("Running algorihm: "+tAlgorithm.getMlAlgorithm().getName());
                for (TaskData dataset : datasets) {     //iterate over datasets(name,source,target,mapping,training,pseudofm)
                    logger.info("Used dataset: "+dataset.dataName);
                    //initialize the algorithm with source and target data, passing its parameters too ( if it is null in case of WOMBAT it will use its defaults)
                    tAlgorithm.getMlAlgorithm().init(null, dataset.source, dataset.target);
                    
                    MLResults mlModel= null; // model resulting from the learning process
                    
                        ActiveMLAlgorithm sml =(ActiveMLAlgorithm)tAlgorithm.getMlAlgorithm();
                        sml.getMl().setConfiguration(dataset.evalData.getConfigReader().getConfiguration());
                            ((DecisionTreeLearning)sml.getMl()).setPropertyMapping(dataset.evalData.getPropertyMapping());
                            ((DecisionTreeLearning)sml.getMl()).setInitialMapping(dataset.initialMapping);
                        mlModel = sml.activeLearn();
                    predictions = tAlgorithm.getMlAlgorithm().predict(dataset.source, dataset.target, mlModel);
                    evaluationResults = eval.evaluate(predictions, dataset.goldStandard, QlMeasures);
                    EvaluationRun er = new EvaluationRun(tAlgorithm.getMlAlgorithm().getName().replaceAll("\\s+", ""),"0",dataset.dataName.replaceAll("//s", ""),evaluationResults, mlModel.getLinkSpecification());
                    runsList.add(er);
                    for(int i = 0; i < expRuns; i++){
                        //mlModel = sml.activeLearn(dataset.training);
//                        AMapping nextExamples = sml.getNextExamples((int)Math.round(0.5*dataset.training.size()));
                        AMapping nextExamples = sml.getNextExamples(10);
                        AMapping oracleFeedback = ev.oracleFeedback(nextExamples,dataset.training);
                        mlModel = sml.activeLearn(oracleFeedback);
                    predictions = tAlgorithm.getMlAlgorithm().predict(dataset.source, dataset.target, mlModel);
                    evaluationResults = eval.evaluate(predictions, dataset.goldStandard, QlMeasures);
                    er = new EvaluationRun(tAlgorithm.getMlAlgorithm().getName().replaceAll("\\s+", ""), i+1 +"",dataset.dataName.replaceAll("//s", ""),evaluationResults, mlModel.getLinkSpecification());
                    runsList.add(er);
                    }
                }
            }
        }
        catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
        }
        return runsList;

    }
        
        public static void main(String[] args){
            testAgainstOthers();
//            testFeedback();
        }
}
