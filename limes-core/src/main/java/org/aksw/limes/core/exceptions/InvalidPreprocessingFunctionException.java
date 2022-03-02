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
package org.aksw.limes.core.exceptions;

public class InvalidPreprocessingFunctionException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = -3378727153103619947L;


    /**
     * Constructor of InvalidPreprocessingFunctionException class.
     *
     * @param name,
     *            Name of the wrong preprocessing function
     */
    public InvalidPreprocessingFunctionException(String name) {
        super("Unknown preprocessing function " + name + ".");
    }

}