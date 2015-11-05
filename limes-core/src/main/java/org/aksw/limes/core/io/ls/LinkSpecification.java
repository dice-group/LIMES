package org.aksw.limes.core.io.ls;

import java.util.List;

import org.aksw.limes.core.measures.mapper.SetOperations.Operator;


public class LinkSpecification implements ILinkSpecification {

    public double threshold;
    public Operator operator;
    public List<LinkSpecification> children;
    public List<LinkSpecification> dependencies;


    public LinkSpecification(String metric, double d) {
	// TODO Auto-generated constructor stub
    }

    public LinkSpecification() {
	// TODO Auto-generated constructor stub
    }

    public boolean isAtomic() {
	// TODO Auto-generated method stub
	return false;
    }

    public String getFilterExpression() {
	// TODO Auto-generated method stub
	return null;
    }

    public int size() {
	// TODO Auto-generated method stub
	return 0;
    }

    public boolean isEmpty() {
	// TODO Auto-generated method stub
	return false;
    }

    public void addDependency(LinkSpecification target) {
	// TODO Auto-generated method stub
	
    }

    public List<LinkSpecification> getAllLeaves() {
	// TODO Auto-generated method stub
	return null;
    }

    public boolean hasDependencies() {
	// TODO Auto-generated method stub
	return false;
    }

    public void readSpec(String string, double d) {
	// TODO Auto-generated method stub
	
    }

    public void addChild(LinkSpecification spec3) {
	// TODO Auto-generated method stub
	
    }

}
