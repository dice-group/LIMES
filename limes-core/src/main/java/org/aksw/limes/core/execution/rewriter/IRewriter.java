package org.aksw.limes.core.execution.rewriter;

import org.aksw.limes.core.io.ls.LinkSpecification;

/**
 * Implements the re-writer interface. It is responsible for re-writing an input
 * link specification.
 *
 * @author Axel-C. Ngonga Ngomo {@literal <}ngonga {@literal @}
 *         informatik.uni-leipzig.de{@literal >}
 * @author Kleanthi Georgala {@literal <}georgala {@literal @}
 *         informatik.uni-leipzig.de{@literal >}
 * @version 1.0
 */
public interface IRewriter {
    /**
     * Rewrites a Link Specification.
     *
     * @param spec,
     *            Input link specification
     * @return Rewritten specification
     */
    public LinkSpecification rewrite(LinkSpecification spec);
}
