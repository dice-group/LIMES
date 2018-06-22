package org.aksw.limes.core.measures.measure.bags;

import com.google.common.collect.Multiset;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cedric Richter
 */
public class JaccardBagMeasure extends ABagMeasure {
    @Override
    public <T> double getSimilarity(Multiset<T> A, Multiset<T> B) {

        Map<T, Pair> index = indexPair(A, B);

        if(index.size() == A.elementSet().size() + B.elementSet().size())
            return 0;

        double minSum = 0, maxSum = 0;

        for(Pair p: index.values()){
            minSum += p.min();
            maxSum += p.max();
        }


        return minSum/maxSum;
    }

    private <T> Map<T, Pair> indexPair(Multiset<T> A, Multiset<T> B){

        Map<T, Pair> index = new HashMap<>();

        for(T e: A.elementSet()){
            index.put(e, new Pair(A.count(e), 0));
        }

        for(T e: B.elementSet()){
            Pair p = index.putIfAbsent(e, new Pair(0, 0));
            index.get(e).b = B.count(e);
        }

        return index;
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize;
    }

    @Override
    public String getName() {
        return "jaccard";
    }

    private class Pair{

        private int a;
        private int b;

        public Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int min(){
            return Math.min(a, b);
        }

        public int max(){
            return Math.max(a, b);
        }

    }

}
