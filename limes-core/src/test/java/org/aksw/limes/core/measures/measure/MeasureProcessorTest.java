package org.aksw.limes.core.measures.measure;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.junit.Test;

public class MeasureProcessorTest {

    @Test
    public void getMeasures() {
        ACache source = new MemoryCache();
        ACache target = new MemoryCache();
        source.addTriple("S1", "pub", "test");
        source.addTriple("S1", "conf", "conf one");
        source.addTriple("S2", "pub", "test2");
        source.addTriple("S2", "conf", "conf2");

        target.addTriple("S1", "pub", "test");
        target.addTriple("S1", "conf", "conf one");
        target.addTriple("S3", "pub", "test1");
        target.addTriple("S3", "conf", "conf three");

        assertTrue(MeasureProcessor.getSimilarity(source.getInstance("S1"), target.getInstance("S3"),
                "ADD(0.5*trigram(x.conf, y.conf),0.5*cosine(y.conf, x.conf))", 0.4, "?x", "?y") > 0.0);

        assertTrue(MeasureProcessor
                .getMeasures("AND(jaccard(x.authors,y.authors)|0.4278,overlap(x.authors,y.authors)|0.4278)").isEmpty() == false);


    }
}
