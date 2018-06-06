package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser2;
import org.aksw.limes.core.evaluation.evaluationDataLoader.Experiment;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;

public class FixedOAEIDataSetIO extends OAEIDataSetIO {
    private ACache src;
    private ACache target;

    @Override
    public ACache loadSourceCache(Configuration config, String path, String type) {
        src = super.loadSourceCache(config, path, type);
        return src;
    }

    @Override
    public ACache loadTargetCache(Configuration config, String path, String type) {
        target = super.loadTargetCache(config, path, type);
        return target;
    }


    @Override
    public AMapping loadMapping(Configuration config, String path){
        return DataSetChooser2.fixReferenceMap(
                super.loadMapping(config, path), src, target
        );
    }

}
