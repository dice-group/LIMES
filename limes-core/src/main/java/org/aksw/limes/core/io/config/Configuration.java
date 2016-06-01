package org.aksw.limes.core.io.config;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.setting.LearningParameters;

import java.util.*;


/**
 * Contain all LIMES configuration parameters
 *
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class Configuration implements IConfiguration {

    protected KBInfo                sourceInfo = new KBInfo();
    protected KBInfo                targetInfo = new KBInfo();

    protected String                metricExpression = new String();

    protected String                acceptanceRelation = new String();

    protected String                verificationRelation = new String();

    protected double                acceptanceThreshold;
    protected String                acceptanceFile;

    protected double                verificationThreshold;
    protected String                verificationFile;

    protected Map<String, String>   prefixes = new HashMap<String, String>();

    protected String                outputFormat;

    protected String                executionPlan = "simple";
    
    protected int                   granularity = 2;

    protected String                mlAlgorithmName = new String();
    protected LearningParameters    mlParameters = new LearningParameters();
    protected MLImplementationType  mlImplementationType = MLImplementationType.UNSUPERVISED;
    private String                  mlTrainingDataFile = null;
    private EvaluatorType          mlPseudoFMeasure = null;

    public Configuration() {
    }




    public Configuration(KBInfo sourceInfo, KBInfo targetInfo, String metricExpression, String acceptanceRelation,
            String verificationRelation, double acceptanceThreshold, String acceptanceFile,
            double verificationThreshold, String verificationFile, Map<String, String> prefixes, String outputFormat,
            String executionPlan, int granularity, String mlAlgorithmName, LearningParameters mlParameters,
            MLImplementationType mlImplementationType, String mlTrainingDataFile, EvaluatorType mlPseudoFMeasure) {
        super();
        this.sourceInfo = sourceInfo;
        this.targetInfo = targetInfo;
        this.metricExpression = metricExpression;
        this.acceptanceRelation = acceptanceRelation;
        this.verificationRelation = verificationRelation;
        this.acceptanceThreshold = acceptanceThreshold;
        this.acceptanceFile = acceptanceFile;
        this.verificationThreshold = verificationThreshold;
        this.verificationFile = verificationFile;
        this.prefixes = prefixes;
        this.outputFormat = outputFormat;
        this.executionPlan = executionPlan;
        this.granularity = granularity;
        this.mlAlgorithmName = mlAlgorithmName;
        this.mlParameters = mlParameters;
        this.mlImplementationType = mlImplementationType;
        this.mlTrainingDataFile = mlTrainingDataFile;
        this.mlPseudoFMeasure = mlPseudoFMeasure;
    }




    public void addMlParameter(String mlParameterName, String mlParameterValue) {
        this.mlParameters.put(mlParameterName, mlParameterValue);
    }

    public void addPrefixes(String label, String namespace) {
        this.prefixes.put(label, namespace);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Configuration other = (Configuration) obj;
        if (acceptanceFile == null) {
            if (other.acceptanceFile != null)
                return false;
        } else if (!acceptanceFile.equals(other.acceptanceFile))
            return false;
        if (acceptanceRelation == null) {
            if (other.acceptanceRelation != null)
                return false;
        } else if (!acceptanceRelation.equals(other.acceptanceRelation))
            return false;
        if (Double.doubleToLongBits(acceptanceThreshold) != Double.doubleToLongBits(other.acceptanceThreshold))
            return false;
        if (executionPlan == null) {
            if (other.executionPlan != null)
                return false;
        } else if (!executionPlan.equals(other.executionPlan))
            return false;
        if (granularity != other.granularity)
            return false;
        if (metricExpression == null) {
            if (other.metricExpression != null)
                return false;
        } else if (!metricExpression.equals(other.metricExpression))
            return false;
        if (mlAlgorithmName == null) {
            if (other.mlAlgorithmName != null)
                return false;
        } else if (!mlAlgorithmName.equals(other.mlAlgorithmName))
            return false;
        if (mlParameters == null) {
            if (other.mlParameters != null)
                return false;
        } else if (!mlParameters.equals(other.mlParameters))
            return false;
        if (outputFormat == null) {
            if (other.outputFormat != null)
                return false;
        } else if (!outputFormat.equals(other.outputFormat))
            return false;
        if (prefixes == null) {
            if (other.prefixes != null)
                return false;
        } else if (!prefixes.equals(other.prefixes))
            return false;
        if (sourceInfo == null) {
            if (other.sourceInfo != null)
                return false;
        } else if (!sourceInfo.equals(other.sourceInfo))
            return false;
        if (targetInfo == null) {
            if (other.targetInfo != null)
                return false;
        } else if (!targetInfo.equals(other.targetInfo))
            return false;
        if (verificationFile == null) {
            if (other.verificationFile != null)
                return false;
        } else if (!verificationFile.equals(other.verificationFile))
            return false;
        if (verificationRelation == null) {
            if (other.verificationRelation != null)
                return false;
        } else if (!verificationRelation.equals(other.verificationRelation))
            return false;
        if (Double.doubleToLongBits(verificationThreshold) != Double.doubleToLongBits(other.verificationThreshold))
            return false;
        return true;
    }

    public String getAcceptanceFile() {
        return acceptanceFile;
    }

    public String getAcceptanceRelation() {
        return acceptanceRelation;
    }

    public double getAcceptanceThreshold() {
        return acceptanceThreshold;
    }

    @Override
    public Set<String> getConfigurationParametersNames() {
        return new HashSet<String>(Arrays.asList("sourceInfo", "targetInfo", "metricExpression", "acceptanceRelation",
                "verificationRelation", "acceptanceThreshold", "acceptanceFile", "verificationThreshold",
                "verificationFile", "exemplars", "prefixes", "outputFormat", "executionPlan", "granularity",
                "recallRegulator", "recallThreshold"));
    }

    public String getExecutionPlan() {
        return executionPlan;
    }

    public int getGranularity() {
        return granularity;
    }

    public String getMetricExpression() {
        return metricExpression;
    }

    public String getMlAlgorithmName() {
        return mlAlgorithmName;
    }

    public MLImplementationType getMlImplementationType() {
        return mlImplementationType;
    }

    public LearningParameters getMlParameters() {
        return mlParameters;
    }

    public EvaluatorType getMlPseudoFMeasure() {
        return mlPseudoFMeasure;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public Map<String, String> getPrefixes() {
        return prefixes;
    }

    public KBInfo getSourceInfo() {
        return sourceInfo;
    }

    public KBInfo getTargetInfo() {
        return targetInfo;
    }

    public String getTrainingDataFile() {
        return mlTrainingDataFile;
    }

    public String getVerificationFile() {
        return verificationFile;
    }

    public String getVerificationRelation() {
        return verificationRelation;
    }

    public double getVerificationThreshold() {
        return verificationThreshold;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((acceptanceFile == null) ? 0 : acceptanceFile.hashCode());
        result = prime * result + ((acceptanceRelation == null) ? 0 : acceptanceRelation.hashCode());
        long temp;
        temp = Double.doubleToLongBits(acceptanceThreshold);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((executionPlan == null) ? 0 : executionPlan.hashCode());
        result = prime * result + granularity;
        result = prime * result + ((metricExpression == null) ? 0 : metricExpression.hashCode());
        result = prime * result + ((mlAlgorithmName == null) ? 0 : mlAlgorithmName.hashCode());
        result = prime * result + ((mlParameters == null) ? 0 : mlParameters.hashCode());
        result = prime * result + ((outputFormat == null) ? 0 : outputFormat.hashCode());
        result = prime * result + ((prefixes == null) ? 0 : prefixes.hashCode());
        result = prime * result + ((sourceInfo == null) ? 0 : sourceInfo.hashCode());
        result = prime * result + ((targetInfo == null) ? 0 : targetInfo.hashCode());
        result = prime * result + ((verificationFile == null) ? 0 : verificationFile.hashCode());
        result = prime * result + ((verificationRelation == null) ? 0 : verificationRelation.hashCode());
        temp = Double.doubleToLongBits(verificationThreshold);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public void setAcceptanceFile(String acceptanceFile) {
        this.acceptanceFile = acceptanceFile;
    }

    public void setAcceptanceRelation(String acceptanceRelation) {
        this.acceptanceRelation = acceptanceRelation;
    }

    public void setAcceptanceThreshold(double acceptanceThreshold) {
        this.acceptanceThreshold = acceptanceThreshold;
    }

    public void setExecutionPlan(String executionPlan) {
        this.executionPlan = executionPlan;
    }

    public void setGranularity(int granularity) {
        this.granularity = granularity;
    }

    public void setMetricExpression(String metricExpression) {
        this.metricExpression = metricExpression;
    }

    public void setMlAlgorithmName(String mlAlgorithmName) {
        this.mlAlgorithmName = mlAlgorithmName;
    }

    public void setMlImplementationType(MLImplementationType mlImplementationType) {
        this.mlImplementationType = mlImplementationType;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public void setPrefixes(HashMap<String, String> prefixes) {
        this.prefixes = prefixes;
    }

    public void setSourceInfo(KBInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public void setTargetInfo(KBInfo targetInfo) {
        this.targetInfo = targetInfo;
    }

    public void setTrainingDataFile(String trainingDataFile) {
        this.mlTrainingDataFile = trainingDataFile;
    }

    public void setVerificationFile(String verificationFile) {
        this.verificationFile = verificationFile;
    }

    public void setVerificationRelation(String verificationRelation) {
        this.verificationRelation = verificationRelation;
    }

    public void setVerificationThreshold(double verificationThreshold) {
        this.verificationThreshold = verificationThreshold;
    }

    @Override
    public String toString() {
        return "Configuration [sourceInfo=" + sourceInfo + ", targetInfo=" + targetInfo + ", metricExpression="
                + metricExpression + ", acceptanceRelation=" + acceptanceRelation + ", verificationRelation="
                + verificationRelation + ", acceptanceThreshold=" + acceptanceThreshold + ", acceptanceFile="
                + acceptanceFile + ", verificationThreshold=" + verificationThreshold + ", verificationFile="
                + verificationFile + ", prefixes=" + prefixes + ", outputFormat=" + outputFormat + ", executionPlan="
                + executionPlan + ", granularity=" + granularity + ", mlAlgorithmName=" + mlAlgorithmName
                + ", mlParameters=" + mlParameters + "]";
    }

    public void setMlPseudoFMeasure(EvaluatorType mlPseudoFMeasure) {
        this.mlPseudoFMeasure = mlPseudoFMeasure;
    }
}
