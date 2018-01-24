package org.aksw.limes.core.evaluation.ppjoin;

import org.junit.Test;

public class PPJoinTest {

	@Test
	public void testPPJoin() {
		//input file
		String path = "src/main/resources/datasets/PPJoinRecords.txt";
		PPJoin ppjoinalgo = new PPJoin();
		//Read input file and fetch records
		String[] recordsInFile = ppjoinalgo.fetchRecords(path);
		//Create record objects 
		ppjoinalgo.createRecords(recordsInFile);
		//find frequency of each token in the records
		ppjoinalgo.updateFrequency();
		
		// create a unique universal set of tokens
		ppjoinalgo.createUniqueUniversal();
		//Print universal set
	    for(int k = 0; k < ppjoinalgo.universal.size(); k++) {   
	        System.out.print(ppjoinalgo.universal.get(k)+" ; ");
	    }
	    
	    for (int i=0; i<ppjoinalgo.records.size();i++) {
	    	ppjoinalgo.records.get(i).orderTokens();
						
		}
	    
	    //Print tokens
	    for (int i=0; i<ppjoinalgo.records.size();i++) {
	    	ppjoinalgo.records.get(i).printRecords();
			
		}
		
	    //Fetch candidate pairs
		ppjoinalgo.candidatePairs();
		System.out.println("Similar Record");
		System.out.println(ppjoinalgo.similarRecords);
		
		assert(ppjoinalgo.similarRecords != null);
	}

}
