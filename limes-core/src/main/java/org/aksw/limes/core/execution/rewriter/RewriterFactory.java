package org.aksw.limes.core.execution.rewriter;

import org.apache.log4j.Logger;

/**
 * Implements the rewriter factory class. The rewriter factory class is
 * responsible for choosing and creating the corresponding rewriter object.
 *
 * @author Kleanthi Georgala <georgala@informatik.uni-leipzig.de>
 * @version 1.0
 */
public class RewriterFactory {
    private static final Logger logger = Logger.getLogger(RewriterFactory.class.getName());

    /**
     * Enum class of allowed rewriter types.
     */
    public enum RewriterFactoryType {
        DEFAULT, ALGEBRAIC
    }

    /**
     * Planner factory field for default rewriter.
     */
    public static final String DEFAULT = "default";
    /**
     * Planner factory field for algebraic rewriter.
     */
    public static final String ALGEBRAIC = "algebraic";

    /**
     * @return default rewriter implementation
     */
    public static Rewriter getDefaultRewriter() {
        return getRewriter(RewriterFactoryType.DEFAULT);
    }

    /**
     * Factory function for retrieving an rewriter name from the set of allowed
     * types.
     * 
     * @param name,
     *            The name/type of the rewriter.
     * @return a specific rewriter type
     */
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
     * Factory function for retrieving the desired rewriter instance.
     * 
     * @param name,
     *            type of the Rewriter
     * 
     * @return a specific rewriter instance
     * 
     */
    public static Rewriter getRewriter(RewriterFactoryType type) {
        switch (type) {
        case DEFAULT:
            return new DefaultRewriter();
        // case ALGEBRAIC:
        // return new AlgebraicRewriter();
        default:
            logger.warn(type.toString() + " is not yet implemented. Returning the default rewriter instead...");
            return getDefaultRewriter();
        }
    }

}
