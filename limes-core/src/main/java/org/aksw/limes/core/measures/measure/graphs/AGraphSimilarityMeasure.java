package org.aksw.limes.core.measures.measure.graphs;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.describe.Descriptor;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.apache.jena.rdf.model.Model;

import java.util.logging.Logger;

public abstract class AGraphSimilarityMeasure extends AMeasure implements IGraphSimilarityMeasure {

    protected Logger logger;

    protected Descriptor descriptor1, descriptor2;


    @Override
    public double getSimilarity(Object object1, Object object2) {
        if(descriptor1 == null || descriptor2 == null)
            throw new UnsupportedOperationException("Both descriptor have to be set beforehand!");
        if(!(object1 instanceof String) || !(object2 instanceof String))
            throw new UnsupportedOperationException("Both objects have to URIs");
        return getSimilarity(
                descriptor1.describe((String)object1).queryDescription(),
                descriptor2.describe((String)object2).queryDescription()
        );
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        if(logger != null){
            logger.warning(String.format("Graph similarity will ignore properties %s, %s", property1, property2));
        }
        return getSimilarity(instance1.getUri(), instance2.getUri());
    }

    public abstract double getSimilarity(Model model1, Model model2);

    public String getType() {
        return "graph";
    }

    public Descriptor getDescriptor1() {
        return descriptor1;
    }

    public void setDescriptor1(Descriptor descriptor1) {
        this.descriptor1 = descriptor1;
    }

    public Descriptor getDescriptor2() {
        return descriptor2;
    }

    public void setDescriptor2(Descriptor descriptor2) {
        this.descriptor2 = descriptor2;
    }

}
