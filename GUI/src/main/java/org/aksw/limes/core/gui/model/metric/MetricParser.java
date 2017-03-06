package org.aksw.limes.core.gui.model.metric;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.gui.model.metric.Property.Origin;

/**
 * Parses Metric to Expression for the Config Reader
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class MetricParser {
    /**
     * Splits the String
     *
     * @param s
     *         String to Split
     * @return String list
     */
    protected static String[] splitFunc(String s) {
        if (!s.contains("(")) {
            return new String[]{s};
        }
        List<String> tokenList = new LinkedList<String>();
        int i = s.indexOf("(");
        tokenList.add(s.substring(0, i));
        s = s.substring(i, s.length());
        int depth = 0;
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case ('('):
                    if (depth > 0) {
                        sb.append(c);
                    }
                    depth++;
                    break;
                case (')'):
                    if (depth > 1) {
                        sb.append(c);
                    }
                    depth--;
                    break;
                case (','):
                    if (depth > 1) {
                        sb.append(c);
                    } else {
                        tokenList.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    break;
                default:
                    sb.append(c);
            }
        }
        tokenList.add(sb.toString());
        return tokenList.toArray(new String[0]);
    }

    /**
     * Sets the parameters of a Node from StringExpression
     *
     * @param node
     *         Node to be configurated
     * @param s
     *         Expression
     * @param pos
     *         Position of Node
     * @return Node String
     */
    protected static String setParametersFromString(Node node, String s, int pos) {
        if (node.hasFrontParameters()) {
            int i = s.indexOf("*");
            if (i == -1)
                return s;
            Double d = Double.valueOf(s.substring(0, i));
            s = s.substring(i + 1);
            switch (pos) {
                case (0):
                    node.param1 = d;
                    break;
                case (1):
                    node.param2 = d;
            }
        } else {
            int i = s.lastIndexOf("|");
            if (i == -1 || i < s.lastIndexOf(")")) {
                return s;
            }
            Double d = Double.valueOf(s.substring(i + 1));
            s = s.substring(0, i);
            switch (pos) {
                case (0):
                    node.param1 = d;
                    break;
                case (1):
                    node.param2 = d;
            }
        }
        return s;
    }

    /**
     * Parses Node from String
     *
     * @param parent
     *         Already parsed Parent
     * @param s
     *         String to parse
     * @param sourceVar
     *         Label
     * @param pos
     *         Position of Node
     * @return Node
     * @throws MetricFormatException
     */
    protected static Node parsePart(Node parent, String s, String sourceVar,
                                    int pos) throws MetricFormatException {
        String[] tokens = splitFunc(s);
        String id = tokens[0].trim();
        if (id.contains("*")) {
            String[] parts = id.split("\\*");
            double d = Double.parseDouble(parts[0]);
            if (parent.param1 == null) {
                parent.param1 = d;
            } else {
                parent.param2 = d;
            }
            id = parts[1];
        }
        if (tokens.length == 1)
            return new Property(id, id.startsWith(sourceVar) ? Origin.SOURCE
                    : Origin.TARGET);

        Node node = Node.createNode(id);
        s = setParametersFromString(parent, s, pos);
        tokens = splitFunc(s);
        int i = 0;
        for (String argument : Arrays.copyOfRange(tokens, 1, tokens.length)) {
            if (!node.addChild(parsePart(node, argument, sourceVar, i))) {
                throw new MetricFormatException("Could not add child \""
                        + argument + '"');
            }
            i++;
        }
        return node;
    }

    /**
     * Parses String to Metric
     *
     * @param s
     *         String to parse
     * @param sourceVar
     *         Label
     * @return Metric as Output
     * @throws MetricFormatException thrown if there is something wrong
     */
    public static Output parse(String s, String sourceVar)
            throws MetricFormatException {
        if (s.isEmpty())
            throw new MetricFormatException();
        Output output = new Output();
        s = setParametersFromString(output, s, 0);
        output.param1 = null;
        output.param2 = null;
        try {
            output.addChild(parsePart(output, s, sourceVar, 0));
        } catch (MetricFormatException e) {
            throw new MetricFormatException(
                    "Error parsing metric expression \"" + s + "\".", e);
        }
        return output;
    }
    
    public static void main(String[]args){
    	System.out.println();
    }
}