/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets.hausdorff;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon;

import java.util.*;

/**
 *
 * @author ngonga
 */
public class ScanIndexedHausdorff extends CentroidIndexedHausdorff {

    @Override
    public double computeDistance(Polygon s, Polygon t, double threshold) {
	Mapping knownDistances = new MemoryMapping();
	Map<Integer, List<Integer>> toCompute = initToCompute(s, t, threshold, knownDistances);

	boolean checkTermination;
	// int count = 0;
	while (!toCompute.isEmpty()) {
	    checkTermination = checkTermination(s, knownDistances, toCompute, threshold);
	    if (checkTermination) {
		return (threshold + 1);
	    } else {
		toCompute = updateToCompute(s, t, threshold, knownDistances, toCompute);
	    }

	}
	// approximated as much as we could. Have to go through known distances;

	for (int i = 0; i < s.points.size(); i++) {
	    if (!knownDistances.getMap().containsKey(i + "")) {
		// means that all min distances for this point are larger than
		// the threshold
		return threshold + 1;
	    }
	}
	// if all points from s are in here then we can compute the real value
	double max = -1;
	double min;

	for (String sIdx : knownDistances.getMap().keySet()) {
	    min = Double.MAX_VALUE;
	    for (String tIdx : knownDistances.getMap().get(sIdx).keySet()) {
		min = Math.min(min, (double) knownDistances.getConfidence(sIdx, tIdx));
	    }
	    max = Math.max(max, min);
	}
	if (max == -1) {
	    return threshold + 1;
	}
	return max;
    }

    /**
     * Checks whether the map contains the pair (source, target)
     *
     * @param map
     * @param source
     * @param target
     * @return
     */
    public boolean contains(Map<Integer, List<Integer>> map, int source, int target) {
	if (!map.containsKey(source)) {
	    return false;
	} else if (!map.get(source).contains(target)) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Checks whether it is known for the source point with index sourceIndex
     * that all distances to points in p are above the threshold
     *
     * @param sourceIndex
     * @param knownDistances
     * @param toCompute
     * @param threshold
     * @return -1 if it is unknown, the known distance if it is known and
     *         (threshold + 1) if the distance is known to be beyond the
     *         threshold
     */
    public double getCurrentApproximation(int sourceIndex, Mapping knownDistances,
	    Map<Integer, List<Integer>> toCompute, double threshold) {
	if (toCompute.containsKey(sourceIndex)) {
	    return -1.0; // distance is unknown
	} else {
	    if (knownDistances.getMap().containsKey(sourceIndex + "")) {
		HashMap<String, Double> distances = knownDistances.getMap().get(sourceIndex + "");
		double d, min = Double.MAX_VALUE;
		for (String key : distances.keySet()) {
		    d = distances.get(key).doubleValue();
		    min = Math.min(d, min);
		}
		return min;
	    } else {
		return threshold + 1.0;
	    }
	}
    }

    /**
     * Checks whether a distance computation should be terminated
     *
     * @param s
     *            Source polygon
     * @param knownDistances
     * @param toCompute
     * @param threshold
     * @return true if computation should be terminated
     */
    public boolean checkTermination(Polygon s, Mapping knownDistances, Map<Integer, List<Integer>> toCompute,
	    double threshold) {
	for (int i = 0; i < s.points.size(); i++) {
	    if (getCurrentApproximation(i, knownDistances, toCompute, threshold) > threshold) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns distances that are still to be computed
     *
     * @param s
     * @param t
     * @param threshold
     * @param sourceReference
     * @param targetReference
     * @param toCompute
     * @return
     */
    public Map<Integer, List<Integer>> initToCompute(Polygon s, Polygon t, double threshold, Mapping knownDistances) {
	// 1. compute first distance
	Map<Integer, List<Integer>> toCompute = new HashMap<Integer, List<Integer>>();
	double approx, d = distance(s.points.get(0), t.points.get(0));
	if (d <= threshold) {
	    knownDistances.add(0 + "", 0 + "", d);
	}
	// 2. approximate distance from s0 to all other points
	for (int j = 1; j < t.points.size(); j++) {
	    approx = d - targetIndex.getDistance(t.uri, t.points.get(0), t.points.get(j));
	    // do not compute values larger than the threshold
	    if (approx <= threshold) {
		if (!toCompute.containsKey(0)) {
		    toCompute.put(0, new ArrayList<Integer>());
		}
		toCompute.get(0).add(j);
	    }
	}

	// 3. Repeat 2. for t0, i.e., approximate distance from t0 to all other
	// points
	for (int i = 1; i < s.points.size(); i++) {
	    approx = d - targetIndex.getDistance(s.uri, s.points.get(0), s.points.get(i));
	    if (approx <= threshold) {
		// remove from toCompute if in there
		if (!toCompute.containsKey(i)) {
		    toCompute.put(i, new ArrayList<Integer>());
		}
		toCompute.get(i).add(0);
	    }
	}

	// 4. now approximate distance from s1 ... sn to t1 ... tm
	for (int i = 1; i < s.points.size(); i++) {
	    approx = d - (sourceIndex.getDistance(s.uri, s.points.get(0), s.points.get(i)));
	    for (int j = 1; j < t.points.size(); j++) {
		if (approx - targetIndex.getDistance(t.uri, t.points.get(0), t.points.get(j)) <= threshold) {
		    if (!toCompute.containsKey(i)) {
			toCompute.put(i, new ArrayList<Integer>());
		    }
		    toCompute.get(i).add(j);
		}
	    }
	}
	return toCompute;
    }

    /**
     * Returns distances that are still to be computed
     *
     * @param s
     * @param t
     * @param threshold
     * @param sourceReference
     * @param targetReference
     * @param toCompute
     * @return
     */
    public Map<Integer, List<Integer>> updateToCompute(Polygon s, Polygon t, double threshold, Mapping knownDistances,
	    Map<Integer, List<Integer>> toCompute) {
	// 1. compute first distance
	int sIndex, tIndex;
	Map.Entry<Integer, List<Integer>> entries = toCompute.entrySet().iterator().next();
	sIndex = entries.getKey();
	tIndex = entries.getValue().get(0);
	double approx, d = distance(s.points.get(sIndex), t.points.get(tIndex));
	
	if (d <= threshold) {
	    knownDistances.add(sIndex + "", tIndex + "", d);
	    
	}

	toCompute.get(sIndex).remove(0);
	if (toCompute.get(sIndex).isEmpty()) {
	    toCompute.remove(sIndex);
	}

	List<List<Integer>> toDelete = new ArrayList<List<Integer>>();
	// compute appoximations based on toCompute list
	for (int sIdx : toCompute.keySet()) {
	    for (int tIdx : toCompute.get(sIdx)) {
		if (sIdx == sIndex) {
		    approx = Math.abs(d - targetIndex.getDistance(t.uri, t.points.get(tIdx), t.points.get(tIndex)));
		} else if (tIdx == tIndex) {
		    approx = Math.abs(d - sourceIndex.getDistance(s.uri, s.points.get(sIdx), s.points.get(sIndex)));
		} else {
		    approx = d - targetIndex.getDistance(t.uri, t.points.get(tIdx), t.points.get(tIndex))
			    - sourceIndex.getDistance(s.uri, s.points.get(sIdx), s.points.get(sIndex));
		}
		
		if (approx > threshold) {
		    List<Integer> entry = new ArrayList<Integer>();
		    entry.add(sIdx);
		    entry.add(tIdx);
		    toDelete.add(entry);
		}
	    }
	}
	
	for (List<Integer> entry : toDelete) {
	    if (toCompute.containsKey(entry.get(0))) {
		toCompute.get(entry.get(0)).remove(entry.get(1));
		if (toCompute.get(entry.get(0)).isEmpty()) {
		    toCompute.remove(entry.get(0));
		}
	    }
	}
	return toCompute;
    }

}
