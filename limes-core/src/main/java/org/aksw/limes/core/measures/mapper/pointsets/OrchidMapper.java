package org.aksw.limes.core.measures.mapper.pointsets;

import com.google.common.collect.Lists;
import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.pointsets.IPointsetsMeasure;
import org.aksw.limes.core.util.LimesWktReader;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class OrchidMapper extends AMapper {

    private static final Logger logger = LoggerFactory.getLogger(OrchidMapper.class);

    IPointsetsMeasure m = null;

    public static List<Point> getPoints(String wktString) {
        LimesWktReader wktReader = new LimesWktReader();
        List<Point> points = new ArrayList<>();
        try {
            Geometry geometry = wktReader.read(wktString);
            for (Coordinate coordinate : geometry.getCoordinates()) {
                points.add(new Point("", Double.isNaN(coordinate.getZ()) ?
                        Lists.newArrayList(coordinate.getX(), coordinate.getY()) :
                        Lists.newArrayList(coordinate.getX(), coordinate.getY(), coordinate.getZ())));
            }
        } catch (ParseException e) {
            logger.warn("Skipping malformed geometry \"" + wktString + "\"...");
        }
        return points;
    }

    /**
     * Computes a polygon out of a WKT string
     *
     * @param wktString
     *            An WKT string
     * @return A polygon
     */
    public static Polygon getPolygon(String wktString) {
        Polygon p = new Polygon("");
        List<Point> points = getPoints(wktString);
        for (Point point : points) {
            p.add(point);
        }
        return p;
    }

    /**
     * Computes a mapping using the setMeasure distance
     *
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceVar
     *            Variable for the source dataset
     * @param targetVar
     *            Variable for the target dataset
     * @param expression
     *            Expression to process. Leads to termination if the expression
     *            is not atomic
     * @param threshold
     *            Similarity threshold. Is transformed internally into a
     *            distance threshold theta with threshold = 1/(1+theta)
     * @return A mapping which contains uris whose polygons are such that their
     *         distance is below the set threshold
     */
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {

        List<String> properties = PropertyFetcher.getProperties(expression, threshold);

        // get sets of polygons from properties
        Set<Polygon> sourcePolygons = getPolygons(source, properties.get(0));
        Set<Polygon> targetPolygons = getPolygons(target, properties.get(1));
        float theta = (1 / (float) threshold) - 1;
        GeoHR3 orchid = new GeoHR3(theta, GeoHR3.DEFAULT_GRANULARITY, MeasureFactory.getMeasureType(expression));
        return orchid.run(sourcePolygons, targetPolygons);
    }

    /**
     * Computes polygons out of strings in the WKT format. Currently works for
     * LINESTRING, POINT, POLYGON
     *
     * @param c
     *            Cache from which the data is to be fetched
     * @param property
     *            Property to use
     * @return Set of polygons. Each polygon contains the uri to which it
     *         matches
     */
    public Set<Polygon> getPolygons(ACache c, String property) {
        Set<Polygon> polygons = new HashSet<>();
        for (String uri : c.getAllUris()) {
            Set<String> values = c.getInstance(uri).getProperty(property);
            if (values.size() > 0) {
                String wkt = values.iterator().next();
                polygons.add(new Polygon(uri, getPoints(wkt)));
            }
        }
        return polygons;
    }

    public String getName() {
        return "Orchid";
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
        if (language.equals(Language.DE)) {
            // error = 667.22
            return 16.27 + 5.1 * sourceSize + 4.9 * targetSize - 23.44 * threshold;
        } else {
            // error = 5.45
            return 22 + 0.005 * (sourceSize + targetSize) - 56.4 * threshold;
        }
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
        if (language.equals(Language.DE)) {
            // error = 667.22
            return 2333 + 0.14 * sourceSize + 0.14 * targetSize - 3905 * threshold;
        } else {
            // error = 5.45
            return 0.006 * (sourceSize + targetSize) - 134.2 * threshold;
        }
    }

}
