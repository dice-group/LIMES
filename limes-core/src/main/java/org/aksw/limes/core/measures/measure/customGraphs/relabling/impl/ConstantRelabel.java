package org.aksw.limes.core.measures.measure.customGraphs.relabling.impl;

import org.aksw.limes.core.measures.measure.customGraphs.relabling.IGraphRelabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabelCollector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class to relabel Literal Constant
 *
 * @author Cedric Richter
 */

public class ConstantRelabel implements IGraphRelabel {

    public static final String LITERAL_CONSTANT = "_LITERAL_";

    @Override
    public ILabelCollector getPriorLabelCollector() {
        return null;
    }

    @Override
    public String relabel(ILabel label) {
        if(label.getType() == ILabel.LabelType.EDGE)
            return null;
        return LITERAL_CONSTANT;
    }

    @Override
    public Map<ILabel, String> relabel(Set<ILabel> labels) {
        Map<ILabel, String> mapping = new HashMap<>();

        for(ILabel label: labels){
            mapping.put(label, relabel(label));
        }

        return mapping;
    }
}
