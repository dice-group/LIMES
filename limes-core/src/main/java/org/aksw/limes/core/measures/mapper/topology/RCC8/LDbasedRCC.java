package org.aksw.limes.core.measures.mapper.topology.RCC8;


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
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 *
 * @author Abdullah Ahmed
 */

/**
 *
 * inspired by the paper "Radon — Rapid Discovery of Topological Relations" by the authors:
 * Mohamed Ahmed Sherif,  Kevin Dreßler, Panayiotis Smeros, Axel-Cyrille Ngonga Ngomo and
 * some of code reused from the original implementation of RADON by Kevin Dreßler.
 * in this implementation, we use qualitative spatial representation and reasoning (mainly RCC8) instead of 
 * the Dimensionally Extended nine-Intersection Model (DE-9IM)  
 */

public class LDbasedRCC {


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
		private final List<Map<String, Set<String>>> result;
		private List<MBBIndex> scheduled;

		public Matcher(List<Map<String, Set<String>>> result) {
			this.result = result;
			this.scheduled = new ArrayList<>();

		}
		public void run() {
			Map<String, Set<String>> temp = new HashMap<>();
			for (int i = 0; i < scheduled.size(); i += 2) {
				MBBIndex s = scheduled.get(i);
				MBBIndex t = scheduled.get(i + 1);
				if (!temp.containsKey(s.origin_uri)) {
					temp.put(s.origin_uri, new HashSet<>());
				}
				temp.get(s.origin_uri).add(t.origin_uri);
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

		private static List<Boolean> relate1(Geometry geometry1, Geometry geometry2) {
			List<Boolean>allRelations=new ArrayList<Boolean>();
			ReducedIntersectionMatrix relationIM = new ReducedIntersectionMatrix(geometry1, geometry2);

			boolean connected_C =relationIM.connected_C();// intersects
			boolean externallyConnected_EC=relationIM.externallyConnected_EC();
			boolean properlyOverlap_PO=relationIM.properlyOverlap_PO();
			boolean equal_EQ=relationIM.equal_EQ();
			boolean tangentialProperPart_TPP=relationIM.tangentialProperPart_TPP();
			boolean nonTangentialProperPart_NTPP=relationIM.nonTangentialProperPart_NTPP();
			boolean tangentialProperPartConvers_TPPc=relationIM.tangentialProperPartConvers_TPPc();
			boolean nonTangentialProperPartConvers_NTPPc=relationIM.nonTangentialProperPartConvers_NTPPc();
			
			allRelations.add(connected_C); //0
			allRelations.add(externallyConnected_EC);   //1
			allRelations.add(properlyOverlap_PO);   //2
			allRelations.add(equal_EQ);     //3
			allRelations.add(tangentialProperPart_TPP);    //4
			allRelations.add(nonTangentialProperPart_NTPP);   //5
			allRelations.add(tangentialProperPartConvers_TPPc);     //6
			allRelations.add(nonTangentialProperPartConvers_NTPPc);     //7
			return allRelations;
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

	// best measure according to our evaluation in the RADON paper
	public static String heuristicStatMeasure = "avg";

	private static final Logger logger = LoggerFactory.getLogger(LDbasedRCC.class);

	public static Map<String, Geometry> getGeometryMapFromCache(ACache c, String property) {
		WKTReader wktReader = new WKTReader();
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

	public static List<AMapping> getMapping(ACache source, ACache target, String sourceVar, String targetVar,
			String expression, double threshold) {
		if (threshold <= 0) {
			throw new InvalidThresholdException(threshold);
		}
		List<String> properties = PropertyFetcher.getProperties(expression, threshold);
		Map<String, Geometry> sourceMap = getGeometryMapFromCache(source, properties.get(0));
		Map<String, Geometry> targetMap = getGeometryMapFromCache(target, properties.get(1));

		return getMapping(sourceMap, targetMap);
	}

	public static  List<AMapping> getMapping(Map<String, Geometry> sourceData, Map<String, Geometry> targetData) {
		double thetaX, thetaY;
		int numThreads = new Double(Math.ceil((double) Runtime.getRuntime().availableProcessors() / 2.0d)).intValue();

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

		}

		// set up indexes
		SquareIndex sourceIndex = index(sourceData, null, thetaX, thetaY);
		SquareIndex targetIndex = index(targetData, sourceIndex, thetaX, thetaY);
		List<AMapping> allMapResults=new ArrayList<AMapping>();
		AMapping connected_C = MappingFactory.createDefaultMapping();
		AMapping externallyConnected_EC = MappingFactory.createDefaultMapping();
		AMapping properlyOverlap_PO = MappingFactory.createDefaultMapping();
		AMapping equal_EQ = MappingFactory.createDefaultMapping();
		AMapping tangentialProperPart_TPP = MappingFactory.createDefaultMapping();
		AMapping nonTangentialProperPart_NTPP = MappingFactory.createDefaultMapping();
		AMapping tangentialProperPartConvers_TPPc = MappingFactory.createDefaultMapping();
		AMapping nonTangentialProperPartConvers_NTPPc = MappingFactory.createDefaultMapping();

		allMapResults.add(connected_C); 
		allMapResults.add(externallyConnected_EC);   
		allMapResults.add(properlyOverlap_PO);   
		allMapResults.add(equal_EQ);     
		allMapResults.add(tangentialProperPart_TPP);    
		allMapResults.add(nonTangentialProperPart_NTPP);   
		allMapResults.add(tangentialProperPartConvers_TPPc);     
		allMapResults.add(nonTangentialProperPartConvers_NTPPc);     

		ExecutorService matchExec = Executors.newFixedThreadPool(numThreads);
		ExecutorService mergerExec = Executors.newFixedThreadPool(1);
		List<Map<String, Set<String>>> results = Collections.synchronizedList(new ArrayList<>());
		List<Map<String, Set<String>>> results1 = Collections.synchronizedList(new ArrayList<>());
		List<Map<String, Set<String>>> results2 = Collections.synchronizedList(new ArrayList<>());
		List<Map<String, Set<String>>> results3 = Collections.synchronizedList(new ArrayList<>());
		List<Map<String, Set<String>>> results4 = Collections.synchronizedList(new ArrayList<>());
		List<Map<String, Set<String>>> results5 = Collections.synchronizedList(new ArrayList<>());
		List<Map<String, Set<String>>> results6 = Collections.synchronizedList(new ArrayList<>());
		List<Map<String, Set<String>>> results7 = Collections.synchronizedList(new ArrayList<>());
		List<List<Map<String, Set<String>>>> allResults = Collections.synchronizedList(new ArrayList<>());
		
		allResults.add(results);
		allResults.add(results1);
		allResults.add(results2);
		allResults.add(results3);
		allResults.add(results4);
		allResults.add(results5);
		allResults.add(results6);
		allResults.add(results7);
		Map<String, Set<String>> computed = new HashMap<>();

		Matcher matcher = new Matcher(results);
		Matcher matcher1 = new Matcher(results1);
		Matcher matcher2 = new Matcher(results2);
		Matcher matcher3 = new Matcher(results3);
		Matcher matcher4 = new Matcher(results4);
		Matcher matcher5 = new Matcher(results5);
		Matcher matcher6 = new Matcher(results6);
		Matcher matcher7 = new Matcher(results7);
		List<Matcher>matchers=new ArrayList<Matcher>();
		matchers.add(matcher);
		matchers.add(matcher1);
		matchers.add(matcher2);
		matchers.add(matcher3);
		matchers.add(matcher4);
		matchers.add(matcher5);
		matchers.add(matcher6);
		matchers.add(matcher7);
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
								List<Boolean>allRelations=new ArrayList<Boolean>();
								allRelations=Matcher.relate1(a.polygon, b.polygon);

								//boolean compute = true;
								//if (compute) {
									if (numThreads == 1) { 
										if(allRelations.get(0)) {

											if (swapped) 
												allMapResults.get(0).add(b.origin_uri, a.origin_uri, 1.0);
											else
												allMapResults.get(0).add(a.origin_uri, b.origin_uri, 1.0);}
										if(allRelations.get(1)) {
											if (swapped) 
												allMapResults.get(1).add(b.origin_uri, a.origin_uri, 1.0);
											else
												allMapResults.get(1).add(a.origin_uri, b.origin_uri, 1.0);}
										if(allRelations.get(2)) {
											if (swapped) 
												allMapResults.get(2).add(b.origin_uri, a.origin_uri, 1.0);
											else
												allMapResults.get(2).add(a.origin_uri, b.origin_uri, 1.0);}
										if(allRelations.get(3)) {
											if (swapped) 
												allMapResults.get(3).add(b.origin_uri, a.origin_uri, 1.0);
											else
												allMapResults.get(3).add(a.origin_uri, b.origin_uri, 1.0);}
										if(allRelations.get(4)) {
											if (swapped) 
												allMapResults.get(4).add(b.origin_uri, a.origin_uri, 1.0);
											else
												allMapResults.get(4).add(a.origin_uri, b.origin_uri, 1.0);}

										if(allRelations.get(5)) {
											if (swapped) 
												allMapResults.get(5).add(b.origin_uri, a.origin_uri, 1.0);
											else
												allMapResults.get(5).add(a.origin_uri, b.origin_uri, 1.0);}
										if(allRelations.get(6)) {
											if (swapped) 
												allMapResults.get(6).add(b.origin_uri, a.origin_uri, 1.0);
											else
												allMapResults.get(6).add(a.origin_uri, b.origin_uri, 1.0);}
										if(allRelations.get(7)) {
											if (swapped) 
												allMapResults.get(7).add(b.origin_uri, a.origin_uri, 1.0);
											else
												allMapResults.get(7).add(a.origin_uri, b.origin_uri, 1.0);}


									} else {

										if(allRelations.get(0)) {

											matchers.get(0).schedule(a, b);
											if (matchers.get(0).size() == Matcher.maxSize) {
												matchExec.execute(matchers.get(0));
												matcher = new Matcher(allResults.get(0));
												if (allResults.get(0).size() > 0) {
													mergerExec.execute(new Merger(allResults.get(0), allMapResults.get(0)));
												}
											}
										}

										if(allRelations.get(1)) {
											matchers.get(1).schedule(a, b);
											if (matchers.get(1).size() == Matcher.maxSize) {
												matchExec.execute(matchers.get(1));
												matcher1 = new Matcher(allResults.get(1));
												if (allResults.get(1).size() > 0) {
													mergerExec.execute(new Merger(allResults.get(1), allMapResults.get(1)));
												}
											}
										}
										if(allRelations.get(2)) {
											matchers.get(2).schedule(a, b);
											if (matchers.get(2).size() == Matcher.maxSize) {
												matchExec.execute(matchers.get(2));
												matcher2 = new Matcher(allResults.get(2));
												if (allResults.get(2).size() > 0) {
													mergerExec.execute(new Merger(allResults.get(2), allMapResults.get(2)));
												}
											}
										}
										if(allRelations.get(3)) {
											matchers.get(3).schedule(a, b);
											if (matchers.get(3).size() == Matcher.maxSize) {
												matchExec.execute(matchers.get(3));
												matcher3 = new Matcher(allResults.get(3));
												if (allResults.get(3).size() > 0) {
													mergerExec.execute(new Merger(allResults.get(3), allMapResults.get(3)));
												}
											}
										}
										if(allRelations.get(4)) {
											matchers.get(4).schedule(a, b);
											if (matchers.get(4).size() == Matcher.maxSize) {
												matchExec.execute(matchers.get(4));
												matcher4 = new Matcher(allResults.get(4));
												if (allResults.get(4).size() > 0) {
													mergerExec.execute(new Merger(allResults.get(4), allMapResults.get(4)));
												}
											}
										}


										if(allRelations.get(5)) {
											matchers.get(5).schedule(a, b);
											if (matchers.get(5).size() == Matcher.maxSize) {
												matchExec.execute(matchers.get(5));
												matcher5 = new Matcher(allResults.get(5));
												if (allResults.get(5).size() > 0) {
													mergerExec.execute(new Merger(allResults.get(5), allMapResults.get(5)));
												}
											}
										}
										if(allRelations.get(6)) {
											matchers.get(6).schedule(a, b);
											if (matchers.get(6).size() == Matcher.maxSize) {
												matchExec.execute(matchers.get(6));
												matcher6 = new Matcher(allResults.get(6));
												if (allResults.get(6).size() > 0) {
													mergerExec.execute(new Merger(allResults.get(6), allMapResults.get(6)));
												}
											}
										}
										if(allRelations.get(7)) {
											matchers.get(7).schedule(a, b);
											if (matchers.get(7).size() == Matcher.maxSize) {
												matchExec.execute(matchers.get(7));
												matcher7 = new Matcher(allResults.get(7));
												if (allResults.get(7).size() > 0) {
													mergerExec.execute(new Merger(allResults.get(7), allMapResults.get(7)));
												}
											}
										}

									}
								//}
							}
						}
					}
				}
			}
		}
		if (numThreads > 1) {

			if (matchers.get(0).size() > 0) {
				matchExec.execute(matchers.get(0));

			}
			if (matchers.get(1).size() > 0) {
				matchExec.execute(matchers.get(1));

			}
			if (matchers.get(2).size() > 0) {
				matchExec.execute(matchers.get(2));
			}
			if (matchers.get(3).size() > 0) {
				matchExec.execute(matchers.get(3));
			}
			if (matchers.get(4).size() > 0) {
				matchExec.execute(matchers.get(4));
			}
			if (matchers.get(5).size() > 0) {
				matchExec.execute(matchers.get(5));
			}
			if (matchers.get(6).size() > 0) {
				matchExec.execute(matchers.get(6));
			}
			if (matchers.get(7).size() > 0) {
				matchExec.execute(matchers.get(7));
			}

			matchExec.shutdown();
			while (!matchExec.isTerminated()) {
				try {
					if (allResults.get(0).size() > 0) {

						mergerExec.execute(new Merger(allResults.get(0), allMapResults.get(0)));
					}
					if (allResults.get(1).size() > 0) {

						mergerExec.execute(new Merger(allResults.get(1), allMapResults.get(1)));
					}
					if (allResults.get(2).size() > 0) {

						mergerExec.execute(new Merger(allResults.get(2), allMapResults.get(2)));
					}
					if (allResults.get(3).size() > 0) {

						mergerExec.execute(new Merger(allResults.get(3), allMapResults.get(3)));
					}
					if (allResults.get(4).size() > 0) {

						mergerExec.execute(new Merger(allResults.get(4), allMapResults.get(4)));
					}
					if (allResults.get(5).size() > 0) {

						mergerExec.execute(new Merger(allResults.get(5), allMapResults.get(5)));
					}
					if (allResults.get(6).size() > 0) {

						mergerExec.execute(new Merger(allResults.get(6), allMapResults.get(6)));
					}
					if (allResults.get(7).size() > 0) {

						mergerExec.execute(new Merger(allResults.get(7), allMapResults.get(7)));
					}

					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (allResults.get(0).size() > 0) {

				mergerExec.execute(new Merger(allResults.get(0), allMapResults.get(0)));
			}
			if (allResults.get(1).size() > 0) {

				mergerExec.execute(new Merger(allResults.get(1), allMapResults.get(1)));
			}
			if (allResults.get(2).size() > 0) {

				mergerExec.execute(new Merger(allResults.get(2), allMapResults.get(2)));
			}
			if (allResults.get(3).size() > 0) {

				mergerExec.execute(new Merger(allResults.get(3), allMapResults.get(3)));
			}
			if (allResults.get(4).size() > 0) {

				mergerExec.execute(new Merger(allResults.get(4), allMapResults.get(4)));
			}
			if (allResults.get(5).size() > 0) {

				mergerExec.execute(new Merger(allResults.get(5), allMapResults.get(5)));
			}
			if (allResults.get(6).size() > 0) {

				mergerExec.execute(new Merger(allResults.get(6), allMapResults.get(6)));
			}
			if (allResults.get(7).size() > 0) {

				mergerExec.execute(new Merger(allResults.get(7), allMapResults.get(7)));
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

		return allMapResults;
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
