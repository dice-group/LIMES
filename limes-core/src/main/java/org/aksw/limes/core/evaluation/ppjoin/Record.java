package org.aksw.limes.core.evaluation.ppjoin;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Record {
	List<Token> tokens;
	int id;
	
	Record() {
		this.tokens = new ArrayList<Token>();
		this.id = 0;
	}
	
	int getId() {
		return this.id;
	}

	void setId(int identify) {
		this.id = identify;
	}
	
	
	
	void orderTokens() {
		Collections.sort(this.tokens);
	}
	
	void printRecords() {
		System.out.println("Record Id: "+this.id);
		for (int i = 0; i < tokens.size(); i++) {
			tokens.get(i).printToken();
		}	
			
	}
}
