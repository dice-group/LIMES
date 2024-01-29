package org.aksw.limes.core.io.ls.nlg.en;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.ls.nlg.ILSVerbalizer;

public class LSVerbalizerEN implements ILSVerbalizer {
    @Override
    public String verbalize(LinkSpecification linkSpecification) {
        return new LsPostProcessorEN().postProcessor(linkSpecification);
    }
}
