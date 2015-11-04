package org.aksw.limes.core.execution.planning.plan;

import org.apache.log4j.Logger;

/**
 * Implements Instruction class. Instruction is an essential component of the
 * execution plan. An execution is expressed as a set of instruction objects.
 *
 * @author ngonga
 * @author kleanthi
 */

public class Instruction {

    static Logger logger = Logger.getLogger("LIMES");

    public enum Command {
	RUN, INTERSECTION, UNION, DIFF, RETURN, FILTER, XOR;
    };

    private Command command;
    private String measureExpression;
    private String threshold;
    private int sourceMapping;
    private int targetMapping;
    private int resultIndex;

    /**
     * Constructor
     *
     * @param c
     *            Command
     * @param measure
     *            Measure expression, for example
     *            "trigrams(x.rdfs:label, y.rdfs:label)"
     * @param t
     *            Threshold
     * @param source
     *            Source mapping
     * @param target
     *            Target mapping TODO: what is result?
     * 
     */
    public Instruction(Command c, String measure, String thrs, int source, int target, int result) {
	command = c;
	measureExpression = measure;
	threshold = thrs;
	sourceMapping = source;
	targetMapping = target;
	resultIndex = result;
    }

    /**
     * @return current result index
     */
    public int getResultIndex() {
	return resultIndex;
    }

    /**
     * 
     * @param resultIndex,
     *            result index to set
     */
    public void setResultIndex(int resultIndex) {
	this.resultIndex = resultIndex;
    }

    /**
     * @return current command
     */
    public Command getCommand() {
	return command;
    }

    /**
     * 
     * @param command,
     *            command to set
     */
    public void setCommand(Command command) {
	this.command = command;
    }

    /**
     * @return current measure expression
     */
    public String getMeasureExpression() {
	return measureExpression;
    }

    /**
     * 
     * @param measureExpression,
     *            measure expression to set
     */
    public void setMeasureExpression(String measureExpression) {
	this.measureExpression = measureExpression;
    }

    /**
     * @return current source mapping
     */
    public int getSourceMapping() {
	return sourceMapping;
    }

    /**
     * 
     * @param sourceMapping,
     *            source mapping to set
     */
    public void setSourceMapping(int sourceMapping) {
	this.sourceMapping = sourceMapping;
    }

    /**
     * @return current target mapping
     */
    public int getTargetMapping() {
	return targetMapping;
    }

    /**
     * 
     * @param targetMapping,
     *            target mapping to set
     */
    public void setTargetMapping(int targetMapping) {
	this.targetMapping = targetMapping;
    }

    public String getThreshold() {
	return threshold;
    }

    /**
     * 
     * @param threshold,
     *            threshold to set
     */
    public void setThreshold(String threshold) {
	this.threshold = threshold;
    }

    /**
     * String representation of Instruction
     * 
     * @return s, instruction as string
     */
    public String toString() {
	String s = "";
	if (command.equals(Command.RUN)) {
	    s = "RUN\t";
	} else if (command.equals(Command.FILTER)) {
	    s = "FILTER\t";
	} else if (command.equals(Command.DIFF)) {
	    s = "DIFF\t";
	} else if (command.equals(Command.INTERSECTION)) {
	    s = "INTERSECTION\t";
	} else if (command.equals(Command.UNION)) {
	    s = "UNION\t";
	} else if (command.equals(Command.XOR)) {
	    s = "XOR\t";
	}

	s = s + measureExpression + "\t";
	s = s + threshold + "\t";
	s = s + sourceMapping + "\t";
	s = s + targetMapping + "\t";
	s = s + resultIndex;
	return s;
    }

}
