package org.aksw.limes.core.execution.rewriter;
/**
* Implements Rewriter interface.
* 
* @author ngonga
* @author kleanthi
*/
public interface Rewriter {
    /**
     * Rewrites a Link Specification and returns an equivalent yet probably more
     * time-efficient link spec
     *
     * @param spec
     *            Input link specification
     * @return Rewritten spec
     */
    public LinkSpec rewrite(LinkSpec spec);
}
