package org.aksw.limes.core.measures.measure.customGraphs.relabling.impl;

import com.google.common.collect.Table;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.IGraphRelabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabelCollector;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ITwoSideLabelCollector;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster.ASimilarityAggregator;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster.AffinityPropagation;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster.ORSimilarityAggregator;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster.SimilarityFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * Class to Relabel AP (AffinityPropagation)
 *
 * @author Cedric Richter
 */

public class APRelabel implements IGraphRelabel {

    static Log logger = LogFactory.getLog(APRelabel.class);

    protected List<SimilarityFilter> definitions;
    protected Set<String> srcSet = new HashSet<>();
    protected Set<String> targetSet = new HashSet<>();
    private Map<String, String> cache;

    private boolean runAP = false;

    public APRelabel(List<SimilarityFilter> definitions) {
        this.definitions = definitions;
    }

    @Override
    public ILabelCollector getPriorLabelCollector() {
        return new ITwoSideLabelCollector() {
            @Override
            public Consumer<ILabel> getTargetLabelConsumer() {
                return (x -> {addLabel(x, false);});
            }

            @Override
            public Consumer<ILabel> getSourceLabelConsumer() {
                return (x -> {addLabel(x, true);});
            }
        };
    }

    private void addLabel(ILabel label, boolean isSource){
        runAP = false;
        if(label.getType() != ILabel.LabelType.NODE)return;
        if(isSource)
            srcSet.add(label.getContent());
        else
            targetSet.add(label.getContent());
    }

    private void runAP(){
        if(runAP)return;

        logger.info("Start affinity propagation");


        long startTime = System.currentTimeMillis();

        Set<String> baseLabels, attachLabels;

        if(srcSet.size() < targetSet.size()){
            baseLabels = srcSet;
            attachLabels = targetSet;
        }else{
            baseLabels = targetSet;
            attachLabels = srcSet;
        }

        baseLabels.addAll(attachLabels);

        ASimilarityAggregator aggregator = new ORSimilarityAggregator();

        AffinityPropagation AP = new AffinityPropagation(
                aggregator.getSimilarities(baseLabels, baseLabels, definitions)
        );
        logger.info("Initialized AP with "+baseLabels.size()+" instances ["+(System.currentTimeMillis() - startTime)+" ms]");

        double last = 0;
        double act = 0;

        int steps = 0;

        int iteration = 1;

        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(1);

        do{

            startTime = System.currentTimeMillis();

            last = act;
            act = AP.iterate();

            long time = System.currentTimeMillis() - startTime;
            double improve = (Math.abs(last - act)/last);

            logger.info("AP iteration "+iteration+": "+act +"("+format.format(improve)+" improvement ) ["+time+" ms]");
            iteration++;

            if(Math.abs(act - last) < 0.001) {
                steps++;
            }else{
                steps = 0;
            }

        }while (steps < 3);

        cache = AP.cluster();
        int size = baseLabels.size();

        baseLabels.removeAll(cache.keySet());
        baseLabels.addAll(cache.values());

        format.setMaximumFractionDigits(2);

        logger.info("Finished AP. Reduce labels by "+format.format(1 - (double)baseLabels.size()/size));

        runAP = true;

/*
        logger.info("Predict label for "+attachLabels.size()+" entities");
        startTime = System.currentTimeMillis();

        Table<String, String, Double> predict = aggregator.getSimilarities(attachLabels, baseLabels, definitions);

        int neigh = 0;
        int count = 0;

        for(String s: predict.rowKeySet()){
            Map<String, Double> row = predict.row(s);
            neigh += row.size();
            count++;

            double max = Double.NEGATIVE_INFINITY;
            String example = null;

            for(Map.Entry<String, Double> e: row.entrySet()){
                if(e.getValue() > max)
                    example = e.getKey();
            }

            logger.info(s+" --> "+example);

            cache.put(s, example);
        }

        logger.info("Average options: "+((double)neigh/count));

        logger.info("Finished prediction. ["+(System.currentTimeMillis() - startTime)+" ms]");



        runAP = true;
        */
    }

    @Override
    public Map<ILabel, String> relabel(Set<ILabel> labels){
        runAP();

        Map<ILabel, String> result = new HashMap<>();


        for(ILabel label: labels){
            if(label.getType() == ILabel.LabelType.EDGE)
                result.put(label, null);
            else if(cache.containsKey(label.getContent())) {
                result.put(label, cache.get(label.getContent()));
            }else{
                result.put(label, label.getContent());
            }
        }


        return result;
    }

    @Override
    public String relabel(ILabel label) {
        if(label.getType() == ILabel.LabelType.EDGE)
            return null;

        Set<ILabel> labels = new HashSet<>();
        labels.add(label);

        return relabel(labels).get(label);

    }

}
