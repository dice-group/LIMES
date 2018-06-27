package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.Experiment;
import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;
import org.aksw.limes.core.evaluation.evaluationDataLoader.PropMapper;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

/**
 * Class to Select OAEIDataSetIO Dataset for the evaluation.
 * Using this class you can perform a variety of functions on the selected dataset.
 * You can loadProperties using baseFolder and ConfigFile as parameters.
 * Load Source Cache, Target Cache and Mapping.
 *Specifc Format Classes.
 *
 * @author Cedric Richter
 *
 */

public class OAEIDataSetIO implements IDataSetIO {

    /**
     * @return PropertyMapping from BaseFolder and ConfigFile
     */
    @Override
    public PropertyMapping loadProperties(String baseFolder, String configFile) {
        return PropMapper.getPropertyMappingFromFile(baseFolder, configFile);
    }

    /**
     * @return SourceCache after reading OAEIFile based on path and type
     */
    @Override
    public ACache loadSourceCache(Configuration config, String path, String type) {
        return Experiment.readOAEIFile(path, type);
    }

    /**
     * @return TargetCache after reading OAEIFile based on path and type
     */
    @Override
    public ACache loadTargetCache(Configuration config, String path, String type) {
        return Experiment.readOAEIFile(path, type);
    }

    /**
     * @return PropertyMapping after reading OAEIFile by taking path as an argument.
     */
    @Override
    public AMapping loadMapping(Configuration config, String path) {
        return Experiment.readOAEIMapping(path);
    }
}
