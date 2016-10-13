/**
 * 
 */
package org.aksw.limes.core.datastrutures;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.quantitativeMeasures.RunRecord;

/**
 * This class represents a single run for an algorithm with specific implementation using specific datasets with its qualitative scores and quantitative records
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */

public class EvaluationRun {
    private final int stringSize=200;
    /** The name of the used machine learning algorithm e.g. EAGLE*/
    private String algorithmName="";
    /** The impementation of the machine learning algorithm e.g. UNSUPERVISED */
    private String implementationType="";
    /** The used dataset for evaluation*/
    private String datasetName="";
    /** The qualitative measures scores e.g. F-MEASURE*/
    Map<EvaluatorType, Double> qualititativeScores = new HashMap<EvaluatorType, Double>();
    /** The quantitative measures record */
    RunRecord quanititativeRecord = new RunRecord();
    
    public EvaluationRun(){};
    /** 
     * @param  algorithmName The name of the evaluated algorithm
     * @param  datasetName The name of used dataset for evaluation
     * @param  evaluatorsScores A map of pairs (evaluator,score), e.g (F-MEASURE,0.9)
     * */
    public EvaluationRun(String algorithmName, String datasetName,Map<EvaluatorType, Double> evaluatorsScores)
    {
        this.algorithmName = algorithmName;
        this.datasetName=datasetName;
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
    public EvaluationRun(String algorithmName,String implementation, String datasetName,Map<EvaluatorType, Double> evaluatorsScores)
    {
        this.algorithmName = algorithmName;
        this.datasetName=datasetName;
        this.implementationType=implementation;
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
    public EvaluationRun(String algorithmName, String datasetName,Map<EvaluatorType, Double> evaluatorsScores, RunRecord quantitativeRecord)
    {
        this.algorithmName = algorithmName;
        this.datasetName=datasetName;
        for (EvaluatorType evaluator : evaluatorsScores.keySet()) {
            this.qualititativeScores.put(evaluator, evaluatorsScores.get(evaluator));
        }
        this.quanititativeRecord = quantitativeRecord;
    }
    
    
    
    public Set<EvaluatorType> getQualititativeMeasures()
    {
        return qualititativeScores.keySet();
    }
    
    /**
     * This method displays the evaluation run values in a tabular form
     */
    public void display()
    {
        System.out.println("==================================================================================================================");
        System.out.println("ALGORITHM_NAME\tIMPLEMENTATION\tDATASET");
        System.out.println(getAlgorithmName()+"\t"+getImplementationType()+"\t"+getDatasetName());
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        for (EvaluatorType evaluator : qualititativeScores.keySet()) {
            System.out.println(evaluator+"\t"+qualititativeScores.get(evaluator));
        }
        System.out.println("==================================================================================================================");


    }
    @Override
    public String toString() {
        String erString = Serialize(":");
        return erString+"\n";
    }
    
    public String Serialize(String separator)
    {
        StringBuilder sb = new StringBuilder(stringSize);
        sb.append(getAlgorithmName());
        sb.append(separator);
        sb.append(getImplementationType());
        sb.append(separator);
        sb.append(getDatasetName());
        sb.append(separator);
        for (EvaluatorType evaluator : qualititativeScores.keySet()) {
            sb.append(qualititativeScores.get(evaluator).toString());
            sb.append(separator);
        }
        sb.replace(sb.length()-1, sb.length(), "");
        return sb.toString();
    }
    public String getAlgorithmName() {
        return algorithmName;
    }

    public String getImplementationType() {
        return implementationType;
    }

    public String getDatasetName() {
        return datasetName;
    }


}
