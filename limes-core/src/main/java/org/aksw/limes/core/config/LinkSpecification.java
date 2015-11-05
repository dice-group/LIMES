package org.aksw.limes.core.config;

import java.util.List;

import org.aksw.limes.core.measures.mapper.SetOperations.Operator;


public class LinkSpecification implements ILinkSpecification {

    public double threshold;
    public Operator operator;
    public List<LinkSpecification> children;
    public List<LinkSpecification> dependencies;


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

}
