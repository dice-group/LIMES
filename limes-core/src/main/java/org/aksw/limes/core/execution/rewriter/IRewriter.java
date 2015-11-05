package org.aksw.limes.core.execution.rewriter;

import org.aksw.limes.core.config.LinkSpecification;

/**
* Implements Rewriter interface.
* 
* @author ngonga
* @author kleanthi
*/
public interface IRewriter {
    /**
     * Rewrites a Link Specification and returns an equivalent yet probably more
     * time-efficient link spec
     *
     * @param spec
     *            Input link specification
     * @return Rewritten spec
     */
    public LinkSpecification rewrite(LinkSpecification spec);
}
