package org.aksw.limes.core.execution.planning.plan;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements Instruction class. Instruction is an essential component of the
 * execution plan. An execution is expressed as a set of instruction objects.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */

public class Instruction {
	/**
	 * Enum class of allowed command.
	 */
	public enum Command {
		RUN, INTERSECTION, UNION, DIFF, RETURN, FILTER, XOR, REVERSEFILTER;
	}

	static Logger logger = LoggerFactory.getLogger(Instruction.class);
	/**
	 * Command of Instruction.
	 */
	private Command command;
	/**
	 * Measure expression of Instruction.
	 */
	private String measureExpression;
	/**
	 * Metric threshold of Instruction.
	 */
	private String threshold;
	/**
	 * Main metric threshold of Instruction in case of non-null
	 * measureExpression.
	 */
	private String mainThreshold = null;
	/**
	 * Index for storing the source mapping in the execution engine buffer.
	 */
	private int sourceIndex;
	/**
	 * Index for storing the target mapping in the execution engine buffer.
	 */
	private int targetIndex;
	/**
	 * Index for storing the result mapping in the execution engine buffer.
	 *
	 */
	private int resultIndex;

	/**
	 * Constructor of Instruction class.
	 *
	 * @param c
	 *            Command
	 * @param measure
	 *            Measure expression
	 * @param thrs
	 *            Threshold
	 * @param source
	 *            Source index
	 * @param target
	 *            Target index
	 * @param result
	 *            Result index
	 *
	 */
	public Instruction(Command c, String measure, String thrs, int source, int target, int result) {
		command = c;
		measureExpression = measure;
		threshold = thrs;
		sourceIndex = source;
		targetIndex = target;
		resultIndex = result;
	}

	/* Setters and Getters for private fields */
	/**
	 * Returns the result index of the instruction.
	 *
	 * @return the result index of the instruction
	 */
	public int getResultIndex() {
		return resultIndex;
	}

	/**
	 * Sets the result index of the instruction.
	 *
	 * @param resultIndex
	 *            The resultIndex to set
	 */
	public void setResultIndex(int resultIndex) {
		this.resultIndex = resultIndex;
	}

	/**
	 * Returns the command of the instruction.
	 *
	 * @return command of the instruction
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * Sets the command of the instruction.
	 *
	 * @param command
	 *            The command to set
	 */
	public void setCommand(Command command) {
		this.command = command;
	}

	/**
	 * Returns the measure expression of the instruction.
	 *
	 * @return the measure expression of the instruction
	 */
	public String getMeasureExpression() {
		return measureExpression;
	}

	/**
	 * Sets the measure expression of the instruction.
	 *
	 * @param measureExpression
	 *            The measure expression to set
	 */
	public void setMeasureExpression(String measureExpression) {
		this.measureExpression = measureExpression;
	}

	/**
	 * Returns the resource index of the instruction.
	 *
	 * @return the resource index of the instruction
	 */
	public int getSourceIndex() {
		return sourceIndex;
	}

	/**
	 * Sets the source index of the instruction.
	 *
	 * @param resourceIndex
	 *            The resourceIndex to set
	 */
	public void setSourceIndex(int resourceIndex) {
		sourceIndex = resourceIndex;
	}

	/**
	 * Returns the target index of the instruction.
	 *
	 * @return the target index of the instruction
	 */
	public int getTargetIndex() {
		return targetIndex;
	}

	/**
	 * Sets the target index of the instruction.
	 *
	 * @param targetIndex
	 *            The targetIndex to set
	 */
	public void setTargetIndex(int targetIndex) {
		this.targetIndex = targetIndex;
	}

	/**
	 * Returns the threshold of the instruction.
	 *
	 * @return the threshold of the instruction
	 */
	public String getThreshold() {
		return threshold;
	}

	/**
	 * Sets the threshold of the instruction.
	 *
	 * @param threshold
	 *            The threshold to set
	 */
	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	/**
	 * Returns the mainThreshold of the instruction.
	 *
	 * @return the mainThreshold
	 */
	public String getMainThreshold() {
		return mainThreshold;
	}

	/**
	 * Sets the mainThreshold of the instruction.
	 *
	 * @param threshold
	 *            The mainThreshold to set
	 */
	public void setMainThreshold(String threshold) {
		mainThreshold = threshold;
	}

	/**
	 * Compares the current instruction with another instruction. If the other
	 * instruction is null then it returns false. Otherwise, the function checks
	 * if each field of the current instruction is equal to the corresponding
	 * field of the other instruction.
	 *
	 * @return true if both instructions are equal, and false otherwise
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Instruction)) {
			return false;
		}
		EqualsBuilder eb = new EqualsBuilder();
		Instruction o = (Instruction) other;
		eb.append(command, o.getCommand());
		eb.append(measureExpression, o.getMeasureExpression());
		eb.append(threshold, o.getThreshold());
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hb = new HashCodeBuilder();
		hb.append(command);
		hb.append(measureExpression);
		hb.append(threshold);
		return hb.toHashCode();
	}

	@Override
	public Instruction clone() {

		Command command = this.command;
		int sourceMapping = sourceIndex;
		int targetMapping = targetIndex;
		int resultIndex = this.resultIndex;

		Instruction newInstruction = new Instruction(command, "", "", sourceMapping, targetMapping, resultIndex);
		if (mainThreshold == null) {
			newInstruction.setMainThreshold(null);
		} else {
			newInstruction.setMainThreshold(new String(mainThreshold));
		}

		if (threshold == null) {
			newInstruction.setThreshold(null);
		} else {
			newInstruction.setThreshold(new String(threshold));
		}

		if (measureExpression == null) {
			newInstruction.setMeasureExpression(null);
		} else {
			newInstruction.setMeasureExpression(new String(measureExpression));
		}

		return newInstruction;
	}

	/**
	 * String representation of Instruction.
	 *
	 * @return a string representations of instruction
	 */
	@Override
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
		} else if (command.equals(Command.REVERSEFILTER)) {
			s = "REVERSEFILTER\t";
		}

		s = s + measureExpression + "\t";
		s = s + threshold + "\t";
		s = s + sourceIndex + "\t";
		s = s + targetIndex + "\t";
		s = s + resultIndex;
		return s;
	}

}
