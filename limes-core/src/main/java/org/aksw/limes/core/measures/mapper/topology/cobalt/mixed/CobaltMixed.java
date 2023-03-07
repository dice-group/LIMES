package org.aksw.limes.core.measures.mapper.topology.cobalt.mixed;

import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.measures.mapper.topology.cobalt.CobaltMeasures;
import org.aksw.limes.core.measures.mapper.topology.cobalt.RTree;
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

import static org.aksw.limes.core.measures.mapper.topology.cobalt.CobaltMeasures.*;

/**
 * Some parts of this class are taken from RADON / kdressler
 *
 * @see org.aksw.limes.core.measures.mapper.topology.RADON
 */
public class CobaltMixed {

    private static final Logger logger = LoggerFactory.getLogger(CobaltMixed.class);

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
                                              String expression, double threshold, String relation) {
        if (threshold <= 0) {
            throw new InvalidThresholdException(threshold);
        }
        List<String> properties = PropertyFetcher.getProperties(expression, threshold);
        Map<String, Geometry> sourceMap = getGeometryMapFromCache(source, properties.get(0));
        Map<String, Geometry> targetMap = getGeometryMapFromCache(target, properties.get(1));
        return getMappingEnvelope(toEnvelopeMap(sourceMap), toEnvelopeMap(targetMap), relation);
    }

    public static AMapping getMapping(Set<Polygon> sourceData, Set<Polygon> targetData, String relation) {
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
        return getMappingEnvelope(toEnvelopeMap(source), toEnvelopeMap(target), relation);
    }

    public static AMapping getMapping(Map<String, Geometry> sourceData, Map<String, Geometry> targetData, String relation) {
        return getMappingEnvelope(toEnvelopeMap(sourceData), toEnvelopeMap(targetData), relation);
    }

    public static Map<String, Envelope> toEnvelopeMap(Map<String, Geometry> data){
        Map<String, Envelope> envelopeMap = new HashMap<>();
        data.forEach((s, geometry) -> envelopeMap.put(s, geometry.getEnvelopeInternal()));
        return envelopeMap;
    }

    public static AMapping getMappingEnvelope(Map<String, Envelope> sourceData, Map<String, Envelope> targetData, String relation) {
        int numThreads = 1;
        List<RTree.Entry> entries = new ArrayList<>(sourceData.size());
        sourceData.forEach((s, geometry) -> {
            entries.add(new RTree.Entry(s, geometry, null));
        });

        boolean disjointStrategy = relation.equals(DISJOINT);
        if (disjointStrategy) {
            relation = INTERSECTS;
        }
        RTree rTree = RTree.buildSTR(entries);

        AMapping m = MappingFactory.createDefaultMapping();

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
                                        return relate(abb, bbb, finalRelation);
                                    }
                            ).forEach(x -> value.add(x.getUri()));
                });
            } else {
                String finalRelation = relation;
                AMapping finalM = m;
                List<RTree.Entry> search = rTree.search(envelope);

                search.stream()
                        .filter(x -> {
                                    Envelope abb = x.getEnvelope();
                                    Envelope bbb = envelope;
                                    return relate(abb, bbb, finalRelation);
                                }
                        ).forEach(x -> finalM.add(x.getUri(), uri, 1.0));
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

        if (disjointStrategy) {
            AMapping disjoint = MappingFactory.createDefaultMapping();
            for (String s : sourceData.keySet()) {
                for (String t : targetData.keySet()) {
                    if (!m.contains(s, t)) {
                        disjoint.add(s, t, 1.0d);
                    }
                }
            }
            m = disjoint;
        }
        return m;
    }


    public static boolean relate(Envelope mbbA, Envelope mbbB, String relation) {
        double X = CobaltMeasures.fM(mbbA, mbbB);
        double Y = CobaltMeasures.fM(mbbB, mbbA);

        return relate(X, Y, relation);
    }

    public static boolean relate(double X, double Y, String relation) {
        switch (relation) {
            case EQUALS:
                if (X == -1 && Y == -1) {
                    return true;
                } else {
                    return false;
                }
            case DISJOINT:
                if (1 < X && 1 < Y) {
                    return true;
                } else {
                    return false;
                }
            case INTERSECTS:
                if (relate(X, Y, EQUALS) || relate(X, Y, TOUCHES) || relate(X, Y, CONTAINS)
                        || relate(X, Y, COVERS) || relate(X, Y, COVEREDBY) || relate(X, Y, WITHIN)
                        || relate(X, Y, OVERLAPS)
                ) {
                    return true;
                } else {
                    return false;
                }
            case TOUCHES: //meet
                if (X == 1 && Y == 1) {
                    return true;
                } else {
                    return false;
                }
            //DE-9IM does not care if the boundaries of two polygons have common points or not
            case CONTAINS:
            case COVERS:
                return (Math.abs(X) < 1 && Y == -1) || (Math.abs(X) < 1 && Y < -1) || relate(X, Y, EQUALS);

            //DE-9IM does not care if the boundaries of two polygons have common points or not
            case WITHIN:
            case COVEREDBY:
                return (X < -1 && Math.abs(Y) < 1) || (X == -1 && Math.abs(Y) < 1) || relate(X, Y, EQUALS);
            case OVERLAPS:
                if (Math.abs(X) < 1 && Math.abs(Y) < 1) {
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }


}
