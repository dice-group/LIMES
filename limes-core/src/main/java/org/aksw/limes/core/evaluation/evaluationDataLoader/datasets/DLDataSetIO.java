package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.Experiment;
import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;
import org.aksw.limes.core.evaluation.evaluationDataLoader.PropMapper;
import org.aksw.limes.core.evaluation.oracle.OracleFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

public class DLDataSetIO implements IDataSetIO {
    public PropertyMapping loadProperties(String baseFolder, String configFile) {
        return PropMapper.getPropertyMappingFromFile(baseFolder, configFile);
    }
    public ACache loadSourceCache(Configuration cfg, String path, String type) {
        return HybridCache.getData(cfg.getSourceInfo());
    }

    public ACache loadTargetCache(Configuration cfg, String path, String type) {
        return HybridCache.getData(cfg.getTargetInfo());
    }
    public AMapping loadMapping(Configuration config, String path) {
        return Experiment.readOAEIMapping(path);
    }

//    public AMapping loadMapping(Configuration cfg, String path) {
//        return OracleFactory.getOracle(
//                path, "csv", "simple"
//        ).getMapping();
//    }
}
