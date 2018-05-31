package org.aksw.limes.core.evaluation.evaluationDataLoader;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

public interface IDataSetIO {

    public PropertyMapping loadProperties(String baseFolder, String configFile);

    public ACache loadSourceCache(Configuration cfg, String path, String type);

    public ACache loadTargetCache(Configuration cfg, String path, String type);

    public AMapping loadMapping(Configuration cfg, String path);
}
