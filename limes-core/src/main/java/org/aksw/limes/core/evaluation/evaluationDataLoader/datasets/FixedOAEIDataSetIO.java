package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser2;
import org.aksw.limes.core.evaluation.evaluationDataLoader.Experiment;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;


    /**
     *  This class is used to select FixedOAEIDataSetIO
     * Basically this is another implementation of OAEIDataSetIO that supports immutability.
     * You can load the source and target cache, provide it with a configuration and fix reference mapping of OAEIDataSetIO.
     *
     * @author Cedric Richter
     *
     */
    public class FixedOAEIDataSetIO extends OAEIDataSetIO {
        private ACache src;
        private ACache target;


    /**
     * @return sourceCache
     */
    @Override
    public ACache loadSourceCache(Configuration config, String path, String type) {
        src = super.loadSourceCache(config, path, type);
        return src;
    }


    /**
     * @return TargetCahce
     */
    @Override
    public ACache loadTargetCache(Configuration config, String path, String type) {
        target = super.loadTargetCache(config, path, type);
        return target;
    }



    /**
     * @return Fixes ReferenceMapping in Source and Target Mappings
     */
    @Override
    public AMapping loadMapping(Configuration config, String path){
        return DataSetChooser2.fixReferenceMap(
                super.loadMapping(config, path), src, target
        );
    }

}
