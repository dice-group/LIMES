package org.aksw.limes.core.measures.measure.customGraphs.relabling.impl;

import com.google.common.collect.Table;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.IGraphRelabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabelCollector;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster.MappingHelper;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster.SimilarityFilter;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Cedric Richter
 */
public class MaxSimRelabel implements IGraphRelabel {

    protected List<SimilarityFilter> definitions;
    protected Set<String> labels = new HashSet<>();

    public MaxSimRelabel(List<SimilarityFilter> definitions) {
        this.definitions = definitions;
    }

    @Override
    public ILabelCollector getPriorLabelCollector() {
        return new ILabelCollector() {
            @Override
            public Consumer<ILabel> getSourceLabelConsumer() {
                return (x -> {if(x.getType()==ILabel.LabelType.NODE)labels.add(x.getContent());});
            }
        };
    }


    @Override
    public String relabel(ILabel label) {
        if(label.getType() == ILabel.LabelType.EDGE)
            return null;

        Set<ILabel> labels = new HashSet<>();
        labels.add(label);

        return relabel(labels).get(label);

    }

    private Map<String, String> innerRelabel(Set<String> L){

        Map<String, String> result = new HashMap<>();

        for(String s: L)
            result.put(s, s);


        Map<String, Map<String, ParetoFrontHelper.AnnotatedVector>> paretoMap = new HashMap<>();

        for(int i = 0; i < definitions.size(); i++){

            SimilarityFilter filter = definitions.get(i);

            AMapping mapping = MappingHelper.filter(labels, L, filter);

            for(Map.Entry<String, HashMap<String, Double>> e: mapping.getMap().entrySet()){

                if(!paretoMap.containsKey(e.getKey()))
                    paretoMap.put(e.getKey(), new HashMap<>());
                Map<String, ParetoFrontHelper.AnnotatedVector> map = paretoMap.get(e.getKey());

                for(Map.Entry<String, Double> t: e.getValue().entrySet()){

                    if(!map.containsKey(t.getKey())) {
                        ParetoFrontHelper.AnnotatedVector vector = new ParetoFrontHelper.AnnotatedVector(t.getKey());
                        for(int j = 0; j < i; j++)
                            vector.vector.add(0.0);
                        map.put(t.getKey(), vector);
                    }

                    ParetoFrontHelper.AnnotatedVector vector = map.get(t.getKey());

                    vector.vector.add((t.getValue() - filter.getThreshold())/(1 - filter.getThreshold()));

                }

            }
        }

        for(Map.Entry<String, Map<String, ParetoFrontHelper.AnnotatedVector>> res: paretoMap.entrySet()){

            Set<ParetoFrontHelper.AnnotatedVector> pareto = ParetoFrontHelper.instance().pareto(
                    new HashSet<>(res.getValue().values())
            );

            String relabel = res.getKey();
            double min_dist = Double.POSITIVE_INFINITY;

            for(ParetoFrontHelper.AnnotatedVector vec : pareto){
                double dist = 0.0;

                for(int i = 0; i < vec.vector.size(); i++)
                    dist += vec.vector.get(i) * vec.vector.get(i);

                dist = Math.sqrt(dist);

                if(dist < min_dist){
                    min_dist = dist;
                    relabel = vec.annotation;
                }

            }

            result.put(res.getKey(), relabel);
        }

        return result;
    }


    @Override
    public Map<ILabel, String> relabel(Set<ILabel> labels) {
        Map<ILabel, String> out = new HashMap<>();
        Map<String, ILabel> reverse = new HashMap<>();

        for(ILabel label: labels){
            if(label.getType() == ILabel.LabelType.EDGE){
                out.put(label, null);
            }else {
                reverse.put(label.getContent(), label);
            }
        }

        if(!reverse.isEmpty()){

            Map<String, String> relabel = innerRelabel(reverse.keySet());

            for(Map.Entry<String, String> e: relabel.entrySet()){
                out.put(reverse.get(e.getKey()), e.getValue());
            }

        }

        return out;
    }


}
