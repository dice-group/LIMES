package org.aksw.limes.core.measures.mapper.wrapper;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.IMapper;

import java.util.concurrent.Callable;

/**
 * @author Cedric Richter
 */
public class ChunkExecutor implements Callable<AMapping> {

    private IMapper delegate;

    private ACache source;
    private ACache target;

    private String sourceVar;
    private String targetVar;

    private String expression;

    private double threshold;

    public ChunkExecutor(IMapper delegate, ACache source, ACache target, String sourceVar, String targetVar, String expression, double threshold) {
        this.delegate = delegate;
        this.source = source;
        this.target = target;
        this.sourceVar = sourceVar;
        this.targetVar = targetVar;
        this.expression = expression;
        this.threshold = threshold;
    }



    @Override
    public AMapping call() throws Exception {
        return this.delegate.getMapping(source, target, sourceVar, targetVar, expression, threshold);
    }
}
