package org.aksw.limes.core.measures.mapper.topology.cobalt.splitting;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

public class FittingSplitter extends CobaltSplitter {

    public FittingSplitter() {
        super();
    }

    @Override
    public Envelope[][] getSplit(Geometry geo, int times) {
        Geometry[][] splitGeo = new Geometry[][]{
                new Geometry[]{
                        geo
                }
        };
        Envelope envelope = geo.getEnvelopeInternal();
        double startX = envelope.getMinX();
        double endX = envelope.getMaxX();
        double diffX = endX - startX;

        double startY = envelope.getMinY();
        double endY = envelope.getMaxY();
        double diffY = endY - startY;

        for (int splitIteration = 1; splitIteration <= times; splitIteration++) {
            Geometry[][] temp = new Geometry[splitGeo.length * 2][];
            for (int i = 0; i < splitGeo.length; i++) {
                temp[i * 2] = new Geometry[splitGeo[i].length * 2];
                temp[i * 2 + 1] = new Geometry[splitGeo[i].length * 2];

                for (int j = 0; j < splitGeo[i].length; j++) {
                    Envelope splitEnv = splitGeo[i][j].getEnvelopeInternal();
                    double midX = splitEnv.getMinX() + (splitEnv.getMaxX() - splitEnv.getMinX()) / 2;
                    double midY = splitEnv.getMinY() + (splitEnv.getMaxY() - splitEnv.getMinY()) / 2;
                    Geometry[][] partSplit = getSplitGeo(splitGeo[i][j], midX, midY);

                    temp[i * 2][j * 2] = partSplit[0][0];
                    temp[i * 2 + 1][j * 2] = partSplit[1][0];
                    temp[i * 2][j * 2 + 1] = partSplit[0][1];
                    temp[i * 2 + 1][j * 2 + 1] = partSplit[1][1];
                }

            }
            splitGeo = temp;
        }

        Envelope[][] split = new Envelope[splitGeo.length][];
        for (int i = 0; i < splitGeo.length; i++) {
            split[i] = new Envelope[splitGeo[i].length];
            for (int j = 0; j < splitGeo[i].length; j++) {
                Envelope splitEnvelope = splitGeo[i][j].getEnvelopeInternal();
                if (splitEnvelope.getArea() == 0) {//If area = 0, this is not a polygon and cannot be used with cobalt
                    splitEnvelope = new Envelope();
                }
                split[i][j] = splitEnvelope;
            }
        }
        return split;
    }

}
