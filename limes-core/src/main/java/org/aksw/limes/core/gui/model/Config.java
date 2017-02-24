package org.aksw.limes.core.gui.model;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.planner.HeliosPlanner;
import org.aksw.limes.core.gui.model.metric.MetricFormatException;
import org.aksw.limes.core.gui.model.metric.MetricParser;
import org.aksw.limes.core.gui.model.metric.Output;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.aksw.limes.core.gui.util.sparql.PrefixHelper;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.config.writer.RDFConfigurationWriter;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Contains all important information for the graphical representation
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class Config extends Configuration {
	
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    
    /**
     * Checks if the restriction has the form "?y a prefix:class" or "?y rdf:type dbpedia:Drug"
     */
    private static final String restrictionRegex = "\\?\\w+\\s+\\w+(:\\w+){0,1}\\s+\\w+:\\w+";

    /**
     * PropertyMapping of current query
     */
    public PropertyMapping propertyMapping;
    /**
     * current Metric
     */
    private Output metric = null;
    /**
     * Source Endpoint
     */
    private Endpoint sourceEndpoint;
    /**
     * Target Endpoint
     */
    private Endpoint targetEndpoint;
    /**
     * Mapping of current MappingTask
     */
    private AMapping mapping;

    private static final String sourceVar = "?x";
    private static final String targetVar = "?y";
    public static final double defaultAcceptanceThreshold = 0.9;
    public static final double defaultReviewThreshold = 0.7;
    private static final String defaultAcceptanceRelation = "owl:sameAs";
    private static final String defaultVerificationRelation = "owl:sameAs";

    /**
     * Constructor
     */
    public Config() {
	this.acceptanceRelation = defaultAcceptanceRelation;
	this.verificationRelation = defaultVerificationRelation;
	this.sourceInfo = new KBInfo();
	this.sourceInfo.setVar(sourceVar);
	this.targetInfo = new KBInfo();
	this.targetInfo.setVar(targetVar);
	metric = new Output();
	this.sourceEndpoint = new Endpoint(this.sourceInfo, this);
	this.targetEndpoint = new Endpoint(this.targetInfo, this);
    }

    /**
     * Config of the GUI
     * 
     * @param sourceInfo
     *            sourceInfo
     * @param targetInfo
     *            targetInfo
     * @param metricExpression
     *            metricExpression
     * @param acceptanceRelation
     *            acceptanceRelation
     * @param verificationRelation
     *            verificationRelation
     * @param acceptanceThreshold
     *            acceptanceThreshold
     * @param acceptanceFile
     *            acceptanceFile
     * @param verificationThreshold
     *            verificationThreshold
     * @param verificationFile
     *            verificationFile
     * @param prefixes
     *            prefixes
     * @param outputFormat
     *            outputFormat
     * @param executionRewriter
     *            executionRewriter
     * @param executionPlanner
     *            executionPlanner
     * @param executionEngine
     *            executionEngine
     * @param granularity
     *            granularity
     * @param mlAlgorithmName
     *            mlAlgorithmName
     * @param mlParameters
     *            mlParameters
     * @param mlImplementationType
     *            mlImplementationType
     * @param mlTrainingDataFile
     *            mlTrainingDataFile
     * @param mlPseudoFMeasure
     *            mlPseudoFMeasure
     */
    public Config(KBInfo sourceInfo, KBInfo targetInfo, String metricExpression,
	    String acceptanceRelation, String verificationRelation, double acceptanceThreshold,
	    String acceptanceFile, double verificationThreshold, String verificationFile,
	    Map<String, String> prefixes, String outputFormat, String executionRewriter,
	    String executionPlanner, String executionEngine, int granularity,
	    String mlAlgorithmName, List<LearningParameter> mlParameters,
	    MLImplementationType mlImplementationType, String mlTrainingDataFile,
	    EvaluatorType mlPseudoFMeasure) {
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
	this.executionRewriter = executionRewriter;
	this.executionPlanner = executionPlanner;
	this.executionEngine = executionEngine;
	this.granularity = granularity;
	this.mlAlgorithmName = mlAlgorithmName;
	this.mlAlgorithmParameters = mlParameters;
	this.mlImplementationType = mlImplementationType;
	this.mlTrainingDataFile = mlTrainingDataFile;
	this.mlPseudoFMeasure = mlPseudoFMeasure;

	if (this.acceptanceRelation.equals("") || this.acceptanceRelation == null
		|| this.acceptanceRelation.startsWith("file:")) {
	    this.acceptanceRelation = defaultAcceptanceRelation;
	}
	if (this.verificationRelation.equals("") || this.verificationRelation == null
		|| this.verificationRelation.startsWith("file:")) {
	    this.verificationRelation = defaultVerificationRelation;
	}
	
	metric = new Output();
	this.sourceEndpoint = new Endpoint(this.sourceInfo, this);
	this.targetEndpoint = new Endpoint(this.targetInfo, this);

	//get Class restriction
	if(sourceInfo.getRestrictions().size() == 1 && targetInfo.getRestrictions().size() == 1){
		if(Pattern.matches(restrictionRegex, sourceInfo.getRestrictions().get(0)) && Pattern.matches(restrictionRegex, targetInfo.getRestrictions().get(0))){
			String sourceRestriction = sourceInfo.getRestrictions().get(0);
			String targetRestriction = targetInfo.getRestrictions().get(0);
			String sourceClass = sourceRestriction.substring(sourceRestriction.lastIndexOf(" ")).trim();
			String targetClass = targetRestriction.substring(targetRestriction.lastIndexOf(" ")).trim();
			this.sourceEndpoint.setCurrentClassAsString(PrefixHelper.expand(sourceClass));
			this.targetEndpoint.setCurrentClassAsString(PrefixHelper.expand(targetClass));
			System.err.println(this.sourceEndpoint.getCurrentClass().getUri() + " " + this.targetEndpoint.getCurrentClass().getUri());
		}else{
			logger.error("Restrictions that are more complex than \" ?y a prefix:class \" are not yet implemented in the GUI");
		}
	}else{
		logger.error("Restrictions that are more complex than \" ?y a prefix:class \" are not yet implemented in the GUI");
	}
    }

    /**
     * loads the linkspec from file
     *
     * @param file
     *            file that should be loaded
     * @return Config Config with loaded file
     * @throws Exception
     *             FileNotFoundException
     */
    public static Config loadFromFile(File file) throws Exception {
	AConfigurationReader reader = null;
	if (file.getAbsolutePath().contains(".xml")) {
	    reader = new XMLConfigurationReader(file.getPath());
	} else if (file.getAbsolutePath().contains(".rdf")
		|| file.getAbsolutePath().contains(".ttl")
		|| file.getAbsolutePath().contains(".n3") || file.getAbsolutePath().contains(".nt")) {
	    reader = new RDFConfigurationReader(file.getPath());
	} else {
	    throw new RuntimeException("Unknown filetype!");
	}
	Config outConfig;
	Configuration tmp = reader.read();
	if (tmp.getSourceInfo() == null || tmp.getTargetInfo() == null) {
	    throw new RuntimeException("Invalid configuration file!");
	}
	outConfig = new Config(tmp.getSourceInfo(), tmp.getTargetInfo(), tmp.getMetricExpression(),
		tmp.getAcceptanceRelation(), tmp.getVerificationRelation(),
		tmp.getAcceptanceThreshold(), tmp.getAcceptanceFile(),
		tmp.getVerificationThreshold(), tmp.getVerificationFile(),
		(Map<String, String>) tmp.getPrefixes(), tmp.getOutputFormat(),
		tmp.getExecutionRewriter(), tmp.getExecutionPlanner(), tmp.getExecutionEngine(),
		tmp.getGranularity(), tmp.getMlAlgorithmName(),
		(List<LearningParameter>) tmp.getMlAlgorithmParameters(),
		tmp.getMlImplementationType(), tmp.getMlTrainingDataFile(),
		tmp.getMlPseudoFMeasure());
	outConfig.setMlTrainingDataFile(tmp.getMlTrainingDataFile());
	for (String s : outConfig.getSourceInfo().getPrefixes().keySet()) {
	    PrefixHelper.addPrefix(s, outConfig.getSourceInfo().getPrefixes().get(s));
	}
	for (String s : outConfig.getTargetInfo().getPrefixes().keySet()) {
	    PrefixHelper.addPrefix(s, outConfig.getTargetInfo().getPrefixes().get(s));
	}
	if (tmp.getMlAlgorithmName() == null || tmp.getMlAlgorithmName().equals("")) {
	    outConfig.metric = MetricParser.parse(outConfig.metricExpression, outConfig
		    .getSourceInfo().getVar().replaceAll("\\?", ""));
	    outConfig.metric.param1 = outConfig.acceptanceThreshold;
	    outConfig.metric.param2 = outConfig.verificationThreshold;
	} else {
	    Alert alert = new Alert(AlertType.INFORMATION);
	    alert.setContentText("Running machine learning configurations in the GUI is possible, although using the command line for this most of the time is more preferable. All the usable information has been saved though and you can run any machine learning type from the GUI now");
	    alert.showAndWait();
	    outConfig.metric = new Output();
	}
	return outConfig;
    }

    /**
     * saves the config to file
     *
     * @param file
     *            file in which the config should be saved
     * @throws Exception
     *             FileNotFoundException
     */
    public void save(File file) throws Exception {
	if (!metric.isComplete()) {
	    throw new MetricFormatException();
	}
	// String format =
	// file.getName().substring(file.getName().lastIndexOf("."),
	// file.getName().length());
	// Not implemented yet
	// if (format.equals(".xml")) {
	// XMLConfigurationWriter xmlwriter = new XMLConfigurationWriter();
	// xmlwriter.write(this, file.getAbsolutePath());
	// } else {

	// In case the relation are not abbreaviated this an lead to errors
	if (this.acceptanceRelation != null) {
	    if (this.acceptanceRelation.startsWith("http:")) {
		this.acceptanceRelation = PrefixHelper.abbreviate(this.acceptanceRelation);
	    }
	}
	if (this.verificationRelation != null) {
	    if (this.verificationRelation.startsWith("http:")) {
		this.verificationRelation = PrefixHelper.abbreviate(this.verificationRelation);
	    }
	}

	// If there is renaming, change it in the metric because it is not
	// possible to save in RDF
	String newME = metricExpression;
	for (String s : sourceEndpoint.getInfo().getFunctions().keySet()) {
	    for (String t : sourceEndpoint.getInfo().getFunctions().get(s).keySet()) {
		newME = metricExpression.replace(t, s);
	    }
	}
	for (String s : targetEndpoint.getInfo().getFunctions().keySet()) {
	    for (String t : targetEndpoint.getInfo().getFunctions().get(s).keySet()) {
		newME = metricExpression.replace(t, s);
	    }
	}
	metricExpression = newME;

	RDFConfigurationWriter rdfwriter = new RDFConfigurationWriter();
	rdfwriter.write(this, file.getAbsolutePath());
	// }
    }

    /**
     * Returns the Acceptance Threshold
     *
     * @return the Acceptance Threshold
     */
    public double getAcceptanceThreshold() {
	if (metric == null || metric.param1 == null) {
	    return defaultAcceptanceThreshold;
	}
	return metric.param1;
    }

    /**
     * Sets the acceptanceThreshold
     *
     * @param acceptanceThreshold
     *            threshold
     */
    public void setAcceptanceThreshold(double acceptanceThreshold) {
	if (metric == null) {
	    metric = new Output();
	}
	metric.param1 = acceptanceThreshold;
    }

    /**
     * Returns the Verification Threshold
     *
     * @return Verification Threshold
     */
    public double getVerificationThreshold() {
	if (metric == null || metric.param2 == null) {
	    DecimalFormat twoDForm = new DecimalFormat("#.####");
	    NumberFormat format = NumberFormat.getInstance();
	    Number number;
	    try {
		number = format.parse(twoDForm.format(getAcceptanceThreshold() - 0.1d));
	    } catch (Exception e) {
		System.err.println(e);
		return defaultReviewThreshold;
	    }
	    return number.doubleValue();
	} else {
	    return metric.param2;
	}
    }

    /**
     * creates the Task in which the mappingProcess is realized
     *
     * @param results
     *            of the mapping
     * @return null
     */
    public Task<Void> createMappingTask(ObservableList<Result> results) {
	return new Task<Void>() {
	    @Override
	    protected Void call() {
		ACache sourceCache = sourceEndpoint.getCache();
		ACache targetCache = targetEndpoint.getCache();
		LinkSpecification ls = new LinkSpecification();
		ls.readSpec(getMetricExpression(), getAcceptanceThreshold());
		HeliosPlanner hp = new HeliosPlanner(sourceCache, targetCache);
		NestedPlan plan = hp.plan(ls);
		SimpleExecutionEngine ee = new SimpleExecutionEngine(sourceCache, targetCache,
			getSourceInfo().getVar(), getTargetInfo().getVar());
		mapping = ee.executeStatic(plan);
		setMapping(mapping);
		mapping.getMap().forEach((sourceURI, map2) -> {
		    map2.forEach((targetURI, value) -> {
			results.add(new Result(sourceURI, targetURI, value));
		    });
		});
		return null;
	    }
	};

    }

    /**
     * Getter SourceEndpoint
     *
     * @return SourceEndpoint
     */
    public Endpoint getSourceEndpoint() {
	return sourceEndpoint;
    }

    /**
     * Getter TargetEndpoint
     *
     * @return TargetEndpoint
     */
    public Endpoint getTargetEndpoint() {
	return targetEndpoint;
    }

    /**
     * Sets the metric to the metricExpression and source using the MetricParser
     *
     * @param metricExpression
     *            to be written to metric
     */
    public void setMetricExpression(String metricExpression) {
	this.metricExpression = metricExpression;
	if (metric != null) {
	    double param1 = 2.0d;
	    double param2 = 2.0d;
	    if (metric.param1 != null) {
		param1 = metric.param1;
	    }
	    if (metric.param2 != null) {
		param2 = metric.param2;
	    }
	    metric = MetricParser.parse(metricExpression,
		    getSourceInfo().getVar().replaceAll("\\?", ""));
	    if (param1 <= 1) {
		metric.param1 = param1;
	    }
	    if (param2 <= 1) {
		metric.param2 = param2;
	    }
	} else {
	    metric = MetricParser.parse(metricExpression,
		    getSourceInfo().getVar().replaceAll("\\?", ""));
	}
    }

    /**
     * Returns the property Label
     *
     * @param propString
     *            name of property
     * @param sourceOrTarget
     *            is Source or Target
     * @return Property String
     */
    public String getPropertyString(String propString, SourceOrTarget sourceOrTarget) {
	if (sourceOrTarget == SOURCE) {
	    return getSourceInfo().getVar().substring(1) + "." + propString;

	} else {
	    return getTargetInfo().getVar().substring(1) + "." + propString;
	}
    }

    /**
     * Getter Metric as Output
     *
     * @return Metric
     */
    public Output getMetric() {
	return this.metric;
    }

    public HashMap<String, String> getPrefixes() {
	return (HashMap<String, String>) this.prefixes;
    }

    /**
     * sets property matching
     * 
     * @param sourcePropertiesToAdd
     *            source properties
     * @param targetPropertiesToAdd
     *            target properties
     */
    public void setPropertiesMatching(List<String> sourcePropertiesToAdd,
	    List<String> targetPropertiesToAdd) {
	List<String> sourceProperties = sourceEndpoint.getInfo().getProperties();
	List<String> targetProperties = targetEndpoint.getInfo().getProperties();
	sourceProperties.clear();
	targetProperties.clear();
	for (String sourceProp : sourcePropertiesToAdd) {
	    sourceProperties.add(sourceProp);
	    addFunction(sourceEndpoint, sourceProp);
	}
	for (String targetProp : targetPropertiesToAdd) {
	    targetProperties.add(targetProp);
	    addFunction(targetEndpoint, targetProp);
	}
    }

    /**
     * adds a function to endpoint and adds appropriate prefix
     * 
     * @param endpoint
     * @param property
     */
    private void addFunction(Endpoint endpoint, String property) {
	KBInfo info = endpoint.getInfo();
	String abbr = PrefixHelper.abbreviate(property);
	HashMap<String, String> map = new HashMap<String, String>();
	map.put(abbr, "nolang->lowercase");
	info.getFunctions().put(abbr, map);

	String[] parts = property.split(":");
	String prefixToAdd = parts[0];
	info.getPrefixes().put(prefixToAdd, PrefixHelper.getURI(prefixToAdd));
	prefixes.put(prefixToAdd, PrefixHelper.getURI(prefixToAdd));
    }

    /**
     * returns mapping
     * 
     * @return mapping
     */
    public AMapping getMapping() {
	return mapping;
    }

    /**
     * sets mapping
     * 
     * @param mapping
     *            mapping
     */
    public void setMapping(AMapping mapping) {
	this.mapping = mapping;
    }

}
