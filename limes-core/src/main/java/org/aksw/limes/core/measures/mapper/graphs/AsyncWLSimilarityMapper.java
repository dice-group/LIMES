package org.aksw.limes.core.measures.mapper.graphs;

import org.aksw.limes.core.io.mapping.AMapping;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Just to show how easy an async execution could be achieved
 *
 * @author Cedric Richter
 */
public class AsyncWLSimilarityMapper extends WLSimilarityMapper {

    private ExecutorService executor;

    public AsyncWLSimilarityMapper(ExecutorService executor) {
        this.executor = executor;
    }


    @Override
    protected Future<AMapping> execute(GraphMapperCallable callable){
        return executor.submit(callable);
    }


}
