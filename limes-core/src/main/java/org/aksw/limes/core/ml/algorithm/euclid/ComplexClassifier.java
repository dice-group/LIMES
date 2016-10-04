package org.aksw.limes.core.ml.algorithm.euclid;

import org.aksw.limes.core.io.mapping.AMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 *
 */
public class ComplexClassifier {
    public List<SimpleClassifier> classifiers;
    public double fMeasure;
    public AMapping mapping;

    public ComplexClassifier(List<SimpleClassifier> classifiers, double fMeasure)
    {
        this.classifiers = classifiers;
        this.fMeasure = fMeasure;
    }

    public ComplexClassifier clone()
    {
        ComplexClassifier copy = new ComplexClassifier(null, 0);
        copy.fMeasure = fMeasure;
        copy.classifiers = new ArrayList<>();
        for(int i=0; i<classifiers.size(); i++)
            copy.classifiers.add(classifiers.get(i).clone());
        return copy;
    }

    public String toString() {
        String s = "";
        for(SimpleClassifier sc : classifiers) {
            s+= "CC{ ("+sc.toString2()+") }";
        }
        return s;
    }
}
