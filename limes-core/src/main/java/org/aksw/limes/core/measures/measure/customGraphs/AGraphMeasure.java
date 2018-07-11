package org.aksw.limes.core.measures.measure.customGraphs;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.describe.IResourceDescriptor;

/**
 * Abstract class to get graph measure between two objects using IResourceDescriptor
 * @author Cedric Richter
 */
public abstract class AGraphMeasure implements IGraphMeasure {

    @Override
    public double getSimilarity(Object object1, Object object2) {
        if(object1 instanceof IResourceDescriptor &&
                object2 instanceof IResourceDescriptor)
            return getSimilarity((IResourceDescriptor)object1, (IResourceDescriptor)object2);
        return 0;
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        return 0;
    }


    @Override
    public String getType() {
        return "resource_graph";
    }
}
