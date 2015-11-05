package org.aksw.limes.core.io.parser;

import org.aksw.limes.core.measures.mapper.SetOperations.Operator;

public class Parser implements IParser {

    public String threshold1;
    public String threshold2;
    public double coef2;
    public String op;
    public double coef1;

    public Parser(String object, double threshold) {
	// TODO Auto-generated constructor stub
    }

    public String getOperation() {
	// TODO Auto-generated method stub
	return null;
    }

    public boolean isAtomic() {
	// TODO Auto-generated method stub
	return false;
    }

    public String getTerm1() {
	// TODO Auto-generated method stub
	return null;
    }

    public String getTerm2() {
	// TODO Auto-generated method stub
	return null;
    }

}
