package org.aksw.limes.core.execution.rewriter;

import org.aksw.limes.core.io.ls.LinkSpecification;

/**
* Implements Rewriter interface.
* 
* @author ngonga
* @author kleanthi
*/
public interface IRewriter {
    /**
     * Rewrites a Link Specification and returns an equivalent yet probably more
     * time-efficient link specification
     *
     * @param spec
     *            Input link specification
     * @return Rewritten specification
     */
    public LinkSpecification rewrite(LinkSpecification spec);
}
