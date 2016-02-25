package org.aksw.limes.core.execution.rewriter;

import org.apache.log4j.Logger;

public class RewriterFactory {
    public static final String DEFAULT = "default";
    public static final String ALGEBRAIC = "algebraic";
    private static final Logger logger = Logger.getLogger(RewriterFactory.class.getName());

    /**
     * @param name,
     *            type of the rewriter
     * @return a specific rewriter instance
     * @author kleanthi
     */
    public static Rewriter getRewriter(String name) {

	if (name.equalsIgnoreCase(DEFAULT))
	    return new DefaultRewriter();
	//if (name.equalsIgnoreCase(ALGEBRAIC))
	//    return new AlgebraicRewriter();

	logger.error("Sorry, " + name + " is not yet implemented. Exit with error ...");
	System.exit(1);
	return null;
    }
}
