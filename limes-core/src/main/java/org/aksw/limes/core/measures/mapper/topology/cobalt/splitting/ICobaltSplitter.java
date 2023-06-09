package org.aksw.limes.core.measures.mapper.topology.cobalt.splitting;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

public interface ICobaltSplitter {

    Envelope[][] getSplit(Geometry geo, int times);

}
