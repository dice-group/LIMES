package org.aksw.limes.core.ml.algorithm.classifier;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 *
 */
public class ComplexClassifier {

    protected List<SimpleClassifier> classifiers;
    protected double fMeasure;
    protected AMapping mapping;

    public ComplexClassifier(List<SimpleClassifier> classifiers, double fMeasure)
    {
        this.classifiers = classifiers;
        this.fMeasure = fMeasure;
    }

    public ComplexClassifier clone(){
        ComplexClassifier copy = new ComplexClassifier(null, 0);
        copy.fMeasure = fMeasure;
        copy.classifiers = new ArrayList<>();
        for(int i=0; i<classifiers.size(); i++)
            copy.classifiers.add(classifiers.get(i).clone());
        return copy;
    }
    
    
    public List<SimpleClassifier> getClassifiers(){
        return classifiers;
    }

    public void setClassifiers(List<SimpleClassifier> classifiers) {
        this.classifiers = classifiers;
    }

    public double getfMeasure(){
        return fMeasure;
    }

    public void setfMeasure(double fMeasure){
        this.fMeasure = fMeasure;
    }

    public AMapping getMapping() {
        return mapping;
    }

    public void setMapping(AMapping mapping){
        this.mapping = mapping;
    }


    public String toString() {
        String s = "";
        for(SimpleClassifier sc : classifiers) {
            s+= "CC{ ("+sc.toLinkSpecString()+") }";
        }
        return s;
    }
}
