package org.aksw.limes.core.evaluation.evaluationDataLoader;

import org.aksw.limes.core.evaluation.evaluationDataLoader.datasets.*;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to Load, Choose and Perform evaluation on different Datasets. Also grants central access to evaluation datasets.
 * Using this class you can perform a variety of functions. Like choose which data set to evaluate,
 * registerDataSet,  mapDataSet, get data for evaluation, initializeMapping and fixReferenceMapping
 *
 *
 * @author Cedric Richter
 *
 */


public class DataSetChooser2 {

    static Logger logger = LoggerFactory.getLogger(DataSetChooser2.class);

    public static String getEvalFolder() {
        return "results/";
    }

    /**
     * Initializes dataSet based on choice
     * @return the choosen dataSet
     */
    private static DataSetChooser2 initDefault(){
        DataSetChooser2 chooser = new DataSetChooser2();

        chooser.registerDataSet(new PersonNewDataSet());
        chooser.registerDataSet(new Person1DataSet());
        chooser.registerDataSet(new Person2DataSet());
        chooser.registerDataSet(new RestaurantsDataSet());
        chooser.registerDataSet(new RestaurantsFixedDataSet());
        chooser.registerDataSet(new AbtBuyDataSet());
        chooser.registerDataSet(new DBLPACMDataSet());
        chooser.registerDataSet(new AmazonGoogleDataset());
        chooser.registerDataSet(new DBPLinkMDBDataset());
        chooser.registerDataSet(new DrugsDataset());
        chooser.registerDataSet(new CitiesDataSet());

        return chooser;
    }

    private static DataSetChooser2 globalInstance;


    public static DataSetChooser2 instance(){
        if(globalInstance == null){
            globalInstance = initDefault();
        }
        return globalInstance;
    }

    /**
     * Creates a new HashMap, registersDataSet
     * @return dataSetMap
     */
    private Map<String, IDataSet> dataSetMap = new HashMap<>();

    public DataSetChooser2(){}

    public boolean registerDataSet(IDataSet set){
        String key = calcKey(set.getName());
        if(!dataSetMap.containsKey(key)){
            dataSetMap.put(key, set);
            return true;
        }
        return false;
    }
    /**
     * @return data from evaluation build from DataSetMap
     */
    public EvaluationData getData(String name){
        String key = calcKey(name);
        Map<DataSetChooser.MapKey, Object> param = new HashMap<>();

        if(dataSetMap.containsKey(key)){
            IDataSet dataSet = dataSetMap.get(key);
            initMapping(param, dataSet);
        }

        EvaluationData data = EvaluationData.buildFromHashMap(param);
        return data;
    }

    private String calcKey(String s){
        return s.replace("-", "").toLowerCase();
    }

    /**
     * Initalizes Mapping on choosen dataBase, takes all the necessary info like dataSetName, basefolder, datasetFolder,
     * configFile, referenceFile, source & target file, evaluationFolder & evaluationFile
     * Resolves path issues and loads everything in the MapKey.
     */
    private void initMapping(Map<DataSetChooser.MapKey, Object> param, IDataSet dataSet){
        param.put(DataSetChooser.MapKey.BASE_FOLDER, dataSet.getBaseFolder());
        param.put(DataSetChooser.MapKey.DATASET_FOLDER, dataSet.getDataSetFolder());
        param.put(DataSetChooser.MapKey.CONFIG_FILE, dataSet.getConfigFile());
        param.put(DataSetChooser.MapKey.REFERENCE_FILE, dataSet.getReferenceFile());
        param.put(DataSetChooser.MapKey.SOURCE_FILE, dataSet.getSourceFile());
        param.put(DataSetChooser.MapKey.TARGET_FILE, dataSet.getTargetFile());
        param.put(DataSetChooser.MapKey.EVALUATION_RESULTS_FOLDER, getEvalFolder());
        param.put(DataSetChooser.MapKey.EVALUATION_FILENAME, dataSet.getEvaluationFilename());
        param.put(DataSetChooser.MapKey.NAME, dataSet.getName());

        String crPath = PathResolver.resolvePath((String) param.get(DataSetChooser.MapKey.BASE_FOLDER) + param.get(DataSetChooser.MapKey.CONFIG_FILE));

        AConfigurationReader cR = new XMLConfigurationReader(crPath);
        cR.read();
        param.put(DataSetChooser.MapKey.CONFIG_READER, cR);

        Configuration cfg = cR.getConfiguration();

        PathResolver.resolvePath(cfg.getSourceInfo());
        PathResolver.resolvePath(cfg.getTargetInfo());

        IDataSetIO io = new PathResolvingIO(dataSet.getIO());

        param.put(DataSetChooser.MapKey.PROPERTY_MAPPING, io.loadProperties((String) param.get(DataSetChooser.MapKey.BASE_FOLDER),
                (String) param.get(DataSetChooser.MapKey.CONFIG_FILE)));
        param.put(DataSetChooser.MapKey.SOURCE_CACHE, io.loadSourceCache(cfg,(String) param.get(DataSetChooser.MapKey.DATASET_FOLDER) + param.get(DataSetChooser.MapKey.SOURCE_FILE), dataSet.getOAEIType()));
        param.put(DataSetChooser.MapKey.TARGET_CACHE, io.loadTargetCache(cfg,(String) param.get(DataSetChooser.MapKey.DATASET_FOLDER) + param.get(DataSetChooser.MapKey.TARGET_FILE), dataSet.getOAEIType()));
        param.put(DataSetChooser.MapKey.REFERENCE_MAPPING,
                  io.loadMapping(cfg, (String) param.get(DataSetChooser.MapKey.DATASET_FOLDER) + (String) param.get(DataSetChooser.MapKey.REFERENCE_FILE)));

        param.put(DataSetChooser.MapKey.SOURCE_CLASS, dataSet.getSourceClass());
        param.put(DataSetChooser.MapKey.TARGET_CLASS, dataSet.getTargetClass());

        param.put(DataSetChooser.MapKey.MAX_RUNS, 5);


    }

    /**
     * Reference Mapping is fixed in this function
     */
    public static AMapping fixReferenceMap(AMapping original, ACache sC, ACache tC) {
        int count = 0;
        AMapping fixed = MappingFactory.createMapping(MappingFactory.MappingType.MEMORY_MAPPING);
        for (String sk : original.getMap().keySet()) {
            if (sC.containsUri(sk)) {
                for (String tk : original.getMap().get(sk).keySet()) {
                    if (tC.containsUri(tk)) {
                        fixed.add(sk, tk, original.getConfidence(sk, tk));
                    } else {
                        count++;
                    }
                }
            } else {
                count += original.getMap().get(sk).size();
            }
        }
        logger.info("Removed " + count + " mappings as the instances are not found in the Caches");
        return fixed;
    }

}
