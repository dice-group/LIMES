package org.aksw.limes.core.datastrutures;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;

import java.util.List;

/**
 * This class contains the gold standard mapping and the sopurce and target URIs
 *
 * @author mofeed
 * @version 1.0
 */
public class GoldStandard {
    public AMapping referenceMappings;
    public List<String> sourceUris;
    public List<String> targetUris;

    public GoldStandard(AMapping reference, List<String> sourceUris, List<String> targetUris) {
        super();
        this.referenceMappings = reference;
        this.sourceUris = sourceUris;
        this.targetUris = targetUris;
    }
    
    public GoldStandard(AMapping reference, Cache sourceUris, Cache targetUris) {
        super();
        this.referenceMappings = reference;
        this.sourceUris = sourceUris.getAllUris();
        this.targetUris = targetUris.getAllUris();
    }

    public GoldStandard(AMapping m) {
        this.referenceMappings = m;
    }

}
