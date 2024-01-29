package org.aksw.limes.core.measures.mapper.topology.cobalt.splitting;

import org.aksw.limes.core.util.LimesWktReader;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FittingSplitterTest {

    public FittingSplitterTest() {
        super();
    }

    @Test
    public void testGetMapping() throws Exception {
        LimesWktReader reader = new LimesWktReader();
        Geometry geo = reader.read("POLYGON((0 5, 10 10, 10 0, 0 5))");
        FittingSplitter fittingSplitter = new FittingSplitter();
        Envelope[][] split = fittingSplitter.getSplit(geo, 2);
        assertTrue("Expect top left corner to have one empty part", split[0][0].isNull());
        assertFalse("Expect top left corner to have one empty part", split[0][2].isNull());
    }

}
