package org.aksw.limes.core.io.config;

import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class Configuration {
	private static final Logger logger = Logger.getLogger(Configuration.class.getName());

	protected KBInfo sourceInfo 				= new KBInfo();
	protected KBInfo targetInfo 				= new KBInfo();
	protected String metricExpression 			= new String();
	protected String acceptanceRelation 		= new String();
	protected String verificationRelation		= new String();
	protected double acceptanceThreshold;
	protected String acceptanceFile;
	protected double verificationThreshold;
	protected String verificationFile;
	protected int exemplars 					= -1;
	protected HashMap<String, String> prefixes 	= new HashMap<String, String>();;
	protected String outputFormat;
	protected String executionPlan 				= "simple";
	protected int granularity 					= 2;
	protected String recallRegulator;
	protected double recallThreshold;



	public Configuration(){
	}

	public Configuration(KBInfo sourceInfo, KBInfo targetInfo,
			String metricExpression, String acceptanceRelation,
			String verificationRelation, double acceptanceThreshold,
			String acceptanceFile, double verificationThreshold,
			String verificationFile, int exemplars,
			HashMap<String, String> prefixes, String outputFormat,
			String executionPlan, int granularity, String recallRegulator,
			double recallThreshold) {
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
		this.exemplars = exemplars;
		this.prefixes = prefixes;
		this.outputFormat = outputFormat;
		this.executionPlan = executionPlan;
		this.granularity = granularity;
		this.recallRegulator = recallRegulator;
		this.recallThreshold = recallThreshold;
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

	public int getExemplars() {
		return exemplars;
	}

	public void setExemplars(int exemplars) {
		this.exemplars = exemplars;
	}

	public HashMap<String, String> getPrefixes() {
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

	public String getRecallRegulator() {
		return recallRegulator;
	}

	public void setRecallRegulator(String recallRegulator) {
		this.recallRegulator = recallRegulator;
	}

	public double getRecallThreshold() {
		return recallThreshold;
	}

	public void setRecallThreshold(double recallThreshold) {
		this.recallThreshold = recallThreshold;
	}

	@Override
	public String toString() {
		return "Configuration" 
				+ "\n["
				+ "\nsourceInfo=" + sourceInfo 
				+ "\ntargetInfo=" + targetInfo 
				+ "\nmetricExpression=" + metricExpression
				+ "\nacceptanceRelation=" + acceptanceRelation
				+ "\nverificationRelation=" + verificationRelation
				+ "\nacceptanceThreshold=" + acceptanceThreshold
				+ "\nacceptanceFile=" + acceptanceFile
				+ "\nverificationThreshold=" + verificationThreshold
				+ "\nverificationFile=" + verificationFile 
				+ "\nexemplars=" + exemplars 
				+ "\nprefixes=" + prefixes 
				+ "\noutputFormat="	+ outputFormat 
				+ "\nexecutionPlan=" + executionPlan
				+ "\ngranularity=" + granularity 
				+ "\nrecallRegulator="	+ recallRegulator 
				+ "\nrecallThreshold=" + recallThreshold
				+ "\n]";
	}
}
