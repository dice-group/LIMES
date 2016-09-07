package org.aksw.limes.core.measures.measure.pointsets.mean;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.junit.Test;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 *
 */
public class NaiveMeanTest {

    @Test
    public void test() {

        // Malta in DBpedia
        Point maltaDbpediaP1 = new Point("MaltaDbpediaP1", Arrays.asList(new Double[]{14.4625, 35.8967}));
        Point maltaDbpediaP2 = new Point("MaltaDbpediaP2", Arrays.asList(new Double[]{14.4625, 35.8833}));
        Point maltaDbpediaP3 = new Point("MaltaDbpediaP3", Arrays.asList(new Double[]{14.5, 35.8833}));
        Point maltaDbpediaP4 = new Point("MaltaDbpediaP4", Arrays.asList(new Double[]{14.5, 35.8967}));
        Polygon maltaDbpediaPoly1 = new Polygon("maltaDbpediaPoly1",
                Arrays.asList(new Point[]{maltaDbpediaP1, maltaDbpediaP2, maltaDbpediaP3, maltaDbpediaP4}));
        Set<Polygon> maltaDbpedia = new HashSet<Polygon>();
        maltaDbpedia.add(maltaDbpediaPoly1);

        // Malta in Nuts
        Point maltaNutsP1 = new Point("MaltaNutsP1",
                Arrays.asList(new Double[]{14.342771550000066, 35.931038250000043}));
        Point maltaNutsP2 = new Point("MaltaNutsP2",
                Arrays.asList(new Double[]{14.328761050000054, 35.990215250000048}));
        Point maltaNutsP3 = new Point("MaltaNutsP3",
                Arrays.asList(new Double[]{14.389599050000101, 35.957935750000019}));
        Point maltaNutsP4 = new Point("MaltaNutsP4", Arrays.asList(new Double[]{14.56211105, 35.819926750000036}));
        Point maltaNutsP5 = new Point("MaltaNutsP5",
                Arrays.asList(new Double[]{14.416516550000068, 35.828308250000049}));
        Point maltaNutsP6 = new Point("maltaNutsP6", Arrays.asList(new Double[]{14.212639050000092, 36.07996375}));
        Point maltaNutsP7 = new Point("maltaNutsP7",
                Arrays.asList(new Double[]{14.336017550000065, 36.032375750000057}));
        Point maltaNutsP8 = new Point("maltaNutsP8",
                Arrays.asList(new Double[]{14.218683050000095, 36.021091250000026}));
        Point maltaNutsP9 = new Point("maltaNutsP9",
                Arrays.asList(new Double[]{14.18619805000003, 36.036388750000029}));
        Polygon maltaNutsPoly1 = new Polygon("maltaNutsPoly1", Arrays.asList(new Point[]{maltaNutsP1, maltaNutsP2,
                maltaNutsP3, maltaNutsP4, maltaNutsP5, maltaNutsP6, maltaNutsP7, maltaNutsP8, maltaNutsP9}));
        Set<Polygon> maltaNuts = new HashSet<Polygon>();
        maltaNuts.add(maltaNutsPoly1);

        // Malta in LGD
        Point maltaLgdP1 = new Point("maltaLgdP1", Arrays.asList(new Double[]{14.504285, 35.8953019}));
        Polygon maltaLgdPoly1 = new Polygon("maltaLgdPoly1", Arrays.asList(new Point[]{maltaLgdP1}));
        Set<Polygon> maltaLgd = new HashSet<Polygon>();
        maltaLgd.add(maltaLgdPoly1);

        NaiveMeanMeasure mean = new NaiveMeanMeasure();
        System.out.println(mean.run(maltaNuts, maltaDbpedia, Double.MAX_VALUE));

    }

}
