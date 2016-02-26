package org.aksw.limes.core.gui.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.aksw.limes.core.gui.util.sparql.SPARQLHelper;
/** A SPARQL restriction which consists of a property and an object, restricting the resulting triples of a query to only
 * those subjects ?s who fulfill the statement "?s property object". Example: "rdfs:type dbpedia-ontology:City".
 * Also contains utility methods which help construct such queries.
 * @author Konrad Höffner */
// TODO: Behandlung von <classname> und sowas, mitspeichern, entfernen, erzeugen?
public class Restriction implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String property;
	private String object;

	public String getProperty(){return property;}
	public String getObject(){return object;}

	@SuppressWarnings("unused")
	private Restriction(){} // for GWT RPC serialization

	public Restriction(String property, String object)
	{
		if(property==null|object==null||property.isEmpty()||object.isEmpty()) throw new IllegalArgumentException();
		this.property = property;
		this.object = object;
	}

	public static Restriction fromString(String s) throws IllegalArgumentException
	{
		String tokens[] = s.split("\\s+");
		if(tokens.length<2||tokens.length>3) throw new IllegalArgumentException("parameter s has to consist of exactly two or three parts separated by space characters, instead it is \""+s+"\".");
		if(tokens.length==3) {return new Restriction(tokens[1],tokens[2]);}
		return new Restriction(tokens[0],tokens[1]);
	}

	public static String restrictionUnion(Collection<Restriction> restrictions, String var) throws IllegalArgumentException
	{
			StringBuilder restrictionBuilder = new StringBuilder();
			for(Restriction restriction:restrictions)
			{
				if(restriction==null) throw new IllegalArgumentException("Oone of the input restrictions was null.");
				if(restrictionBuilder.length()>0) restrictionBuilder.append(" UNION ");
				// will result in a superflous pair curly of brackets if there is no union however thats not harmful to SPARQL at least not in my tests so there is no special code for this case
				restrictionBuilder.append('{'+restriction.toString(var)+'}');
			}
			return restrictionBuilder.toString();
	}

	@Override
	public String toString()
	{
		return property+' '+object;
	}

	public String toString(String var)
	{
		return '?'+var+' '+SPARQLHelper.wrapIfNecessary(property)+' '+SPARQLHelper.wrapIfNecessary(object);
	}

	public static List<Restriction> fromString(List<String> restrictionStrings)
	{
		Vector<Restriction> restrictions = new Vector<Restriction>();
		for(String restrictionString: restrictionStrings) {restrictions.add(Restriction.fromString(restrictionString));}
		return restrictions;
	}

	public static String restrictionUnion(Vector<String> restrictionStrings,String var)
	{
		return restrictionUnion(fromString(restrictionStrings), var);
	}
}
