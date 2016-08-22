package org.aksw.limes.core.measures.measure.resourcesets;

import java.util.Set;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.AMeasure;

/**
 * @author Kevin Dreßler
 * @since 1.0
 */
public class SetJaccardMeasure extends AMeasure {
    @Override
    public double getSimilarity(Object object1, Object object2) {
        if (object1 instanceof Set && object2 instanceof Set) {
            @SuppressWarnings("unchecked")
            Set<String> s = (Set<String>) object1;
            @SuppressWarnings("unchecked")
            Set<String> t = (Set<String>) object2;
            if (s.isEmpty() && t.isEmpty())
                return 1d;
            int matches = 0;
            for (String x : s)
                if (t.contains(x))
                    matches++;
            return matches / ((double) s.size() + (double) t.size() - (double) matches);
        } else {
            return 0d;
        }
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        Set<String> object1 = instance1.getProperty(property1);
        Set<String> object2 = instance2.getProperty(property2);
        return getSimilarity(object1, object2);
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

    @Override
    public String getName() {
        return "set_jaccard";
    }

    @Override
    public String getType() {
        return "resource_sets";
    }
}
