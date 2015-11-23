package org.aksw.limes.core.io.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

/**
 *
 * @author ngonga
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 23, 2015
 */
public class Preprocessor {
	static Logger logger = Logger.getLogger(Preprocessor.class.getName());

	private static final String DATE 			= "date";
	private static final String REPLACE 		= "replace";
	private static final String REGEXREPLACE	= "regexreplace";
	private static final String FAHRENHEIT 		= "fahrenheit";
	private static final String URIASSTRING		= "uriasstring";
	private static final String REMOVEBRACES	= "removebraces";
	private static final String CELSIUS 		= "celsius";
	private static final String REGULAR_ALPHABET= "regularAlphabet";
	private static final String UPPERCASE 		= "uppercase";
	private static final String LOWERCASE 		= "lowercase";
	private static final String CLEANIRI 		= "cleaniri";
	private static final String AT 				= "@";
	private static final String NOLANG 			= "nolang";
	private static final String NUMBER 			= "number";


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
		if (function.startsWith(LOWERCASE)) {
			return entry.toLowerCase();
		}
		if (function.startsWith(UPPERCASE)) {
			return entry.toUpperCase();
		}
		if (function.startsWith(REPLACE)) {
			//function = function.replaceAll(Pattern.quote("_"), " ");
			//System.out.println(">>>"+function);
			String replaced = function.substring(8, function.indexOf(","));
			String replacee = function.substring(function.indexOf(",") + 1, function.indexOf(")"));
			//System.out.println("<"+replaced + ">, <" + replacee + ">");
			return entry.replaceAll(Pattern.quote(replaced), replacee);
		}
		if (function.startsWith(REGEXREPLACE)) { //e.g replace((*),)
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
		if (function.startsWith(NOLANG)) {
			if (entry.contains(AT)) {
				return entry.substring(0, entry.lastIndexOf(AT));
			} else {
				return entry;
			}
		}
		if (function.startsWith(CLEANIRI)) {
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
		if (function.startsWith(REMOVEBRACES)) {
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
		if (function.startsWith(URIASSTRING)) {
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
