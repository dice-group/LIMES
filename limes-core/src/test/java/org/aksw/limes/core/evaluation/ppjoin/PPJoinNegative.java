package org.aksw.limes.core.evaluation.ppjoin;

import org.junit.Test;

public class PPJoinNegative {
	
	@Test
	public void testPPJoinNegative() {
		//input file
		String path = "src/main/resources/datasets/PPJoinNegative.txt";
		PPJoin ppjoinalgo = new PPJoin();
		//Read input file and fetch records
		String[] recordsInFile = ppjoinalgo.fetchRecords(path);
		//Create record objects 
		ppjoinalgo.createRecords(recordsInFile);
		//find frequency of each token in the records
		ppjoinalgo.updateFrequency();
		
		// create a unique universal set of tokens
		ppjoinalgo.createUniqueUniversal();
			    
	    for (int i=0; i<ppjoinalgo.records.size();i++) {
	    	ppjoinalgo.records.get(i).orderTokens();
						
		}
	    
	    //Fetch candidate pairs
		ppjoinalgo.candidatePairs();
		assert(ppjoinalgo.similarRecords.isEmpty());
	}
}
