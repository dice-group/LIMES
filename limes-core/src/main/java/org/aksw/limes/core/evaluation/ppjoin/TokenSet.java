package org.aksw.limes.core.evaluation.ppjoin;

import java.util.ArrayList;
import java.util.List;

public class TokenSet {
	List<Token> tokenset;
	List<Token> subsetl;
	List<Token> subsetr;
	Token token;
	boolean isInSearchngRange;
	boolean isNotInTokenSet;
	int left;
	int right;
	
	TokenSet() {
		this.tokenset = new ArrayList<Token>();
		this.subsetl = new ArrayList<Token>();
		this.subsetr = new ArrayList<Token>();
		this.token = new Token();
		this.isInSearchngRange = false;
		this.isNotInTokenSet = true;
		this.left = 0;
		this.right = 0;
	}
	
	
	public List<Token> getTokenset() {
		return tokenset;
	}
	public void setTokenset(List<Token> tokenset) {
		this.tokenset = tokenset;
	}
	public List<Token> getSubsetl() {
		return subsetl;
	}
	public void setSubsetl(List<Token> subsetl) {
		this.subsetl = subsetl;
	}
	public List<Token> getSubsetr() {
		return subsetr;
	}
	public void setSubsetr(List<Token> subsetr) {
		this.subsetr = subsetr;
	}
	public Token getToken() {
		return token;
	}
	public void setToken(Token token) {
		this.token = token;
	}
	public boolean isInSearchngRange() {
		return isInSearchngRange;
	}
	public void setInSearchngRange(boolean isInSearchngRange) {
		this.isInSearchngRange = isInSearchngRange;
	}
	public boolean isNotInTokenSet() {
		return isNotInTokenSet;
	}
	public void setNotInTokenSet(boolean isInTokenSet) {
		this.isNotInTokenSet = isInTokenSet;
	}
	
	
	
}
