package org.aksw.limes.core.evaluation.evaluationDataLoader;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

/**
 * Interface class for DataSetIO, contains the abstract methods that BaseDataSet can implement.
 *
 * @author Cedric Richter
 */
public interface IDataSetIO {

    /**
     * @returns Properties from fixed file directory and filename
     */
    public PropertyMapping loadProperties(String baseFolder, String configFile);

    /**
     * @returns loads the source cache
     */
    public ACache loadSourceCache(Configuration cfg, String path, String type);

    /**
     * @returns loads the target cache
     */
    public ACache loadTargetCache(Configuration cfg, String path, String type);

    /**
     * @returns loads the mapping of the caches
     */
    public AMapping loadMapping(Configuration cfg, String path);
}
