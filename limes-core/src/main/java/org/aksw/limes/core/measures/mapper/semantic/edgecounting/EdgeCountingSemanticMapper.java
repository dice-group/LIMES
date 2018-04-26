package org.aksw.limes.core.measures.mapper.semantic.edgecounting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.AEdgeCountingSemanticMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.SemanticFactory;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.SemanticType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

public class EdgeCountingSemanticMapper extends AMapper {
    static Logger logger = LoggerFactory.getLogger(EdgeCountingSemanticMapper.class);

    public class SimilarityThread implements Callable<Pair<Pair<String, String>, Double>> {
        AEdgeCountingSemanticMeasure measure = null;
        Instance instance1 = null;
        Instance instance2 = null;
        String property1 = null;
        String property2 = null;

        public SimilarityThread(String expression, Instance s, Instance t, String prop1, String prop2) {
            SemanticType type = SemanticFactory.getMeasureType(expression);
            measure = (AEdgeCountingSemanticMeasure) SemanticFactory.createMeasure(type);
            instance1 = s;
            instance2 = t;
            property1 = prop1;
            property2 = prop2;
        }

        @Override
        public Pair<Pair<String, String>, Double> call() throws Exception {
            double sim = measure.getSimilarity(instance1, instance2, property1, property2);
            Pair<String, String> p1 = new ImmutablePair<String, String>(instance1.getUri(), instance2.getUri());
            Pair<Pair<String, String>, Double> p2 = new ImmutablePair<Pair<String, String>, Double>(p1, sim);
            return p2;
        }

    }

    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        if (threshold <= 0) {
            throw new InvalidThresholdException(threshold);
        }
        List<String> properties = PropertyFetcher.getProperties(expression, threshold);
        // if no properties then terminate
        if (properties.get(0) == null || properties.get(1) == null) {
            logger.error(MarkerFactory.getMarker("FATAL"), "Property values could not be read. Exiting");
            throw new RuntimeException();
        }
        // if expression not atomic, terminate
        Parser p = new Parser(expression, threshold);
        if (!p.isAtomic()) {
            logger.error(MarkerFactory.getMarker("FATAL"), "Mappers can only deal with atomic expression");
            logger.error(MarkerFactory.getMarker("FATAL"),
                    "Expression " + expression + " was given to a mapper to process");
        }

        AMapping m = MappingFactory.createDefaultMapping();

        int poolSize = source.getAllInstances().size() * target.getAllInstances().size();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        List<Future<Pair<Pair<String, String>, Double>>> list = new ArrayList<Future<Pair<Pair<String, String>, Double>>>();
        for (Instance sourceInstance : source.getAllInstances()) {
            for (Instance targetInstance : target.getAllInstances()) {

                SimilarityThread thread = new SimilarityThread(p.getOperator(), sourceInstance, targetInstance,
                        properties.get(0), properties.get(1));

                Future<Pair<Pair<String, String>, Double>> similarity = executor.submit(thread);
                list.add(similarity);
            }
        }
        for (Future<Pair<Pair<String, String>, Double>> entry : list) {
            try {
                Pair<Pair<String, String>, Double> pair = entry.get();
                if (pair != null) {
                    String uri1 = pair.getLeft().getLeft();
                    String uri2 = pair.getLeft().getRight();
                    Double sim = pair.getRight();

                    if (sim >= threshold) {
                        m.add(uri1, uri2, sim);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        executor.shutdown();
        return m;
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
        return "Semantic Mapper";
    }

}
