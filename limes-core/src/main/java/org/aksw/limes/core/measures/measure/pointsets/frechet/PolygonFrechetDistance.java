/**
 * 
 */
package org.aksw.limes.core.measures.measure.pointsets.frechet;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.OrthodromicDistance;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.util.GeometricShapeFactory;

/**
 * @author sherif
 *
 */
public class PolygonFrechetDistance {
    Polygon poly1, poly2;
    public double[][] a, b, c, d;
    static GeometricShapeFactory gsf = new GeometricShapeFactory();
    static GeometryFactory gf = new GeometryFactory();
    static double delta = 0.01;

    public PolygonFrechetDistance(Polygon p1, Polygon p2) {
	poly1 = p1;
	poly2 = p2;
	a = new double[poly1.points.size()][poly2.points.size()];
	b = new double[poly1.points.size()][poly2.points.size()];
	c = new double[poly1.points.size()][poly2.points.size()];
	d = new double[poly1.points.size()][poly2.points.size()];
    }

    public double computeFrechetDistance() {
	// case one point polygon
	if (poly1.points.size() == 1) {
	    return getFrechetPointToPolygonDistance(poly1.points.get(0), poly2);
	}
	if (poly2.points.size() == 1) {
	    return getFrechetPointToPolygonDistance(poly2.points.get(0), poly1);
	}

	// case polygon of more than one point
	Double[] cv = computeCriticalValues();
	Arrays.sort(cv);
	int index = binarySearch(cv);
	return cv[index].doubleValue();
    }

    public double getFrechetPointToPolygonDistance(Point p, Polygon poly) {
	ArrayList<Double> list = new ArrayList<Double>();

	// (1) distances between the point and all the polygon points
	for (Point q : poly.points) {
	    list.add(OrthodromicDistance.getDistanceInDegrees(p, q));
	}

	// (2) distances between the point and all the polygon edges
	for (int i = 0; i < poly.points.size() - 1; i++) {
	    double d = (double) Line2D.ptSegDist( // TODO alter to (double)
						  // OrthodromicDistance.getPoint2LineDistanceInDegrees(a,b)
		    poly.points.get(i).coordinates.get(0), poly.points.get(i).coordinates.get(1),
		    poly.points.get(i + 1).coordinates.get(0), poly.points.get(i + 1).coordinates.get(1),
		    p.coordinates.get(0), p.coordinates.get(1));
	    list.add(d);
	}

	// (3) return the minimum distance of the list
	double min;
	min = Double.MAX_VALUE;
	for (double d : list) {
	    if (d < min)
		min = d;
	}
	return min;
    }

    public Double[] computeCriticalValues() {
	ArrayList<Double> list = new ArrayList<Double>();

	// (1) distances between starting and ending points
	list.add((double) OrthodromicDistance.getDistanceInDegrees(poly1.points.get(0), poly2.points.get(0)));
	list.add((double) OrthodromicDistance.getDistanceInDegrees(poly1.points.get(poly1.points.size() - 1),
		poly2.points.get(poly2.points.size() - 1)));

	// (2) distances between vertices of one polygon and edges of the other
	// polygon
	for (int i = 0; i < poly1.points.size(); i++) {
	    for (int j = 0; j < poly2.points.size() - 1; j++) {
		double d = Line2D.ptSegDist( // TODO alter to (double)
					     // OrthodromicDistance.getPoint2LineDistanceInDegrees(a,b)
			poly2.points.get(j).coordinates.get(0), poly2.points.get(j).coordinates.get(1),
			poly2.points.get(j + 1).coordinates.get(0), poly2.points.get(j + 1).coordinates.get(1),
			poly1.points.get(i).coordinates.get(0), poly1.points.get(i).coordinates.get(1));
		list.add(d);
	    }
	}

	for (int j = 0; j < poly2.points.size(); j++) {
	    for (int i = 0; i < poly1.points.size() - 1; i++) {
		double d = Line2D.ptSegDist(poly1.points.get(i).coordinates.get(0),
			poly1.points.get(i).coordinates.get(1), poly1.points.get(i + 1).coordinates.get(0),
			poly1.points.get(i + 1).coordinates.get(1), poly2.points.get(j).coordinates.get(0),
			poly2.points.get(j).coordinates.get(1));
		list.add(d);
	    }
	}

	// convert into coordinate array
	Coordinate[] poly1Curve = new Coordinate[poly1.points.size()];
	Coordinate[] poly2Curve = new Coordinate[poly2.points.size()];
	for (int i = 0; i < poly1.points.size(); i++) {
	    poly1Curve[i] = new Coordinate(poly1.points.get(i).coordinates.get(0),
		    poly1.points.get(i).coordinates.get(1));
	}
	for (int i = 0; i < poly2.points.size(); i++) {
	    poly2Curve[i] = new Coordinate(poly2.points.get(i).coordinates.get(0),
		    poly2.points.get(i).coordinates.get(1));
	}

	// (3) common distance of two vertices of one polygon to the
	// intersection
	// point of their bisector with some edge of the other
	LineString ls;
	LineString temp;
	LineSegment lseg;
	Coordinate c1, midPoint, c2;
	Coordinate intersect = null;
	for (int i = 0; i < poly1.points.size() - 2; i++) {
	    for (int j = i + 2; j < poly1.points.size(); j++) {
		// compute seg between i and j
		// compute bisector and intersection point with q
		// compute the distance
		// ls = new LineString(poly1Curve[i], poly1Curve[j]);
		lseg = new LineSegment(poly1Curve[i], poly1Curve[j]);
		midPoint = lseg.midPoint();
		double origSlope = getSlope(poly1Curve[i].x, poly1Curve[i].y, poly1Curve[j].x, poly1Curve[j].y);
		double bisectSlope = 0;
		if (origSlope != Double.MAX_VALUE) {
		    if (origSlope == 0) {
			bisectSlope = Double.MAX_VALUE;
		    } else {
			bisectSlope = -1 / origSlope;
		    }
		}

		// linear func: y-midPoint.y = bisectSlope*(x-midpoint.x)
		double step = poly2Curve[0].distance(midPoint); // TODO alter to
								// (double)
								// OrthodromicDistance.getDistanceInDegrees(a,b)
		c1 = new Coordinate(midPoint.x - step, bisectSlope * (-step) + midPoint.y);
		c2 = new Coordinate(midPoint.x + step, bisectSlope * step + midPoint.y);
		// (c1, midPoint, c2) is the bisector linestring of the
		// linesegment(i,j)
		ls = gf.createLineString(new Coordinate[] { c1, midPoint, c2 });
		temp = gf.createLineString(poly2Curve);
		if (ls.intersects(temp)) {
		    try {
			intersect = ls.intersection(temp).getCoordinate();
		    } catch (Exception e) {
			intersect = null;
		    }
		}
		if (intersect != null) {
		    list.add(intersect.distance(poly1Curve[i]));
		}
	    }
	}

	for (int i = 0; i < poly2.points.size() - 2; i++) {
	    for (int j = i + 2; j < poly2.points.size(); j++) {
		lseg = new LineSegment(poly2Curve[i], poly2Curve[j]);
		midPoint = lseg.midPoint();
		double origSlope = getSlope(poly2Curve[i].x, poly2Curve[i].y, poly2Curve[j].x, poly2Curve[j].y);
		double bisectSlope = 0;
		if (origSlope != Double.MAX_VALUE) {
		    if (origSlope == 0) {
			bisectSlope = Double.MAX_VALUE;
		    } else {
			bisectSlope = -1 / origSlope;
		    }
		}
		// linear func: y-midPoint.y = bisectSlope*(x-midpoint.x)
		double step = poly1Curve[0].distance(midPoint);
		if (bisectSlope == Double.MAX_VALUE) {
		    // vertical line
		    c1 = new Coordinate(midPoint.x, midPoint.y - step);
		    c2 = new Coordinate(midPoint.x, midPoint.y + step);
		} else {
		    c1 = new Coordinate(midPoint.x - step, bisectSlope * (-step) + midPoint.y);
		    c2 = new Coordinate(midPoint.x + step, bisectSlope * step + midPoint.y);
		}
		ls = gf.createLineString(new Coordinate[] { c1, midPoint, c2 });
		temp = gf.createLineString(poly1Curve);

		if (ls.intersects(temp)) {
		    intersect = ls.intersection(temp).getCoordinate();

		}
		if (intersect != null) {
		    list.add(intersect.distance(poly2Curve[i]));
		}
	    }
	}
	return list.toArray(new Double[list.size()]);
    }

    private double getSlope(double x1, double y1, double x2, double y2) {
	if ((x2 - x1) == 0)
	    return Double.MAX_VALUE;
	else
	    return (y2 - y1) / (x2 - x1);
    }

    /**
     * Performs the standard binary search using two comparisons per level.
     * 
     * @return index where item is found, or NOT_FOUND.
     */
    public int binarySearch(Double[] a) {
	int low = 0;
	int high = a.length - 1;
	int mid = 0;

	while (low <= high) {
	    mid = (low + high) / 2;
	    if (!isFrechet(a[mid])) {
		low = mid + 1;
	    } else {
		high = mid - 1;
	    }
	}
	return mid;
    }

    public boolean isFrechet(double epsilon) {
	if (epsilon == Double.POSITIVE_INFINITY || epsilon == Double.NEGATIVE_INFINITY)
	    return false;

	// check first pair of segments
	if (Line2D.ptSegDist(poly1.points.get(0).coordinates.get(0), poly1.points.get(0).coordinates.get(1),
		poly1.points.get(1).coordinates.get(0), poly1.points.get(1).coordinates.get(1),
		poly2.points.get(0).coordinates.get(0), poly2.points.get(0).coordinates.get(1)) > epsilon &&

	Line2D.ptSegDist(poly1.points.get(0).coordinates.get(0), poly1.points.get(0).coordinates.get(1),
		poly1.points.get(1).coordinates.get(0), poly1.points.get(1).coordinates.get(1),
		poly2.points.get(1).coordinates.get(0), poly2.points.get(1).coordinates.get(1)) > epsilon) {

	    return false;
	}

	if (Line2D.ptSegDist(poly2.points.get(0).coordinates.get(0), poly2.points.get(0).coordinates.get(1),
		poly2.points.get(1).coordinates.get(0), poly2.points.get(1).coordinates.get(1),
		poly1.points.get(0).coordinates.get(0), poly1.points.get(0).coordinates.get(1)) > epsilon &&

	Line2D.ptSegDist(poly2.points.get(0).coordinates.get(0), poly2.points.get(0).coordinates.get(1),
		poly2.points.get(1).coordinates.get(0), poly2.points.get(1).coordinates.get(1),
		poly1.points.get(1).coordinates.get(0), poly1.points.get(1).coordinates.get(1)) > epsilon) {

	    return false;
	}

	// check last pair of segments
	if (Line2D.ptSegDist(poly1.points.get(poly1.points.size() - 2).coordinates.get(0),
		poly1.points.get(poly1.points.size() - 2).coordinates.get(1),
		poly1.points.get(poly1.points.size() - 1).coordinates.get(0),
		poly1.points.get(poly1.points.size() - 1).coordinates.get(1),
		poly2.points.get(poly2.points.size() - 1).coordinates.get(0),
		poly2.points.get(poly2.points.size() - 1).coordinates.get(1)) > epsilon &&

	Line2D.ptSegDist(poly1.points.get(poly1.points.size() - 2).coordinates.get(0),
		poly1.points.get(poly1.points.size() - 2).coordinates.get(1),
		poly1.points.get(poly1.points.size() - 1).coordinates.get(0),
		poly1.points.get(poly1.points.size() - 1).coordinates.get(1),
		poly2.points.get(poly2.points.size() - 2).coordinates.get(0),
		poly2.points.get(poly2.points.size() - 2).coordinates.get(1)) > epsilon) {

	    return false;
	}
	if (Line2D.ptSegDist(poly2.points.get(poly2.points.size() - 2).coordinates.get(0),
		poly2.points.get(poly2.points.size() - 2).coordinates.get(1),
		poly2.points.get(poly2.points.size() - 1).coordinates.get(0),
		poly2.points.get(poly2.points.size() - 1).coordinates.get(1),
		poly1.points.get(poly1.points.size() - 2).coordinates.get(0),
		poly1.points.get(poly1.points.size() - 2).coordinates.get(1)) > epsilon &&

	Line2D.ptSegDist(poly2.points.get(poly2.points.size() - 2).coordinates.get(0),
		poly2.points.get(poly2.points.size() - 2).coordinates.get(1),
		poly2.points.get(poly2.points.size() - 1).coordinates.get(0),
		poly2.points.get(poly2.points.size() - 1).coordinates.get(1),
		poly1.points.get(poly1.points.size() - 1).coordinates.get(0),
		poly1.points.get(poly1.points.size() - 1).coordinates.get(1)) > epsilon) {

	    return false;
	}

	LineString tempLsQ;
	LineString tempLsP;
	Coordinate p1, p2, q1, q2;
	com.vividsolutions.jts.geom.Polygon tempCircle;
	Geometry tempGeom;

	for (int i = 0; i < poly1.points.size() - 1; i++) {
	    for (int j = 0; j < poly2.points.size() - 1; j++) {

		p1 = new Coordinate(poly1.points.get(i).coordinates.get(0), poly1.points.get(i).coordinates.get(1));
		p2 = new Coordinate(poly1.points.get(i + 1).coordinates.get(0),
			poly1.points.get(i + 1).coordinates.get(1));
		q1 = new Coordinate(poly2.points.get(j).coordinates.get(0), poly2.points.get(j).coordinates.get(1));
		q2 = new Coordinate(poly2.points.get(j + 1).coordinates.get(0),
			poly2.points.get(j + 1).coordinates.get(1));

		if (Line2D.ptSegDist(poly2.points.get(j).coordinates.get(0), poly2.points.get(j).coordinates.get(1),
			poly2.points.get(j + 1).coordinates.get(0), poly2.points.get(j + 1).coordinates.get(1),
			poly1.points.get(i).coordinates.get(0), poly1.points.get(i).coordinates.get(1)) > epsilon) {

		    a[i][j] = b[i][j] = -1;

		} else {
		    // make line string out of j's two end points
		    tempLsQ = gf.createLineString(new Coordinate[] { q1, q2 });

		    // make circle with i's first end point
		    gsf.setCentre(p1);
		    gsf.setSize(2 * epsilon);
		    tempCircle = gsf.createCircle();

		    if (tempCircle.contains(tempLsQ)) {
			a[i][j] = 0;
			b[i][j] = 1;
		    } else {
			// collapse the circle and the line
			tempGeom = tempCircle.intersection(tempLsQ);
			int numCoords = tempGeom.getCoordinates().length;

			if (numCoords == 2) {
			    // 2 points
			    Coordinate[] intersections = tempGeom.getCoordinates();
			    a[i][j] = getProportion(intersections[0], tempLsQ);
			    b[i][j] = getProportion(intersections[1], tempLsQ);
			} else if (numCoords == 1) {
			    // 1 point
			    Coordinate intersection = tempGeom.getCoordinate();
			    if (p1.distance(q1) < p1.distance(q2)) {
				a[i][j] = 0;
				b[i][j] = getProportion(intersection, tempLsQ);
			    } else {
				a[i][j] = getProportion(intersection, tempLsQ);
				b[i][j] = 1;
			    }
			}
		    }
		}

		// fill up c_ij and d_ij
		double val1 = Line2D.ptSegDist(poly1.points.get(i).coordinates.get(0),
			poly1.points.get(i).coordinates.get(1), poly1.points.get(i + 1).coordinates.get(0),
			poly1.points.get(i + 1).coordinates.get(1), poly2.points.get(j).coordinates.get(0),
			poly2.points.get(j).coordinates.get(1));

		if (val1 > epsilon) {
		    c[i][j] = d[i][j] = -1;
		} else {
		    tempLsP = gf.createLineString(new Coordinate[] { p1, p2 });
		    gsf.setCentre(q1);
		    gsf.setSize(2 * epsilon + delta);
		    tempCircle = gsf.createCircle();
		    if (tempCircle.contains(tempLsP)) {
			c[i][j] = 0;
			d[i][j] = 1;
		    } else {
			// collapse the circle and the line
			tempGeom = tempCircle.intersection(tempLsP);

			int numCoords = tempGeom.getCoordinates().length;
			// 0 point
			if (numCoords == 0) {
			    c[i][j] = 0;
			    d[i][j] = 0;
			} else if (numCoords == 1) {
			    // 1 point
			    Coordinate intersect = tempGeom.getCoordinate();
			    if (q1.distance(p1) < q1.distance(p2)) {
				c[i][j] = 0;
				d[i][j] = getProportion(intersect, tempLsP);
			    } else {
				c[i][j] = getProportion(intersect, tempLsP);
				d[i][j] = 1;
			    }
			} else {
			    // 2 points
			    Coordinate[] intersections = ((LineString) tempGeom).getCoordinates();
			    c[i][j] = getProportion(intersections[0], tempLsP);
			    d[i][j] = getProportion(intersections[1], tempLsP);
			}
		    }
		}
	    }
	}

	// determine B^R_i,1
	boolean flag = true;
	for (int i = 0; i < poly1.points.size(); i++) {
	    if (flag && c[i][0] == -1 && d[i][0] == -1) {
		flag = false;
	    } else if (!flag) {
		c[i][0] = d[i][0] = -1;
	    }
	}

	flag = true;
	// determine L^R_1,j
	for (int j = 1; j < poly2.points.size(); j++) {
	    if (flag && a[0][j] == -1 && b[0][j] == -1) {
		flag = false;
	    } else if (!flag) {
		a[0][j] = b[0][j] = -1;
	    }
	}

	// TODO: the complicated loop to compute L^R_(i+1),j and B^R_i,(j+1)
	boolean retVal = true;

	// cannot enter the upper right cell
	if (a[poly1.points.size() - 1][poly2.points.size() - 1] == -1
		&& b[poly1.points.size() - 1][poly2.points.size() - 1] == -1
		&& c[poly1.points.size() - 1][poly2.points.size() - 1] == -1
		&& d[poly1.points.size() - 1][poly2.points.size() - 1] == -1) {
	    retVal = false;
	}

	return retVal;
    }

    private double getProportion(Coordinate coord, LineString ls) {
	// coord is a point in line ls
	Coordinate[] ends = ls.getCoordinates();
	return coord.distance(ends[0]) / ls.getLength();
    }

}
