package org.aksw.limes.core.io.config.reader.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public class RDFConfigurationReader extends AConfigurationReader {
    private static final Logger logger = LoggerFactory.getLogger(RDFConfigurationReader.class);

    private Model configModel = ModelFactory.createDefaultModel();
    private Resource specsSubject;

    /**
     * @param fileNameOrUri
     *            file name or URI to be read
     */
    public RDFConfigurationReader(String fileNameOrUri) {
        super(fileNameOrUri);
    }

    /**
     * read RDF model from file/URL
     *
     * @param fileNameOrUri
     *            file name or URI to be read
     * @return Model that contains the data in the fileNameOrUri
     */
    public static Model readModel(String fileNameOrUri) {
        long startTime = System.currentTimeMillis();
        Model model = ModelFactory.createDefaultModel();

        try (InputStream in = FileManager.get().open(fileNameOrUri)) {
            if (fileNameOrUri.contains(".ttl") || fileNameOrUri.contains(".n3")) {
                logger.info("Opening Turtle file");
                model.read(in, null, "TTL");
            } else if (fileNameOrUri.contains(".rdf")) {
                logger.info("Opening RDFXML file");
                model.read(in, null);
            } else if (fileNameOrUri.contains(".nt")) {
                logger.info("Opening N-Triples file");
                model.read(in, null, "N-TRIPLE");
            } else {
                logger.info("Content negotiation to get RDFXML from " + fileNameOrUri);
                model.read(fileNameOrUri);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Loading " + fileNameOrUri + " is done in " + (System.currentTimeMillis() - startTime) + "ms.");
        return model;
    }

    /**
     * @return A filled Configuration object from the inputFile
     */
    @Override
    public Configuration read() {
        return read(readModel(fileNameOrUri));
    }

    /**
     * @param configurationModel
     *            A model filled with the configurations
     * @return true if the configurationModel contains all mandatory properties
     */
    public Configuration read(Model configurationModel) {
        configModel = configurationModel;
        StmtIterator stats = configModel.listStatements(null, RDF.type, LIMES.LimesSpecs);
        if (stats.hasNext()) {
            specsSubject = stats.next().getSubject();
        } else {
            logger.error("Missing " + LIMES.LimesSpecs + ", Exit with error.");
            throw new RuntimeException();
        }
        configuration = new Configuration();
        configuration.setSourceInfo(new KBInfo());
        configuration.setTargetInfo(new KBInfo());
        configuration.setPrefixes((HashMap<String, String>) configModel.getNsPrefixMap());

        // 1. 2. Source & Target information
        readKBDescription((Resource) getObject(specsSubject, LIMES.hasSource, true));
        readKBDescription((Resource) getObject(specsSubject, LIMES.hasTarget, true));

        // 3. METRIC
        if (configModel.contains(specsSubject, LIMES.hasMetric, (RDFNode) null)) {
            Resource metric = (Resource) getObject(specsSubject, LIMES.hasMetric, true);
            configuration.setMetricExpression(getObject(metric, LIMES.expression, true).toString());
        } else

        // 4. ML Algorithm
        if (configModel.contains(specsSubject, LIMES.hasMLAlgorithm, (RDFNode) null)) {
            Resource mlAlgorithmUri = getObject(specsSubject, LIMES.hasMLAlgorithm, true).asResource();
            readMLAlgorithmConfigurations(mlAlgorithmUri);
        } else {
            logger.error("Neither Metric nor ML Algorithm provided, exit with error ");
            throw new RuntimeException();
        }

        // 5. ACCEPTANCE file and conditions
        Resource acceptance = (Resource) getObject(specsSubject, LIMES.hasAcceptance, true);
        configuration.setAcceptanceThreshold(parseDouble(getObject(acceptance, LIMES.threshold, true).toString()));
        configuration.setAcceptanceFile(getObject(acceptance, LIMES.file, true).toString());
        configuration.setAcceptanceRelation(getObject(acceptance, LIMES.relation, true).toString());

        // 6. VERIFICATION file and conditions
        Resource review = (Resource) getObject(specsSubject, LIMES.hasReview, true);
        configuration.setVerificationThreshold(parseDouble(getObject(review, LIMES.threshold, true).toString()));
        configuration.setVerificationFile(getObject(review, LIMES.file, true).toString());
        configuration.setVerificationRelation(getObject(review, LIMES.relation, true).toString());

        // 7. EXECUTION
        RDFNode exeParamsSubject = getObject(specsSubject, LIMES.hasExecutionParameters, false);
        readExecutionParameters(exeParamsSubject);

        // 8. TILING if necessary
        RDFNode g = getObject(specsSubject, LIMES.granularity, false);
        if (g != null) {
            configuration.setGranularity(Integer.parseInt(g.toString()));
        }

        // 9. OUTPUT format
        RDFNode output = getObject(specsSubject, LIMES.outputFormat, false);
        if (output != null) {
            configuration.setOutputFormat(output.toString());
        }

        return configuration;
    }

    protected void readExecutionParameters(RDFNode exeParamsSubject) {
        if (exeParamsSubject != null) {
            Resource exeParamResource = exeParamsSubject.asResource();
            RDFNode exePlanner = getObject(exeParamResource, LIMES.executionPlanner, false);
            if (exePlanner != null) {
                configuration.setExecutionPlanner(exePlanner.toString());
            } else {
                logger.info("Use default execution planner.");
            }
            RDFNode exeRewriter = getObject(exeParamResource, LIMES.executionRewriter, false);
            if (exeRewriter != null) {
                configuration.setExecutionRewriter(exeRewriter.toString());
            } else {
                logger.info("Use default execution rewriter.");
            }
            RDFNode exeEngine = getObject(exeParamResource, LIMES.executionEngine, false);
            if (exeEngine != null) {
                configuration.setExecutionEngine(exeEngine.toString());
            } else {
                logger.info("Use default execution engine.");
            }
            RDFNode maxOpt = getObject(exeParamResource, LIMES.optimizationTime, false);
            if (maxOpt != null) {
                if (Long.parseLong(maxOpt.toString()) < 0) {
                    logger.info("\nIgnore this message if you chose the default or simple execution engine:"
                            + "\nOptimization time cannot be negative. Your input value is " + maxOpt
                            + ".\nSetting it to the default value: 0ms." + "\n--End of message--");
                    configuration.setOptimizationTime(0l);
                } else
                    configuration.setOptimizationTime(Long.parseLong(maxOpt.toString()));
            } else {
                logger.info("\nIgnore this message if you chose the default or simple execution engine:"
                        + "Use default optimization time: 0ms." + "\n--End of message--");
                configuration.setOptimizationTime(0l);
            }
            RDFNode k = getObject(exeParamResource, LIMES.expectedSelectivity, false);
            if (k != null) {
                if (Double.parseDouble(k.toString()) < 0.0 || Double.parseDouble(k.toString()) > 1.0) {
                    logger.info("\nIgnore this message if you chose the default or simple execution engine:"
                            + "\nExpected selectivity must be between 0.0 and 1.0. Your input value is " + k
                            + ".\nSetting it to the default value: 1.0." + "\n--End of message--");
                    configuration.setExpectedSelectivity(1.0d);
                } else
                    configuration.setExpectedSelectivity(Double.parseDouble(k.toString()));
            } else {
                logger.info("\nIgnore this message if you chose the default or simple execution engine:"
                        + "Use default expected selectivity: 1.0" + "\n--End of message--");
                configuration.setExpectedSelectivity(1.0d);
            }

        } else {
            logger.info("Use default execution parameters.");
        }
    }

    private void readMLAlgorithmConfigurations(Resource mlAlgorithmUri) {
        // read MLAlgorithm type
        if (configModel.contains(mlAlgorithmUri, RDF.type, LIMES.SupervisedMLAlgorithm)) {
            configuration.setMlImplementationType(MLImplementationType.SUPERVISED_BATCH);

            // read training data
            RDFNode trainingDataFile = getObject(mlAlgorithmUri, LIMES.hasTrainingDataFile, true);
            configuration.setTrainingDataFile(trainingDataFile.toString());
        } else if (configModel.contains(mlAlgorithmUri, RDF.type, LIMES.UnsupervisedMLAlgorithm)) {
            configuration.setMlImplementationType(MLImplementationType.UNSUPERVISED);

            // read pseudo-F-Measure
            RDFNode pfm = getObject(mlAlgorithmUri, LIMES.pseudoFMeasure, false);
            if (pfm != null) {
                EvaluatorType evalType = EvaluatorType.valueOf(pfm.toString());
                if (evalType == null) {
                    logger.warn(pfm.toString()
                            + " is not valid pseudo-F-Measure, continue with the default pseudo-F-Measure.");
                } else {
                    configuration.setMlPseudoFMeasure(evalType);
                }
            }

        } else if (configModel.contains(mlAlgorithmUri, RDF.type, LIMES.ActiveMLAlgorithm)) {
            configuration.setMlImplementationType(MLImplementationType.SUPERVISED_ACTIVE);
        } else {
            logger.warn(mlAlgorithmUri + " missing ML type. use the default type: " + LIMES.UnsupervisedMLAlgorithm);
            configuration.setMlImplementationType(MLImplementationType.UNSUPERVISED);
        }

        // read MLAlgorithm name
        RDFNode mlAlgorithmName = getObject(mlAlgorithmUri, LIMES.mlAlgorithmName, true);
        configuration.setMlAlgorithmName(mlAlgorithmName.toString());

        // read MLAlgorithm parameters
        StmtIterator parametersItr = configModel.listStatements(null, RDF.type, LIMES.MLParameter);
        while (parametersItr.hasNext()) {
            Resource ParameterSubject = parametersItr.next().getSubject();
            RDFNode mlParameterName = getObject(ParameterSubject, LIMES.mlParameterName, false);
            RDFNode mlParametervalue = getObject(ParameterSubject, LIMES.mlParameterValue, false);
            configuration.addMlAlgorithmParameter(mlParameterName.toString(), mlParametervalue.toString());
        }
    }

    /**
     * Read either the source or target dataset description
     *
     * @param kb
     *            knowledge base resource URI
     */
    public void readKBDescription(Resource kb) {
        KBInfo kbinfo = null;

        // Type
        if (configModel.contains(kb, RDF.type, LIMES.SourceDataset)) {
            kbinfo = configuration.getSourceInfo();
        } else if (configModel.contains(kb, RDF.type, LIMES.TargetDataset)) {
            kbinfo = configuration.getTargetInfo();
        } else {
            logger.error("Dataset type missing, " + "either " + LIMES.SourceDataset + " or " + LIMES.TargetDataset
                    + " is required.");
            throw new RuntimeException();
        }
        // Label
        kbinfo.setId(getObject(kb, RDFS.label, true).toString());

        // End-point & graph
        kbinfo.setEndpoint(getObject(kb, LIMES.endPoint, true).toString());
        RDFNode graph = getObject(kb, LIMES.graph, false);
        if (graph != null) {
            kbinfo.setGraph(graph.toString());
        }

        // Restrictions
        Set<RDFNode> restriction = getObjects(kb, LIMES.restriction, false);
        if (restriction != null) {
            for (RDFNode r : restriction) {
                String restrictionStr = r.toString();
                if (restrictionStr.endsWith(".")) {
                    restrictionStr = restrictionStr.substring(0, restrictionStr.length() - 1);
                }
                kbinfo.addRestriction(restrictionStr);
            }
        }

        // properties
        for (RDFNode property : getObjects(kb, LIMES.property, true)) {
            XMLConfigurationReader.processProperty(kbinfo, property.toString());
        }

        // optional properties
        Set<RDFNode> optionalProperties = getObjects(kb, LIMES.optionalProperty, false);
        if (optionalProperties != null) {
            for (RDFNode optionalProperty : optionalProperties) {
                XMLConfigurationReader.processOptionalProperty(kbinfo, optionalProperty.toString());
            }
        }

        // complex functions
        for (RDFNode function : getObjects(kb, LIMES.function, false)) {
            XMLConfigurationReader.setComplexFunction(kbinfo, function.toString());
        }

        // Page size
        kbinfo.setPageSize(parseInt(getObject(kb, LIMES.pageSize, true).toString()));

        // KB variable
        kbinfo.setVar(getObject(kb, LIMES.variable, true).toString());

        // KB type
        RDFNode type = getObject(kb, LIMES.type, false);
        if (type != null) {
            kbinfo.setType(type.toString().toLowerCase());
        }

        // Prefixes
        kbinfo.setPrefixes(configuration.getPrefixes());
    }

    /**
     * @param s
     * @return
     * @author sherif
     */
    private int parseInt(String s) {
        if (s.contains("^")) {
            s = s.substring(0, s.indexOf("^"));
        }
        if (s.contains("@")) {
            s = s.substring(0, s.indexOf("@"));
        }
        return Integer.parseInt(s);
    }

    /**
     * @param s
     * @param p
     * @param isMandatory
     *            if set the program exit in case o not found, otherwise a null
     *            value returned
     * @return the object o of triple (s, p, o) if exists, null otherwise
     * @author sherif
     */
    private RDFNode getObject(Resource s, Property p, boolean isMandatory) {
        StmtIterator statements = configModel.listStatements(s, p, (RDFNode) null);
        if (statements.hasNext()) {
            return statements.next().getObject();
        } else {
            if (isMandatory) {
                logger.error("Missing mandatory property " + p + ", Exit with error.");
                throw new RuntimeException();
            }
        }
        return null;
    }

    /**
     * @param s
     * @param p
     * @param isMandatory
     *            if set the program exit in case o not found, otherwise a null
     *            value returned
     * @return Set of all objects o of triples (s, p, o) if exist, null
     *         otherwise
     * @author sherif
     */
    private Set<RDFNode> getObjects(Resource s, Property p, boolean isMandatory) {
        Set<RDFNode> result = new HashSet<>();
        StmtIterator statements = configModel.listStatements(s, p, (RDFNode) null);
        while (statements.hasNext()) {
            result.add(statements.next().getObject());
        }
        if (isMandatory && result.size() == 0) {
            logger.error("Missing mandatory property: " + p + ", Exit with error.");
            throw new RuntimeException();
        }
        return result;
    }

    /**
     * @param s
     * @return
     * @author sherif
     */
    private double parseDouble(String s) {
        if (s.contains("^")) {
            s = s.substring(0, s.indexOf("^"));
        }
        if (s.contains("@")) {
            s = s.substring(0, s.indexOf("@"));
        }
        return Double.parseDouble(s);
    }

}