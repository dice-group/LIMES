package org.aksw.limes.core.measures.mapper.topology.cobalt.splitting;

import org.aksw.limes.core.util.LimesWktReader;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import static org.junit.Assert.assertTrue;

public class EqualSplitterTest {

    public EqualSplitterTest() {
        super();
    }

    @Test
    public void testSplit() throws Exception {
        LimesWktReader reader = new LimesWktReader();
        Geometry geo = reader.read("POLYGON((0 5, 10 10, 10 0, 0 5))");
        EqualSplitter equalSplitter = new EqualSplitter();
        Envelope[][] split = equalSplitter.getSplit(geo, 2);
        assertTrue("Expect top left corner to have two empty parts", split[0][0].isNull());
        assertTrue("Expect top left corner to have two empty parts", split[0][2].isNull());
    }

}
