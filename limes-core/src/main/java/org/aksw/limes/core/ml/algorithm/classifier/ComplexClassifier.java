/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.ml.algorithm.classifier;

import org.aksw.limes.core.io.mapping.AMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 *
 */
public class ComplexClassifier {

    protected List<SimpleClassifier> classifiers;
    protected double fMeasure;
    protected AMapping mapping;

    public ComplexClassifier(List<SimpleClassifier> classifiers, double fMeasure) {
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
