package org.aksw.limes.core.evaluation;

import static org.junit.Assert.*;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.junit.Test;

public class DataChooserTest {

	@Test
	public void test() {
		String[] datasets ={"PERSON1","PERSON1_CSV","PERSON2","PERSON2_CSV","RESTAURANTS","OAEI2014BOOKS"};
		//"RESTAURANTS_CSV","DBLPACM","ABTBUY","DBLPSCHOLAR","AMAZONGOOGLE","DBPLINKEDMDB","DRUGS"
			try{
			/*	for(DataSets ds : DataSets.values())
				{
					System.out.println(ds);
					DataSetChooser.getData(ds);
				}*/
					
			for (String dataset : datasets) {
				DataSetChooser.getData("DBLPACM");
			}
			} catch(Exception e) {
				assertTrue(false);
			}
			assertTrue(true);
	}

}
