package org.aksw.limes.core.evaluation.evaluationDataLoader;

import java.io.File;
import java.util.Map;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.MapKey;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.util.Map;

//import de.uni_leipzig.simba.io.ConfigReader;

/**
 * Class to specify evaluation parameters. Hold all neeeded data: caches, ConfigReader, and additional folder settings.
 * <p>
 * To support the older HashMap setting,
 * it provides a static constructor-like method  {@code buildFromHashMap(Map<MapKey, Object> map)}.
 *
 * @author Klaus Lyko
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class EvaluationData {
    static Logger logger = LoggerFactory.getLogger(EvaluationData.class);

    private String name;
    private String baseFolder = "reources/";
    private String datasetFolder;
    private String configFileName;
    private String sourceFileName;
    private String targetFileName;
    private String goldStandardFile;
    private String evauationResultFolder = "resources/results/";
    private String evaluationResultFileName;

    private AConfigurationReader configReader;
    private PropertyMapping propertyMapping;
    private ACache sourceCache;
    private ACache targetCache;
    private String sourceClass;
    private String targetClass;
    private AMapping referenceMapping;
    private int maxRuns = 5;

    /**
     * For the time beeing and convenience a method to construct EvaluationData using
     * the outdated HashMap-based approach.
     *
     * @param map
     *         HashMap meeting the standards of the {@link DataSetChooser}.
     * @return EvaluationData-Contains all information regarding the evaluation such as dataset folder, dataset files and property mapping
     */
    public static EvaluationData buildFromHashMap(Map<MapKey, Object> map) {
        EvaluationData data = new EvaluationData();
        data.baseFolder = (String) map.get(MapKey.BASE_FOLDER);
        data.configFileName = (String) map.get(MapKey.CONFIG_FILE);
        data.configReader = (AConfigurationReader) map.get(MapKey.CONFIG_READER);
        data.datasetFolder = (String) map.get(MapKey.DATASET_FOLDER);
        data.evaluationResultFileName = (String) map.get(MapKey.EVALUATION_FILENAME);
        data.evauationResultFolder = (String) map.get(MapKey.EVALUATION_RESULTS_FOLDER);
        data.goldStandardFile = (String) map.get(MapKey.REFERENCE_FILE);
        data.name = (String) map.get(MapKey.NAME);
        data.propertyMapping = (PropertyMapping) map.get(MapKey.PROPERTY_MAPPING);
        data.referenceMapping = (AMapping) map.get(MapKey.REFERENCE_MAPPING);
        data.sourceCache = (ACache) map.get(MapKey.SOURCE_CACHE);
        data.sourceFileName = (String) map.get(MapKey.SOURCE_FILE);
        data.targetCache = (ACache) map.get(MapKey.TARGET_CACHE);
        data.targetFileName = (String) map.get(MapKey.TARGET_FILE);
        data.sourceClass = (String) map.get(MapKey.SOURCE_CLASS);
        data.targetClass = (String) map.get(MapKey.TARGET_CLASS);
        return data;
    }

    public File getConfigFile() {
        return new File(baseFolder + configFileName);
    }

    public File getSourceFile() {
        return new File(datasetFolder + sourceFileName);
    }

    public File getTargetFile() {
        return new File(datasetFolder + targetFileName);
    }

    public File getReferenceDataFile() {
        return new File(datasetFolder + goldStandardFile);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *         the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the baseFolder
     */
    public String getBaseFolder() {
        return baseFolder;
    }

    /**
     * @param baseFolder
     *         the baseFolder to set
     */
    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
    }

    /**
     * @return the datasetFolder
     */
    public String getDatasetFolder() {
        return datasetFolder;
    }

    /**
     * @param datasetFolder
     *         the datasetFolder to set
     */
    public void setDatasetFolder(String datasetFolder) {
        this.datasetFolder = datasetFolder;
    }

    /**
     * @return the configFileName
     */
    public String getConfigFileName() {
        return configFileName;
    }

    /**
     * @param configFileName
     *         the configFileName to set
     */
    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    /**
     * @return the sourceFileName
     */
    public String getSourceFileName() {
        return sourceFileName;
    }

    /**
     * @param sourceFileName
     *         the sourceFileName to set
     */
    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    /**
     * @return the targetFileName
     */
    public String getTargetFileName() {
        return targetFileName;
    }

    /**
     * @param targetFileName
     *         the targetFileName to set
     */
    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    /**
     * @return the goldStandardFile
     */
    public String getGoldStandardFile() {
        return goldStandardFile;
    }

    /**
     * @param goldStandardFile
     *         the goldStandardFile to set
     */
    public void setGoldStandardFile(String goldStandardFile) {
        this.goldStandardFile = goldStandardFile;
    }

    /**
     * @return the evauationResultFolder
     */
    public String getEvauationResultFolder() {
        return evauationResultFolder;
    }

    /**
     * @param evauationResultFolder
     *         the evauationResultFolder to set
     */
    public void setEvauationResultFolder(String evauationResultFolder) {
        this.evauationResultFolder = evauationResultFolder;
    }

    /**
     * @return the evaluationResultFileName
     */
    public String getEvaluationResultFileName() {
        return evaluationResultFileName;
    }

    /**
     * @param evaluationResultFileName
     *         the evaluationResultFileName to set
     */
    public void setEvaluationResultFileName(String evaluationResultFileName) {
        this.evaluationResultFileName = evaluationResultFileName;
    }

    /**
     * @return the configReader
     */
    public AConfigurationReader getConfigReader() {
        return configReader;
    }

    /**
     * @param configReader
     *         the configReader to set
     */
    public void setConfigReader(AConfigurationReader configReader) {
        this.configReader = configReader;
    }

    /**
     * @return the propertyMapping
     */
    public PropertyMapping getPropertyMapping() {
        return propertyMapping;
    }

    /**
     * @param propertyMapping
     *         the propertyMapping to set
     */
    public void setPropertyMapping(PropertyMapping propertyMapping) {
        this.propertyMapping = propertyMapping;
    }

    /**
     * @return the targetCache
     */
    public ACache getTargetCache() {
        return targetCache;
    }

    /**
     * @param targetCache
     *         the targetCache to set
     */
    public void setTargetCache(ACache targetCache) {
        this.targetCache = targetCache;
    }

    /**
     * @return the sourceCache
     */
    public ACache getSourceCache() {
        return sourceCache;
    }

    /**
     * @param sourceCache
     *         the sourceCache to set
     */
    public void setSourceCache(ACache sourceCache) {
        this.sourceCache = sourceCache;
    }

    /**
     * @return the referenceMapping
     */
    public AMapping getReferenceMapping() {
        return referenceMapping;
    }

    /**
     * @param referenceMapping
     *         the referenceMapping to set
     */
    public void setReferenceMapping(AMapping referenceMapping) {
        this.referenceMapping = referenceMapping;
    }

    /**
     * Getter for using the deprecated MapKeys.
     *
     * @param key the key for the required information required concerning teh dataset
     * @return The corresponding information to the given key such as the base folder
     */
    public Object getValue(MapKey key) {
        switch (key) {
            case BASE_FOLDER:
                return getBaseFolder();
            case CONFIG_FILE:
                return getConfigFileName();
            case CONFIG_READER:
                return getConfigReader();
            case DATASET_FOLDER:
                return getDatasetFolder();
            case EVALUATION_FILENAME:
                return getEvaluationResultFileName();
            case EVALUATION_RESULTS_FOLDER:
                return getEvauationResultFolder();
            case MAX_RUNS:
                return getMaxRuns();
            case NAME:
                return getName();
            case PROPERTY_MAPPING:
                return getPropertyMapping();
            case REFERENCE_FILE:
                return getGoldStandardFile();
            case REFERENCE_MAPPING:
                return getReferenceMapping();
            case SOURCE_CACHE:
                return getSourceCache();
            case SOURCE_FILE:
                return getSourceFileName();
            case TARGET_CACHE:
                return getTargetCache();
            case TARGET_FILE:
                return getTargetFileName();
            default:
                break;
        }
        return null;
    }

    /**
     * @return the maxRuns
     */
    public int getMaxRuns() {
        return maxRuns;
    }

    /**
     * @param maxRuns
     *         the maxRuns to set
     */
    public void setMaxRuns(int maxRuns) {
        this.maxRuns = maxRuns;
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }
}
