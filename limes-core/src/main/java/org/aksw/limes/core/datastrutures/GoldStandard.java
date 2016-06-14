package org.aksw.limes.core.datastrutures;

import java.util.List;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 * This class contains the gold standard mapping and the sopurce and target URIs
 *
 * @author mofeed
 * @version 1.0
 */
public class GoldStandard {
    public AMapping goldStandardMappings;
    public List<String> sourceUris;
    public List<String> targetUris;

    public GoldStandard(AMapping goldStandard, List<String> sourceUris, List<String> targetUris) {
        super();
        this.goldStandardMappings = goldStandard;
        this.sourceUris = sourceUris;
        this.targetUris = targetUris;
    }

    public GoldStandard(AMapping m) {
        this.goldStandardMappings = m;
    }
}
