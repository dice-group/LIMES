package org.aksw.limes.core.measures.measure.string;
import org.aksw.limes.core.io.cache.Instance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;

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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class LessThanMeasure extends StringMeasure  {


	public double getSimilarity(Object a, Object b) {
		String string1 = a + ""; 
		String string2 = b + "";

		return string1.compareTo(string2) < 0 ? 1.0d : 0d;

	}

	public String getType() {
		return "string";
	}

	public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {

		for (String p1 : instance1.getProperty(property1)) {
			for (String p2 : instance2.getProperty(property2)) {
				if (p1.compareTo(p2) < 0) 
					return 1.0d;
			}
		}
		return 0.d;
	}

	public String getName() {
		return "less_than";
	}






	@Override
	public double getRuntimeApproximation(double mappingSize) {
		return mappingSize / 1000d;
	}

	@Override
	public int getPrefixLength(int tokensNumber, double threshold) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMidLength(int tokensNumber, double threshold) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSimilarity(int overlap, int lengthA, int lengthB) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean computableViaOverlap() {
		// TODO Auto-generated method stub
		return false;
	}


}
