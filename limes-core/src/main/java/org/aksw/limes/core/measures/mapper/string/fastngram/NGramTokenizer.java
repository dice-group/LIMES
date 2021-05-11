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
package org.aksw.limes.core.measures.mapper.string.fastngram;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class NGramTokenizer implements ITokenizer {

    @Override
    public Set<String> tokenize(String s, int q) {
        if (s == null) {
            s = "";
        }
        String pad = String.join("", Collections.nCopies(q-1, " "));
        s = pad + s + pad;
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < s.length() - q + 1; i++) {
            tokens.add(s.substring(i, i + q));
        }
        return tokens;
    }
}
