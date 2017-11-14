package org.aksw.limes.core.ml.algorithm.ligon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kevin Dreßler
 */
public class EvaluationResult {

    private List<List<String>> measurements;

    public EvaluationResult() {
        this.measurements = new ArrayList<>();
        this.measurements.add(new ArrayList<>());
    }

    public EvaluationResult addRecord(String...m) {
        this.measurements.get(this.measurements.size() - 1).addAll(Arrays.asList(m));
        return this;
    }

    public EvaluationResult endRecord() {
        this.measurements.add(new ArrayList<>());
        return this;
    }

}
