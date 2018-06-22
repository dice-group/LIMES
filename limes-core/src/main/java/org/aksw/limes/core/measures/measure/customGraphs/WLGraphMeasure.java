package org.aksw.limes.core.measures.measure.customGraphs;

import com.google.common.collect.Multiset;
import javafx.util.Pair;
import org.aksw.limes.core.io.describe.IResourceDescriptor;
import org.aksw.limes.core.measures.measure.bags.IBagMeasure;
import org.aksw.limes.core.measures.measure.bags.JaccardBagMeasure;
import org.aksw.limes.core.measures.measure.customGraphs.description.IDescriptionGraphView;
import org.aksw.limes.core.measures.measure.customGraphs.description.impl.DescriptionGraphFactory;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.*;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.impl.JaccardIndexRelabel;
import org.aksw.limes.core.measures.measure.customGraphs.subgraphs.WLSubgraphCollector;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * This measure is inspired by the Weisfeiler Lehmann test of isomorphism
 *
 * @author Cedric Richter
 */
public class WLGraphMeasure extends AGraphMeasure {

    private int depth;
    private IGraphRelabel relabel;
    private IBagMeasure bagMeasure;

    public WLGraphMeasure(int depth, IGraphRelabel relabel, IBagMeasure bagMeasure) {
        this.depth = depth;
        this.relabel = relabel;
        this.bagMeasure = bagMeasure;
    }

    public WLGraphMeasure(IGraphRelabel relabel, IBagMeasure bagMeasure) {
        this(2, relabel, bagMeasure);
    }

    public WLGraphMeasure(IBagMeasure bagMeasure) {
        this(new JaccardIndexRelabel(3), bagMeasure);
    }

    public WLGraphMeasure() {
        this(new JaccardBagMeasure());
    }

    private IDescriptionGraphView prepareGraph(IResourceDescriptor d, Consumer<ILabel> consumer){
        if(consumer == null){
            return DescriptionGraphFactory.lazy(d).build();
        }else {
            return DescriptionGraphFactory.eager(d).listen(consumer).build();
        }
    }

    private <A, B> Stream<Pair<A, B>> zip(Stream<A> as, Stream<B> bs){
        Iterator<A> i=as.iterator();
        return bs.filter(x->i.hasNext()).map(b->new Pair<>(i.next(), b));
    }

    @Override
    public double getSimilarity(IResourceDescriptor d1, IResourceDescriptor d2) {
        IDescriptionGraphView source, target;

        ILabelCollector collector = relabel.getPriorLabelCollector();

        if(collector == null){
            source = prepareGraph(d1, null);
            target = prepareGraph(d2, null);
        }else{
            source = prepareGraph(d1, collector.getSourceLabelConsumer());
            Consumer<ILabel> targetConsumer = null;
            if(collector instanceof ITwoSideLabelCollector){
                targetConsumer = ((ITwoSideLabelCollector) collector).getTargetLabelConsumer();
            }
            target = prepareGraph(d2, targetConsumer);
        }

        source = new RelablingWrapper(source, relabel);
        target = new RelablingWrapper(target, relabel);

        Stream<Multiset<String>> sourceStream = new WLSubgraphCollector(source).iterate(depth+1).streamAll();
        Stream<Multiset<String>> targetStream = new WLSubgraphCollector(target).iterate(depth+1).streamAll();

        return zip(sourceStream, targetStream)
                .map(pair -> bagMeasure.getSimilarity(pair.getKey(), pair.getValue()))
                .reduce((x, y) -> x+y).get() / (depth+1);
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return 1000d;
    }

    @Override
    public String getName() {
        return "weisfeiler_lehman";
    }
}
