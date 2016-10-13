package org.aksw.limes.core.io.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 23, 2015
 */
public class Preprocessor {
    public static final String DATE             = "date";
    public static final String REPLACE          = "replace";
    public static final String REG_EX_REPLACE   = "regexreplace";
    public static final String FAHRENHEIT       = "fahrenheit";
    public static final String URI_AS_STRING    = "uriasstring";
    public static final String REMOVE_BRACES    = "removebraces";
    public static final String CELSIUS          = "celsius";
    public static final String REGULAR_ALPHABET = "regularAlphabet";
    public static final String UPPER_CASE       = "uppercase";
    public static final String LOWER_CASE       = "lowercase";
    public static final String CLEAN_IRI        = "cleaniri";
    public static final String AT               = "@";
    public static final String NO_LANG          = "nolang";
    public static final String NUMBER 		    = "number";
    static Logger logger = LoggerFactory.getLogger(Preprocessor.class.getName());

    public static String process(String entry, String functionChain) {
        String result = entry.split("\\^")[0];
        logger.debug("Function chain = " + functionChain);
        if (functionChain != null) {
            if (!functionChain.equals("")) {
                {
                    String split[] = functionChain.split("->");
                    for (int i = 0; i < split.length; i++) {
                        result = atomicProcess(result, split[i]);
                        logger.debug(result);
                    }
                }
            }
        }
        logger.debug("<"+entry+">" + " -> <" + result+">");
        return result;
    }

    public static String atomicProcess(String entry, String function) {
        logger.debug(entry +" -> "+ function);
        if (function.length() < 2) {
            return entry;
        }
        //remove unneeded xsd information
        //function = function.toLowerCase();
        if (function.startsWith(LOWER_CASE)) {
            return entry.toLowerCase();
        }
        if (function.startsWith(UPPER_CASE)) {
            return entry.toUpperCase();
        }
        if (function.startsWith(REPLACE)) {
            //function = function.replaceAll(Pattern.quote("_"), " ");
            logger.debug(">>>"+function);
            String replaced = function.substring(8, function.indexOf(","));
            String replacee = function.substring(function.indexOf(",") + 1, function.indexOf(")"));
            logger.debug("<"+replaced + ">, <" + replacee + ">");
            return entry.replaceAll(Pattern.quote(replaced), replacee);
        }
        if (function.startsWith(REG_EX_REPLACE)) { //e.g replace((*),)
            try {
                String replaced = function.substring(13, function.lastIndexOf(","));
                String replacee = function.substring(function.lastIndexOf(",") + 1, function.indexOf(")", function.lastIndexOf(",")));
                return entry.replaceAll(replaced, replacee).trim();
            } catch (IndexOutOfBoundsException e1) {
                logger.warn("Preprocessing function " + function + " could not be read.");
            } catch (PatternSyntaxException e2) {
                logger.warn("Preprocessing function " + function + " could not be read. Error in Regular Expression.");
            }
            return entry;
        }
        if (function.startsWith(NO_LANG)) {
            if (entry.contains(AT)) {
                return entry.substring(0, entry.lastIndexOf(AT));
            } else {
                return entry;
            }
        }
        if (function.startsWith(CLEAN_IRI)) {
            if (entry.contains("/")) {
                return entry.substring(entry.lastIndexOf("/") + 1);
            } else {
                return entry;
            }
        }
        if (function.startsWith(NUMBER)) {
            //get rid of the type information
            String value = entry.replaceAll("[^0-9,.,-]", "");
            if (value.length() == 0) {
                return 0 + "";
            } else {
                try {
                    Double.parseDouble(value);
                } catch (Exception e) {
                    return 0 + "";
                }
            }
            return value;
        }
        if (function.startsWith(CELSIUS)) {
            //get rid of the type information
            double value = Double.parseDouble(atomicProcess(entry, NUMBER));
            double result = 32 + value * 9 / 5;
            return result + "";
        }
        if (function.startsWith(FAHRENHEIT)) {
            //get rid of the type information
            double value = Double.parseDouble(atomicProcess(entry, NUMBER));
            double result = (value - 32) * 5 / 9;
            return result + "";
        }
        if (function.startsWith(DATE)) {
            return entry.replaceAll("[^0-9,.-]", "");
        }
        if (function.startsWith(REMOVE_BRACES)) {
            int openBrace = entry.indexOf("(");
            int closingBrace = entry.indexOf(")", Math.max(openBrace, 0));
            if (closingBrace > -1 && openBrace > -1) {
                return entry.substring(0, Math.min(closingBrace, openBrace) - 1) + entry.substring(Math.max(openBrace, closingBrace) + 1);
            } else {
                String ret = entry.replaceAll("\\(", "");
                return ret.replaceAll("\\)", "");
            }
        }
        if (function.startsWith(REGULAR_ALPHABET)) {
            return atomicProcess(entry, "regexreplace([^A-Za-z0-9 ],)");
        }
        if (function.startsWith(URI_AS_STRING)) {
            return URIasString(entry);
        }
        //function not known...
        else {
            logger.warn("Unknown preprocessing function " + function);
            return entry;
        }
    }


    public static List<Double> getPoints(String rawValue) {
        if (!(rawValue.contains("(") && rawValue.contains(")"))) {
            return new ArrayList<Double>();
        }
        String s = rawValue.substring(rawValue.indexOf("(") + 1, rawValue.indexOf(")"));
        String split[] = s.split(" ");
        List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < split.length; i++) {
            result.add(Double.parseDouble(split[i]));
        }
        return result;
    }

    /**
     * Returns the last part of an URI as a String. Additional parsing _ as space.
     *
     * @return
     */
    private static String URIasString(String org) {
        String result = org;
        if (org.lastIndexOf("/") > 0 && org.lastIndexOf("/") < (org.length() - 1)) {
            result = org.substring(org.lastIndexOf("/") + 1);
        }
        if (org.lastIndexOf(":") > 0 && org.lastIndexOf(":") < (org.length() - 1)) {
            result = org.substring(org.lastIndexOf(":") + 1);
        }
        result = result.replaceAll("_", " ");
        logger.debug("parsed URI " + org + " as " + result);
        return result;
    }
}
