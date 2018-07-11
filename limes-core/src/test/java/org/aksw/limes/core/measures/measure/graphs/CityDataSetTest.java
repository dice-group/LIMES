package org.aksw.limes.core.measures.measure.graphs;

import org.aksw.limes.core.measures.measure.graphs.CityDataSet;
import org.aksw.limes.core.measures.measure.graphs.CityDataSetWiki;

import org.junit.Test;

public class CityDataSetTest {
    @Test
    public void citydataset_test(){

       CityDataSetWiki tc = new CityDataSetWiki();
        try {
            tc.getQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
