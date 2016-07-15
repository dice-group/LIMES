package org.aksw.limes.core.execution.rewriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the rewriter factory class. The rewriter factory class is
 * responsible for choosing and creating the corresponding rewriter object.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class RewriterFactory {
    private static final Logger logger = LoggerFactory.getLogger(RewriterFactory.class.getName());

    /**
     * Enum class of allowed rewriter types.
     */
    public enum RewriterType {
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
        return getRewriter(RewriterType.DEFAULT);
    }

    /**
     * Factory function for retrieving an rewriter name from the set of allowed
     * types.
     * 
     * @param name,
     *            The name/type of the rewriter.
     * @return a specific rewriter type
     */
    public static RewriterType getRewriterType(String name) {
        if (name.equalsIgnoreCase(DEFAULT)) {
            return RewriterType.DEFAULT;
        }
        if (name.equalsIgnoreCase(ALGEBRAIC)) {
            return RewriterType.ALGEBRAIC;
        }
        logger.error("Sorry, " + name + " is not yet implemented. Returning the default rewriter type instead...");
        return RewriterType.DEFAULT;
    }

    /**
     * Factory function for retrieving the desired rewriter instance.
     * 
     * @param type,
     *            type of the Rewriter
     * 
     * @return a specific rewriter instance
     * 
     */
    public static Rewriter getRewriter(RewriterType type) {
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
