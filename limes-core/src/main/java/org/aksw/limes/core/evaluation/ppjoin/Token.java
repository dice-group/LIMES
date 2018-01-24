package org.aksw.limes.core.evaluation.ppjoin;


public class Token implements Comparable<Token> {
	String word;
	int frequency;

	Token() {
		this.word = null;
		this.frequency = 0;
	}

	int getFrequency() {
		return this.frequency;
	}

	void setFrequency(int freq) {
		this.frequency = freq;
	}

	String getToken() {
		return this.word;
	}

	void setToken(String token) {
		this.word = token;
	}

	void printToken() {
		System.out.println("Word : "+this.word+" Frequency: "+this.frequency);
	}

	@Override
	public int compareTo(Token o) {
		
		if ((this.frequency < o.frequency)) {
			return -1;
		}

		return 1;
	}
	
	
    public int compare(Token object1, Token object2) {
    	return object1.compareTo(object2);
    }

}
