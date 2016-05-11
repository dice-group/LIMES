package org.aksw.limes.core.datastrutures;

import java.util.List;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * This class contains the gold standard mapping and the sopurce and target URIs
 * @author mofeed
 * @version 1.0
 */
public class GoldStandard {
	public Mapping goldStandard;
	public List<String> sourceUris;
	public List<String> targetUris;
	
	public GoldStandard(Mapping goldStandard, List<String> sourceUris, List<String> targetUris) {
		super();
		this.goldStandard = goldStandard;
		this.sourceUris = sourceUris;
		this.targetUris = targetUris;
	}

	public GoldStandard(Mapping m){
		this.goldStandard = m;
	}
}
