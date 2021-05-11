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
package org.aksw.limes.core.util;

import org.apache.log4j.Logger;

import java.util.Stack;

public class ParenthesisMatcher {
    static Logger logger = Logger.getLogger("LIMES");

    public ParenthesisMatcher() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Computes the position of a matching closing parenthesis given the
     * position of the opening parenthesis
     *
     * @param str
     *            String in which the parenthesis should be matched
     * @param openParenthesisIndex
     *            index of the parenthesis which should be matched with a
     *            closing one
     * @return index of closing parenthesis or -1 if string is empty or contains
     *         unmatched parenthesis
     */
    public static int findMatchingParenthesis(String str,
                                              int openParenthesisIndex) {
        if (str.isEmpty())
            return -1;
        if (str.charAt(openParenthesisIndex) != '{'
                && str.charAt(openParenthesisIndex) != '('
                && str.charAt(openParenthesisIndex) != '[') {
            logger.info("openParenthesisIndex is not the index of a open parenthesis. Returning -1");
            return -1;
        }

        Stack<Character> stack = new Stack<Character>();
        for (int i = openParenthesisIndex; i < str.length(); i++) {
            char current = str.charAt(i);
            if (current == '{' || current == '(' || current == '[') {
                stack.push(current);
            }

            if (current == '}' || current == ')' || current == ']') {
                if (stack.isEmpty())
                    return -1;

                char last = stack.peek();
                if (current == '}' && last == '{' || current == ')'
                        && last == '(' || current == ']' && last == '[') {
                    stack.pop();
                    if(stack.isEmpty()){
                        return i;
                    }
                } else {
                    return -1;
                }
            }

        }

        return -7;
    }

    public static void main(String[] args){
        int openingParenthesis = 44;
//		String test = "for (int i = 0; i < 10; i++) { System.out.println(i);}";
        String test = "[jaccard#title|name: <= 0.857143, > 0.857143[trigrams#description|description: <= 0.769231, > 0.769231[exactmatch#manufacturer|manufacturer: <= 0, > 0[levenshtein#manufacturer|manufacturer: <= 0.0625, > 0.0625[positive (1383.0/1.0)][jaro#manufacturer|manufacturer: <= 0.753846, > 0.753846[levenshtein#title|name: <= 0.184211, > 0.184211[negative (2.0)][positive (2.0)]][positive (73.0)]]][positive (43.0/1.0)]][cosine#manufacturer|manufacturer: <= 0, > 0[positive (20.0/1.0)][cosine#title|name: <= 0, > 0[positive (3.0/1.0)][negative (2.0)]]]][negative (108.0)]]";
        System.out.println(test.substring(openingParenthesis +1, ParenthesisMatcher.findMatchingParenthesis(test, openingParenthesis)));
    }
}
