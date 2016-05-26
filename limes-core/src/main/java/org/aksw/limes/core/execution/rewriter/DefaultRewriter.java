package org.aksw.limes.core.execution.rewriter;

import org.aksw.limes.core.io.ls.LinkSpecification;


public class DefaultRewriter extends Rewriter {

    public DefaultRewriter() {
    }

    ;

    @Override
    public LinkSpecification rewrite(LinkSpecification spec) {
        if (spec.isEmpty())
            throw new IllegalArgumentException();
        return spec;
    }

}
