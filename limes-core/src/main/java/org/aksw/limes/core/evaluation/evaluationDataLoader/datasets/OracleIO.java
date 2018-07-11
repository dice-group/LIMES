package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;
import org.aksw.limes.core.evaluation.evaluationDataLoader.PropMapper;
import org.aksw.limes.core.evaluation.oracle.OracleFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

/**
 * Class to Create OracleIO, which helps in selecting different DataSet for the evaluation.
 * Basically creates an oracle based on the input type (i.e., the type of file within which the
 * oracle data is contained) and the type of oracle needed.
 *
 *@returns An oracle that contains the data found at filePath
 *
 * @author Cedric Richter
 *
 */

public class OracleIO implements IDataSetIO {
    @Override

    /**
     * @return PropertyMapping from BaseFolder and ConfigFile
     */
    public PropertyMapping loadProperties(String baseFolder, String configFile) {
        return PropMapper.getPropertyMappingFromFile(baseFolder, configFile);
    }
    /**
     * @return a HybridCache filled with sourceInfo
     */
    @Override
    public ACache loadSourceCache(Configuration cfg, String path, String type) {
        return HybridCache.getData(cfg.getSourceInfo());
    }

    /**
     * @return  a HybridCache filled with TargetInfo
     */
    @Override
    public ACache loadTargetCache(Configuration cfg, String path, String type) {
        return HybridCache.getData(cfg.getTargetInfo());
    }

    /**
     * @return An Oracle with data found at path specified and in csv format
     */
    @Override
    public AMapping loadMapping(Configuration cfg, String path) {
        return OracleFactory.getOracle(
                path, "csv", "simple"
        ).getMapping();
    }
}
