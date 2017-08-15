package org.aksw.limes.core.evaluation.evaluator;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

public class FoldData {

	public AMapping map = MappingFactory.createDefaultMapping();
	public ACache sourceCache = new HybridCache();
	public ACache targetCache = new HybridCache();
	public int size = -1;
	
	public FoldData(AMapping map, ACache sourceCache, ACache targetCache) {
		super();
		this.map = map;
		this.size = map.getSize();
		this.sourceCache = sourceCache;
		this.targetCache = targetCache;
	}

	public FoldData() {

	}
}
