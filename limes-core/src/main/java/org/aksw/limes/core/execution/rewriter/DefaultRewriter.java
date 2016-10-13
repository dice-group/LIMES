package org.aksw.limes.core.execution.rewriter;

import org.aksw.limes.core.io.ls.LinkSpecification;

/**
 * Implements the default rewriter class. The input link specification is
 * returned without any modification.
 * 
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class DefaultRewriter extends Rewriter {
    /**
     * Constructor for the default rewriter.
     * 
     */
    public DefaultRewriter() {
    }

    /**
     * Rewrites a Link Specification. No modification to the input link
     * specification.
     *
     * @param spec,
     *            Input link specification
     * @return the input link specification.
     */
    @Override
    public LinkSpecification rewrite(LinkSpecification spec) {
        if (spec.isEmpty())
            throw new IllegalArgumentException();
        return spec;
    }

}
