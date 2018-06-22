package org.aksw.limes.core.measures.mapper.customGraphs;

import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import javafx.util.Pair;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.describe.Descriptor;
import org.aksw.limes.core.io.describe.IResourceDescriptor;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.measures.mapper.bags.IBagMapper;
import org.aksw.limes.core.measures.measure.customGraphs.description.IDescriptionGraphView;
import org.aksw.limes.core.measures.measure.customGraphs.description.impl.DescriptionGraphFactory;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.*;
import org.aksw.limes.core.measures.measure.customGraphs.subgraphs.WLSubgraphCollector;
import org.aksw.limes.core.measures.measure.customGraphs.subgraphs.WLSubgraphProcessor;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConfigurableGraphMapper implements IMapper {

    private int descriptionRecursionDepth;
    private int wlIterationDepth;
    private IGraphRelabel relabel;
    private IBagMapper bagMapper;

    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression, double threshold) {


        Pair<List<WLSubgraphCollector>, List<WLSubgraphCollector>> pair = initGraphViews(source, target);

        Map<String, WLSubgraphProcessor> srcMap = Maps.uniqueIndex(
                                                        pair.getKey().stream()
                                                        .map(WLSubgraphCollector::iterate)
                                                        .iterator(),
                                                        x -> x.getGraph().getRoot());

        Map<String, WLSubgraphProcessor> targetMap = Maps.uniqueIndex(
                                                            pair.getValue().stream()
                                                                    .map(WLSubgraphCollector::iterate)
                                                                    .iterator(),
                                                            x -> x.getGraph().getRoot());


        AMapping result = MappingFactory.createDefaultMapping();

        double epsilon = threshold;

        for(int i = 1; i < wlIterationDepth; i++) {

            AMapping mapping = runMapping(srcMap, targetMap, epsilon);

            Map<String, WLSubgraphProcessor> srcNextGen = new HashMap<>();
            Map<String, WLSubgraphProcessor> targetNextGen = new HashMap<>();

            for(Map.Entry<String, HashMap<String, Double>> e: mapping.getMap().entrySet()){
                srcNextGen.put(e.getKey(), srcMap.get(e.getKey()).iterate());
                for(String t: e.getValue().keySet()){

                }
            }





        }

        return null;
    }

    private IDescriptionGraphView prepareGraph(IResourceDescriptor d, Consumer<ILabel> consumer){
        if(consumer == null){
            return DescriptionGraphFactory.lazy(d).build();
        }else {
            return DescriptionGraphFactory.eager(d).listen(consumer).build();
        }
    }

    private Pair<List<WLSubgraphCollector>, List<WLSubgraphCollector>> initGraphViews(ACache source, ACache target){
        List<IResourceDescriptor> srcResources = new Descriptor(source.getKbInfo()).describeAll(
                source.getAllUris(), descriptionRecursionDepth
        );

        List<IResourceDescriptor> targetResources = new Descriptor(target.getKbInfo()).describeAll(
                target.getAllUris(), descriptionRecursionDepth
        );

        ILabelCollector collector = relabel.getPriorLabelCollector();

        Consumer<ILabel> srcConsumer = null;
        Consumer<ILabel> targetConsumer = null;

        if(collector != null){
            srcConsumer = collector.getSourceLabelConsumer();
            if(collector instanceof ITwoSideLabelCollector){
                targetConsumer = ((ITwoSideLabelCollector) collector).getTargetLabelConsumer();
            }
        }

        final Consumer<ILabel> finalSrcConsumer = srcConsumer;
        final Consumer<ILabel> finalTargetConsumer = targetConsumer;

        List<WLSubgraphCollector> srcViews = srcResources.stream()
                                                .map(x -> prepareGraph(x, finalSrcConsumer))
                                                .map(x -> new RelablingWrapper(x, relabel))
                                                .map(x -> new WLSubgraphCollector(x))
                                                .collect(Collectors.toList());
        List<WLSubgraphCollector> targetViews = targetResources.stream()
                                                    .map(x -> prepareGraph(x, finalTargetConsumer))
                                                    .map(x -> new RelablingWrapper(x, relabel))
                                                    .map(x -> new WLSubgraphCollector(x))
                                                    .collect(Collectors.toList());

        return new Pair<>(srcViews, targetViews);
    }

    private AMapping runMapping(Map<String, WLSubgraphProcessor> srcMap, Map<String, WLSubgraphProcessor> targetMap, double threshold){

        Map<String, Multiset<String>> src = srcMap.entrySet().stream()
                                                .collect(
                                                        Collectors.toMap(
                                                                e -> e.getKey(),
                                                                e -> e.getValue().collect()
                                                        )
                                                );
        Map<String, Multiset<String>> target = targetMap.entrySet().stream()
                                                    .collect(
                                                            Collectors.toMap(
                                                                    e -> e.getKey(),
                                                                    e -> e.getValue().collect()
                                                            )
                                                    );

        return bagMapper.getMapping(src, target, threshold);
    }


    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 0;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 0;
    }

    @Override
    public String getName() {
        return "configurable_graph";
    }
}
