package org.aksw.limes.core.measures.measure.graphs.representation;

import com.google.common.cache.LoadingCache;

import java.util.AbstractList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WLDescription extends AbstractList<WLModelRepresentation> {

    private List<String> uris;
    private LoadingCache<String, WLModelRepresentation> cache;

    WLDescription(List<String> uris, LoadingCache<String, WLModelRepresentation> cache) {
        this.uris = uris;
        this.cache = cache;
    }


    @Override
    public WLModelRepresentation get(int index) {
        try {
            return cache.get(uris.get(index));
        } catch (ExecutionException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return uris.size();
    }
}
