package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.junit.Test;

public class DataChooserTest {

	@Test
	public void test() {
		String[] datasets ={"PERSON1","PERSON1_CSV","PERSON2","PERSON2_CSV","RESTAURANTS","OAEI2014BOOKS"};
		//"RESTAURANTS_CSV","DBLPACM","ABTBUY","DBLPSCHOLAR","AMAZONGOOGLE","DBPLINKEDMDB","DRUGS"
			try{
				for(String ds : datasets)
				{
					System.out.println(ds);
					DataSetChooser.getData(ds);
				}
					
				
			//	DataSetChooser.getData("DRUGS");
			} catch(Exception e) {
				assertTrue(false);
			}
			assertTrue(true);
	}

}
