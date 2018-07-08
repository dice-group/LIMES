package org.aksw.limes.core.measures.mapper.wrapper;

import org.aksw.commons.collections.IteratorUtils;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author Cedric Richter
 */
public class ChunkedMapper implements IMapper {

    static Log log = LogFactory.getLog(ChunkedMapper.class);

    private int chunkSize;
    private IMapper delegate;

    public ChunkedMapper(int chunkSize, IMapper delegate) {
        this.chunkSize = chunkSize;
        this.delegate = delegate;
    }

    protected Future<AMapping> execute(ChunkExecutor executor){
        FutureTask<AMapping> task = new FutureTask<>(executor);
        task.run();
        return task;
    }


    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression, double threshold) {

        Iterator<ACache> sourceChunks = new ChunkIterator(source, chunkSize);
        Iterator<ACache> targetChunks;

        List<Future<AMapping>> futures = new ArrayList<>();

        for(ACache sourceCache: IteratorUtils.makeIterable(sourceChunks)){
            targetChunks = new ChunkIterator(target, chunkSize);
            for(ACache targetCache: IteratorUtils.makeIterable(targetChunks)){
                log.info("Execute chunk.");
                futures.add(execute(new ChunkExecutor(delegate, sourceCache, targetCache, sourceVar, targetVar, expression, threshold)));
            }
        }

        AMapping mapping = null;

        for(Future<AMapping> futureMapping: futures){
            try {
                if(mapping == null){
                    mapping = futureMapping.get();
                }else{
                    for(Map.Entry<String, HashMap<String, Double>> e: futureMapping.get().getMap().entrySet())
                        mapping.add(e.getKey(), e.getValue());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if(mapping == null)
            mapping = MappingFactory.createDefaultMapping();


        return mapping;
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return delegate.getRuntimeApproximation(sourceSize, targetSize, theta, language);
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return delegate.getMappingSizeApproximation(sourceSize, targetSize, theta, language);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    private class ChunkIterator implements Iterator<ACache>{

        private ACache base;
        private int chunkSize;

        private Iterator<String> urlIterator;

        public ChunkIterator(ACache base, int chunkSize){
            this.base = base;
            this.chunkSize = chunkSize;

            //Replace with sufficient iterator (e.g. in case of file cache)
            urlIterator = this.base.getAllUris().iterator();

        }

        @Override
        public boolean hasNext() {
            return urlIterator.hasNext();
        }

        private ACache buildCache(){
            if(hasNext()){
                MemoryCache cache = new MemoryCache();
                for(int i = 0; i < chunkSize && urlIterator.hasNext(); i++)
                    cache.addInstance(this.base.getInstance(urlIterator.next()));
                cache.setKbInfo(this.base.getKbInfo());
                return cache;
            }
            return null;
        }

        @Override
        public ACache next() {

            ACache cache = buildCache();

            if(cache == null)
                throw new NoSuchElementException();

            return cache;
        }
    }
}
