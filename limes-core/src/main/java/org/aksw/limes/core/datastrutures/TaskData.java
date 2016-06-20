package org.aksw.limes.core.datastrutures;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.datastrutures.GoldStandard;

import java.util.List;

/**
 * This class contains all information regarding a dataset used for evaluating an algorithm
 * It includes the name, cache of the source dataset, cache of the target dataset, the mapping predicted and the gold standard
 *
 * @author mofeed
 * @version 1.0
 */
public class TaskData implements Comparable<TaskData> {
    public String dataName;
    public Cache source;
    public Cache target;
    public AMapping mapping;
    public AMapping training; // for supervised tasks
    public PseudoFMeasure pseudoFMeasure; // for unsupervised tasks

    public GoldStandard goldStandard;

    public TaskData(){};
    public TaskData(GoldStandard goldStandard, AMapping mapping, Cache source, Cache target) {
        this.goldStandard = goldStandard;
        this.mapping = mapping;
        this.source = source;
        this.target = target;
    }

    public TaskData(GoldStandard goldStandard, Cache source, Cache target) {
        this.goldStandard = goldStandard;
        this.mapping = goldStandard.referenceMappings;
        this.source = source;
        this.target = target;
    }

    public TaskData(AMapping mapping, GoldStandard goldStandard) {
        this.goldStandard = goldStandard;
        this.mapping = mapping;
    }

    public TaskData(AMapping mapping) {
        this.mapping = mapping;
    }

    public TaskData(String name) {
        this.dataName = name;
    }

    public List<String> getSourceURIs() {
        return source.getAllUris();
    }

    public List<String> getTargetURIs() {
        return target.getAllUris();
    }
    @Override
    public int compareTo(TaskData other) {
        return this.dataName.compareTo(other.dataName);
    }

 
}
