package org.aksw.limes.core.execution.rewriter;

import org.apache.log4j.Logger;

public class RewriterFactory {

    private static final Logger logger = Logger.getLogger(RewriterFactory.class.getName());

    public enum RewriterFactoryType{
        DEFAULT,
        ALGEBRAIC
    }

    /**
     * @return default rewriter implementation
     */
    public static Rewriter getDefaultRewriter() {
        return getRewriter(RewriterFactoryType.DEFAULT);
    }
    
    /**
     * @param name, type of the rewriter
     * @return a specific rewriter instance
     * @author kleanthi
     */
    public static Rewriter getRewriter(RewriterFactoryType type) {
        if (type == RewriterFactoryType.DEFAULT)
            return new DefaultRewriter();
        //if (name.equalsIgnoreCase(ALGEBRAIC))
        //    return new AlgebraicRewriter();
        logger.warn(type.toString() + " is not yet implemented. Generating " + RewriterFactoryType.DEFAULT + " rewriter ...");
        return getDefaultRewriter();
    }
}
