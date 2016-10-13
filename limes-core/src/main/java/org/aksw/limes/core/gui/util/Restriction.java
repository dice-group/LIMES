package org.aksw.limes.core.gui.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.aksw.limes.core.gui.util.sparql.SPARQLHelper;

/**
 * A SPARQL restriction which consists of a property and an object, restricting the resulting triples of a query to only
 * those subjects ?s who fulfill the statement "?s property object". Example: "rdfs:type dbpedia-ontology:City".
 * Also contains utility methods which help construct such queries.
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
// TODO: Behandlung von <classname> und sowas, mitspeichern, entfernen, erzeugen?
public class Restriction implements Serializable {
    private static final long serialVersionUID = 1L;
    private String property;
    private String object;

    @SuppressWarnings("unused")
    private Restriction() {
    } // for GWT RPC serialization

    /**
     * Constructor
     * @param property property
     * @param object object
     */
    public Restriction(String property, String object) {
        if (property == null | object == null || property.isEmpty() || object.isEmpty())
            throw new IllegalArgumentException();
        this.property = property;
        this.object = object;
    }

    /**
     * returns a Restriction from String
     * @param s string to get restriction from
     * @return restriction
     * @throws IllegalArgumentException thrown if parameter s does not consist of exactly two or three parts separated by space characters
     */
    public static Restriction fromString(String s) throws IllegalArgumentException {
        String tokens[] = s.split("\\s+");
        if (tokens.length < 2 || tokens.length > 3)
            throw new IllegalArgumentException("parameter s has to consist of exactly two or three parts separated by space characters, instead it is \"" + s + "\".");
        if (tokens.length == 3) {
            return new Restriction(tokens[1], tokens[2]);
        }
        return new Restriction(tokens[0], tokens[1]);
    }

    /**
     * joins restriction with union
     * @param restrictions restrictions
     * @param var var
     * @return restriction joined with union
     * @throws IllegalArgumentException thrown if on of the input restrictions was null
     */
    public static String restrictionUnion(Collection<Restriction> restrictions, String var) throws IllegalArgumentException {
        StringBuilder restrictionBuilder = new StringBuilder();
        for (Restriction restriction : restrictions) {
            if (restriction == null) throw new IllegalArgumentException("Oone of the input restrictions was null.");
            if (restrictionBuilder.length() > 0) restrictionBuilder.append(" UNION ");
            // will result in a superflous pair curly of brackets if there is no union however thats not harmful to SPARQL at least not in my tests so there is no special code for this case
            restrictionBuilder.append('{' + restriction.toString(var) + '}');
        }
        return restrictionBuilder.toString();
    }

    /**
     * creates a list of restrictions from a list of strings
     * @param restrictionStrings restrictionStrings
     * @return list of restrictions
     */
    public static List<Restriction> fromString(List<String> restrictionStrings) {
        Vector<Restriction> restrictions = new Vector<Restriction>();
        for (String restrictionString : restrictionStrings) {
            restrictions.add(Restriction.fromString(restrictionString));
        }
        return restrictions;
    }

    /**
     * creates a list of restrictions from the vector of strings and joins them with union operator
     * @param restrictionStrings restrictionStrings
     * @param var var
     * @return string with restrictions joined with union
     */
    public static String restrictionUnion(Vector<String> restrictionStrings, String var) {
        return restrictionUnion(fromString(restrictionStrings), var);
    }

    /**
     * returns property
     * @return property
     */
    public String getProperty() {
        return property;
    }

    /**
     * returns object
     * @return object
     */
    public String getObject() {
        return object;
    }

    @Override
    public String toString() {
        return property + ' ' + object;
    }

    /**
     * String representation with var
     * @param var var
     * @return string representation
     */
    public String toString(String var) {
        return '?' + var + ' ' + SPARQLHelper.wrapIfNecessary(property) + ' ' + SPARQLHelper.wrapIfNecessary(object);
    }
}
