package org.aksw.limes.core.io.ls.nlg.de;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.ls.nlg.ILSVerbalizer;

public class LSVerbalizerDE implements ILSVerbalizer {
    @Override
    public String verbalize(LinkSpecification linkSpecification) {
        LsPostProcessorDE lsPostProcessorDe = new LsPostProcessorDE();
        String s = lsPostProcessorDe.postProcessor(linkSpecification);
        return lsPostProcessorDe.realisng();
    }


}
