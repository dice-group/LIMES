package org.aksw.limes.core.measures.mapper.topology.cobalt;

import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.measures.mapper.topology.cobalt.matcher.ICobaltMatcher;
import org.aksw.limes.core.measures.mapper.topology.cobalt.splitting.CobaltSplitMatcher;
import org.aksw.limes.core.measures.mapper.topology.cobalt.splitting.FittingSplitter;
import org.aksw.limes.core.util.LimesWktReader;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.aksw.limes.core.measures.mapper.topology.cobalt.CobaltMeasures.*;

/**
 * Some parts of this class are taken from RADON / kdressler
 *
 * @see org.aksw.limes.core.measures.mapper.topology.RADON
 */
public class Cobalt {

    private static final Logger logger = LoggerFactory.getLogger(Cobalt.class);

    public static Map<String, Geometry> getGeometryMapFromCache(ACache c, String property) {
        LimesWktReader wktReader = new LimesWktReader();
        Map<String, Geometry> gMap = new HashMap<>();
        for (String uri : c.getAllUris()) {
            Set<String> values = c.getInstance(uri).getProperty(property);
            if (values.size() > 0) {
                String wkt = values.iterator().next();
                try {
                    gMap.put(uri, wktReader.read(wkt));
                } catch (ParseException e) {
                    logger.warn("Skipping malformed geometry at " + uri + "...");
                }
            }
        }
        return gMap;
    }

    public static AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar,
                                      String expression, double threshold, String relation, ICobaltMatcher matcher) {
        return getMapping(source, target, sourceVar, targetVar, expression, threshold, relation, matcher, 0);
    }
        public static AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar,
                                      String expression, double threshold, String relation, ICobaltMatcher matcher, int splits) {
        if (threshold <= 0) {
            throw new InvalidThresholdException(threshold);
        }
        List<String> properties = PropertyFetcher.getProperties(expression, threshold);
        Map<String, Geometry> sourceMap = getGeometryMapFromCache(source, properties.get(0));
        Map<String, Geometry> targetMap = getGeometryMapFromCache(target, properties.get(1));
        if(splits > 0){
            return getMappingSplits(sourceMap, targetMap, relation, matcher, splits);
        }
        return getMappingEnvelope(toEnvelopeMap(sourceMap), toEnvelopeMap(targetMap), relation, matcher);
    }
    public static AMapping getMapping(Set<Polygon> sourceData, Set<Polygon> targetData, String relation, ICobaltMatcher matcher) {
        return getMapping(sourceData, targetData, relation, matcher, 0);
    }

    public static AMapping getMapping(Set<Polygon> sourceData, Set<Polygon> targetData, String relation, ICobaltMatcher matcher, int splits) {
        Map<String, Geometry> source, target;
        source = new HashMap<>();
        target = new HashMap<>();
        for (Polygon polygon : sourceData) {
            try {
                source.put(polygon.uri, polygon.getGeometry());
            } catch (ParseException e) {
                logger.warn("Skipping malformed geometry at " + polygon.uri + "...");
            }
        }
        for (Polygon polygon : targetData) {
            try {
                target.put(polygon.uri, polygon.getGeometry());
            } catch (ParseException e) {
                logger.warn("Skipping malformed geometry at " + polygon.uri + "...");
            }
        }
        if(splits > 0){
            return getMappingSplits(source, target, relation, matcher, splits);
        }
        return getMappingEnvelope(toEnvelopeMap(source), toEnvelopeMap(target), relation, matcher);
    }
    public static AMapping getMapping(Map<String, Geometry> sourceData, Map<String, Geometry> targetData, String relation, ICobaltMatcher matcher) {
        return getMapping(sourceData, targetData, relation, matcher, 0);
    }
    public static AMapping getMapping(Map<String, Geometry> sourceData, Map<String, Geometry> targetData, String relation, ICobaltMatcher matcher, int splits) {
        if(splits > 0){
            return getMappingSplits(sourceData, targetData, relation, matcher, splits);
        }
        return getMappingEnvelope(toEnvelopeMap(sourceData), toEnvelopeMap(targetData), relation, matcher);
    }

    public static Map<String, Envelope> toEnvelopeMap(Map<String, Geometry> data) {
        Map<String, Envelope> envelopeMap = new HashMap<>();
        data.forEach((s, geometry) -> envelopeMap.put(s, geometry.getEnvelopeInternal()));
        return envelopeMap;
    }

    private static AMapping getMappingEnvelope(Map<String, Envelope> sourceData, Map<String, Envelope> targetData, String relation, ICobaltMatcher matcher) {
        int numThreads = 1;
        List<RTree.Entry> entries = new ArrayList<>(sourceData.size());
        sourceData.forEach((s, geometry) -> {
            entries.add(new RTree.Entry(s, geometry, null));
        });

        AMapping m = MappingFactory.createDefaultMapping();
        RTree rTree = RTree.buildSTR(entries);

        ExecutorService exec = Executors.newFixedThreadPool(numThreads);
        Map<String, Set<String>> results = new HashMap<>(); //Target -> Source Mappings

        for (Map.Entry<String, Envelope> entry : targetData.entrySet()) {
            String uri = entry.getKey();
            Envelope envelope = entry.getValue();

            if (numThreads > 1) {
                HashSet<String> value = new HashSet<>();
                results.put(uri, value);
                String finalRelation = relation;

                exec.submit(() -> {
                    List<RTree.Entry> search = rTree.search(envelope);
                    search.stream()
                            .filter(x -> {
                                        Envelope abb = x.getEnvelope();
                                        Envelope bbb = envelope;
                                        return matcher.relate(abb, bbb, finalRelation);
                                    }
                            ).forEach(x -> value.add(x.getUri()));
                    if (relation.equals(DISJOINT)) {
                        value.addAll(rTree.searchExcept(envelope).stream().map(RTree.Entry::getUri).collect(Collectors.toList()));
                    }
                });
            } else {
                String finalRelation = relation;
                AMapping finalM = m;
                List<RTree.Entry> search = rTree.search(envelope);

                search.stream()
                        .filter(x -> {
                                    Envelope abb = x.getEnvelope();
                                    Envelope bbb = envelope;
                                    return matcher.relate(abb, bbb, finalRelation);
                                }
                        ).forEach(x -> finalM.add(x.getUri(), uri, 1.0));
                if (relation.equals(DISJOINT)) {
                    rTree.searchExcept(envelope).stream().map(RTree.Entry::getUri).forEach(sourceUri -> finalM.add(sourceUri, uri, 1.0));
                }
            }
        }
        if (numThreads > 1) {
            exec.shutdown();
            try {
                exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (Map.Entry<String, Set<String>> entry : results.entrySet()) {
                String t = entry.getKey();
                for (String s : entry.getValue()) {
                    m.add(s, t, 1.0);
                }
            }
        }

        return m;
    }

    private static AMapping getMappingSplits(Map<String, Geometry> sourceData, Map<String, Geometry> targetData, String relation, ICobaltMatcher matcher, int splits) {
        int numThreads = 1;
        CobaltSplitMatcher splitMatcher = new CobaltSplitMatcher(splits, new FittingSplitter(), matcher);
        List<RTree.Entry> entries = new ArrayList<>(sourceData.size());
        sourceData.forEach((s, geometry) -> {
            entries.add(new RTree.Entry(s, geometry.getEnvelopeInternal(), geometry));
        });

        AMapping m = MappingFactory.createDefaultMapping();
        RTree rTree = RTree.buildSTR(entries);

        ExecutorService exec = Executors.newFixedThreadPool(numThreads);
        Map<String, Set<String>> results = new HashMap<>(); //Target -> Source Mappings

        for (Map.Entry<String, Geometry> entry : targetData.entrySet()) {
            String uri = entry.getKey();
            Geometry geoEntry = entry.getValue();
            Envelope envelope = geoEntry.getEnvelopeInternal();

            if (numThreads > 1) {
                HashSet<String> value = new HashSet<>();
                results.put(uri, value);
                String finalRelation = relation;

                exec.submit(() -> {
                    List<RTree.Entry> search = rTree.search(envelope);
                    search.stream()
                            .filter(x -> {
                                        Envelope abb = x.getEnvelope();
                                        Envelope bbb = envelope;
                                        return splitMatcher.relate(x.getUri(), x.getGeometry(), uri, geoEntry, finalRelation);
                                    }
                            ).forEach(x -> value.add(x.getUri()));
                    if (relation.equals(DISJOINT)) {
                        value.addAll(rTree.searchExcept(envelope).stream().map(RTree.Entry::getUri).collect(Collectors.toList()));
                    }
                });
            } else {
                String finalRelation = relation;
                AMapping finalM = m;
                List<RTree.Entry> search = rTree.search(envelope);

                search.stream()
                        .filter(x -> {
                                    Envelope abb = x.getEnvelope();
                                    Envelope bbb = envelope;
                                    return splitMatcher.relate(x.getUri(), x.getGeometry(), uri, geoEntry, finalRelation);
                                }
                        ).forEach(x -> finalM.add(x.getUri(), uri, 1.0));
                if (relation.equals(DISJOINT)) {
                    rTree.searchExcept(envelope).stream().map(RTree.Entry::getUri).forEach(sourceUri -> finalM.add(sourceUri, uri, 1.0));
                }
            }
        }
        if (numThreads > 1) {
            exec.shutdown();
            try {
                exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (Map.Entry<String, Set<String>> entry : results.entrySet()) {
                String t = entry.getKey();
                for (String s : entry.getValue()) {
                    m.add(s, t, 1.0);
                }
            }
        }

        return m;
    }

}
