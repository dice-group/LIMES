package org.aksw.limes.core.measures.measure.graphs.representation;

import org.aksw.limes.core.io.describe.IResourceDescriptor;
import org.aksw.limes.core.measures.measure.graphs.gouping.INodeLabelGrouper;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;
import java.util.List;

public class WLModelRepresentation {

    private GraphModelRepresentation representation;

    private List<WLModelIteration> list = new ArrayList<>();

    public WLModelRepresentation(IResourceDescriptor descriptor, INodeLabelGrouper grouper){
        this.representation = new GraphModelRepresentation(descriptor, grouper);
    }

    public IResourceDescriptor getModelDescriptor(){
        return representation.getRootDescriptor();
    }

    public WLModelIteration getIteration(int i){
        if(i < 0) throw new IllegalArgumentException("Iteration have to be greater 0");

        if(i < list.size()){
            return list.get(i);
        }else if(i == 0){
            WLModelIteration iteration = new WLModelIterationZero(representation);
            list.add(iteration);
            return iteration;
        }else{
            WLModelIteration previous = getIteration(i - 1);
            WLModelIteration iteration = new WLModelIterationNext(representation, previous);
            list.add(iteration);
            return iteration;
        }
    }




}
