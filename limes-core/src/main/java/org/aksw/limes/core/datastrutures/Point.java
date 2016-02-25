package org.aksw.limes.core.datastrutures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Point {

	public List<Double> coordinates;
	public String label;

	public Point() {
		label = "";
		coordinates = Arrays.asList(new Double[] { 0.0, 0.0 });
	}

	public Point(Double... points) {
		label = "";
		coordinates = new ArrayList<>();
		for (Double p : points) {
			coordinates.add(p);
		}	
	}

	public Point(String name, List<Double> position) {
		label = name;
		coordinates = position;
	}

	@Override
	public boolean equals(Object ob) {
		if (ob == null)
			return false;
		if (ob.getClass() != getClass())
			return false;
		Point other = (Point) ob;
		if (!label.equals(other.label))
			return false;
		if (!coordinates.equals(other.coordinates))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return label.hashCode() ^ coordinates.hashCode();
	}

	@Override
	public String toString() {
		return coordinates.toString();
	}

	public static Point add(Point p, Point q) {
		return add(p.label + "+" + q.label, p, q);
	}

	public static Point add(String label, Point p, Point q) {
		List<Double> position = Arrays.asList(new Double[] { 0.0, 0.0 });
		for (int i = 0; i < p.coordinates.size(); i++) {
			position.set(i, p.coordinates.get(i) + q.coordinates.get(i));
		}
		return new Point(label, position);
	}

	public static void main(String args[]) {
		Point p = new Point("p", Arrays.asList(new Double[] { 1.0, 1.0 }));
		Point q = new Point("q", Arrays.asList(new Double[] { 1.0, 2.0 }));
		System.out.println(p + "+" + q + " = " + add(p, q));
	}

}
