/**
 * 
 */
package org.aksw.limes.core.datastrutures;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.quantitativeMeasures.RunRecord;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.apache.commons.math3.util.Pair;

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
    public Map<EvaluatorType, Double> qualititativeScores = new HashMap<EvaluatorType, Double>();
	/**
	 * The qualitative measures scores e.g. F-MEASURE with value as first pair
	 * value, and variance as second
	 */
	public Map<EvaluatorType, Pair<Double, Double>> qualititativeScoresWithVariance = new HashMap<>();
	private int runInExperiment = 0;
    /** The quantitative measures record */
    RunRecord quanititativeRecord = new RunRecord();
    private LinkSpecification learnedLS;
    
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

	public EvaluationRun(String algorithmName, String implementation, String datasetName,
			Map<EvaluatorType, Double> evaluatorsScores, int run) {
		this.algorithmName = algorithmName;
		this.datasetName = datasetName;
		this.implementationType = implementation;
		for (EvaluatorType evaluator : evaluatorsScores.keySet()) {
			this.qualititativeScores.put(evaluator, evaluatorsScores.get(evaluator));
		}
		this.runInExperiment = run;
	}
    
	/**
	 * @param algorithmName
	 *            The name of the evaluated algorithm
	 * @param implementation
	 *            The implementation type of the evaluated algorithm
	 * @param datasetName
	 *            The name of used dataset for evaluation
	 * @param evaluatorsScores
	 *            A map of pairs (evaluator,score), e.g (F-MEASURE,0.9)
	 * @param learnedLS
	 *            learned LinkSpec
	 */
    public EvaluationRun(String algorithmName,String implementation, String datasetName,Map<EvaluatorType, Double> evaluatorsScores, LinkSpecification learnedLS)
    {
        this.algorithmName = algorithmName;
        this.datasetName=datasetName;
        this.implementationType=implementation;
        for (EvaluatorType evaluator : evaluatorsScores.keySet()) {
            this.qualititativeScores.put(evaluator, evaluatorsScores.get(evaluator));
        }
        this.learnedLS = learnedLS;
    }

	public EvaluationRun(String algorithmName, String implementation, String datasetName,
			Map<EvaluatorType, Double> evaluatorsScores, int run, LinkSpecification learnedLS) {
		this(algorithmName, implementation, datasetName, evaluatorsScores, learnedLS);
		this.runInExperiment = run;
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
        if(learnedLS != null){
            System.out.println("------------------------------------------------------------------------------------------------------------------");
			System.out.println(learnedLS.toStringPretty());
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

	public int getRunInExperiment() {
		return runInExperiment;
	}

	public void setRunInExperiment(int runInExperiment) {
		this.runInExperiment = runInExperiment;
	}

	public RunRecord getQuanititativeRecord() {
		return quanititativeRecord;
	}

	public void setQuanititativeRecord(RunRecord quanititativeRecord) {
		this.quanititativeRecord = quanititativeRecord;
	}

	@Override
	public EvaluationRun clone() {
		EvaluationRun clone = new EvaluationRun();
		clone.algorithmName = algorithmName;
		clone.implementationType = implementationType;
		clone.datasetName = datasetName;
		Map<EvaluatorType, Double> cloneQualiScores = new HashMap<>();
		qualititativeScores.forEach((key, value) -> cloneQualiScores.put(key, value));
		clone.qualititativeScores = cloneQualiScores;
		Map<EvaluatorType, Pair<Double, Double>> cloneQualiScoresWithVariance = new HashMap<>();
		qualititativeScoresWithVariance.forEach((key, value) -> cloneQualiScoresWithVariance.put(key,
				new Pair<Double, Double>(value.getFirst(), value.getSecond())));
		clone.qualititativeScoresWithVariance = cloneQualiScoresWithVariance;
		clone.runInExperiment = runInExperiment;
		if (quanititativeRecord != null) {
			clone.quanititativeRecord = quanititativeRecord.clone();
		}
		if (learnedLS != null) {
			clone.learnedLS = learnedLS.clone();
		}
		return clone;
	}

}
