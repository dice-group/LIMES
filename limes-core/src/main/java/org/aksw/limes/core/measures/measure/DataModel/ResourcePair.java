package org.aksw.limes.core.measures.measure.DataModel;

import javafx.util.Pair;

public class ResourcePair {
    private String label;
    private String source;
    private String target;

    public ResourcePair (String source, String label, String target)
    {
        this.label = label;
        this.target = target;
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public String setTarget() {
        return target;
    }

    public String getLabel() {
        return label;
    }

}
