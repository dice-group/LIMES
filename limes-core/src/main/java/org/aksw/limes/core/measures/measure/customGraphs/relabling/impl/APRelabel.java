package org.aksw.limes.core.measures.measure.customGraphs.relabling.impl;

import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.IGraphRelabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabelCollector;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ITwoSideLabelCollector;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster.AffinityPropagation;
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
    protected Set<String> labels = new HashSet<>();
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
                return getSourceLabelConsumer();
            }

            @Override
            public Consumer<ILabel> getSourceLabelConsumer() {
                return (x -> {if(x.getType()==ILabel.LabelType.NODE)labels.add(x.getContent());});
            }
        };
    }

    private void runAP(){
        if(runAP)return;

        logger.info("Start affinity propagation");


        long startTime = System.currentTimeMillis();

        AffinityPropagation AP = new AffinityPropagation(definitions, labels);
        logger.info("Initialized AP with "+labels.size()+" instances ["+(System.currentTimeMillis() - startTime)+" ms]");

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

            if(Math.abs(act - last) < 0.00001) {
                steps++;
            }else{
                steps = 0;
            }

        }while (steps < 10);

        cache = AP.cluster();
        int size = labels.size();

        labels.removeAll(cache.keySet());
        labels.addAll(cache.values());

        format.setMaximumFractionDigits(2);

        logger.info("Finished AP. Reduce labels by "+format.format(1 - (double)labels.size()/size));

        runAP = true;
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
