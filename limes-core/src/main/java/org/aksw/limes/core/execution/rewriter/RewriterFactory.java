package org.aksw.limes.core.execution.rewriter;

import org.apache.log4j.Logger;

public class RewriterFactory {

    private static final Logger logger = Logger.getLogger(RewriterFactory.class.getName());

    public enum RewriterFactoryType {
        DEFAULT, ALGEBRAIC
    }

    public static final String DEFAULT = "default";
    public static final String ALGEBRAIC = "algebraic";

    /**
     * @return default rewriter implementation
     */
    public static Rewriter getDefaultRewriter() {
        return getRewriter(RewriterFactoryType.DEFAULT);
    }

    public static RewriterFactoryType getRewriterFactoryType(String name) {
        if (name.equalsIgnoreCase(DEFAULT)) {
            return RewriterFactoryType.DEFAULT;
        }
        if (name.equalsIgnoreCase(ALGEBRAIC)) {
            return RewriterFactoryType.ALGEBRAIC;
        }
        logger.error("Sorry, " + name + " is not yet implemented. Returning the default rewriter type instead...");
        return RewriterFactoryType.DEFAULT;
    }
    
    /**
     * @param name, type of the rewriter
     * @return a specific rewriter instance
     * @author kleanthi
     */
    public static Rewriter getRewriter(RewriterFactoryType type) {
        switch (type) {
            case DEFAULT:
                return new DefaultRewriter();
//            case ALGEBRAIC:
//                return new AlgebraicRewriter();
            default:
                logger.warn(type.toString() + " is not yet implemented. Returning the default rewriter instead...");
                return getDefaultRewriter();
        }
    }
}
