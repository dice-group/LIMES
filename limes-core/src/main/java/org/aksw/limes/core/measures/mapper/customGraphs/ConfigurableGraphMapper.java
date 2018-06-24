package org.aksw.limes.core.measures.mapper.customGraphs;

import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import javafx.util.Pair;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontend;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontendImpl;
import org.aksw.jena_sparql_api.cache.file.CacheBackendFile;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.describe.*;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.measures.mapper.bags.IBagMapper;
import org.aksw.limes.core.measures.measure.customGraphs.description.IDescriptionGraphView;
import org.aksw.limes.core.measures.measure.customGraphs.description.impl.DescriptionGraphFactory;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.*;
import org.aksw.limes.core.measures.measure.customGraphs.subgraphs.WLSubgraphCollector;
import org.aksw.limes.core.measures.measure.customGraphs.subgraphs.WLSubgraphProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConfigurableGraphMapper implements IMapper {

    private static final String CACHE_FOLDER = "cache/";

    static Log logger = LogFactory.getLog(ConfigurableGraphMapper.class);

    private int descriptionRecursionDepth;
    private int wlIterationDepth;
    private IGraphRelabel relabel;
    private IBagMapper bagMapper;

    public ConfigurableGraphMapper(int descriptionRecursionDepth, int wlIterationDepth, IGraphRelabel relabel, IBagMapper bagMapper) {
        this.descriptionRecursionDepth = descriptionRecursionDepth;
        this.wlIterationDepth = wlIterationDepth;
        this.relabel = relabel;
        this.bagMapper = bagMapper;
    }

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

        MapAndLog<Integer, Integer> log = new MapAndLog(x -> x, x -> String.format("Finished iteration %d", x),
                this.getClass().getCanonicalName(), wlIterationDepth+1);


        for(int i = 0; i <= wlIterationDepth; i++) {

            AMapping mapping;

            if(i == 0)
                mapping = initMapping(srcMap, targetMap, epsilon);
            else
                mapping = runMapping(srcMap, targetMap, epsilon);

            log.apply(i);

            AMapping newResult = MappingFactory.createDefaultMapping();
            Map<String, WLSubgraphProcessor> srcNextGen = new HashMap<>();
            Map<String, WLSubgraphProcessor> targetNextGen = new HashMap<>();

            for(Map.Entry<String, HashMap<String, Double>> e: mapping.getMap().entrySet()){
                for(Map.Entry<String, Double> t: e.getValue().entrySet()){

                    double sim = ((i>0?i*result.getConfidence(e.getKey(), t.getKey()):0.0) + t.getValue())/(i+1);
                    if(sim >= threshold){
                        srcNextGen.putIfAbsent(e.getKey(), srcMap.get(e.getKey()).iterate());
                        targetNextGen.putIfAbsent(t.getKey(), targetMap.get(t.getKey()).iterate());
                        newResult.add(e.getKey(), t.getKey(), sim);

                        epsilon = Math.min((i+2)*threshold - (i+1) * sim, epsilon);
                        epsilon = Math.max(epsilon, 0.01);

                    }

                }
            }

            result = newResult;
            srcMap = srcNextGen;
            targetMap = targetNextGen;

            if(result.size() == 0)
                break;

        }

        return result;
    }

    private IDescriptionGraphView prepareGraph(IResourceDescriptor d, Consumer<ILabel> consumer){
        if(consumer == null){
            return DescriptionGraphFactory.lazy(d).build();
        }else {
            return DescriptionGraphFactory.eager(d).listen(consumer).build();
        }
    }

    private IDescriptor initCachedDescriptor(KBInfo info, Set<String> resources, int recursion){
        CacheFrontend frontend = new CacheFrontendImpl(
                new CacheBackendFile(
                        new File(CACHE_FOLDER), 3600000
                )
        );
        IDescriptor backend =  new Descriptor(info, new DefaultConnetionConfig(), frontend);
        return new BunchDescriptor(info, backend, resources, recursion);
    }

    private List<IResourceDescriptor> listResources(KBInfo info, List<String> uris){
        return initCachedDescriptor(info, new HashSet<>(uris), descriptionRecursionDepth)
                .describeAll(uris, descriptionRecursionDepth);
    }

    private Pair<List<WLSubgraphCollector>, List<WLSubgraphCollector>> initGraphViews(ACache source, ACache target){
        List<IResourceDescriptor> srcResources = listResources(source.getKbInfo(), source.getAllUris());

        List<IResourceDescriptor> targetResources = listResources(target.getKbInfo(), target.getAllUris());

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
                                                .map(new MapAndLog<>(
                                                        x -> prepareGraph(x, finalSrcConsumer),
                                                        x -> String.format("Load graph for %s", x.getURI()),
                                                        "Source Resource Loader",
                                                        srcResources.size(), 40
                                                ))
                                                .map(x -> new RelablingWrapper(x, relabel))
                                                .map(x -> new WLSubgraphCollector(x))
                                                .collect(Collectors.toList());
        List<WLSubgraphCollector> targetViews = targetResources.stream()
                                                    .map(new MapAndLog<>(
                                                            x -> prepareGraph(x, finalTargetConsumer),
                                                            x -> String.format("Load graph for %s", x.getURI()),
                                                            "Target Resource Loader",
                                                            targetResources.size(),
                                                            40
                                                    ))
                                                    .map(x -> new RelablingWrapper(x, relabel))
                                                    .map(x -> new WLSubgraphCollector(x))
                                                    .collect(Collectors.toList());

        return new Pair<>(srcViews, targetViews);
    }

    private AMapping initMapping(Map<String, WLSubgraphProcessor> srcMap, Map<String, WLSubgraphProcessor> targetMap, double threshold){

        Map<String, Multiset<String>> src = srcMap.entrySet().stream()
                                                .map(new MapAndLog<>(
                                                        e -> new Pair<>(e.getKey(), e.getValue().collect()),
                                                        e -> String.format("Collecting source subgraphs for %s", e.getKey()),
                                                        "Source loading",
                                                        srcMap.size(),
                                                        40
                                                ))
                                                .collect(
                                                        Collectors.toMap(
                                                                e -> e.getKey(),
                                                                e -> e.getValue()
                                                        )
                                                );
        Map<String, Multiset<String>> target = targetMap.entrySet().stream()
                                                    .map(new MapAndLog<>(
                                                            e -> new Pair<>(e.getKey(), e.getValue().collect()),
                                                            e -> String.format("Collecting target subgraphs for %s", e.getKey()),
                                                            "Target loading",
                                                            targetMap.size(),
                                                            40
                                                    ))
                                                    .collect(
                                                            Collectors.toMap(
                                                                    e -> e.getKey(),
                                                                    e -> e.getValue()
                                                            )
                                                    );

        return bagMapper.getMapping(src, target, threshold);
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
        return 1000d;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public String getName() {
        return "configurable_graph";
    }
}
