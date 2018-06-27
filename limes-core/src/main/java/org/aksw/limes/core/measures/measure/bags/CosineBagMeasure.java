package org.aksw.limes.core.measures.measure.bags;

import com.google.common.collect.Multiset;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.SparseVector;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * This Bag Measure works on the idea of Cosine Similarity, in which:
 * the number of common attributes is divided by the total number of possible attributes
 *
 * Cosine similarity is for comparing two real-valued vectors
 *
 * if x and y are two vectors then: the dot product of (x, y) divided by
 * the sqrt of dot product of (x, x) multiplied by sqrt of the dot product of (y, y)
 *
 * @see org.aksw.limes.core.measures.mapper.bags.cosine.CosineBagMapper
 *
 * @author Cedric Richter
 */

public class CosineBagMeasure extends ABagMeasure {

    public static final double VECTOR_DENSITY_THRESHOLD = 0.7;

    @Override
    public <T> double getSimilarity(Multiset<T> A, Multiset<T> B) {

        Map<T, Integer> index = index(A, B);

        double density = 1 - index.size() / A.elementSet().size()+B.elementSet().size();

        if(density == 0)return 0;

        Vector Va = initVector(A, index, density);
        Vector Vb = initVector(B, index, density);

        return Va.dot(Vb) / Math.sqrt((Va.dot(Va)*Vb.dot(Vb)));
    }

    private <T> Map<T, Integer> index(Multiset<T> A, Multiset<T> B){
        HashMap<T, Integer> out = new HashMap<>(Math.max(A.size(), B.size()));
        int count = 0;

        for(T obj: A.elementSet()){
            if(!out.containsKey(obj)){
                out.put(obj, count++);
            }
        }

        for(T obj: B.elementSet()){
            if(!out.containsKey(obj)){
                out.put(obj, count++);
            }
        }

        return out;
    }

    private <T> Vector initVector(Multiset<T> bag, Map<T, Integer> index, double density){
        Vector out;
        if(density >= VECTOR_DENSITY_THRESHOLD){
            out = new DenseVector(index.size());
        }else{
            out = new SparseVector(index.size());
        }

        for(T obj: bag.elementSet()){
            out.set(index.get(obj), bag.count(obj));
        }

        return out;
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize;
    }

    @Override
    public String getName() {
        return "cosine";
    }

}
