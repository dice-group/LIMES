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
package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.classifier.SimpleClassifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wraps around results of a ML algorithm.
 *
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 */

public class MLResults {
	protected LinkSpecification linkspec;
	protected AMapping mapping;
    protected double quality;
    protected Map<String, Object> details = new HashMap<>();
    protected List<SimpleClassifier> classifiers = null;

    /**
     * MLResults constructor.
     */
    public MLResults() {
        super();
    }

    /**
     * MLResults full constructor.
     *
     * @param linkspec the link specification
     * @param mapping the mapping
     * @param quality the value of quality measure
     * @param details additional computation details
     */
    public MLResults(LinkSpecification linkspec, AMapping mapping, double quality, Map<String, Object> details) {
        super();
        this.linkspec = linkspec;
        this.mapping = mapping;
        this.quality = quality;
        this.details = details;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("RESULT:\n");
        s.append("LS: " + linkspec + "\n");
        s.append("Quality: " + quality + "\n");
        if(mapping != null)
            s.append("Mapping: " + mapping.size() + "\n");
        return s.toString();
    }

    /**
     * @return the link specification
     */
    public LinkSpecification getLinkSpecification() {
        return linkspec;
    }

    /**
     * @param spec the link specification
     */
    public void setLinkSpecification(LinkSpecification spec) {
        this.linkspec = spec;
    }

    /**
     * @return the mapping
     */
    public AMapping getMapping() {
        return mapping;
    }

    /**
     * @param mapping the mapping
     */
    public void setMapping(AMapping mapping) {
        this.mapping = mapping;
    }

    /**
     * @return the value of quality measure
     */
    public double getQuality() {
        return quality;
    }

    /**
     * @param quality the value of quality measure
     */
    public void setQuality(double quality) {
        this.quality = quality;
    }

    /**
     * Add additional computation detail.
     *
     * @param key computation detail name
     * @param value computation detail value
     */
    public void addDetail(String key, Object value) {
        details.put(key, value);
    }

    /**
     * @return the additional computation details
     */
    public Map<String, Object> getDetails() {
        return details;
    }

    public List<SimpleClassifier> getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(List<SimpleClassifier> classifiers) {
        this.classifiers = classifiers;
    }

    /**
     * Some ML algorithms such as Euclid don't produce LS but Mapping build by so called
     * classifiers. This method checks whether this (also) holds those.
     * @return
     */
    public boolean classifiersSet() {
        return classifiers != null && classifiers.size()>0;
    }

}
