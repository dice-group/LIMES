/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.io.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

/**
 *
 * @author ngonga
 */
public class Preprocessor {

    static Logger logger = Logger.getLogger("LIMES");

    public static String process(String entry, String functionChain) {
        String result = entry.split("\\^")[0];
        //System.out.println("Function chain = "+functionChain);
        if (functionChain != null) {
            if (!functionChain.equals("")) {
                {
                    String split[] = functionChain.split("->");
                    for (int i = 0; i < split.length; i++) {
                        result = atomicProcess(result, split[i]);
                        //System.out.println(result);
                    }
                }
            }
        }
        //System.out.println("<"+entry+">" + " -> <" + result+">");
        return result;
    }

    public static String atomicProcess(String entry, String function) {
        //System.out.println(entry +" -> "+ function);
        if (function.length() < 2) {
            return entry;
        }
        //remove unneeded xsd information        
        //function = function.toLowerCase();
        if (function.startsWith("lowercase")) {
            return entry.toLowerCase();
        }
        if (function.startsWith("uppercase")) {
            return entry.toUpperCase();
        }
        if (function.startsWith("replace")) {
            //function = function.replaceAll(Pattern.quote("_"), " ");
            //System.out.println(">>>"+function);
            String replaced = function.substring(8, function.indexOf(","));
            String replacee = function.substring(function.indexOf(",") + 1, function.indexOf(")"));
            //System.out.println("<"+replaced + ">, <" + replacee + ">");
            return entry.replaceAll(Pattern.quote(replaced), replacee);
        }
        if (function.startsWith("regexreplace")) { //e.g replace((*),)
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
        if (function.startsWith("nolang")) {
            if (entry.contains("@")) {
                return entry.substring(0, entry.lastIndexOf("@"));
            } else {
                return entry;
            }
        }
        if (function.startsWith("cleaniri")) {
            if (entry.contains("/")) {
                return entry.substring(entry.lastIndexOf("/") + 1);
            } else {
                return entry;
            }
        }
        if (function.startsWith("number")) {
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
        if (function.startsWith("celsius")) {
            //get rid of the type information
            double value = Double.parseDouble(atomicProcess(entry, "number"));
            double result = 32 + value * 9 / 5;
            return result + "";
        }
        if (function.startsWith("fahrenheit")) {
            //get rid of the type information
            double value = Double.parseDouble(atomicProcess(entry, "number"));
            double result = (value - 32) * 5 / 9;
            return result + "";
        }
        if (function.startsWith("date")) {
            return entry.replaceAll("[^0-9,.-]", "");
        }
        if (function.startsWith("removebraces")) {
            int openBrace = entry.indexOf("(");
            int closingBrace = entry.indexOf(")", Math.max(openBrace, 0));
            if (closingBrace > -1 && openBrace > -1) {
                return entry.substring(0, Math.min(closingBrace, openBrace) - 1) + entry.substring(Math.max(openBrace, closingBrace) + 1);
            } else {
                String ret = entry.replaceAll("\\(", "");
                return ret.replaceAll("\\)", "");
            }
        }
        if (function.startsWith("regularAlphabet")) {
            return atomicProcess(entry, "regexreplace([^A-Za-z0-9 ],)");
        } 
        if (function.startsWith("uriasstring")) {
        	return URIasString(entry);
        }
        //function not known...
        else {
        	logger.warn("Unknown preprocessing function "+function);
            return entry;
        }
    }

    public static void main(String args[]) {
//        System.out.println(getPoints("POINT(-0.274278 51.9302)"));
//        String s = "X AS Y";
//        String AS = " AS ";
////        System.out.println("<"+s.substring(s.indexOf(AS)+AS.length(), s.length())+"<");
////        System.out.println("<"+s.substring(0,s.indexOf(AS))+"<");
////        System.out.println(process("Estato do Maria", "replace(Estato do ,)"));
//        String func = "lowercase->regexreplace(\\(.*\\),)";
//        String func2 = "lowercase->removebraces";
//        String func3 = "regularAlphabet";
//        String func4 = "replace(hotel ,)";
//        func = "lowercase->regexreplace(\\(.*\\),)";
//        String toreplace[] = {"hotel larissa", "Kill or Cure (1962 film)", "Shoot Loud, Louder... I Don't Understand", "The Unholy Three (1930 film)"};
////        System.out.println(toreplace.replaceAll(func, ""));
//        for (String entry : toreplace) {
////	        System.out.println(Preprocessor.process(entry, func));
////	        System.out.println(Preprocessor.process(entry, func2));
//            System.out.println(Preprocessor.process(entry, func4));
//        }
//
//
//
//        String dates[] = {"30C", "30.05.1988", "88889", "am 1.1.1983", "1.1.1954", "5-12-1988"};
//        for (String ds : dates) {
//            System.out.println(ds + " =(date)=>" + Preprocessor.process(ds, "date")
//                    + " =(number)=>" + Preprocessor.process(ds, "date")
//                    + " =(celsius)=>" + Preprocessor.process(ds, "date")
//                    + " =(fahrenheit)=>" + Preprocessor.process(ds, "date"));
//        }
        
        String uriReplace[] = {"http://dbpedia.org/resource/Category:Random_House_books",
        		"philip roth, good, category1967 novels, house books, categoryrandom house",
        	};
        String funcs[] = {"uriasstring->lowercase", "replace(category,)"};
        
        for(String org : uriReplace) {
        	System.out.println(org);
        	for(String func:funcs) {
        		
        		System.out.println("\t"+func+" => "+ process(org, func));
        	}
        	
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
     * Returns the last part of an URI as a String. Additional pasing _ as space.
     * @return
     */
    private static String URIasString(String org) {
    	String result = org;
    	if(org.lastIndexOf("/") > 0 && org.lastIndexOf("/")<(org.length()-1)) {
    		result = org.substring(org.lastIndexOf("/")+1);
    	}
    	if(org.lastIndexOf(":") > 0 && org.lastIndexOf(":")<(org.length()-1)) {
    		result = org.substring(org.lastIndexOf(":")+1);
    	}
    	result = result.replaceAll("_", " ");
    	logger.debug("parsed URI "+org+" as "+result );
    	return result;
    }
}
