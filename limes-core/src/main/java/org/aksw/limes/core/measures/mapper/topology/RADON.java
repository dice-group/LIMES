package org.aksw.limes.core.measures.mapper.topology;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.util.LimesWktReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

/**
 *
 * @author kdressler
 */
public class RADON {

    public static class GridSizeHeuristics {

        public final static String AVG = "avg";
        public final static String MIN = "min";
        public final static String MAX = "max";
        public final static String MED = "median";
        public static boolean swap = false;

        public static double[] decideForTheta(GridSizeHeuristics s, GridSizeHeuristics t, String measure) {
            double[] stats;
            switch (measure) {
            case MAX:
                stats = new double[] { s.maxX, s.maxY, t.maxX, t.maxY };
                break;
            case AVG:
                stats = new double[] { s.avgX, s.avgY, t.avgX, t.avgY };
                break;
            case MED:
                stats = new double[] { s.medX, s.medY, t.medX, t.medY };
                break;
            case MIN:
            default:
                stats = new double[] { s.minX, s.minY, t.minX, t.minY };
            }
            double estAreaS = stats[0] * stats[1] * s.size;
            double estAreaT = stats[2] * stats[3] * t.size;
            // we want to swap towards the smallest area coverage to optimizethe
            // number of comparisons
            swap = estAreaS > estAreaT;
            return new double[] { (2.0d) / (stats[0] + stats[2]), (2.0d) / (stats[1] + stats[3]) };
        }

        private double size;
        private double minX;
        private double maxX;
        private double avgX;
        private double medX;
        private double minY;
        private double maxY;
        private double avgY;
        private double medY;

        public GridSizeHeuristics(Collection<Geometry> input) {
            double[] x = new double[input.size()];
            double[] y = new double[input.size()];
            int i = 0;
            for (Geometry geometry : input) {
                Envelope e = geometry.getEnvelopeInternal();
                y[i] = e.getHeight();
                x[i] = e.getWidth();
                i++;
            }
            this.size = input.size();
            Arrays.sort(x);
            this.minX = x[0];
            this.maxX = x[x.length - 1];
            this.avgX = Arrays.stream(x).average().getAsDouble();
            this.medX = x.length % 2 == 0 ? (x[x.length / 2 - 1] + x[x.length / 2]) / 2.0d : x[x.length / 2];
            Arrays.sort(y);
            this.minY = y[0];
            this.maxY = y[y.length - 1];
            this.avgY = Arrays.stream(y).average().getAsDouble();
            this.medY = y.length % 2 == 0 ? (y[y.length / 2 - 1] + y[y.length / 2]) / 2.0d : y[y.length / 2];
        }

        public double getSize() {
            return size;
        }

        public double getMinX() {
            return minX;
        }

        public double getMaxX() {
            return maxX;
        }

        public double getAvgX() {
            return avgX;
        }

        public double getMedX() {
            return medX;
        }

        public double getMinY() {
            return minY;
        }

        public double getMaxY() {
            return maxY;
        }

        public double getAvgY() {
            return avgY;
        }

        public double getMedY() {
            return medY;
        }

        public String toString() {
            DecimalFormat df = new DecimalFormat("0.0000");
            return "[MIN(" + df.format(minX) + ";" + df.format(minY) + ");MAX(" + df.format(maxX) + ";"
                    + df.format(maxY) + ";AVG(" + df.format(avgX) + ";" + df.format(avgY) + ");MED(" + df.format(medX)
                    + ";" + df.format(medY) + ")]";
        }

    }

    public static class MBBIndex {

        public int lat1, lat2, lon1, lon2;
        public Geometry polygon;
        private String uri;
        private String origin_uri;

        public MBBIndex(int lat1, int lon1, int lat2, int lon2, Geometry polygon, String uri) {
            this.lat1 = lat1;
            this.lat2 = lat2;
            this.lon1 = lon1;
            this.lon2 = lon2;
            this.polygon = polygon;
            this.uri = uri;
            this.origin_uri = uri;
        }

        public MBBIndex(int lat1, int lon1, int lat2, int lon2, Geometry polygon, String uri, String origin_uri) {
            this.lat1 = lat1;
            this.lat2 = lat2;
            this.lon1 = lon1;
            this.lon2 = lon2;
            this.polygon = polygon;
            this.uri = uri;
            this.origin_uri = origin_uri;
        }

        public boolean contains(MBBIndex i) {
            return this.lat1 <= i.lat1 && this.lon1 <= i.lon1 && this.lon2 >= i.lon2 && this.lat2 >= i.lat2;
        }

        public boolean covers(MBBIndex i) {
            return this.lat1 <= i.lat1 && this.lon1 <= i.lon1 && this.lon2 >= i.lon2 && this.lat2 >= i.lat2;
        }

        public boolean intersects(MBBIndex i) {
            return !this.disjoint(i);
        }

        public boolean disjoint(MBBIndex i) {
            return this.lat2 < i.lat1 || this.lat1 > i.lat2 || this.lon2 < i.lon1 || this.lon1 > i.lon2;
        }

        public boolean equals(Object o) {
            if (!(o instanceof MBBIndex)) {
                return false;
            }
            MBBIndex i = ((MBBIndex) o);
            return lat1 == i.lat1 && lat2 == i.lat2 && lon1 == i.lon1 && lon2 == i.lon2;
        }

    }

    public static class SquareIndex {

        public HashMap<Integer, HashMap<Integer, List<MBBIndex>>> map = new HashMap<>();

        public SquareIndex() {

        }

        public SquareIndex(int capacity) {
            this.map = new HashMap<>(capacity);
        }

        public void add(int i, int j, MBBIndex m) {
            if (!map.containsKey(i)) {
                map.put(i, new HashMap<>());
            }
            if (!map.get(i).containsKey(j)) {
                map.get(i).put(j, new ArrayList<>());
            }
            map.get(i).get(j).add(m);
        }

        public List<MBBIndex> getSquare(int i, int j) {
            if (!map.containsKey(i) || !map.get(i).containsKey(j))
                return null;
            else
                return map.get(i).get(j);
        }
    }

    public static class Matcher implements Runnable {

        public static int maxSize = 1000;
        private String relation;
        private final List<Map<String, Set<String>>> result;
        private List<MBBIndex> scheduled;

        public Matcher(String relation, List<Map<String, Set<String>>> result) {
            this.relation = relation;
            this.result = result;
            this.scheduled = new ArrayList<>();
        }

        @Override
        public void run() {
            Map<String, Set<String>> temp = new HashMap<>();
            for (int i = 0; i < scheduled.size(); i += 2) {
                MBBIndex s = scheduled.get(i);
                MBBIndex t = scheduled.get(i + 1);
                if (relate(s.polygon, t.polygon, relation)) {
                    if (!temp.containsKey(s.origin_uri)) {
                        temp.put(s.origin_uri, new HashSet<>());
                    }
                    temp.get(s.origin_uri).add(t.origin_uri);
                }
            }
            synchronized (result) {
                result.add(temp);
            }
        }

        public void schedule(MBBIndex s, MBBIndex t) {
            scheduled.add(s);
            scheduled.add(t);
        }

        public int size() {
            return scheduled.size();
        }

        private static Boolean relate(Geometry geometry1, Geometry geometry2, String relation) {
            switch (relation) {
            case EQUALS:
                return geometry1.equals(geometry2);
            case DISJOINT:
                return geometry1.disjoint(geometry2);
            case INTERSECTS:
                return geometry1.intersects(geometry2);
            case TOUCHES:
                return geometry1.touches(geometry2);
            case CROSSES:
                return geometry1.crosses(geometry2);
            case WITHIN:
                return geometry1.within(geometry2);
            case CONTAINS:
                return geometry1.contains(geometry2);
            case COVERS:
                return geometry1.covers(geometry2);
            case COVEREDBY:
                return geometry1.coveredBy(geometry2);
            case OVERLAPS:
                return geometry1.overlaps(geometry2);
            default:
                return geometry1.relate(geometry2, relation);
            }
        }
    }

    public static class Merger implements Runnable {

        private AMapping m;
        private List<Map<String, Set<String>>> localResults = new ArrayList<>();

        public Merger(List<Map<String, Set<String>>> results, AMapping m) {
            this.m = m;
            // copy over entries to local list
            synchronized (results) {
                for (Iterator<Map<String, Set<String>>> iterator = results.listIterator(); iterator.hasNext();) {
                    localResults.add(iterator.next());
                    iterator.remove();
                }
            }
        }

        @Override
        public void run() {
            // merge back to m
            for (Map<String, Set<String>> result : localResults) {
                for (String s : result.keySet()) {
                    for (String t : result.get(s)) {
                        if (GridSizeHeuristics.swap)
                            m.add(t, s, 1.0d);
                        else
                            m.add(s, t, 1.0d);
                    }
                }
            }
        }
    }

    public static final String EQUALS = "equals";
    public static final String DISJOINT = "disjoint";
    public static final String INTERSECTS = "intersects";
    public static final String TOUCHES = "touches";
    public static final String CROSSES = "crosses";
    public static final String WITHIN = "within";
    public static final String CONTAINS = "contains";
    public static final String OVERLAPS = "overlaps";
    public static final String COVERS = "covers";
    public static final String COVEREDBY = "coveredby";
    // best measure according to our evaluation in the RADON paper
    public static String heuristicStatMeasure = "avg";

    private static final Logger logger = LoggerFactory.getLogger(RADON.class);

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
        return getMapping(sourceMap, targetMap, relation);
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
        return getMapping(source, target, relation);
    }

    public static AMapping getMapping(Map<String, Geometry> sourceData, Map<String, Geometry> targetData,
            String relation) {
        double thetaX, thetaY;
        int numThreads = new Double(Math.ceil((double) Runtime.getRuntime().availableProcessors() / 2.0d)).intValue();
        // Relation thats actually used for computation.
        // Might differ from input relation when swapping occurs or the input
        // relation is 'disjoint'.
        String rel = relation;

        // When relation for AMapping M is 'disjoint' we compute AMapping M'
        // relation 'intersects'
        // and return M = (S x T) \ M'
        boolean disjointStrategy = rel.equals(DISJOINT);
        if (disjointStrategy)
            rel = INTERSECTS;

        GridSizeHeuristics heuristicsS = new GridSizeHeuristics(sourceData.values());
        GridSizeHeuristics heuristicsT = new GridSizeHeuristics(targetData.values());
        double[] theta = GridSizeHeuristics.decideForTheta(heuristicsS, heuristicsT, heuristicStatMeasure);
        thetaX = theta[0];
        thetaY = theta[1];
        // swap smaller dataset to source
        // if swap is necessary is decided in Stats.decideForTheta([...])!
        Map<String, Geometry> swap;
        boolean swapped = GridSizeHeuristics.swap;
        if (swapped) {
            swap = sourceData;
            sourceData = targetData;
            targetData = swap;
            switch (rel) {
            case WITHIN:
                rel = CONTAINS;
                break;
            case CONTAINS:
                rel = WITHIN;
                break;
            case COVERS:
                rel = COVEREDBY;
                break;
            case COVEREDBY:
                rel = COVERS;
                break;
            }
        }

        // set up indexes
        SquareIndex sourceIndex = index(sourceData, null, thetaX, thetaY);
        SquareIndex targetIndex = index(targetData, sourceIndex, thetaX, thetaY);

        // execute matching
        ExecutorService matchExec = Executors.newFixedThreadPool(numThreads);
        ExecutorService mergerExec = Executors.newFixedThreadPool(1);
        AMapping m = MappingFactory.createDefaultMapping();
        List<Map<String, Set<String>>> results = Collections.synchronizedList(new ArrayList<>());
        Map<String, Set<String>> computed = new HashMap<>();
        Matcher matcher = new Matcher(rel, results);

        for (Integer lat : sourceIndex.map.keySet()) {
            for (Integer lon : sourceIndex.map.get(lat).keySet()) {
                List<MBBIndex> source = sourceIndex.getSquare(lat, lon);
                List<MBBIndex> target = targetIndex.getSquare(lat, lon);
                if (target != null && target.size() > 0) {
                    for (MBBIndex a : source) {
                        if (!computed.containsKey(a.uri))
                            computed.put(a.uri, new HashSet<>());
                        for (MBBIndex b : target) {
                            if (!computed.get(a.uri).contains(b.uri)) {
                                computed.get(a.uri).add(b.uri);
                                boolean compute = (rel.equals(COVERS) && a.covers(b))
                                        || (rel.equals(COVEREDBY) && b.covers(a))
                                        || (rel.equals(CONTAINS) && a.contains(b))
                                        || (rel.equals(WITHIN) && b.contains(a)) || (rel.equals(EQUALS) && a.equals(b))
                                        || rel.equals(INTERSECTS) || rel.equals(CROSSES) || rel.equals(TOUCHES)
                                        || rel.equals(OVERLAPS);
                                if (compute) {
                                    if (numThreads == 1) {
                                        if (Matcher.relate(a.polygon, b.polygon, rel)) {
                                            if (swapped)
                                                m.add(b.origin_uri, a.origin_uri, 1.0);
                                            else
                                                m.add(a.origin_uri, b.origin_uri, 1.0);
                                        }
                                    } else {
                                        matcher.schedule(a, b);
                                        if (matcher.size() == Matcher.maxSize) {
                                            matchExec.execute(matcher);
                                            matcher = new Matcher(rel, results);
                                            if (results.size() > 0) {
                                                mergerExec.execute(new Merger(results, m));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (numThreads > 1) {
            if (matcher.size() > 0) {
                matchExec.execute(matcher);
            }
            matchExec.shutdown();
            while (!matchExec.isTerminated()) {
                try {
                    if (results.size() > 0) {
                        mergerExec.execute(new Merger(results, m));
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (results.size() > 0) {
                mergerExec.execute(new Merger(results, m));
            }
            mergerExec.shutdown();
            while (!mergerExec.isTerminated()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Compute M = (S x T) \ M' for disjoint relation
        if (disjointStrategy) {
            AMapping disjoint = MappingFactory.createDefaultMapping();
            for (String s : sourceData.keySet()) {
                for (String t : targetData.keySet()) {
                    if (swapped) {
                        if (!m.contains(t, s)) {
                            disjoint.add(t, s, 1.0d);
                        }
                    } else {
                        if (!m.contains(s, t)) {
                            disjoint.add(s, t, 1.0d);
                        }
                    }
                }
            }
            m = disjoint;
        }
        return m;
    }

    public static SquareIndex index(Map<String, Geometry> input, SquareIndex extIndex, double thetaX, double thetaY) {
        SquareIndex result = new SquareIndex();

        for (String p : input.keySet()) {
            Geometry g = input.get(p);
            Envelope envelope = g.getEnvelopeInternal();

            int minLatIndex = (int) Math.floor(envelope.getMinY() * thetaY);
            int maxLatIndex = (int) Math.ceil(envelope.getMaxY() * thetaY);
            int minLongIndex = (int) Math.floor(envelope.getMinX() * thetaX);
            int maxLongIndex = (int) Math.ceil(envelope.getMaxX() * thetaX);

            // Check for passing over 180th meridian. In case its shorter to
            // pass over it, we assume that is what is
            // meant by the user and we split the geometry into one part east
            // and one part west of 180th meridian.

            if (minLongIndex < (int) Math.floor(-90d * thetaX) && maxLongIndex > (int) Math.ceil(90d * thetaX)) {
                MBBIndex westernPart = new MBBIndex(minLatIndex, (int) Math.floor(-180d * thetaX), maxLatIndex,
                        minLongIndex, g, p + "<}W", p);
                addToIndex(westernPart, result, extIndex);
                MBBIndex easternPart = new MBBIndex(minLatIndex, maxLongIndex, maxLatIndex,
                        (int) Math.ceil(180 * thetaX), g, p + "<}E", p);
                addToIndex(easternPart, result, extIndex);
            } else {
                MBBIndex mbbIndex = new MBBIndex(minLatIndex, minLongIndex, maxLatIndex, maxLongIndex, g, p);
                addToIndex(mbbIndex, result, extIndex);
            }

        }
        return result;
    }

    private static void addToIndex(MBBIndex mbbIndex, SquareIndex result, SquareIndex extIndex) {
        if (extIndex == null) {
            for (int latIndex = mbbIndex.lat1; latIndex <= mbbIndex.lat2; latIndex++) {
                for (int longIndex = mbbIndex.lon1; longIndex <= mbbIndex.lon2; longIndex++) {
                    result.add(latIndex, longIndex, mbbIndex);
                }
            }
        } else {
            for (int latIndex = mbbIndex.lat1; latIndex <= mbbIndex.lat2; latIndex++) {
                for (int longIndex = mbbIndex.lon1; longIndex <= mbbIndex.lon2; longIndex++) {
                    if (extIndex.getSquare(latIndex, longIndex) != null)
                        result.add(latIndex, longIndex, mbbIndex);
                }
            }
        }
    }
}
