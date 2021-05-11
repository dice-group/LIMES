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
package org.aksw.limes.core.measures.measure.temporal.simpleTemporal;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.temporal.TemporalMeasure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implements the temporal successor measure class.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class SuccessorMeasure extends TemporalMeasure {

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSimilarity(Object object1, Object object2) {
        double sim = 0;
        String split1[] = ((String) object1).split("\\|");
        String split2[] = ((String) object2).split("\\|");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date date1, date2;
        long epoch1 = 0, epoch2 = 0;
        try {
            date1 = df.parse(split1[0]);
            epoch1 = date1.getTime();
        } catch (ParseException e) {
            System.err.println("Exiting..");
        }
        try {
            date2 = df.parse(split2[0]);
            epoch2 = date2.getTime();
        } catch (ParseException e) {
            System.err.println("Exiting..");

        }

        if (epoch1 < epoch2)
            sim = 1;
        else
            sim = 0;

        return sim;
    }

    /**
     * Returns the similarity between two instances given their begin dates. If
     * the first instance has lower begin date than the begin date of the second
     * instance then their similarity is 1 and 0 otherwise.
     *
     *
     * @return The similarity of the instances
     */
    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        String beginDate1 = this.getFirstProperty(property1);

        String beginDate2 = this.getFirstProperty(property2);

        String s1 = new String(instance1.getProperty(beginDate1).first());
        String s2 = new String(instance2.getProperty(beginDate2).first());

        return this.getSimilarity(s1, s2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Predecessor";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "temporal";
    }

}
