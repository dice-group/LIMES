package org.aksw.limes.core.ml.algorithm.dragon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.evaluation.evaluationDataLoader.PropMapper;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.FitnessFunctionDTL;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.GiniIndex;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.GlobalFMeasure;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.ErrorEstimatePruning;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.GlobalFMeasurePruning;
import org.aksw.limes.core.ml.algorithm.dragon.Pruning.PruningFunctionDTL;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.apache.log4j.Logger;

import weka.classifiers.trees.J48;

/**
 * This class uses decision trees and an active learning approach to learn link
 * specifications
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class Dragon extends ACoreMLAlgorithm {

    private static Logger logger = Logger.getLogger(Dragon.class);

    private MLResults mlresult;
    private Configuration configuration;

    // Parameters
    public static final String PARAMETER_PRUNING_CONFIDENCE = "confidence threshold for pruning";
    public static final String PARAMETER_PROPERTY_MAPPING = "property mapping";
    public static final String PARAMETER_MAPPING = "initial mapping as training data";
    public static final String PARAMETER_MAX_LINK_SPEC_HEIGHT = "maximum height of the link specification";
    public static final String PARAMETER_MIN_PROPERTY_COVERAGE = "minimum property coverage";
    public static final String PARAMETER_PROPERTY_LEARNING_RATE = "property learning rate";
    public static final String PARAMETER_FITNESS_FUNCTION = "fitness function";
    public static final String PARAMETER_PRUNING_FUNCTION = "pruning function";
    public static final String PARAMETER_ATOMIC_MEASURES = "atomic measures";

    // Strings for different function options
    public static final String FITNESS_NAME_GINI_INDEX = "GiniIndex";
    public static final String FITNESS_NAME_GLOBAL_FMEASURE = "GlobalFMeasure";
    public static final String PRUNING_NAME_ERROR_ESTIMATE_PRUNING = "ErrorEstimate";
    public static final String PRUNING_NAME_GLOBAL_FMEASURE = "GlobalFmeasure";

    // Default parameters
    private static final double pruningConfidence = 0.25;
    private static final int maxLinkSpecHeight = 3;
    private static final double minPropertyCoverage = 0.6;
    private static final double propertyLearningRate = 0.95;
    private static final FitnessFunctionDTL fitnessFunction = new GiniIndex();
    private static final PruningFunctionDTL pruningFunction = new ErrorEstimatePruning();
    private AMapping initialMapping = MappingFactory.createDefaultMapping();
    private AMapping prediction;
    public DecisionTree root;

    private ACache testSourceCache = new MemoryCache();
    private ACache testTargetCache = new MemoryCache();
    private boolean isActive = false;

    // TODO check whats wrong with exactmatch and levenshtein
    // public static final String[] stringMeasures = { "cosine",
    // "exactmatch",
    // "jaccard", "jaro",
    // "levenshtein",
    // "qgrams", "trigrams" };
    // public static final String[] dateMeasures = { "datesim", "daysim",
    // "yearsim" };
    // public static final String[] pointsetMeasures = { "symmetrichausdorff",
    // "frechet", "hausdorff", "geolink",
    // "geomean", "geolink", "surjection", "fairsurjection" };

    // public static final String[] defaultMeasures = { "jaccard", "trigrams",
    // "cosine", "qgrams" };
    // public static final String[] numberMeasures = {};

    public static Set<String> defaultMeasures = new HashSet<String>();

    public static final double threshold = 0.01;

    /**
     * Dragon constructor.
     */
    public Dragon() {
        super();
        setDefaultParameters();
    }

    /**
     * Constructor uses superconstructor, initializes TreeParser object and sets
     * configuration
     * 
     * @param c
     */
    public Dragon(Configuration c) {
        super();
        setDefaultParameters();
        this.configuration = c;
    }

    /**
     * Helper class for easier handling of links or link candidates
     * 
     * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
     *         studserv.uni-leipzig.de{@literal >}
     *
     */
    public class SourceTargetValue {
        String sourceUri;
        String targetUri;
        double value;
        Double compoundMeasureValue = Double.MAX_VALUE;

        public SourceTargetValue(String s, String t, double v) {
            sourceUri = s;
            targetUri = t;
            value = v;
        }

        public SourceTargetValue(String s, String t, double v, Double cmv) {
            sourceUri = s;
            targetUri = t;
            value = v;
            compoundMeasureValue = cmv;
        }

        @Override
        public String toString() {
            return sourceUri + " -> " + targetUri + " : " + value + "     | compound measure value: "
                    + compoundMeasureValue;
        }
    }

    @Override
    public String getName() {
        return "Dragon";
    }

    /**
     * generates an initial training set and calls
     * {@link #activeLearn(AMapping)}
     */
    @Override
    protected MLResults activeLearn() throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException("Not implemented yet!");
    }

    public void getAtomicMeasures() {
        Set<String> atomicMeasures = new HashSet<String>();

        String measuresAsString = getParameter(PARAMETER_ATOMIC_MEASURES).toString().replace("[", "").replace("]", "");
        for (String m : measuresAsString.split(",")) {
            atomicMeasures.add(m.trim());
        }
        defaultMeasures = atomicMeasures;
    }

    /**
     * Creates a training set out of the oracleMapping and uses {@link J48} to
     * build a decision tree The decision tree gets parsed to a
     * {@link LinkSpecification} by {@link TreeParser}
     * 
     * @param oracleMapping
     * @return res wrapper containing learned link specification
     */
    @Override
    protected MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException("Not implemented yet!");
    }

    @Override
    public AMapping predict(ACache source, ACache target, MLResults mlModel) {
        LinkSpecification ls = mlModel.getLinkSpecification();
        Rewriter rw = RewriterFactory.getDefaultRewriter();
        ls = rw.rewrite(ls);
        DynamicPlanner dp = new DynamicPlanner(source, target);
        SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target,
                this.configuration.getSourceInfo().getVar(), this.configuration.getTargetInfo().getVar());
        this.prediction = ee.execute(ls, dp);
        return this.prediction;
    }

    @Override
    public void init(List<LearningParameter> lp, ACache sourceCache, ACache targetCache) {
        super.init(lp, sourceCache, targetCache);
        this.prediction = null;
        if (lp == null) {
            setDefaultParameters();
        } else {
            instantiateFunctionParameters();
            getAtomicMeasures();
        }
    }

    private void instantiateFunctionParameters() {
        if (getParameter(PARAMETER_FITNESS_FUNCTION) instanceof String) {
            if (((String) getParameter(PARAMETER_FITNESS_FUNCTION)).toLowerCase()
                    .equals(FITNESS_NAME_GINI_INDEX.toLowerCase())) {
                setParameter(PARAMETER_FITNESS_FUNCTION, new GiniIndex());
            } else if (((String) getParameter(PARAMETER_FITNESS_FUNCTION)).toLowerCase()
                    .equals(FITNESS_NAME_GLOBAL_FMEASURE.toLowerCase())) {
                setParameter(PARAMETER_FITNESS_FUNCTION, new GlobalFMeasure());
            }
        }
        if (getParameter(PARAMETER_PRUNING_FUNCTION) instanceof String) {
            if (((String) getParameter(PARAMETER_PRUNING_FUNCTION)).toLowerCase()
                    .equals(PRUNING_NAME_GLOBAL_FMEASURE.toLowerCase())) {
                setParameter(PARAMETER_PRUNING_FUNCTION, new GlobalFMeasurePruning());
            } else if (((String) getParameter(PARAMETER_PRUNING_FUNCTION)).toLowerCase()
                    .equals(PRUNING_NAME_ERROR_ESTIMATE_PRUNING.toLowerCase())) {
                setParameter(PARAMETER_PRUNING_FUNCTION, new ErrorEstimatePruning());
            }
        }
    }

    @Override
    public void setDefaultParameters() {
        learningParameters = new ArrayList<>();
        learningParameters.add(new LearningParameter(PARAMETER_PRUNING_CONFIDENCE, pruningConfidence, Double.class, 0d,
                1d, 0.01d, PARAMETER_PRUNING_CONFIDENCE));
        learningParameters.add(new LearningParameter(PARAMETER_PROPERTY_MAPPING, new PropertyMapping(),
                PropertyMapping.class, Double.NaN, Double.NaN, Double.NaN, PARAMETER_PROPERTY_MAPPING));
        learningParameters.add(new LearningParameter(PARAMETER_MAPPING, initialMapping, AMapping.class, Double.NaN,
                Double.NaN, Double.NaN, PARAMETER_MAPPING));
        learningParameters.add(new LearningParameter(PARAMETER_MAX_LINK_SPEC_HEIGHT, maxLinkSpecHeight, Integer.class,
                1, 100000, 1, PARAMETER_MAX_LINK_SPEC_HEIGHT));
        learningParameters.add(new LearningParameter(PARAMETER_MIN_PROPERTY_COVERAGE, minPropertyCoverage, Double.class,
                0d, 1d, 0.01d, PARAMETER_MIN_PROPERTY_COVERAGE));
        learningParameters.add(new LearningParameter(PARAMETER_PROPERTY_LEARNING_RATE, propertyLearningRate,
                Double.class, 0d, 1d, 0.01d, PARAMETER_PROPERTY_LEARNING_RATE));
        learningParameters.add(new LearningParameter(PARAMETER_FITNESS_FUNCTION, fitnessFunction,
                FitnessFunctionDTL.class, new String[] { FITNESS_NAME_GINI_INDEX, FITNESS_NAME_GLOBAL_FMEASURE },
                PARAMETER_FITNESS_FUNCTION));
        learningParameters
                .add(new LearningParameter(PARAMETER_PRUNING_FUNCTION, pruningFunction, PruningFunctionDTL.class,
                        new String[] { PRUNING_NAME_ERROR_ESTIMATE_PRUNING, PRUNING_NAME_GLOBAL_FMEASURE },
                        PARAMETER_FITNESS_FUNCTION));

        Set<String> measures = new HashSet<>(Arrays.asList("jaccard", "trigrams", "cosine", "qgrams"));
        learningParameters.add(new LearningParameter(Dragon.PARAMETER_ATOMIC_MEASURES, measures, MeasureType.class, 0,
                0, 0, Dragon.PARAMETER_ATOMIC_MEASURES));
    }

    @Override
    protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException("Not implemented yet!");
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
        return mlType == MLImplementationType.SUPERVISED_BATCH;
    }

    private PropertyMapping getPropertyMappingFromParameter() {
        PropertyMapping propertyMapping = null;
        if (getParameter(PARAMETER_PROPERTY_MAPPING) instanceof PropertyMapping) {
            propertyMapping = (PropertyMapping) getParameter(PARAMETER_PROPERTY_MAPPING);
        } else {
            propertyMapping = PropMapper
                    .getPropertyMappingFromFile(getParameter(PARAMETER_PROPERTY_MAPPING).toString());
            setParameter(PARAMETER_PROPERTY_MAPPING, propertyMapping);
        }
        return propertyMapping;
    }

    @Override
    protected MLResults learn(AMapping trainingData) throws UnsupportedMLImplementationException {
        if (isActive) {
            root = new DecisionTree(this, testSourceCache, testTargetCache, null,
                    Double.parseDouble(getParameter(PARAMETER_MIN_PROPERTY_COVERAGE).toString()),
                    Double.parseDouble(getParameter(PARAMETER_PROPERTY_LEARNING_RATE).toString()),
                    Double.parseDouble(getParameter(PARAMETER_PRUNING_CONFIDENCE).toString()), trainingData,
                    getPropertyMappingFromParameter());
        } else {
            root = new DecisionTree(this, sourceCache, targetCache, null,
                    (double) getParameter(PARAMETER_MIN_PROPERTY_COVERAGE),
                    (double) getParameter(PARAMETER_PROPERTY_LEARNING_RATE),
                    (double) getParameter(PARAMETER_PRUNING_CONFIDENCE), trainingData,
                    getPropertyMappingFromParameter());
        }
        DecisionTree.fitnessFunction = (FitnessFunctionDTL) getParameter(PARAMETER_FITNESS_FUNCTION);
        DecisionTree.fitnessFunction.setPropertyMapping((PropertyMapping) getParameter(PARAMETER_PROPERTY_MAPPING));
        DecisionTree.pruningFunction = (PruningFunctionDTL) getParameter(PARAMETER_PRUNING_FUNCTION);
        DecisionTree.fitnessFunction.setDt(root);
        DecisionTree.maxDepth = Integer.parseInt(getParameter(PARAMETER_MAX_LINK_SPEC_HEIGHT).toString());
        root.buildTree(DecisionTree.maxDepth);
        logger.info("FULL:\n" + root.toString());

        root.prune();
        logger.info("PRUNED:\n" + root.toString());

        LinkSpecification ls = root.getTotalLS();
        HashMap<String, Object> details = new HashMap<>();
        details.put("tree", root);
        MLResults res = new MLResults(ls, null, -1.0, details);
        return res;
    }

    public ACache getSourceCache() {
        return this.sourceCache;
    }

    public ACache getTargetCache() {
        return this.targetCache;
    }

    public void setSourceCache(ACache sourceCache) {
        this.sourceCache = sourceCache;
    }

    public void setTargetCache(ACache targetCache) {
        this.targetCache = targetCache;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public MLResults getMlresult() {
        return mlresult;
    }

    public void setInitialMapping(AMapping initialMapping) {
        this.initialMapping = initialMapping;
    }

    public ACache getTestSourceCache() {
        return testSourceCache;
    }

    public ACache getTestTargetCache() {
        return testTargetCache;
    }

    @Override
    protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException("Not implemented yet!");
    }

}