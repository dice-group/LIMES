package org.aksw.limes.core.util;


import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 *
 */
public class LimesWktReader {

    private WKTReader reader = new WKTReader();

    public Geometry read(String wktString) throws ParseException {
        wktString = wktString.replaceAll("<.*>","");
        wktString = wktString.replaceAll("\\^\\^.*","");
        return reader.read(wktString);
    }

}