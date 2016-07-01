/**
 * 
 */
package org.aksw.limes.core.datastrutures;

import java.util.HashMap;
import java.util.Map;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.quantitativeMeasures.RunRecord;

/**
 * This class represents a single run for an algorithm with specific implementation using specific datasets with its qualitative scores and quantitative records
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */

public class evaluationRun {
    /** The name of the used machine learning algorithm e.g. EAGLE*/
    String algorithmName="";
    /** The impementation of the machine learning algorithm e.g. UNSUPERVISED */
    String implementationType="";
    /** The used dataset for evaluation*/
    String datasetName="";
    /** The qualitative measures scores e.g. F-MEASURE*/
    Map<EvaluatorType, Double> qualititativeScores = new HashMap<EvaluatorType, Double>();
    /** The quantitative measures record */
    RunRecord quanititativeRecord = new RunRecord();
    
    public evaluationRun(){};
    /** 
     * @param  algorithmName The name of the evaluated algorithm
     * @param  datasetName The name of used dataset for evaluation
     * @param  evaluatorsScores A map of pairs (evaluator,score), e.g (F-MEASURE,0.9)
     * */
    public evaluationRun(String algorithmName, String datasetName,Map<EvaluatorType, Double> evaluatorsScores)
    {
        this.algorithmName=algorithmName;
        this.datasetName = datasetName;
        for (EvaluatorType evaluator : evaluatorsScores.keySet()) {
            this.qualititativeScores.put(evaluator, evaluatorsScores.get(evaluator));
        }
    }
    /** 
     * @param  algorithmName The name of the evaluated algorithm
     * @param  implementation The implementation type of the evaluated algorithm
     * @param  datasetName The name of used dataset for evaluation
     * @param  evaluatorsScores A map of pairs (evaluator,score), e.g (F-MEASURE,0.9)
     * */
    public evaluationRun(String algorithmName,String implementation, String datasetName,Map<EvaluatorType, Double> evaluatorsScores)
    {
        this.algorithmName=algorithmName;
        this.datasetName = datasetName;
        this.implementationType = implementation;
        for (EvaluatorType evaluator : evaluatorsScores.keySet()) {
            this.qualititativeScores.put(evaluator, evaluatorsScores.get(evaluator));
        }
    }
    /** 
     * @param  algorithmName The name of the evaluated algorithm
     * @param  datasetName The name of used dataset for evaluation
     * @param  evaluatorsScores A map of pairs (evaluator,score), e.g (F-MEASURE,0.9)
     * @param  quantitativeRecord A record of the quantitative measures values
     * */
    public evaluationRun(String algorithmName, String datasetName,Map<EvaluatorType, Double> evaluatorsScores, RunRecord quantitativeRecord)
    {
        this.algorithmName=algorithmName;
        this.datasetName = datasetName;
        for (EvaluatorType evaluator : evaluatorsScores.keySet()) {
            this.qualititativeScores.put(evaluator, evaluatorsScores.get(evaluator));
        }
        this.quanititativeRecord = quantitativeRecord;
    }
    /**
     * This method displays the evaluation run values in a tabular form
     */
    public void display()
    {
        System.out.println("==================================================================================================================");
        System.out.println("ALGORITHM NAME\tIMPLEMENTATION\tDATASET");
        System.out.println(algorithmName+"\t"+implementationType+"\t"+datasetName);
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        for (EvaluatorType evaluator : qualititativeScores.keySet()) {
            System.out.println(evaluator+"\t"+qualititativeScores.get(evaluator));
        }
        System.out.println("==================================================================================================================");


    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(algorithmName);
        sb.append(":");
        sb.append(implementationType);
        sb.append(":");
        sb.append(datasetName);
        sb.append(":");
        for (EvaluatorType evaluator : qualititativeScores.keySet()) {
            sb.append(evaluator);
            sb.append("=");
            sb.append(qualititativeScores.get(evaluator).toString());
            sb.append(":");
        }
        sb.append("\n");
        return sb.toString();
    }

}
