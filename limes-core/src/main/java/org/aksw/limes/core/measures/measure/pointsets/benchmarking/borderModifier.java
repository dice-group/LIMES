/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.benchmarking;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.data.Point;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;

/**
 * In order not to generate a self intersecting modified polygons,
 * in this modifier, for each line segment p1 p2 across the input polygon 
 * we generate 2 random points within the line segment p1 p2
 *
 * @author sherif
 */
public class borderModifier extends AbstractPolygonModifier {

	/**
	 * Modifies a polygon by adding a random error between -threshold and
	 * +threshold to its latitude and longitude
	 *
	 * @param poly Polygon to modify
	 * @param threshold Error range
	 * @return Modified polygon with the same name
	 */
	public Polygon modify(Polygon poly, double threshold) {
		if(poly.points.size() <= 2){
			return (new MeasurementErrorModifier()).modify(poly, 1.0);
		}
		Polygon result = new Polygon(poly.uri);
		List<Point> points = new ArrayList<Point>();
		for (int i = 0 ; i < poly.points.size() ; i++) {
			double 
			x1 = poly.points.get(i).coordinates.get(0),
			y1 = poly.points.get(i).coordinates.get(1),
			x2 = poly.points.get((i+1) % poly.points.size()).coordinates.get(0),
			y2 = poly.points.get((i+1) % poly.points.size()).coordinates.get(1);

			double t = threshold; //Math.random();
			double x = x1 + (x2 - x1) * t ;
			double y = y1 + (y2 - y1) * t;
			List<Double> coordinates = new ArrayList<Double>(Arrays.asList(x, y));
			points.add(new Point(poly.points.get(i).label, coordinates));

		}
		result.points = points;
		return result;
	}

	public String getName(){
		return "InLineMeasurementErrorModifier";
	}

	public static void main(String args[]) {
		//Malta in DBpedia
		Point maltaDbpediaP1 = new Point("MaltaDbpediaP1", Arrays.asList(new Double[]{14.4625, 35.8967}));
		Point maltaDbpediaP2 = new Point("MaltaDbpediaP2", Arrays.asList(new Double[]{14.4625, 35.8833}));
		Point maltaDbpediaP3 = new Point("MaltaDbpediaP3", Arrays.asList(new Double[]{14.5, 35.8833}));
		Point maltaDbpediaP4 = new Point("MaltaDbpediaP4", Arrays.asList(new Double[]{14.5, 35.8967}));
		Polygon maltaDbpediaPoly1 = new Polygon("maltaDbpediaPoly1", Arrays.asList(new Point[]{maltaDbpediaP1, maltaDbpediaP2, maltaDbpediaP3, maltaDbpediaP4}));
		Set<Polygon> maltaDbpedia = new HashSet<Polygon>();
		maltaDbpedia.add(maltaDbpediaPoly1);

		//Malta in Nuts
		Point maltaNutsP1 = new Point("MaltaNutsP1", Arrays.asList(new Double[]{14.342771550000066, 35.931038250000043}));
		Point maltaNutsP2 = new Point("MaltaNutsP2", Arrays.asList(new Double[]{14.328761050000054, 35.990215250000048}));
		Point maltaNutsP3 = new Point("MaltaNutsP3", Arrays.asList(new Double[]{14.389599050000101, 35.957935750000019}));
		Point maltaNutsP4 = new Point("MaltaNutsP4", Arrays.asList(new Double[]{14.56211105, 35.819926750000036}));
		Point maltaNutsP5 = new Point("MaltaNutsP5", Arrays.asList(new Double[]{14.416516550000068, 35.828308250000049}));
		Polygon maltaNutsPoly1 = new Polygon("maltaNutsPoly1", Arrays.asList(new Point[]{maltaNutsP1, maltaNutsP2, maltaNutsP3, maltaNutsP4, maltaNutsP5}));

		Point maltaNutsP6 = new Point("maltaNutsP6", Arrays.asList(new Double[]{14.212639050000092, 36.07996375}));
		Point maltaNutsP7 = new Point("maltaNutsP7", Arrays.asList(new Double[]{14.336017550000065, 36.032375750000057}));
		Point maltaNutsP8 = new Point("maltaNutsP8", Arrays.asList(new Double[]{14.218683050000095, 36.021091250000026}));
		Point maltaNutsP9 = new Point("maltaNutsP9", Arrays.asList(new Double[]{14.18619805000003, 36.036388750000029}));
		Polygon maltaNutsPoly2 = new Polygon("maltaNutsPoly2", Arrays.asList(new Point[]{maltaNutsP6, maltaNutsP7, maltaNutsP8, maltaNutsP9}));

		Set<Polygon> maltaNuts = new HashSet<Polygon>();
		maltaNuts.add(maltaNutsPoly1);
		maltaNuts.add(maltaNutsPoly2);

		//Malta in LGD
		Point maltaLgdP1 = new Point("maltaLgdP1", Arrays.asList(new Double[]{14.504285, 35.8953019}));
		Polygon maltaLgdPoly1 = new Polygon("maltaLgdPoly1", Arrays.asList(new Point[]{maltaLgdP1}));
		Set<Polygon> maltaLgd = new HashSet<Polygon>();
		maltaLgd.add(maltaLgdPoly1);

		//Print Modified Malta
		System.out.println("Malta in DBpedia before applying InLineMeasurementErrorModifier modifier: " + maltaDbpediaPoly1.points);
		Polygon maltaDbpediapoly1G = new borderModifier().modify(maltaDbpediaPoly1, 2);
		System.out.println("Malta in DBpedia after applying InLineMeasurementErrorModifier modifier: " + maltaDbpediapoly1G.points);
		System.out.println();

		System.out.println("Malta in Nuts before applying InLineMeasurementErrorModifier modifier: " + maltaNutsPoly1.points);
		Polygon maltaNutsPoly1G = new borderModifier().modify(maltaNutsPoly1, 2);
		System.out.println("Malta in Nuts after applying InLineMeasurementErrorModifier modifier: " + maltaNutsPoly1G.points);
		System.out.println("Malta in Nuts before applying InLineMeasurementErrorModifier modifier: " + maltaNutsPoly2.points);
		Polygon maltaNutsPoly2G = new borderModifier().modify(maltaNutsPoly2, 2);
		System.out.println("Malta in Nuts after applying InLineMeasurementErrorModifier modifier: " + maltaNutsPoly2G.points);
		System.out.println();
		
		System.out.println("Malta in LGD before applying InLineMeasurementErrorModifier modifier: " + maltaLgdPoly1.points);
		Polygon maltaLgdPoly1G = new borderModifier().modify(maltaLgdPoly1, 2);
		System.out.println("Malta in LGD after applying InLineMeasurementErrorModifier modifier: " + maltaLgdPoly1G.points);
	}
}