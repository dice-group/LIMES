package org.aksw.limes.core.datastrutures;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;

import java.util.List;

/**
 * This class contains all information regarding a dataset used for evaluating an algorithm
 * It includes the name, cache of the source dataset, cache of the target dataset, the mapping predicted and the gold standard
 *
 * @author mofeed
 * @version 1.0
 */
public class Task {
    public String pairName;
    public Cache source;
    public Cache target;
    public AMapping mapping;
    public AMapping goldStandard;

    public Task(AMapping goldStandard, AMapping mapping, Cache source, Cache target) {
        this.goldStandard = goldStandard;
        this.mapping = mapping;
        this.source = source;
        this.target = target;
    }

    public Task(AMapping goldStandard, Cache source, Cache target) {
        this.goldStandard = goldStandard;
        this.source = source;
        this.target = target;
    }

    public Task(AMapping mapping, AMapping goldStandard) {
        this.goldStandard = goldStandard;
        this.mapping = mapping;
    }

    public Task(AMapping mapping) {
        this.mapping = mapping;
    }

    public Task(String name) {
        this.pairName = name;
    }

    public List<String> getSourceURIs() {
        return source.getAllUris();
    }

    public List<String> getTargetURIs() {
        return target.getAllUris();
    }


}
