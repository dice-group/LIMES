package org.aksw.limes.core.measures.measure.customGraphs.relabling.impl;

import com.google.common.collect.Lists;
import com.google.common.math.IntMath;

import java.math.RoundingMode;
import java.util.*;

public class ParetoFrontHelper {

    private static ParetoFrontHelper instance;

    public static ParetoFrontHelper instance(){
        if(instance == null){
            instance = new ParetoFrontHelper();
        }
        return instance;
    }


    public Set<AnnotatedVector> pareto(Set<AnnotatedVector> vectorSet){

        if(vectorSet.isEmpty())return new HashSet<>();

        List<AnnotatedVector> vectors = new ArrayList<>(vectorSet);

        Comparator<AnnotatedVector> comp = Collections.reverseOrder(
                new Comparator<AnnotatedVector>() {
                    @Override
                    public int compare(AnnotatedVector o1, AnnotatedVector o2) {
                        return o1.vector.get(0).compareTo(o2.vector.get(0));
                    }
                }
        );

        Collections.sort(vectors, comp);

        if(vectors.get(0).vector.size() == 1){
            Set<AnnotatedVector> ann = new HashSet<>();
            ann.add(vectors.get(0));
            return ann;
        }


        return front(vectors);
    }

    private boolean dominated(AnnotatedVector vec, Set<AnnotatedVector> compare){
        for(AnnotatedVector v: compare){
            boolean isDominated = true;

            for(int i = 1; i < vec.vector.size() && i < v.vector.size() && isDominated; i++){
                isDominated &= v.vector.get(i) >= vec.vector.get(i);
            }

            if(isDominated)return true;
        }
        return false;
    }


    private Set<AnnotatedVector> front(List<AnnotatedVector> vectors){

        if(vectors.size() == 1){
            return new HashSet<>(vectors);
        }

        int partitionSize = IntMath.divide(vectors.size(), 2, RoundingMode.UP);
        List<List<AnnotatedVector>> partitions = Lists.partition(vectors, partitionSize);
        Set<AnnotatedVector> T = front(partitions.get(0));
        Set<AnnotatedVector> B = front(partitions.get(1));

        Set<AnnotatedVector> M = new HashSet<>(T);

        for(AnnotatedVector b: B){
            if(!dominated(b, T)){
                M.add(b);
            }
        }

        return M;
    }



    public static class AnnotatedVector{

        public String annotation;

        public Vector<Double> vector;

        public AnnotatedVector(String annotation) {
            this.annotation = annotation;
            this.vector = new Vector<>();
        }

        public String getAnnotation() {
            return annotation;
        }

        public Vector<Double> getVector() {
            return vector;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AnnotatedVector that = (AnnotatedVector) o;
            return Objects.equals(annotation, that.annotation);
        }

        @Override
        public int hashCode() {

            return Objects.hash(annotation);
        }
    }

}
