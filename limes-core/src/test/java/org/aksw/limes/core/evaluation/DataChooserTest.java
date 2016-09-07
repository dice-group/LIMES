package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.junit.Test;

public class DataChooserTest {

    @Test
    public void test() {
        String[] datasets = {"PERSON1","PERSON2" , "RESTAURANTS","OAEI2014BOOKS","DBLPACM","ABTBUY","DBLPSCHOLAR","AMAZONGOOGLEPRODUCTS","DBPLINKEDMDB","DRUGS","PERSON2_CSV","PERSON2_CSV","PERSON1_CSV","RESTAURANTS_CSV"};
        //  
        try {
            for (String ds : datasets) {
                System.out.println(ds);
                DataSetChooser.getData(ds);
            }


            //	DataSetChooser.getData("DRUGS");
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
    }

}
