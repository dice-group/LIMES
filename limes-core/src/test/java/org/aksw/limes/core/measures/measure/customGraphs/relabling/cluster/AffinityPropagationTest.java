package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;

import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.util.RandomStringGenerator;
import org.apache.jena.base.Sys;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class AffinityPropagationTest {

    @Test
    public void iterate() {

        List<SimilarityFilter> filter = new ArrayList<>();
        filter.add(new SimilarityFilter(MeasureType.LEVENSHTEIN, 0.1));

        Set<String> set = randomSet(1000);

        AffinityPropagation AP = new AffinityPropagation(
                new ORSimilarityAggregator().getSimilarities(set, set, filter)
        );

        double last = 0;
        double act = 0;

        int steps = 0;

        do{
            last = act;
            act = AP.iterate();

            System.out.println(act);

            if(Math.abs(act - last) < 0.00001) {
                steps++;
            }else{
                steps = 0;
            }

        }while (steps < 100);

        AP.stats();

        Map<String,String> map = AP.cluster();

        System.out.println(new HashSet<>(map.values()).size());



    }

    private Set<String> randomSet(int size){
        RandomStringGenerator rsg = new RandomStringGenerator(5, 20);
        Set<String> out = new HashSet<>();

        while(out.size() < size)
            out.add(rsg.generateString());

        return out;
    }
}