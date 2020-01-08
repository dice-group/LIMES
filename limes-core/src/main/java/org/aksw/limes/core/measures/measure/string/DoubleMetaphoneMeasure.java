package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;

import java.util.Arrays;
import java.util.List;

public class DoubleMetaphoneMeasure extends StringMeasure {

    public static List<String> getCode(String word) {
        return Arrays.asList(calculateMetaphone(word));
    }

    private double getProximity(char[] s1, char[] s2) {
        int shorter;
        int longer;
        if (s1.length>s2.length) {
        	shorter = s2.length; 
        	longer = s1.length;
        }else {
        	shorter =  s1.length;
        	longer = s2.length;
        }
        double distance = 0d;
        for (int i = 0; i < shorter; i++)
            if (s1[i] != s2[i])
                distance += 1d;
        return (1.0d - (distance / (double) longer));
    }
    
    public double proximity(String s1, String s2) {
    	List<String> map1 = DoubleMetaphoneMeasure.getCode(s1);
    	List<String> map2 = DoubleMetaphoneMeasure.getCode(s2);
    	double proximity = 0d;
    	double check;
    	for (String k1: map1) {
    		for(String k2: map2) {
    			check = getProximity(k1.toCharArray(), k2.toCharArray());
    			if (check > proximity) {
    				proximity = check;
    			}
    		}
    	}
        return proximity;
    }

    @Override
    public int getPrefixLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMidLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean computableViaOverlap() {
        return false;
    }

    @Override
    public double getSimilarity(Object object1, Object object2) {
        return proximity(object1.toString(), object2.toString());
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        double value = 0;
        double sim = 0;
        for (String source : instance1.getProperty(property1)) {
            for (String target : instance2.getProperty(property2)) {
                sim = proximity(source, target);
                if (sim > value) {
                    value = sim;
                }
            }
        }
        return sim;
    }

    @Override
    public String getName() {
        return "doublemeta";
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

    private static String[] calculateMetaphone(String word) {
        int size = word.length();
        int last = size - 1;
        // pad the original string so that we can index beyond the edge of the world
        word = word.toUpperCase() + "     ";
        String[] result = {"", ""};
        int current = 0;
        boolean slavoGermanic = checkSlavoGermanic(word);
        // skip these when at start of word
        if (stringAt(word, 0, 2, "GN", "KN", "PN", "WR", "PS", "")) {
            current += 1;
        }
        // initial 'X' is pronounced 'Z' e.g. 'Xavier'
        if (getAt(word, 0) == 'X') {
            metaphAdd(result, "S"); //'Z' maps to 'S'
            current += 1;
        }
        while ((result[0].length() < 4) || (result[1].length() < 4)) {
            if (current > last) {
                break;
            }
            switch (getAt(word, current)) {
                case 'A':
                case 'E':
                case 'I':
                case 'O':
                case 'U':
                case 'Y':
                    if (current == 0) { //all init vowels now map to 'A'
                        metaphAdd(result, "A");
                    }
                    current += 1;
                    break;

                case 'B':

                    //"-mb", e.g", "dumb", already skipped over...
                    metaphAdd(result, "P");

                    if (getAt(word, current + 1) == 'B') {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    break;

                case 'Ç':
                    metaphAdd(result, "S");
                    current += 1;
                    break;

                case 'C':
                    //various germanic
                    if ((current > 1)
                            && !isVowel(word, current - 2)
                            && stringAt(word, (current - 1), 3, "ACH", "")
                            && ((getAt(word, current + 2) != 'I') && ((getAt(word, current + 2) != 'E')
                            || stringAt(word, (current - 2), 6, "BACHER", "MACHER", "")))) {
                        metaphAdd(result, "K");
                        current += 2;
                        break;
                    }

                    //special case 'caesar'
                    if ((current == 0) && stringAt(word, current, 6, "CAESAR", "")) {
                        metaphAdd(result, "S");
                        current += 2;
                        break;
                    }

                    //italian 'chianti'
                    if (stringAt(word, current, 4, "CHIA", "")) {
                        metaphAdd(result, "K");
                        current += 2;
                        break;
                    }

                    if (stringAt(word, current, 2, "CH", "")) {
                        //find 'michael'
                        if ((current > 0) && stringAt(word, current, 4, "CHAE", "")) {
                            metaphAdd(result, "K", "X");
                            current += 2;
                            break;
                        }

                        //greek roots e.g. 'chemistry', 'chorus'
                        if ((current == 0)
                                && (stringAt(word, (current + 1), 5, "HARAC", "HARIS", "")
                                || stringAt(word, (current + 1), 3, "HOR", "HYM", "HIA", "HEM", ""))
                                && !stringAt(word, 0, 5, "CHORE", "")) {
                            metaphAdd(result, "K");
                            current += 2;
                            break;
                        }

                        //germanic, greek, or otherwise 'ch' for 'kh' sound
                        if ((stringAt(word, 0, 4, "VAN ", "VON ", "") || stringAt(word, 0, 3, "SCH", ""))
                                // 'architect but not 'arch', 'orchestra', 'orchid'
                                || stringAt(word, (current - 2), 6, "ORCHES", "ARCHIT", "ORCHID", "")
                                || stringAt(word, (current + 2), 1, "T", "S", "")
                                || ((stringAt(word, (current - 1), 1, "A", "O", "U", "E", "") || (current == 0))
                                //e.g., 'wachtler', 'wechsler', but not 'tichner'
                                && stringAt(word, (current + 2), 1, "L", "R", "N", "M", "B", "H", "F", "V", "W", " ", ""))) {
                            metaphAdd(result, "K");
                        } else {
                            if (current > 0) {
                                if (stringAt(word, 0, 2, "MC", "")) { //e.g., "McHugh"
                                    metaphAdd(result, "K");
                                } else {
                                    metaphAdd(result, "X", "K");
                                }
                            } else {
                                metaphAdd(result, "X");
                            }
                        }
                        current += 2;
                        break;
                    }
                    //e.g, 'czerny'
                    if (stringAt(word, current, 2, "CZ", "") && !stringAt(word, (current - 2), 4, "WICZ", "")) {
                        metaphAdd(result, "S", "X");
                        current += 2;
                        break;
                    }

                    //e.g., 'focaccia'
                    if (stringAt(word, (current + 1), 3, "CIA", "")) {
                        metaphAdd(result, "X");
                        current += 3;
                        break;
                    }

                    //double 'C', but not if e.g. 'McClellan'
                    if (stringAt(word, current, 2, "CC", "") && !((current == 1) && (getAt(word, 0) == 'M')))
                        //'bellocchio' but not 'bacchus'
                        if (stringAt(word, (current + 2), 1, "I", "E", "H", "") && !stringAt(word, (current + 2), 2, "HU", "")) {
                            //'accident', 'accede' 'succeed'
                            if (((current == 1) && (getAt(word, current - 1) == 'A'))
                                    || stringAt(word, (current - 1), 5, "UCCEE", "UCCES", "")) {
                                metaphAdd(result, "KS");
                            } else {
                                //'bacci', 'bertucci', other italian
                                metaphAdd(result, "X");
                            }
                            current += 3;
                            break;
                        } else {//Pierce's rule
                            metaphAdd(result, "K");
                            current += 2;
                            break;
                        }

                    if (stringAt(word, current, 2, "CK", "CG", "CQ", "")) {
                        metaphAdd(result, "K");
                        current += 2;
                        break;
                    }

                    if (stringAt(word, current, 2, "CI", "CE", "CY", "")) {
                        //italian vs. english
                        if (stringAt(word, current, 3, "CIO", "CIE", "CIA", "")) {
                            metaphAdd(result, "S", "X");
                        } else {
                            metaphAdd(result, "S");
                        }
                        current += 2;
                        break;
                    }

                    //else
                    metaphAdd(result, "K");

                    //name sent in 'mac caffrey', 'mac gregor
                    if (stringAt(word, (current + 1), 2, " C", " Q", " G", "")) {
                        current += 3;
                    } else if (stringAt(word, (current + 1), 1, "C", "K", "Q", "")
                            && !stringAt(word, (current + 1), 2, "CE", "CI", "")) {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    break;

                case 'D':
                    if (stringAt(word, current, 2, "DG", ""))
                        if (stringAt(word, (current + 2), 1, "I", "E", "Y", "")) {
                            //e.g. 'edge'
                            metaphAdd(result, "J");
                            current += 3;
                            break;
                        } else {
                            //e.g. 'edgar'
                            metaphAdd(result, "TK");
                            current += 2;
                            break;
                        }

                    if (stringAt(word, current, 2, "DT", "DD", "")) {
                        metaphAdd(result, "T");
                        current += 2;
                        break;
                    }

                    //else
                    metaphAdd(result, "T");
                    current += 1;
                    break;

                case 'F':
                    if (getAt(word, current + 1) == 'F') {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    metaphAdd(result, "F");
                    break;

                case 'G':
                    if (getAt(word, current + 1) == 'H') {
                        if ((current > 0) && !isVowel(word, current - 1)) {
                            metaphAdd(result, "K");
                            current += 2;
                            break;
                        }

                        if (current < 3) {
                            //'ghislane', ghiradelli
                            if (current == 0) {
                                if (getAt(word, current + 2) == 'I') {
                                    metaphAdd(result, "J");
                                } else {
                                    metaphAdd(result, "K");
                                }
                                current += 2;
                                break;
                            }
                        }
                        //Parker's rule (with some further refinements) - e.g., 'hugh'
                        if (((current > 1) && stringAt(word, (current - 2), 1, "B", "H", "D", ""))
                                //e.g., 'bough'
                                || ((current > 2) && stringAt(word, (current - 3), 1, "B", "H", "D", ""))
                                //e.g., 'broughton'
                                || ((current > 3) && stringAt(word, (current - 4), 1, "B", "H", ""))) {
                            current += 2;
                            break;
                        } else {
                            //e.g., 'laugh', 'McLaughlin', 'cough', 'gough', 'rough', 'tough'
                            if ((current > 2)
                                    && (getAt(word, current - 1) == 'U')
                                    && stringAt(word, (current - 3), 1, "C", "G", "L", "R", "T", "")) {
                                metaphAdd(result, "F");
                            } else if ((current > 0) && getAt(word, current - 1) != 'I') {
                                metaphAdd(result, "K");
                            }
                            current += 2;
                            break;
                        }
                    }

                    if (getAt(word, current + 1) == 'N') {
                        if ((current == 1) && isVowel(word, 0) && !slavoGermanic) {
                            metaphAdd(result, "KN", "N");
                        } else
                            //not e.g. 'cagney'
                            if (!stringAt(word, (current + 2), 2, "EY", "")
                                    && (getAt(word, current + 1) != 'Y') && !slavoGermanic) {
                                metaphAdd(result, "N", "KN");
                            } else
                                metaphAdd(result, "KN");
                        current += 2;
                        break;
                    }

                    //'tagliaro'
                    if (stringAt(word, (current + 1), 2, "LI", "") && !slavoGermanic) {
                        metaphAdd(result, "KL", "L");
                        current += 2;
                        break;
                    }

                    //-ges-,-gep-,-gel-, -gie- at beginning
                    if ((current == 0)
                            && ((getAt(word, current + 1) == 'Y')
                            || stringAt(word, (current + 1), 2, "ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER", ""))) {
                        metaphAdd(result, "K", "J");
                        current += 2;
                        break;
                    }

                    // -ger-,  -gy-
                    if ((stringAt(word, (current + 1), 2, "ER", "") || (getAt(word, current + 1) == 'Y'))
                            && !stringAt(word, 0, 6, "DANGER", "RANGER", "MANGER", "")
                            && !stringAt(word, (current - 1), 1, "E", "I", "")
                            && !stringAt(word, (current - 1), 3, "RGY", "OGY", "")) {
                        metaphAdd(result, "K", "J");
                        current += 2;
                        break;
                    }

                    // italian e.g, 'biaggi'
                    if (stringAt(word, (current + 1), 1, "E", "I", "Y", "") || stringAt(word, (current - 1), 4, "AGGI", "OGGI", "")) {
                        //obvious germanic
                        if ((stringAt(word, 0, 4, "VAN ", "VON ", "") || stringAt(word, 0, 3, "SCH", ""))
                                || stringAt(word, (current + 1), 2, "ET", "")) {
                            metaphAdd(result, "K");
                        } else
                        //always soft if french ending
                        {
                            if (stringAt(word, (current + 1), 4, "IER ", "")) {
                                metaphAdd(result, "J");
                            } else {
                                metaphAdd(result, "J", "K");
                            }
                        }
                        current += 2;
                        break;
                    }

                    if (getAt(word, current + 1) == 'G') {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    metaphAdd(result, "K");
                    break;

                case 'H':
                    //only keep if first & before vowel or btw. 2 vowels
                    if (((current == 0) || isVowel(word, current - 1))
                            && isVowel(word, current + 1)) {
                        metaphAdd(result, "H");
                        current += 2;
                    } else//also takes care of 'HH'
                        current += 1;
                    break;

                case 'J':
                    //obvious spanish, 'jose', 'san jacinto'
                    if (stringAt(word, current, 4, "JOSE", "") || stringAt(word, 0, 4, "SAN ", "")) {
                        if (((current == 0) && (getAt(word, current + 4) == ' ')) || stringAt(word, 0, 4, "SAN ", ""))
                            metaphAdd(result, "H");
                        else {
                            metaphAdd(result, "J", "H");
                        }
                        current += 1;
                        break;
                    }

                    if ((current == 0) && !stringAt(word, current, 4, "JOSE", "")) {
                        metaphAdd(result, "J", "A");//Yankelovich/Jankelowicz
                    } else {
                        //spanish pron. of e.g. 'bajador'
                        if (isVowel(word, current - 1)
                                && !slavoGermanic
                                && ((getAt(word, current + 1) == 'A') || (getAt(word, current + 1) == 'O'))) {
                            metaphAdd(result, "J", "H");
                        } else if (current == last) {
                            metaphAdd(result, "J", " ");
                        } else if (!stringAt(word, (current + 1), 1, "L", "T", "K", "S", "N", "M", "B", "Z", "")
                                && !stringAt(word, (current - 1), 1, "S", "K", "L", "")) {
                            metaphAdd(result, "J");
                        }
                    }

                    if (getAt(word, current + 1) == 'J') { //it could happen!
                        current += 2;
                    } else {
                        current += 1;
                    }
                    break;

                case 'K':
                    if (getAt(word, current + 1) == 'K') {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    metaphAdd(result, "K");
                    break;

                case 'L':
                    if (getAt(word, current + 1) == 'L') {
                        //spanish e.g. 'cabrillo', 'gallegos'
                        if (((current == (size - 3))
                                && stringAt(word, (current - 1), 4, "ILLO", "ILLA", "ALLE", ""))
                                || ((stringAt(word, (last - 1), 2, "AS", "OS", "") || stringAt(word, last, 1, "A", "O", ""))
                                && stringAt(word, (current - 1), 4, "ALLE", ""))) {
                            metaphAdd(result, "L", " ");
                            current += 2;
                            break;
                        }
                        current += 2;
                    } else {
                        current += 1;
                    }
                    metaphAdd(result, "L");
                    break;

                case 'M':
                    if ((stringAt(word, (current - 1), 3, "UMB", "")
                            && (((current + 1) == last) || stringAt(word, (current + 2), 2, "ER", "")))
                            //'dumb','thumb'
                            || (getAt(word, current + 1) == 'M')) {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    metaphAdd(result, "M");
                    break;

                case 'N':
                    if (getAt(word, current + 1) == 'N') {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    metaphAdd(result, "N");
                    break;

                case 'Ñ':
                    current += 1;
                    metaphAdd(result, "N");
                    break;

                case 'P':
                    if (getAt(word, current + 1) == 'H') {
                        metaphAdd(result, "F");
                        current += 2;
                        break;
                    }

                    //also account for "campbell", "raspberry"
                    if (stringAt(word, (current + 1), 1, "P", "B", "")) {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    metaphAdd(result, "P");
                    break;

                case 'Q':
                    if (getAt(word, current + 1) == 'Q') {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    metaphAdd(result, "K");
                    break;

                case 'R':
                    //french e.g. 'rogier', but exclude 'hochmeier'
                    if ((current == last)
                            && !slavoGermanic
                            && stringAt(word, (current - 2), 2, "IE", "")
                            && !stringAt(word, (current - 4), 2, "ME", "MA", "")) {
                        metaphAdd(result, "", "R");
                    } else {
                        metaphAdd(result, "R");
                    }

                    if (getAt(word, current + 1) == 'R') {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    break;

                case 'S':
                    //special cases 'island', 'isle', 'carlisle', 'carlysle'
                    if (stringAt(word, (current - 1), 3, "ISL", "YSL", "")) {
                        current += 1;
                        break;
                    }

                    //special case 'sugar-'
                    if ((current == 0) && stringAt(word, current, 5, "SUGAR", "")) {
                        metaphAdd(result, "X", "S");
                        current += 1;
                        break;
                    }

                    if (stringAt(word, current, 2, "SH", "")) {
                        //germanic
                        if (stringAt(word, (current + 1), 4, "HEIM", "HOEK", "HOLM", "HOLZ", "")) {
                            metaphAdd(result, "S");
                        } else {
                            metaphAdd(result, "X");
                        }
                        current += 2;
                        break;
                    }

                    //italian & armenian
                    if (stringAt(word, current, 3, "SIO", "SIA", "") || stringAt(word, current, 4, "SIAN", "")) {
                        if (!slavoGermanic) {
                            metaphAdd(result, "S", "X");
                        } else {
                            metaphAdd(result, "S");
                        }
                        current += 3;
                        break;
                    }

                    //german & anglicisations, e.g. 'smith' match 'schmidt', 'snider' match 'schneider'
                    //also, -sz- in slavic language altho in hungarian it is pronounced 's'
                    if (((current == 0)
                            && stringAt(word, (current + 1), 1, "M", "N", "L", "W", ""))
                            || stringAt(word, (current + 1), 1, "Z", "")) {
                        metaphAdd(result, "S", "X");
                        if (stringAt(word, (current + 1), 1, "Z", "")) {
                            current += 2;
                        } else {
                            current += 1;
                        }
                        break;
                    }

                    if (stringAt(word, current, 2, "SC", "")) {
                        //Schlesinger's rule
                        if (getAt(word, current + 2) == 'H') {
                            //dutch origin, e.g. 'school', 'schooner'
                            if (stringAt(word, (current + 3), 2, "OO", "ER", "EN", "UY", "ED", "EM", "")) {
                                //'schermerhorn', 'schenker'
                                if (stringAt(word, (current + 3), 2, "ER", "EN", "")) {
                                    metaphAdd(result, "X", "SK");
                                } else {
                                    metaphAdd(result, "SK");
                                }
                                current += 3;
                                break;
                            } else {
                                if ((current == 0) && !isVowel(word, 3) && (getAt(word, 3) != 'W')) {
                                    metaphAdd(result, "X", "S");
                                } else {
                                    metaphAdd(result, "X");
                                }
                                current += 3;
                                break;
                            }
                        }

                        if (stringAt(word, (current + 2), 1, "I", "E", "Y", "")) {
                            metaphAdd(result, "S");
                            current += 3;
                            break;
                        }
                        //else
                        metaphAdd(result, "SK");
                        current += 3;
                        break;
                    }

                    //french e.g. 'resnais', 'artois'
                    if ((current == last) && stringAt(word, (current - 2), 2, "AI", "OI", "")) {
                        metaphAdd(result, "", "S");
                    } else {
                        metaphAdd(result, "S");
                    }

                    if (stringAt(word, (current + 1), 1, "S", "Z", "")) {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    break;

                case 'T':
                    if (stringAt(word, current, 4, "TION", "")) {
                        metaphAdd(result, "X");
                        current += 3;
                        break;
                    }

                    if (stringAt(word, current, 3, "TIA", "TCH", "")) {
                        metaphAdd(result, "X");
                        current += 3;
                        break;
                    }

                    if (stringAt(word, current, 2, "TH", "")
                            || stringAt(word, current, 3, "TTH", "")) {
                        //special case 'thomas', 'thames' or germanic
                        if (stringAt(word, (current + 2), 2, "OM", "AM", "")
                                || stringAt(word, 0, 4, "VAN ", "VON ", "")
                                || stringAt(word, 0, 3, "SCH", "")) {
                            metaphAdd(result, "T");
                        } else {
                            metaphAdd(result, "0", "T");
                        }
                        current += 2;
                        break;
                    }

                    if (stringAt(word, (current + 1), 1, "T", "D", "")) {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    metaphAdd(result, "T");
                    break;

                case 'V':
                    if (getAt(word, current + 1) == 'V') {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    metaphAdd(result, "F");
                    break;

                case 'W':
                    //can also be in middle of word
                    if (stringAt(word, current, 2, "WR", "")) {
                        metaphAdd(result, "R");
                        current += 2;
                        break;
                    }

                    if ((current == 0)
                            && (isVowel(word, current + 1) || stringAt(word, current, 2, "WH", ""))) {
                        //Wasserman should match Vasserman
                        if (isVowel(word, current + 1)) {
                            metaphAdd(result, "A", "F");
                        } else
                        //need Uomo to match Womo
                        {
                            metaphAdd(result, "A");
                        }
                    }

                    //Arnow should match Arnoff
                    if (((current == last) && isVowel(word, current - 1))
                            || stringAt(word, (current - 1), 5, "EWSKI", "EWSKY", "OWSKI", "OWSKY", "")
                            || stringAt(word, 0, 3, "SCH", "")) {
                        metaphAdd(result, "", "F");
                        current += 1;
                        break;
                    }

                    //polish e.g. 'filipowicz'
                    if (stringAt(word, current, 4, "WICZ", "WITZ", "")) {
                        metaphAdd(result, "TS", "FX");
                        current += 4;
                        break;
                    }

                    //else skip it
                    current += 1;
                    break;

                case 'X':
                    //french e.g. breaux
                    if (!((current == last)
                            && (stringAt(word, (current - 3), 3, "IAU", "EAU", "")
                            || stringAt(word, (current - 2), 2, "AU", "OU", "")))) {
                        metaphAdd(result, "KS");
                    }
                    if (stringAt(word, (current + 1), 1, "C", "X", "")) {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    break;

                case 'Z':
                    //chinese pinyin e.g. 'zhao'
                    if (getAt(word, current + 1) == 'H') {
                        metaphAdd(result, "J");
                        current += 2;
                        break;
                    } else if (stringAt(word, (current + 1), 2, "ZO", "ZI", "ZA", "")
                            || (slavoGermanic && ((current > 0) && getAt(word, current - 1) != 'T'))) {
                        metaphAdd(result, "S", "TS");
                    } else {
                        metaphAdd(result, "S");
                    }

                    if (getAt(word, current + 1) == 'Z') {
                        current += 2;
                    } else {
                        current += 1;
                    }
                    break;

                default:
                    current += 1;
            }
        }
        return result;
    }

    private static boolean checkSlavoGermanic(String word) {
        return word.contains("W") || word.contains("K") || word.contains("CZ") || word.contains("WITZ");
    }

    private static void metaphAdd(String[] result, String main) {
        result[0] += main;
        result[1] += main;
    }

    private static void metaphAdd(String[] result, String main, String alt) {
        result[0] += main;
        result[1] += alt;
    }

    private static char getAt(String word, int index) {
        if (index >= word.length()) {
            return Character.MIN_VALUE;
        }
        return word.charAt(index);
    }

    private static boolean stringAt(String word, int start, int length, String... values) {
        if (start < 0) {
            return false;
        }
        String target = word.substring(start, start + length);
        for (String test : values) {
            if (test.equals(target)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isVowel(String word, int at) {
        if ((at < 0) || (at >= word.length())) {
            return false;
        }
        char it = word.charAt(at);
        return ((it == 'A') || (it == 'E') || (it == 'I') || (it == 'O') || (it == 'U') || (it == 'Y'));
    }
}
