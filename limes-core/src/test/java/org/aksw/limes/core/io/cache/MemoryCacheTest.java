package org.aksw.limes.core.io.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.junit.Before;
import org.junit.Test;

public class MemoryCacheTest {
	
	public MemoryCache cache = new MemoryCache();


	@Before
	public void prepareData() {
        HybridCache tmp = (HybridCache) DataSetChooser.getData(DataSets.DRUGS).getSourceCache();
        tmp.getAllInstances().forEach(i -> cache.addInstance(i));
	}

	@Test
	public void testClone(){
		MemoryCache cloned = cache.clone();
		assertTrue(cloned.getClass() == cache.getClass());
		assertTrue(cloned != cache);
		assertTrue(cloned.getAllInstances() != cache.getAllInstances());
		assertEquals(cache, cloned);
	}
}
