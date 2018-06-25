package org.aksw.limes.core.measures.measure.customGraphs;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.io.describe.Descriptor;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.measures.measure.bags.JaccardBagMeasure;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster.SimilarityFilter;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.impl.MaxSimRelabel;
import org.aksw.limes.core.measures.measure.string.JaccardMeasure;
import org.aksw.limes.core.measures.measure.string.JaroWinklerMeasure;
import org.aksw.limes.core.measures.measure.string.LevenshteinMeasure;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class WLGraphMeasureTest {

    @Test
    public void getSimilarity() {

        EvaluationData data = DataSetChooser.getData("RESTAURANTS");

        Descriptor sourceDescriptor = new Descriptor(data.getSourceCache().getKbInfo());
        Descriptor targetDescriptor = new Descriptor(data.getTargetCache().getKbInfo());

        SimilarityFilter def1 = new SimilarityFilter(MeasureType.JAROWINKLER, 0.5);
        SimilarityFilter def2 = new SimilarityFilter(MeasureType.TRIGRAM, 0.4);
        SimilarityFilter def3 = new SimilarityFilter(MeasureType.LEVENSHTEIN, 0.3);
        List<SimilarityFilter> list = Arrays.asList(new SimilarityFilter[]{def1, def2, def3});

        MaxSimRelabel relabel = new MaxSimRelabel(list);

        WLGraphMeasure measure = new WLGraphMeasure(relabel, new JaccardBagMeasure());
        AMapping reference = data.getReferenceMapping();

        double avg_sim = 0;
        long avg_time = 0;
        int steps = 0;

        for(String src: reference.getMap().keySet()){
            for(String target: reference.getMap().get(src).keySet()){
                long start = System.currentTimeMillis();
                double sim = measure.getSimilarity(
                        sourceDescriptor.describe(src, 2), targetDescriptor.describe(target, 2)
                );
                long time = System.currentTimeMillis() - start;

                System.out.println(src+" : "+target+" = "+ sim +" ("+ time+ "ms)");

                steps++;

                avg_time += 1.0/steps * (time - avg_time);

                if(sim != 0)
                    avg_sim  += 1.0/steps * (sim - avg_sim);


            }

            if(steps >= 100)
                break;
        }

        System.out.println("Average time: "+avg_time+" ms");
        System.out.println("Average sim: "+avg_sim);

        double old_sim = avg_sim;

        int next_steps = 0;
        avg_sim = 0;
        avg_time = 0;

        for(String src: data.getSourceCache().getAllUris()){
            Map<String, Double> ref = reference.getMap().getOrDefault(src, new HashMap<>());
            int target_steps = (int)Math.sqrt(steps);
            for(String target: data.getTargetCache().getAllUris()){
                if(ref.containsKey(target))continue;

                long start = System.currentTimeMillis();
                double sim = measure.getSimilarity(
                        sourceDescriptor.describe(src), targetDescriptor.describe(target)
                );
                long time = System.currentTimeMillis() - start;

                System.out.println(src+" : "+target+" = "+ sim +" ("+ time+ "ms)");

                next_steps++;

                avg_time += 1.0/next_steps * (time - avg_time);

                if(sim != 0)
                    avg_sim  += 1.0/next_steps * (sim - avg_sim);

                target_steps--;

                if(target_steps <= 0)
                    break;
            }

            if(next_steps >= steps)
                break;
        }


        System.out.println("Average time: "+avg_time+" ms");
        System.out.println("Average sim: "+avg_sim);
        System.out.println("Gap: "+(old_sim - avg_sim));


    }
}