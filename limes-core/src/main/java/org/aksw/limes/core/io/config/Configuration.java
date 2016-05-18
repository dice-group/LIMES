package org.aksw.limes.core.io.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//import org.apache.log4j.Logger;

/**
 * Contain all LIMES configuration parameters
 * 
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class Configuration implements IConfiguration {
    // private static final Logger logger =
    // Logger.getLogger(Configuration.class.getName());

    protected KBInfo sourceInfo = new KBInfo();
    protected KBInfo targetInfo = new KBInfo();
    protected String metricExpression = new String();
    protected String acceptanceRelation = new String();
    protected String verificationRelation = new String();
    protected double acceptanceThreshold;
    protected String acceptanceFile;
    protected double verificationThreshold;
    protected String verificationFile;
    protected Map<String, String> prefixes = new HashMap<String, String>();
    protected String outputFormat;
    protected String executionPlan = "simple";
    protected int granularity = 2;
    protected Map<String, String> mlParameters = new HashMap<String, String>();;

    public Map<String, String> getMlParameters() {
	return mlParameters;
    }

    public void addMlParameter(String mlParameterName, String mlParameterValue) {
	this.mlParameters.put(mlParameterName, mlParameterValue);
    }

    public Configuration() {
    }

    public Configuration(KBInfo sourceInfo, KBInfo targetInfo, String metricExpression, String acceptanceRelation,
	    String verificationRelation, double acceptanceThreshold, String acceptanceFile,
	    double verificationThreshold, String verificationFile, HashMap<String, String> prefixes,
	    String outputFormat, String executionPlan, int granularity) {
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
    }

    public KBInfo getSourceInfo() {
	return sourceInfo;
    }

    public void setSourceInfo(KBInfo sourceInfo) {
	this.sourceInfo = sourceInfo;
    }

    public KBInfo getTargetInfo() {
	return targetInfo;
    }

    public void setTargetInfo(KBInfo targetInfo) {
	this.targetInfo = targetInfo;
    }

    public String getMetricExpression() {
	return metricExpression;
    }

    public void setMetricExpression(String metricExpression) {
	this.metricExpression = metricExpression;
    }

    public String getAcceptanceRelation() {
	return acceptanceRelation;
    }

    public void setAcceptanceRelation(String acceptanceRelation) {
	this.acceptanceRelation = acceptanceRelation;
    }

    public String getVerificationRelation() {
	return verificationRelation;
    }

    public void setVerificationRelation(String verificationRelation) {
	this.verificationRelation = verificationRelation;
    }

    public double getAcceptanceThreshold() {
	return acceptanceThreshold;
    }

    public void setAcceptanceThreshold(double acceptanceThreshold) {
	this.acceptanceThreshold = acceptanceThreshold;
    }

    public String getAcceptanceFile() {
	return acceptanceFile;
    }

    public void setAcceptanceFile(String acceptanceFile) {
	this.acceptanceFile = acceptanceFile;
    }

    public double getVerificationThreshold() {
	return verificationThreshold;
    }

    public void setVerificationThreshold(double verificationThreshold) {
	this.verificationThreshold = verificationThreshold;
    }

    public String getVerificationFile() {
	return verificationFile;
    }

    public void setVerificationFile(String verificationFile) {
	this.verificationFile = verificationFile;
    }

    public Map<String, String> getPrefixes() {
	return prefixes;
    }

    public void setPrefixes(HashMap<String, String> prefixes) {
	this.prefixes = prefixes;
    }

    public void addPrefixes(String label, String namespace) {
	this.prefixes.put(label, namespace);
    }

    public String getOutputFormat() {
	return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
	this.outputFormat = outputFormat;
    }

    public String getExecutionPlan() {
	return executionPlan;
    }

    public void setExecutionPlan(String executionPlan) {
	this.executionPlan = executionPlan;
    }

    public int getGranularity() {
	return granularity;
    }

    public void setGranularity(int granularity) {
	this.granularity = granularity;
    }

    @Override
    public String toString() {
	return "Configuration" + "\n[" + "\nsourceInfo=" + sourceInfo + "\ntargetInfo=" + targetInfo
		+ "\nmetricExpression=" + metricExpression + "\nacceptanceRelation=" + acceptanceRelation
		+ "\nverificationRelation=" + verificationRelation + "\nacceptanceThreshold=" + acceptanceThreshold
		+ "\nacceptanceFile=" + acceptanceFile + "\nverificationThreshold=" + verificationThreshold
		+ "\nverificationFile=" + verificationFile + "\nprefixes=" + prefixes + "\noutputFormat=" + outputFormat
		+ "\nexecutionPlan=" + executionPlan + "\ngranularity=" + granularity + "\n]";
    }

    @Override
    public Set<String> getConfigurationParametersNames() {
	return new HashSet<String>(Arrays.asList("sourceInfo", "targetInfo", "metricExpression", "acceptanceRelation",
		"verificationRelation", "acceptanceThreshold", "acceptanceFile", "verificationThreshold",
		"verificationFile", "exemplars", "prefixes", "outputFormat", "executionPlan", "granularity",
		"recallRegulator", "recallThreshold"));
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
	result = prime * result + ((outputFormat == null) ? 0 : outputFormat.hashCode());
	result = prime * result + ((prefixes == null) ? 0 : prefixes.hashCode());
	result = prime * result + (int) (temp ^ (temp >>> 32));
	result = prime * result + ((sourceInfo == null) ? 0 : sourceInfo.hashCode());
	result = prime * result + ((targetInfo == null) ? 0 : targetInfo.hashCode());
	result = prime * result + ((verificationFile == null) ? 0 : verificationFile.hashCode());
	result = prime * result + ((verificationRelation == null) ? 0 : verificationRelation.hashCode());
	temp = Double.doubleToLongBits(verificationThreshold);
	result = prime * result + (int) (temp ^ (temp >>> 32));
	return result;
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

}
