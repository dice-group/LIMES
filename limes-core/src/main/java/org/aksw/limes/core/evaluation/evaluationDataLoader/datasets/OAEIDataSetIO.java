package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.Experiment;
import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;
import org.aksw.limes.core.evaluation.evaluationDataLoader.PropMapper;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

public class OAEIDataSetIO implements IDataSetIO {
    @Override
    public PropertyMapping loadProperties(String baseFolder, String configFile) {
        return PropMapper.getPropertyMappingFromFile(baseFolder, configFile);
    }

    @Override
    public ACache loadSourceCache(Configuration config, String path, String type) {
        return Experiment.readOAEIFile(path, type);
    }

    @Override
    public ACache loadTargetCache(Configuration config, String path, String type) {
        return Experiment.readOAEIFile(path, type);
    }

    @Override
    public AMapping loadMapping(Configuration config, String path) {
        return Experiment.readOAEIMapping(path);
    }
}
