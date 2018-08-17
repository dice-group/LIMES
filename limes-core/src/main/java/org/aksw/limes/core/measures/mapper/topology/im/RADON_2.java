package org.aksw.limes.core.measures.mapper.topology.im;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * @author kdressler
 */

/**
 *
 * based on the original RADON
 */

public class RADON_2 {



	// best measure according to our evaluation in the RADON paper
	public String heuristicStatMeasure = "avg";
	boolean swapped;
	private final Logger logger = LoggerFactory.getLogger(RADON_2.class);

	public Map<String, Geometry> getGeometryMapFromCache(ACache c, String property) {
		WKTReader wktReader = new WKTReader();
		Map<String, Geometry> gMap = new HashMap<>();
		for (String uri : c.getAllUris()) {
			Set<String> values = c.getInstance(uri).getProperty(property);
			if (values.size() > 0) {
				String wkt = values.iterator().next();
				try {
					gMap.put(uri, wktReader.read(wkt));
				} catch (ParseException e) {
					//logger.warn("Skipping malformed geometry at " + uri + "...");
				}
			}
		}
		return gMap;
	}


	public List<AMapping> getMapping(ACache source, ACache target, String sourceVar, String targetVar,
			String expression, double threshold) {;
			if (threshold <= 0) {
				throw new InvalidThresholdException(threshold);
			}
			List<String> properties = PropertyFetcher.getProperties(expression, threshold);
			Map<String, Geometry> sourceMap = getGeometryMapFromCache(source, properties.get(0));
			Map<String, Geometry> targetMap = getGeometryMapFromCache(target, properties.get(1));

			return getMapping(sourceMap, targetMap);
	}

	public List<AMapping> getMapping(Map<String, Geometry> sourceData, Map<String, Geometry> targetData) {
		double thetaX, thetaY;
		//int numThreads = new Double(Math.ceil((double) Runtime.getRuntime().availableProcessors() / 2.0d)).intValue();


		GridSizeHeuristics heuristicsS = new GridSizeHeuristics(sourceData.values());
		GridSizeHeuristics heuristicsT = new GridSizeHeuristics(targetData.values());
		double[] theta = GridSizeHeuristics.decideForTheta(heuristicsS, heuristicsT, heuristicStatMeasure);
		thetaX = theta[0];
		thetaY = theta[1];
		// swap smaller dataset to source
		// if swap is necessary is decided in Stats.decideForTheta([...])!
		Map<String, Geometry> swap;
		swapped = GridSizeHeuristics.swap;
		if (swapped) {

			swap = sourceData;
			sourceData = targetData;
			targetData = swap;

		}

		// set up indexes
		SquareIndex sourceIndex = index(sourceData, null, thetaX, thetaY);
		SquareIndex targetIndex = index(targetData, sourceIndex, thetaX, thetaY);


		AMapping mIntersects = MappingFactory.createDefaultMapping();
		AMapping mContains = MappingFactory.createDefaultMapping();
		AMapping mCoverdby = MappingFactory.createDefaultMapping();
		AMapping mCovers = MappingFactory.createDefaultMapping();
		AMapping mCrosses = MappingFactory.createDefaultMapping();
		//AMapping mDisjoint = MappingFactory.createDefaultMapping();
		AMapping mOverlaps = MappingFactory.createDefaultMapping();
		AMapping mEquals = MappingFactory.createDefaultMapping();
		AMapping mWithin = MappingFactory.createDefaultMapping();
		AMapping mTouches = MappingFactory.createDefaultMapping();
		List<AMapping> allMapResults=new ArrayList<AMapping>();
		allMapResults.add(mIntersects); //0
		allMapResults.add(mContains);   //1
		allMapResults.add(mCoverdby);   //2
		allMapResults.add(mCovers);     //3
		allMapResults.add(mCrosses);    //4   //5
		allMapResults.add(mOverlaps);   //5
		allMapResults.add(mEquals);     //6
		allMapResults.add(mWithin);      //7
		allMapResults.add(mTouches);   //8
		//allMapResults.add(mDisjoint);//9

		//ExecutorService matchExec = Executors.newFixedThreadPool(1);
		//ExecutorService mergerExec = Executors.newFixedThreadPool(1);
		//List<Map<String, Set<String>>> results = Collections.synchronizedList(new ArrayList<>());

		Map<String, Set<String>> computed = new HashMap<>();

		//Matcher matcher  = new Matcher(results);

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

								List<Boolean> allRelations=new ArrayList<Boolean>();
								//if()
								allRelations=relate1(a.polygon, b.polygon);
								//System.out.println("a polygon: "+ a.polygon);
								//System.out.println("b polygon: "+ b.polygon);
								boolean compute = (allRelations.get(3) && a.covers(b))
										|| (allRelations.get(2) && b.covers(a))
										|| (allRelations.get(1)&& a.contains(b))
										|| (allRelations.get(7) && b.contains(a)) || (allRelations.get(6) && a.equals(b))
										||  allRelations.get(0) || allRelations.get(4) 
										||  allRelations.get(5)||  allRelations.get(8) ;


								if (compute) {


									if(allRelations.get(0)) {
										if (swapped) 
											mIntersects.add(b.origin_uri, a.origin_uri, 1.0);

										else
											mIntersects.add(a.origin_uri, b.origin_uri, 1.0);}

									if(allRelations.get(1)) {
										if (swapped) 
											mContains.add(b.origin_uri, a.origin_uri, 1.0);

										else
											mContains.add(a.origin_uri, b.origin_uri, 1.0);}

									if(allRelations.get(2)) {
										if (swapped) 
											mCoverdby.add(b.origin_uri, a.origin_uri, 1.0);

										else
											mCoverdby.add(a.origin_uri, b.origin_uri, 1.0);}

									if(allRelations.get(3)) {
										if (swapped) 
											mCovers.add(b.origin_uri, a.origin_uri, 1.0);

										else
											mCovers.add(a.origin_uri, b.origin_uri, 1.0);}

									if(allRelations.get(4)) {
										if (swapped) 
											mCrosses.add(b.origin_uri, a.origin_uri, 1.0);

										else
											mCrosses.add(a.origin_uri, b.origin_uri, 1.0);}

									if(allRelations.get(5)) {
										if (swapped) 
											mOverlaps.add(b.origin_uri, a.origin_uri, 1.0);

										else
											mOverlaps.add(a.origin_uri, b.origin_uri, 1.0);}

									if(allRelations.get(6)) {

										if (swapped) 
											mEquals.add(b.origin_uri, a.origin_uri, 1.0);

										else
											mEquals.add(a.origin_uri, b.origin_uri, 1.0);}

									if(allRelations.get(7)) {
										if (swapped) 
											mWithin.add(b.origin_uri, a.origin_uri, 1.0);

										else
											mWithin.add(a.origin_uri, b.origin_uri, 1.0);}

									if(allRelations.get(8)) {
										if (swapped) 
											mTouches.add(b.origin_uri, a.origin_uri, 1.0);

										else
											mTouches.add(a.origin_uri, b.origin_uri, 1.0);}

									//									if(allRelations.get(9)) {
									//										if (swapped) 
									//											mTouches.add(b.origin_uri, a.origin_uri, 1.0);
									//
									//										else
									//											mTouches.add(a.origin_uri, b.origin_uri, 1.0);}

								}
							}
						}
					}
				}
			}
		}

		//boolean disJoint=true;
		//if (disJoint) {

		//		AMapping disjoint = MappingFactory.createDefaultMapping();
		//		for (String s : sourceData.keySet()) {
		//
		//			for (String t : targetData.keySet()) {
		//				if (swapped) {
		//					if (!mIntersects.contains(t, s)&&!mContains.contains(t, s)
		//							&&!mCovers.contains(t, s)&&!mCoverdby.contains(t, s)
		//							&&!mCrosses.contains(t, s)&&!mEquals.contains(t, s)
		//							&&!mTouches.contains(t, s)&&!mOverlaps.contains(t, s)
		//							&&!mWithin.contains(t, s)) {
		//
		//						disjoint.add(t, s, 1.0d);
		//					}
		//				} else {
		//					if (!mIntersects.contains(t, s)&&!mContains.contains(t, s)
		//							&&!mCovers.contains(t, s)&&!mCoverdby.contains(t, s)
		//							&&!mCrosses.contains(t, s)&&!mEquals.contains(t, s)
		//							&&!mTouches.contains(t, s)&&!mOverlaps.contains(t, s)
		//							&&!mWithin.contains(t, s)) {
		//						disjoint.add(s, t, 1.0d);
		//
		//					}
		//
		//				}
		//			}
		//
		//		}
		//		allMapResults.remove(5);
		//		allMapResults.add(5, disjoint);

		//}

		return allMapResults;
	}
	private List<Boolean> relate1(Geometry geometry1, Geometry geometry2) {

		Boolean disjoint=false;

		RelateDE9IM relationIM = new RelateDE9IM(geometry1, geometry2);

		List<Boolean>allRelations=new ArrayList<Boolean>();

		Boolean intersects=false;
		Boolean	contains=false;
		Boolean coverdby=false;
		Boolean covers=false;
		Boolean crosses=false;
		//disjoint=relationIM.isDisjoint();
		Boolean overlaps=false;
		Boolean equals=false;
		Boolean within=false;
		Boolean touches=false;

		if(swapped) {covers=relationIM.isCoveredBy();
		coverdby=relationIM.isCovers();
		within=relationIM.isContains();
		contains=relationIM.isWithin();}

		else {
			intersects=relationIM.isIntersects();
			contains=relationIM.isContains();
			coverdby=relationIM.isCoveredBy();
			covers=relationIM.isCovers();
			crosses=relationIM.isCrosses();
			//disjoint=relationIM.isDisjoint();
			overlaps=relationIM.isOverlaps();
			equals=relationIM.isEquals();
			within=relationIM.isWithin();
			touches=relationIM.isTouches();}
		allRelations.add(intersects); //0
		allRelations.add(contains);   //1
		allRelations.add(coverdby);   //2
		allRelations.add(covers);     //3
		allRelations.add(crosses);    //4
		//	allRelations.add(disjoint);  
		allRelations.add(overlaps);   //5
		allRelations.add(equals);     //6
		allRelations.add(within);     //7
		allRelations.add(touches);    //8

		return allRelations;

	}
	public SquareIndex index(Map<String, Geometry> input, SquareIndex extIndex, double thetaX, double thetaY) {
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

	private void addToIndex(MBBIndex mbbIndex, SquareIndex result, SquareIndex extIndex) {
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