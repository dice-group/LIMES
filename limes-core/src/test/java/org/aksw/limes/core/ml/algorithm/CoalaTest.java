package org.aksw.limes.core.ml.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.measures.measure.string.CosineMeasure;
import org.aksw.limes.core.measures.measure.string.SoundexMeasure;
import org.aksw.limes.core.ml.algorithm.eagle.coala.Clustering;
import org.aksw.limes.core.ml.algorithm.eagle.core.ALDecider;
import org.aksw.limes.core.ml.algorithm.eagle.core.ALDecider.Triple;
import org.junit.Test;

import weka.core.Instances;

/**
 * Tests COALA functionality.
 * @author Klaus Lyko
 *
 */
public class CoalaTest extends MLAlgorithmTest {
	
//	@Test
//	public void testComputeASimilariy() {
//		Clustering cluster = new Clustering(this.extendedSourceCache, this.extendedTargetCache, pm);
//		ALDecider decider = new ALDecider();
//		Triple t = decider.new Triple("ex:i1", "ex:i4", 0.5d);
//		PairSimilar<String> pair = new PairSimilar<String>("name", "name");
//		CosineMeasure cosine = new CosineMeasure();
//		SoundexMeasure soundex = new SoundexMeasure();
//		
//		
//		System.out.println("i1-i4(name) = " + cluster.getSimilarity(soundex, pair, t));
//		
//		PairSimilar<String> pair2 = new PairSimilar<String>("surname", "surname");
//		System.out.println("i1-i4(surname) = " + cluster.getSimilarity(cosine, pair2, t) );
//		
//	}
	
	@Test
	public void testComputeASimilariy() {
		Clustering cluster = new Clustering(this.extendedSourceCache, this.extendedTargetCache, pm);
		ALDecider decider = new ALDecider();
		PairSimilar<String> pair = new PairSimilar<String>("name", "name");
		CosineMeasure cosine = new CosineMeasure();
		SoundexMeasure soundex = new SoundexMeasure();
		List<Triple> candidates = new ArrayList<Triple>();
//		i1-i4-0.8
//		i2-i5-0.8 
//		i1-i3-0.2 // should be regarded as negative examples?
//		i3-i4-0.2
		candidates.add(decider.new Triple("ex:i1", "ex:i4", 0.9d));
		candidates.add(decider.new Triple("ex:i2", "ex:i5", 0.8d));
		candidates.add(decider.new Triple("ex:i1", "ex:i3", 0.2d));
		candidates.add(decider.new Triple("ex:i3", "ex:i4", 0.1d));
		
		
		Instances inst = cluster.computeSimilaritySpace(candidates);
		for(int i=0; i<inst.size(); i++) {
			System.out.println(inst.get(i));
		}
		// all triples (instances) computed
		assert(inst.size() == candidates.size());
		// assert all
		assert(inst.numAttributes() == pm.stringPropPairs.size()+1);
		// 1st attribute is the match - no match value	
		for(int i=0; i<inst.size(); i++) {
			assert(inst.get(i).value(0) == candidates.get(i).getSimilarity());
		}
		
	}	
}
