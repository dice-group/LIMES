package org.aksw.limes.core.measures.measure.graphs.representation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontend;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontendImpl;
import org.aksw.jena_sparql_api.cache.file.CacheBackendFile;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.describe.DefaultConnetionConfig;
import org.aksw.limes.core.io.describe.Descriptor;
import org.aksw.limes.core.measures.measure.graphs.gouping.ExactNodeLabelGrouper;
import org.aksw.limes.core.measures.measure.graphs.gouping.INodeLabelGrouper;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class WLDescriptionFactory {

    public static final String CACHE_FOLDER = "cache/";

    private KBInfo info;
    private INodeLabelGrouper grouper;
    private LoadingCache<String, WLModelRepresentation> cache;

    public WLDescriptionFactory(KBInfo info, INodeLabelGrouper grouper) {
        this.info = info;
        this.grouper = grouper;
        initCache();
    }

    public WLDescriptionFactory(KBInfo info) {
        this(info, new ExactNodeLabelGrouper());
    }

    private Descriptor initMemCachedDescriptor(KBInfo info){
        CacheFrontend frontend = new CacheFrontendImpl(
          new CacheBackendFile(
                  new File(CACHE_FOLDER), 3600000
          )
        );
        return new Descriptor(info, new DefaultConnetionConfig(), frontend);
    }

    private void initCache(){
        Descriptor descriptor = initMemCachedDescriptor(info);
        cache = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .build(new CacheLoader<String, WLModelRepresentation>() {
                    @Override
                    public WLModelRepresentation load(String s) throws Exception {
                        return new WLModelRepresentation(descriptor.describe(s), grouper);
                    }
                });
    }

    public WLDescription createWLDescriptor(List<String> uris){
        return new WLDescription(uris, cache);
    }

}
