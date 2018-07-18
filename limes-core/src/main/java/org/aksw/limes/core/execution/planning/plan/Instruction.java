package org.aksw.limes.core.execution.planning.plan;

import java.util.EnumSet;

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
 * @author Daniel Obraczka (obraczka@studserv.uni-leipzig.de)
 * @version 1.0
 */

public class Instruction {
	/**
	 * Enum class of allowed command.
	 */
	public enum Command {
		RUN, INTERSECTION, UNION, DIFF, RETURN, FILTER, XOR, REVERSEFILTER, LUKASIEWICZT, LUKASIEWICZTCO,
		LUKASIEWICZDIFF, ALGEBRAICT, ALGEBRAICTCO, ALGEBRAICDIFF, EINSTEINT, EINSTEINTCO, EINSTEINDIFF, HAMACHERT,
		HAMACHERTCO, HAMACHERDIFF, YAGERT, YAGERTCO, YAGERDIFF;

		public static EnumSet<Command> unions = EnumSet.of(UNION, LUKASIEWICZTCO, ALGEBRAICTCO, EINSTEINTCO,
				HAMACHERTCO, YAGERTCO);
		public static EnumSet<Command> intersections = EnumSet.of(INTERSECTION, LUKASIEWICZT, ALGEBRAICT, EINSTEINT,
				HAMACHERT, YAGERT);
		public static EnumSet<Command> diffs = EnumSet.of(DIFF, LUKASIEWICZDIFF, ALGEBRAICDIFF, EINSTEINDIFF,
				HAMACHERDIFF, YAGERDIFF);

		public static EnumSet<Command> crisp = EnumSet.of(UNION, INTERSECTION, DIFF);
		public static EnumSet<Command> lukasiewicz = EnumSet.of(LUKASIEWICZT, LUKASIEWICZTCO, LUKASIEWICZDIFF);
		public static EnumSet<Command> algebraic = EnumSet.of(ALGEBRAICT, ALGEBRAICTCO, ALGEBRAICDIFF);
		public static EnumSet<Command> einstein = EnumSet.of(EINSTEINT, EINSTEINTCO, EINSTEINDIFF);
		public static EnumSet<Command> hamacher = EnumSet.of(HAMACHERT, HAMACHERTCO, HAMACHERDIFF);
		public static EnumSet<Command> yager = EnumSet.of(YAGERT, YAGERTCO, YAGERDIFF);
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
	 * Used for parameterized t-norms/t-conorms
	 */
	private double parameter = Double.NaN;

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

	/**
	 * Constructor of Instruction class with parameters
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
	public Instruction(Command c, String measure, String thrs, int source, int target, int result, double parameter) {
		command = c;
		measureExpression = measure;
		threshold = thrs;
		sourceIndex = source;
		targetIndex = target;
		resultIndex = result;
		setParameter(parameter);
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
		eb.append(parameter, o.getParameter());
		eb.append(threshold, o.getThreshold());
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hb = new HashCodeBuilder();
		hb.append(command);
		hb.append(measureExpression);
		hb.append(parameter);
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

		newInstruction.setParameter(parameter);
		return newInstruction;
	}

	/**
	 * String representation of Instruction.
	 *
	 * @return a string representations of instruction
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (command.equals(Command.RUN)) {
			s.append("RUN\t");
		} else if (command.equals(Command.FILTER)) {
			s.append("FILTER\t");
		} else if (command.equals(Command.DIFF)) {
			s.append("DIFF\t");
		} else if (command.equals(Command.INTERSECTION)) {
			s.append("INTERSECTION\t");
		} else if (command.equals(Command.UNION)) {
			s.append("UNION\t");
		} else if (command.equals(Command.XOR)) {
			s.append("XOR\t");
		} else if (command.equals(Command.REVERSEFILTER)) {
			s.append("REVERSEFILTER\t");
		} else if (command.equals(Command.LUKASIEWICZT)) {
			s.append("LUKASIEWICZTNORM\t");
		} else if (command.equals(Command.LUKASIEWICZTCO)) {
			s.append("LUKASIEWICZTCONORM\t");
		} else if (command.equals(Command.LUKASIEWICZDIFF)) {
			s.append("LUKASIEWICZDIFF\t");
		} else if (command.equals(Command.ALGEBRAICT)) {
			s.append("ALGEBRAICTNORM\t");
		} else if (command.equals(Command.ALGEBRAICTCO)) {
			s.append("ALGEBRAICTCONORM\t");
		} else if (command.equals(Command.ALGEBRAICDIFF)) {
			s.append("ALGEBRAICDIFF\t");
		} else if (command.equals(Command.EINSTEINT)) {
			s.append("EINSTEINTNORM\t");
		} else if (command.equals(Command.EINSTEINTCO)) {
			s.append("EINSTEINTCONORM\t");
		} else if (command.equals(Command.EINSTEINDIFF)) {
			s.append("EINSTEINDIFF\t");
		} else if (command.equals(Command.HAMACHERT)) {
			s.append("HAMACHERTNORM\t");
		} else if (command.equals(Command.HAMACHERTCO)) {
			s.append("HAMACHERTCONORM\t");
		} else if (command.equals(Command.HAMACHERDIFF)) {
			s.append("HAMACHERDIFF\t");
		}

		s.append(measureExpression + "\t");
		s.append(threshold + "\t");
		s.append(sourceIndex + "\t");
		s.append(targetIndex + "\t");
		s.append(resultIndex);
		if (parameter != Double.NaN) {
			s.append(parameter);
		}
		return s.toString();
	}

	public double getParameter() {
		return parameter;
	}

	public void setParameter(double parameter) {
		this.parameter = parameter;
	}

}
